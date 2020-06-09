package com.hupu.themis.admin.modules.core.service;

import com.hupu.themis.admin.modules.common.exception.EntityNotFoundException;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.modules.core.security.JwtUser;
import com.hupu.themis.admin.system.domain.Permission;
import com.hupu.themis.admin.system.domain.Role;
import com.hupu.themis.admin.system.domain.User;
import com.hupu.themis.admin.system.mapper.PermissionMapper;
import com.hupu.themis.admin.system.mapper.UserMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@CacheConfig(cacheNames = "themis_role")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class JwtUserDetailsService implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    @Cacheable(key = "'loadPermissionByUser:' + #p0")
    public UserDetails loadUserByUsername(String username) {

        User user = null;
        if (ValidationUtil.isEmail(username)) {
            user = userMapper.findByEmail(username);
        } else {
            user = userMapper.findByUsername(username);
        }

        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", username);
        } else {
            return create(user);
        }
    }

    public UserDetails create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getAvatar(),
                user.getEmail(),
                mapToGrantedAuthorities(user.getRoles(), permissionMapper),
                user.getEnabled(),
                user.getCreateTime(),
                user.getLastPasswordResetTime()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<Role> roles, PermissionMapper permissionMapper) {

        Set<Permission> permissions = new HashSet<>();
        for (Role role : roles) {
            permissions.addAll(role.getPermissions());
        }

        List<GrantedAuthority> collect = permissions.parallelStream()
                .map(permission ->
                        new SimpleGrantedAuthority("ROLE_" + permission.getName()))
                .collect(Collectors.toList());

        return collect;
    }
}
