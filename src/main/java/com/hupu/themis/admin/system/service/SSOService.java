package com.hupu.themis.admin.system.service;


import com.hupu.themis.admin.system.domain.SSOUser;

public interface SSOService {
    SSOUser getUserDetail(String SSOToken);
}
