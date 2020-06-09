package com.hupu.themis.admin.system.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hupu.themis.admin.system.enums.ExperimentItemTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 实验详情
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExperimentItem implements Serializable {

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
     * 实验key(冗余)
     */
    @NotNull(message = "实验参数key不能为空")
    private String paramKey;

    /**
     * 值
     */
    @NotNull(message = "实验参数value不能为空")
    private String paramValue;

    /**
     * 桶ID,逗号拼接
     */
    private String bucketIds;

    /**
     * 桶数
     */
    private Integer bucketNum;

    /**
     * 所属实验组的总桶数
     */
    private Integer experimentGroupBucketNum;

    /**
     * 实验组类型 实验组/参照组
     */
    private ExperimentItemTypeEnum itemType;

    /**
     * 层ID
     */
    private Long experimentGroupId;

    @TableField(exist = false)
    private Double rateFlow;

    @TableField(strategy = FieldStrategy.IGNORED)
    private String whiteList;

    private String name;

    private String description;

}
