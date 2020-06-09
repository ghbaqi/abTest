package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.Menu;
import com.hupu.themis.admin.system.domain.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * findByName
     * @param name
     * @return
     */
    @Select("select * from menu where name=#{name}")
    Menu findByName(String name);

    /**
     * findByRoles
     * @param roleSet
     * @return
     */
    Set<Menu> findByRolesOrderBySort(Set<Role> roleSet);

    /**
     * findByPid
     * @param pid
     * @return
     */
    @Select("select * from menu where pid = #{pid}")
    List<Menu> findByPid(long pid);

    /**
     *  通过角色id查询菜单
     * @param rid
     * @return
     */
    @Select("SELECT * FROM menu m,menus_roles mr WHERE m.id = mr.menu_id and mr.role_id = #{rid}")
    List<Menu> findByRid(long rid);

    /**
     * 条件分页查询
     * @param wrapper
     * @return
     */
    @Results({
            @Result(id=true,property="id",column="id"),
            @Result(column = "id" ,property = "roles" ,many = @Many(select = "com.hupu.themis.admin.system.mapper.RoleMapper.queryByMenuId")),
    })
    @Select("select * from menu ${ew.customSqlSegment}")
    List<Menu> queryAll(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("insert into menus_roles values(#{mid},#{rid})")
    void addMenuRoleByRid(@Param("mid") Long id, @Param("rid") Long rid);

    @Delete("delete from menus_roles where menu_id = #{mid}")
    void delMenuRoleByMuenId(Long mid);
}
