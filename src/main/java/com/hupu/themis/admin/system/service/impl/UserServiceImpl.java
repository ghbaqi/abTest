package com.hupu.themis.admin.system.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.aop.CacheClear;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.exception.EntityExistException;
import com.hupu.themis.admin.modules.common.exception.EntityNotFoundException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.modules.core.security.JwtUser;
import com.hupu.themis.admin.modules.core.utils.JwtTokenUtil;
import com.hupu.themis.admin.system.domain.User;
import com.hupu.themis.admin.system.mapper.UserMapper;
import com.hupu.themis.admin.system.service.PermissionService;
import com.hupu.themis.admin.system.service.UserService;
import com.hupu.themis.admin.system.service.dto.UserDTO;
import com.hupu.themis.admin.system.service.mapstruct.UserMapStruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @date 2019-11-23
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapStruct userMapStruct;

    @Autowired
    PermissionService permissionService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDTO findById(long id) {
        User user = baseMapper.selectById(id);
        Optional<User> opt = Optional.ofNullable(user);
        ValidationUtil.isNull(opt, "User", "id", id);
        return userMapStruct.toDto(opt.get());
    }

    @Override
    public Map queryAll(UserDTO userDTO, Pageable pageable) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .eq(!ObjectUtils.isEmpty(userDTO.getId()), User::getId, userDTO.getId())
                .eq(!ObjectUtils.isEmpty(userDTO.getEnabled()), User::getEnabled, userDTO.getEnabled())
                .like(!ObjectUtils.isEmpty(userDTO.getUsername()), User::getUsername, userDTO.getUsername())
                .like(!ObjectUtils.isEmpty(userDTO.getEmail()), User::getEmail, userDTO.getEmail());
        long count = baseMapper.queryAllCount(queryWrapper);
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order idOrder = sort.getOrderFor("id");
            if (idOrder != null) {
                queryWrapper.orderByDesc(User::getId);
            }
        }
        queryWrapper.last("limit " + pageable.getOffset() + "," + pageable.getPageSize());
        List<User> users = baseMapper.queryAll(queryWrapper);
        List<UserDTO> userDto = userMapStruct.toDto(users);
        return PageUtil.toPage(userDto, count, pageable.getPageSize());
    }

    @CacheClear(cacheNames = "themis_user")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO create(User resources) {

        if (baseMapper.findByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }

        if (baseMapper.findByEmail(resources.getEmail()) != null) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }

        if (resources.getRoles() == null || resources.getRoles().size() == 0) {
            throw new BadRequestException("角色不能为空");
        }

        // 默认密码 123456
        resources.setPassword("e10adc3949ba59abbe56e057f20f883e");
        resources.setAvatar("");
        resources.setCreateTime(DateUtil.date().toTimestamp());
        if (baseMapper.insert(resources) > 0) {
            //删除用户所有角色
            baseMapper.delUserRoleByUid(resources.getId());
            //重新添加角色
            List<Long> rids = resources.getRoles().stream().map(x -> x.getId()).collect(Collectors.toList());
            rids.forEach((rid) -> {
                baseMapper.addRoleByUid(resources.getId(), rid);
            });
        }
        return userMapStruct.toDto(resources);
    }

    @CacheClear(cacheNames = "themis_user")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User resources) {

        Optional<User> opt = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(opt, "User", "id", resources.getId());
        User user = opt.get();

        /**
         * 根据实际需求修改
         */
        if (user.getId().equals(1L)) {
            throw new BadRequestException("该账号不能被修改");
        }

        User user1 = baseMapper.findByUsername(user.getUsername());
        User user2 = baseMapper.findByEmail(user.getEmail());

        if (resources.getRoles() == null || resources.getRoles().size() == 0) {
            throw new BadRequestException("角色不能为空");
        }

        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }

        if (user2 != null && !user.getId().equals(user2.getId())) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }

        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setRoles(resources.getRoles());
        int i = baseMapper.updateById(user);
        if (i > 0) {
            //删除用户所有角色
            baseMapper.delUserRoleByUid(user.getId());
            //重新添加角色
            List<Long> rids = user.getRoles().stream().map(x -> x.getId()).collect(Collectors.toList());
            rids.forEach((rid) -> {
                baseMapper.addRoleByUid(user.getId(), rid);
            });

        }
    }

    @CacheClear(cacheNames = "themis_user")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id.equals(1L)) {
            throw new BadRequestException("该账号不能被删除");
        }
        baseMapper.delUserRoleByUid(id);
        baseMapper.deleteById(id);
    }

    @Override
    public User findByName(String userName) {
        User user = null;
        if (ValidationUtil.isEmail(userName)) {
            user = baseMapper.findByEmail(userName);
        } else {
            user = baseMapper.findByUsername(userName);
        }
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return user;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(JwtUser jwtUser, String pass) {
        baseMapper.updatePass(jwtUser.getId(), pass);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(JwtUser jwtUser, String url) {
        baseMapper.updateAvatar(jwtUser.getId(), url);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(JwtUser jwtUser, String email) {
        baseMapper.updateEmail(jwtUser.getId(), email);
    }
}
