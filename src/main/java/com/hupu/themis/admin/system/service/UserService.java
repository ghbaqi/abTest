package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.modules.core.security.JwtUser;
import com.hupu.themis.admin.system.domain.User;
import com.hupu.themis.admin.system.service.dto.UserDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 *
 */
@CacheConfig(cacheNames = "themis_user")
public interface UserService extends IService<User> {

    /**
     * get
     * @param id
     * @return
     */
    @Cacheable(key = "#p0")
    UserDTO findById(long id);

    /**
     * 分页查询
     * @param userDTO
     * @param pageable
     * @return
     */
    @Cacheable(keyGenerator = "keyGenerator")
    Map queryAll(UserDTO userDTO, Pageable pageable);

    /**
     * create
     * @param resources
     * @return
     */
    UserDTO create(User resources);

    /**
     * update
     * @param resources
     */
    void update(User resources);

    /**
     * delete
     * @param id
     */
    void delete(Long id);

    /**
     * findByName
     * @param userName
     * @return
     */
    //@Cacheable(key = "'findByName'+#p0")
    User findByName(String userName);

    /**
     * 修改密码
     * @param jwtUser
     * @param encryptPassword
     */
    void updatePass(JwtUser jwtUser, String encryptPassword);

    /**
     * 修改头像
     * @param jwtUser
     * @param url
     */
    void updateAvatar(JwtUser jwtUser, String url);

    /**
     * 修改邮箱
     * @param jwtUser
     * @param email
     */
    void updateEmail(JwtUser jwtUser, String email);
}
