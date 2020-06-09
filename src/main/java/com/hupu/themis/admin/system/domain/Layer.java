package com.hupu.themis.admin.system.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 层配置
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Layer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 层名称
     */
    @NotNull(message = "层名称不能为空")
    private String name;

    /**
     * 层描述
     */
    @NotNull(message = "层描述不能为空")
    private String description;

    /**
     * 剩余桶ID,逗号拼接
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    @JsonIgnore
    private String surplusBucketIds;

    /**
     * 剩余桶数
     */
    @JsonIgnore
    private Integer surplusBucketNum;

}
