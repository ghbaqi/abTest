package com.hupu.themis.admin.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.aop.CacheClear;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.exception.EntityExistException;
import com.hupu.themis.admin.modules.common.utils.StringUtils;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.system.domain.Permission;
import com.hupu.themis.admin.system.mapper.PermissionMapper;
import com.hupu.themis.admin.system.service.PermissionService;
import com.hupu.themis.admin.system.service.dto.PermissionDTO;
import com.hupu.themis.admin.system.service.mapstruct.PermissionMapStruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper,Permission> implements PermissionService {

   
    @Autowired
    private PermissionMapStruct permissionMapStruct;

    @Override
    public PermissionDTO findById(long id) {
        Optional<Permission> permission = Optional.ofNullable(baseMapper.selectById(id));
        ValidationUtil.isNull(permission,"Permission","id",id);
        return permissionMapStruct.toDto(permission.get());
    }

    @CacheClear(cacheNames = "themis_permission")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionDTO create(Permission resources) {
        if(baseMapper.findByName(resources.getName()) != null){
            throw new EntityExistException(Permission.class,"name",resources.getName());
        }
        return permissionMapStruct.toDto(baseMapper.insert(resources)>0?resources:null);
    }

    @CacheClear(cacheNames = "themis_permission")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Permission resources) {
        Optional<Permission> optionalPermission = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(optionalPermission,"Permission","id",resources.getId());

        Permission permission = optionalPermission.get();

        /**
         * 根据实际需求修改
         */
        if(permission.getId().equals(1L)){
            throw new BadRequestException("该权限不能被修改");
        }

        Permission permission1 = baseMapper.findByName(resources.getName());

        if(permission1 != null && !permission1.getId().equals(permission.getId())){
            throw new EntityExistException(Permission.class,"name",resources.getName());
        }

        permission.setName(resources.getName());
        permission.setAlias(resources.getAlias());
        permission.setPid(resources.getPid());
        baseMapper.updateById(permission);
    }

    @CacheClear(cacheNames = "themis_permission")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        /**
         * 根据实际需求修改
         */
        if(id.equals(1L)){
            throw new BadRequestException("该权限不能被删除");
        }
        List<Permission> permissionList = this.findByPid(id);
        for (Permission permission : permissionList) {
            baseMapper.deleteById(permission.getId());
        }
        baseMapper.deleteById(id);
    }

    @Override
    public Object getPermissionTree(List<Permission> permissions) {
        //TODO 待优化
        List<Map<String,Object>> list = new LinkedList<>();
        permissions.forEach(permission -> {
                    if (permission!=null){
                        List<Permission> permissionList = this.findByPid(permission.getId());
                        Map<String,Object> map = new HashMap<>();
                        map.put("id",permission.getId());
                        map.put("label",permission.getAlias());
                        if(permissionList!=null && permissionList.size()!=0){
                            map.put("children",getPermissionTree(permissionList));
                        }
                        list.add(map);
                    }
                }
        );
        return list;
    }

    @Override
    public List<Permission> findByPid(long pid) {
        return baseMapper.selectList(Wrappers.<Permission>query().eq("pid",pid));
    }

    @Override
    public Object buildTree(List<PermissionDTO> permissionDTOS) {

        List<PermissionDTO> trees = new ArrayList<PermissionDTO>();

        for (PermissionDTO permissionDTO : permissionDTOS) {

            if ("0".equals(permissionDTO.getPid().toString())) {
                trees.add(permissionDTO);
            }

            for (PermissionDTO it : permissionDTOS) {
                if (it.getPid().equals(permissionDTO.getId())) {
                    if (permissionDTO.getChildren() == null) {
                        permissionDTO.setChildren(new ArrayList<PermissionDTO>());
                    }
                    permissionDTO.getChildren().add(it);
                }
            }
        }

        Integer totalElements = permissionDTOS!=null?permissionDTOS.size():0;

        Map map = new HashMap();
        map.put("content",trees.size() == 0?permissionDTOS:trees);
        map.put("totalElements",totalElements);
        return map;
    }

    @Override
    public List<PermissionDTO> queryAll(String name) {
        List<Permission> permissions = baseMapper.selectList(Wrappers.<Permission>lambdaQuery()
                .like(StringUtils.isNotBlank(name), Permission::getName, name));
        return permissionMapStruct.toDto(permissions);
    }
}
