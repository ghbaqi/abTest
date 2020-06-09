package com.hupu.themis.admin.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
 * 角色
 */
@TableName("role")
@Getter
@Setter
public class Role implements Serializable {
    @TableId(value = "id" ,type = IdType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    private String remark;

    @TableField(exist = false)
    private Set<User> users;

    @TableField(exist = false)
    private Set<Permission> permissions;

    @TableField(exist = false)
    private Set<Menu> menus;

    private Timestamp createTime;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", createDateTime=" + createTime +
                '}';
    }
}
