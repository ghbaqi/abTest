package com.hupu.themis.admin.modules.core.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 *
 * @date 2019-11-23
 * 返回token
 */
@Getter
@AllArgsConstructor
public class AuthenticationToken implements Serializable {

    private final String token;
}
