package com.hupu.themis.admin.modules.core.rest;

import com.google.common.collect.Sets;
import com.hupu.themis.admin.modules.common.aop.log.Log;
import com.hupu.themis.admin.modules.core.security.AuthenticationToken;
import com.hupu.themis.admin.modules.core.security.AuthorizationUser;
import com.hupu.themis.admin.modules.core.security.JwtUser;
import com.hupu.themis.admin.modules.core.utils.JwtTokenUtil;
import com.hupu.themis.admin.system.domain.Role;
import com.hupu.themis.admin.system.domain.SSOUser;
import com.hupu.themis.admin.system.domain.User;
import com.hupu.themis.admin.system.service.SSOService;
import com.hupu.themis.admin.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @date 2019-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Autowired
    private SSOService ssoService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    /**
     * 登录授权
     *
     * @param authorizationUser
     * @return
     */
    @Log(description = "用户登录")
    @PostMapping(value = "${jwt.auth.path}")
    public ResponseEntity<?> authenticationLogin(@RequestBody AuthorizationUser authorizationUser) {
//        if(!userDetails.getPassword().equals(EncryptUtils.encryptPassword(authorizationUser.getPassword()))){
//            throw new AccountExpiredException("密码错误");
//        }
//
//        if(!userDetails.isEnabled()){
//            throw new AccountExpiredException("账号已停用，请联系管理员");
//        }

        SSOUser ssoUser = ssoService.getUserDetail(authorizationUser.getTokenKey());
        if (ssoUser == null) {
            throw new AccountExpiredException("账号或者密码错误");
        }
        String userName = null;
        User user = null;
        try {
            log.info("当前登录用户: " + ssoUser.getUsername());
            user = userService.findByName(ssoUser.getUsername());
            userName = user.getUsername();
        } catch (Exception e) {
            log.info("初次登录:", e);
        }
        if (user == null) {
            user = new User();
            user.setEmail(ssoUser.getUsername() + "@hupu.com");
            user.setEnabled(true);
            user.setUsername(ssoUser.getUsername());
            Role role = new Role();
            role.setId(1L);
            user.setRoles(Sets.newHashSet(role));
            userService.create(user);
            userName = ssoUser.getUsername();
        }
        // 生成令牌
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if (!userDetails.isEnabled()) {
            throw new AccountExpiredException("账号已停用，请联系管理员");
        }
        final String token = jwtTokenUtil.generateToken(userDetails);
        // 返回 token
        return ResponseEntity.ok(new AuthenticationToken(token));
    }

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @GetMapping(value = "${jwt.auth.account}")
    public ResponseEntity getUserInfo(HttpServletRequest request) {
        JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(jwtTokenUtil.getUserName(request));
        return ResponseEntity.ok(jwtUser);
    }
}
