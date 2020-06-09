package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hupu.themis.admin.system.domain.Permission;
import com.hupu.themis.admin.system.domain.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * findByName
     * @param name
     * @return
     */
    @Select("select * from permission where name = #{name}")
    Permission findByName(String name);

    /**
     * findByRoles
     * @param roleSet
     * @return
     */
    Set<Permission> findByRoles(Set<Role> roleSet);


    /**
     *  通过角色id查权限
     */
    @Select("SELECT p.* from roles_permissions up LEFT JOIN permission p ON(up.permission_id = p.id) WHERE up.role_id = #{rid} ")
    List<Permission> findByRoleId(long rid);

}
