package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 *
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * findByName
     * @param name
     * @return
     */
    @Select("select * from role where name = #{name}")
    Role findByName(String name);

    /**
     * 通过用户id查询角色
     * @param uid
     * @return
     */
    @Results({
            @Result(id=true,property="id",column="id"),
            @Result(column = "id" ,property = "permissions" ,one = @One(select = "com.hupu.themis.admin.system.mapper.PermissionMapper.findByRoleId")),
            @Result(column = "id" ,property = "menus" ,one = @One(select = "com.hupu.themis.admin.system.mapper.MenuMapper.findByRid"))
    })
    @Select("SELECT r.* from role r,users_roles ur where r.id =ur.role_id and ur.user_id = #{uid}")
    List<Role> findByUid(long uid);


    @Select("select count(1) from role ${ew.customSqlSegment}")
    long queryAllCount(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Results({
            @Result(id=true,property="id",column="id"),
            @Result(column = "id" ,property = "permissions" ,one = @One(select = "com.hupu.themis.admin.system.mapper.PermissionMapper.findByRoleId")),
    })
    @Select("select * from role ${ew.customSqlSegment}")
    List<Role> queryAll(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Delete("delete from roles_permissions where role_id = #{id}")
    void delRolePermissionByRid(Long id);

    @Insert("insert into roles_permissions values(#{rid},#{pid})")
    void addRolePermissionByPid(@Param("rid") Long id, @Param("pid") Long pid);

    @Select("SELECT r.* FROM role r LEFT JOIN menus_roles mr ON (mr.role_id = r.id) where mr.menu_id = #{mid}")
    List<Role> queryByMenuId(Long mid);


}
