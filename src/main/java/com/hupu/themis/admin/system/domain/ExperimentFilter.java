package com.hupu.themis.admin.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2020-02-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExperimentFilter implements Serializable {

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
