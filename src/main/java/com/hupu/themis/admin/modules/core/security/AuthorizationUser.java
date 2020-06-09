package com.hupu.themis.admin.modules.core.security;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @date 2019-11-30
 */
@Getter
@Setter
public class AuthorizationUser {


    private String username;

//    @NotBlank
//    private String password;

    @NotBlank
    private String tokenKey;

//    @Override
//    public String toString() {
//        return "{username=" + username  + ", password= ******}";
//    }
}
