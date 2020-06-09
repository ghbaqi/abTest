package com.hupu.themis.admin.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Set;

/**
 *
 */
@Getter
@Setter
@TableName("menu")
public class Menu {
    @TableId(value = "id" ,type = IdType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    private Long sort;

    private String path;

    private String component;

    private String icon;

    private Long pid;

    /**
     * 是否为外链 true/false
     */
    private Boolean iFrame;


    @TableField(exist = false)
    private Set<Role> roles;

    private Timestamp createTime;
}
