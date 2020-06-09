package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.Menu;
import com.hupu.themis.admin.system.domain.Role;
import com.hupu.themis.admin.system.service.dto.MenuDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@CacheConfig(cacheNames = "themis_menu")
public interface MenuService extends IService<Menu> {

    /**
     * get
     *
     * @param id
     * @return
     */
    @Cacheable(key = "#p0")
    MenuDTO findById(long id);

    /**
     * create
     *
     * @param resources
     * @return
     */
    MenuDTO create(Menu resources);

    /**
     * update
     *
     * @param resources
     */
    void update(Menu resources);

    /**
     * delete
     *
     * @param id
     */
    void delete(Long id);

    /**
     * permission tree
     *
     * @return
     */
    @Cacheable(key = "'tree'")
    Object getMenuTree(List<Menu> menus);

    /**
     * findByPid
     *
     * @param pid
     * @return
     */
    @Cacheable(key = "'pid:'+#p0")
    List<Menu> findByPid(long pid);

    /**
     * build Tree
     *
     * @param menuDTOS
     * @return
     */
    Map buildTree(List<MenuDTO> menuDTOS);

    /**
     * findByRoles
     *
     * @param roles
     * @return
     */
    List<MenuDTO> findByRoles(Set<Role> roles);

    /**
     * buildMenus
     *
     * @param byRoles
     * @return
     */
    Object buildMenus(List<MenuDTO> byRoles);

    /**
     * queryAll
     *
     * @param name
     * @return
     */
    @Cacheable(key = "'queryAll:'+#p0")
    List<MenuDTO> queryAll(String name);
}
