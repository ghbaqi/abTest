package com.hupu.themis.admin.system.service.impl;


import com.hupu.themis.admin.system.domain.SSOUser;
import com.hupu.themis.admin.system.service.SSOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SSOServiceImpl implements SSOService {
    @Value("${jwt.sso.host}")
    private String SSOHost;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SSOUser getUserDetail(String SSOToken) {
        try {
            ResponseEntity<SSOUser> entity =
                restTemplate.getForEntity(SSOHost + "/api/sso/token/" + SSOToken, SSOUser.class);
            if (entity.hasBody()) {
                return entity.getBody();
            }
        } catch (Exception e) {
            log.error("user:[" + SSOToken + "]登录失败", e);
        }
        return null;
    }
}
