package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.modules.common.aop.CacheClear;
import com.hupu.themis.admin.system.domain.Role;
import com.hupu.themis.admin.system.service.dto.RoleDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 *
 * @date 2018-12-03
 */
@CacheConfig(cacheNames = "themis_role")
public interface RoleService extends IService<Role> {

    /**
     * get
     * @param id
     * @return
     */
    @Cacheable(key = "#p0")
    RoleDTO findById(long id);

    /**
     * 分页查询
     * @param resources
     * @param pageable
     * @return
     */
    @Cacheable(keyGenerator = "keyGenerator")
    Map queryAll(RoleDTO resources, Pageable pageable);

    /**
     * create
     * @param resources
     * @return
     */
    RoleDTO create(Role resources);

    /**
     * update
     * @param resources
     */
    void update(Role resources);

    /**
     * delete
     * @param id
     */
    void delete(Long id);

    /**
     * role tree
     * @return
     */
    @Cacheable(key = "'tree'")
    Object getRoleTree();


}
