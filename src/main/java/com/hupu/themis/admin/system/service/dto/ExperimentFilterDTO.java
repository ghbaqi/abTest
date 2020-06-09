package com.hupu.themis.admin.system.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hupu.themis.admin.system.enums.SqlKeywordEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 实验过滤表
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
public class ExperimentFilterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifyTime;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签表的名字
     */
    private String tagTableName;

    /**
     * 标签表的字段
     */
    private String tagColumnName;

    /**
     * 操作符
     */
    private SqlKeywordEnum operator;

    /**
     * 值
     */
    private String value;

    /**
     * 与下一个关系AND/OR
     */
    private SqlKeywordEnum nextFilterRelation;

    /**
     * 组内排序
     */
    private Integer sort;

    /**
     * 组编码
     */
    private String groupId;

    /**
     * 组排序
     */
    private Integer groupSort;

    /**
     * 实验配置ID
     */
    private Long experimentGroupId;

}
