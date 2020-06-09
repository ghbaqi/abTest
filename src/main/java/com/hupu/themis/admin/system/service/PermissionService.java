package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.Permission;
import com.hupu.themis.admin.system.service.dto.PermissionDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 *
 * @date 2018-12-08
 */
@CacheConfig(cacheNames = "themis_permission")
public interface PermissionService extends IService<Permission> {

    /**
     * get
     * @param id
     * @return
     */
    @Cacheable(key = "#p0")
    PermissionDTO findById(long id);

    /**
     * create
     * @param resources
     * @return
     */
    PermissionDTO create(Permission resources);

    /**
     * update
     * @param resources
     */
    void update(Permission resources);

    /**
     * delete
     * @param id
     */
    void delete(Long id);

    /**
     * permission tree
     * @return
     */
    @Cacheable(key = "'tree'")
    Object getPermissionTree(List<Permission> permissions);

    /**
     * findByPid
     * @param pid
     * @return
     */
    @Cacheable(key = "'pid:'+#p0")
    List<Permission> findByPid(long pid);

    /**
     * build Tree
     * @param permissionDTOS
     * @return
     */
    Object buildTree(List<PermissionDTO> permissionDTOS);

    /**
     * queryAll
     * @param name
     * @return
     */
    @Cacheable(key = "'queryAll:'+#p0")
    List<PermissionDTO> queryAll(String name);
}
