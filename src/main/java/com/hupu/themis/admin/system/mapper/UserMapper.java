package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface UserMapper extends BaseMapper<User> {
    /**
     * findByUsername
     * @param username
     * @return
     */
    @Results({
            @Result(id=true,property="id",column="id"),
            @Result(column = "id",property = "roles",many = @Many(select = "com.hupu.themis.admin.system.mapper.RoleMapper.findByUid"))
    })
    @Select("select * from user u where u.username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 条件分页查询
     * @param wrapper
     * @return
     */
    @Results({
            @Result(id=true,property="id",column="id"),
            @Result(column = "id",property = "roles",many = @Many(select = "com.hupu.themis.admin.system.mapper.RoleMapper.findByUid"))
    })
    @Select("select * from user u ${ew.customSqlSegment}")
    List<User> queryAll(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select count(1) from user u ${ew.customSqlSegment}")
    long queryAllCount(@Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * findByEmail
     * @param email
     * @return
     */
    @Results({
            @Result(id=true,property="id",column="id"),
            @Result(column = "id",property = "roles",many = @Many(select = "com.hupu.themis.admin.system.mapper.RoleMapper.findByUid"))
    })
    @Select("select * from user u where u.email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 修改密码
     * @param id
     * @param pass
     */
    @Select(value = "update user set password = #{pass} where id = #{id}")
    void updatePass(@Param("id") Long id, @Param("pass") String pass);

    /**
     * 修改头像
     * @param id
     * @param url
     */
    @Select("update user set avatar = #{url} where id = #{id}")
    void updateAvatar(@Param("id") Long id, @Param("url") String url);

    /**
     * 修改邮箱
     * @param id
     * @param email
     */
    @Select("update user set email = #{email} where id = #{id}")
    void updateEmail(@Param("id") Long id, @Param("email") String email);

    @Delete("delete from users_roles where user_id = #{id}")
    void delUserRoleByUid(Long id);

    @Insert("insert into users_roles values(#{uid},#{rid})")
    void addRoleByUid(@Param("uid") Long id, @Param("rid") Long rid);
}
