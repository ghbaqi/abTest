package com.hupu.themis.admin.system.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.aop.CacheClear;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.exception.EntityExistException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.system.domain.Role;
import com.hupu.themis.admin.system.mapper.RoleMapper;
import com.hupu.themis.admin.system.service.RoleService;
import com.hupu.themis.admin.system.service.dto.RoleDTO;
import com.hupu.themis.admin.system.service.mapstruct.RoleMapStruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Autowired
    private RoleMapStruct roleMapStruct;

    @Override
    public RoleDTO findById(long id) {
        Optional<Role> role = Optional.ofNullable(baseMapper.selectById(id));
        ValidationUtil.isNull(role, "Role", "id", id);
        return roleMapStruct.toDto(role.get());
    }

    @Override
    public Map queryAll(RoleDTO resources, Pageable pageable) {
        LambdaQueryWrapper<Role> queryWrapper = Wrappers.<Role>lambdaQuery().like(!ObjectUtils.isEmpty(resources.getName()), Role::getName, resources.getName());
        long count = baseMapper.queryAllCount(queryWrapper);
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order idOrder = sort.getOrderFor("id");
            if (idOrder != null) {
                queryWrapper.orderByDesc(Role::getId);
            }
        }
        queryWrapper.last("limit " + pageable.getOffset() + "," + pageable.getPageSize());
        List<Role> roles = baseMapper.queryAll(queryWrapper);
        List<RoleDTO> roleDto = roleMapStruct.toDto(roles);
        return PageUtil.toPage(roleDto, count, pageable.getPageSize());
    }

    @CacheClear(cacheNames = "themis_role")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO create(Role resources) {
        if (baseMapper.findByName(resources.getName()) != null) {
            throw new EntityExistException(Role.class, "username", resources.getName());
        }
        resources.setCreateTime(DateUtil.date().toTimestamp());
        if (baseMapper.insert(resources) > 0) {
            //删除角色所有权限
            baseMapper.delRolePermissionByRid(resources.getId());
            //重新添加角色
            List<Long> pids = resources.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());
            pids.forEach((pid) -> {
                baseMapper.addRolePermissionByPid(resources.getId(), pid);
            });
        }
        return roleMapStruct.toDto(resources);
    }

    @CacheClear(cacheNames = "themis_role")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Role resources) {
        Optional<Role> optionalRole = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(optionalRole, "Role", "id", resources.getId());
        Role role = optionalRole.get();

        /**
         * 根据实际需求修改
         */
        if (role.getId().equals(1L)) {
            throw new BadRequestException("该角色不能被修改");
        }

        Role role1 = baseMapper.findByName(resources.getName());

        if (role1 != null && !role1.getId().equals(role.getId())) {
            throw new EntityExistException(Role.class, "username", resources.getName());
        }

        role.setName(resources.getName());
        role.setRemark(resources.getRemark());
        role.setPermissions(resources.getPermissions());
        int i = baseMapper.updateById(role);
        if (i > 0) {
            //删除角色所有权限
            baseMapper.delRolePermissionByRid(role.getId());
            //重新添加角色
            List<Long> pids = role.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());
            pids.forEach((pid) -> {
                baseMapper.addRolePermissionByPid(role.getId(), pid);
            });
        }
    }

    @CacheClear(cacheNames = "themis_role")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {

        /**
         * 根据实际需求修改
         */
        if (id.equals(1L)) {
            throw new BadRequestException("该角色不能被删除");
        }
        baseMapper.delRolePermissionByRid(id);
        baseMapper.deleteById(id);
    }

    @Override
    public Object getRoleTree() {

        List<Role> roleList = baseMapper.selectList(null);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Role role : roleList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", role.getId());
            map.put("label", role.getName());
            list.add(map);
        }
        return list;
    }
}
