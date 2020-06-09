package com.hupu.themis.admin.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.aop.CacheClear;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.exception.EntityExistException;
import com.hupu.themis.admin.modules.common.utils.StringUtils;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.system.domain.Menu;
import com.hupu.themis.admin.system.domain.Role;
import com.hupu.themis.admin.system.domain.vo.MenuMetaVo;
import com.hupu.themis.admin.system.domain.vo.MenuVo;
import com.hupu.themis.admin.system.mapper.MenuMapper;
import com.hupu.themis.admin.system.service.MenuService;
import com.hupu.themis.admin.system.service.dto.MenuDTO;
import com.hupu.themis.admin.system.service.mapstruct.MenuMapStruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Autowired
    private MenuMapStruct menuMapStruct;

    @Override
    public MenuDTO findById(long id) {
        Optional<Menu> menu = Optional.ofNullable(baseMapper.selectById(id));
        ValidationUtil.isNull(menu, "Menu", "id", id);
        return menuMapStruct.toDto(menu.get());
    }

    @Override
    public List<MenuDTO> findByRoles(Set<Role> roles) {
        Set<Menu> menus = new LinkedHashSet<>();
        for (Role role : roles) {
            menus.addAll(role.getMenus());
        }
        return menus.stream().map(menuMapStruct::toDto).sorted(Comparator.comparing(MenuDTO::getSort)).collect(Collectors.toList());
    }

    @CacheClear(cacheNames = "themis_menu")
    @Override
    public MenuDTO create(Menu resources) {
        if (baseMapper.findByName(resources.getName()) != null) {
            throw new EntityExistException(Menu.class, "name", resources.getName());
        }
        if (resources.getIFrame()) {
            if (!(resources.getPath().toLowerCase().startsWith("http://") || resources.getPath().toLowerCase().startsWith("https://"))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
        resources.setCreateTime(DateUtil.date().toTimestamp());
        if (baseMapper.insert(resources) > 0) {
            List<Long> rids = resources.getRoles().stream().map(x -> x.getId()).collect(Collectors.toList());
            rids.forEach(rid -> baseMapper.addMenuRoleByRid(resources.getId(), rid));
        }
        return menuMapStruct.toDto(resources);
    }

    @CacheClear(cacheNames = "themis_menu")
    @Override
    public void update(Menu resources) {
        Optional<Menu> optionalMenu = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(optionalMenu, "Menu", "id", resources.getId());

        if (resources.getIFrame()) {
            if (!(resources.getPath().toLowerCase().startsWith("http://") || resources.getPath().toLowerCase().startsWith("https://"))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
        Menu menu = optionalMenu.get();
        Menu menu1 = baseMapper.findByName(resources.getName());

        if (menu1 != null && !menu1.getId().equals(menu.getId())) {
            throw new EntityExistException(Menu.class, "name", resources.getName());
        }
        menu.setName(resources.getName());
        menu.setComponent(resources.getComponent());
        menu.setPath(resources.getPath());
        menu.setIcon(resources.getIcon());
        menu.setIFrame(resources.getIFrame());
        menu.setPid(resources.getPid());
        menu.setSort(resources.getSort());
        menu.setRoles(resources.getRoles());
        if (baseMapper.updateById(menu) > 0) {
            baseMapper.delMenuRoleByMuenId(menu.getId());
            List<Long> rids = resources.getRoles().stream().map(x -> x.getId()).collect(Collectors.toList());
            rids.forEach(rid -> baseMapper.addMenuRoleByRid(resources.getId(), rid));
        }
    }

    @CacheClear(cacheNames = "themis_menu")
    @Override
    public void delete(Long id) {
        List<Menu> menuList = baseMapper.findByPid(id);
        for (Menu menu : menuList) {
            //删除外键约束
            baseMapper.delMenuRoleByMuenId(menu.getId());
            baseMapper.deleteById(menu.getId());
        }
        baseMapper.delMenuRoleByMuenId(id);
        baseMapper.deleteById(id);
    }

    @Override
    public Object getMenuTree(List<Menu> menus) {
        List<Map<String, Object>> list = new LinkedList<>();
        menus.forEach(menu -> {
                    if (menu != null) {
                        List<Menu> menuList = baseMapper.findByPid(menu.getId());
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", menu.getId());
                        map.put("label", menu.getName());
                        if (menuList != null && menuList.size() != 0) {
                            map.put("children", getMenuTree(menuList));
                        }
                        list.add(map);
                    }
                }
        );
        return list;
    }

    @Override
    public List<Menu> findByPid(long pid) {
        return baseMapper.selectList(Wrappers.<Menu>lambdaQuery().eq(Menu::getPid, pid));
    }

    @Override
    public Map buildTree(List<MenuDTO> menuDTOS) {
        List<MenuDTO> trees = new ArrayList<MenuDTO>();

        for (MenuDTO menuDTO : menuDTOS) {

            if ("0".equals(menuDTO.getPid().toString())) {
                trees.add(menuDTO);
            }

            for (MenuDTO it : menuDTOS) {
                if (it.getPid().equals(menuDTO.getId())) {
                    if (menuDTO.getChildren() == null) {
                        menuDTO.setChildren(new ArrayList<MenuDTO>());
                    }
                    menuDTO.getChildren().add(it);
                }
            }
        }

        Integer totalElements = menuDTOS != null ? menuDTOS.size() : 0;
        Map map = new HashMap();
        map.put("content", trees.size() == 0 ? menuDTOS : trees);
        map.put("totalElements", totalElements);
        return map;
    }

    @Override
    public List<MenuVo> buildMenus(List<MenuDTO> menuDTOS) {
        List<MenuVo> list = new LinkedList<>();
        menuDTOS.forEach(menuDTO -> {
                    if (menuDTO != null) {
                        List<MenuDTO> menuDTOList = menuDTO.getChildren();
                        MenuVo menuVo = new MenuVo();
                        menuVo.setName(menuDTO.getName());
                        menuVo.setPath(menuDTO.getPath());

                        // 如果不是外链
                        if (!menuDTO.getIFrame()) {
                            if (menuDTO.getPid().equals(0L)) {
                                //一级目录需要加斜杠，不然访问不了
                                menuVo.setPath("/" + menuDTO.getPath());
                                menuVo.setComponent(StrUtil.isEmpty(menuDTO.getComponent()) ? "Layout" : menuDTO.getComponent());
                            } else if (!StrUtil.isEmpty(menuDTO.getComponent())) {
                                menuVo.setComponent(menuDTO.getComponent());
                            }
                        }
                        menuVo.setMeta(new MenuMetaVo(menuDTO.getName(), menuDTO.getIcon()));
                        if (menuDTOList != null && menuDTOList.size() != 0) {
                            menuVo.setAlwaysShow(true);
                            menuVo.setRedirect("noredirect");
                            menuVo.setChildren(buildMenus(menuDTOList));
                            // 处理是一级菜单并且没有子菜单的情况
                        } else if (menuDTO.getPid().equals(0L)) {
                            MenuVo menuVo1 = new MenuVo();
                            menuVo1.setMeta(menuVo.getMeta());
                            // 非外链
                            if (!menuDTO.getIFrame()) {
                                menuVo1.setPath("index");
                                menuVo1.setName(menuVo.getName());
                                menuVo1.setComponent(menuVo.getComponent());
                            } else {
                                menuVo1.setPath(menuDTO.getPath());
                            }
                            menuVo.setName(null);
                            menuVo.setMeta(null);
                            menuVo.setComponent("Layout");
                            List<MenuVo> list1 = new ArrayList<MenuVo>();
                            list1.add(menuVo1);
                            menuVo.setChildren(list1);
                        }
                        list.add(menuVo);
                    }
                }
        );
        return list;
    }

    @Override
    public List<MenuDTO> queryAll(String name) {
        List<Menu> menus = baseMapper.queryAll(Wrappers.<Menu>lambdaQuery().like(StringUtils.isNotBlank(name), Menu::getName, name).orderByAsc(Menu::getSort));
        return menuMapStruct.toDto(menus);
    }
}
