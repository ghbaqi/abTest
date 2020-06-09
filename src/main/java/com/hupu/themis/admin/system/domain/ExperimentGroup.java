package com.hupu.themis.admin.system.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.RateFlowStrategy;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 实验组配置
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExperimentGroup implements Serializable {

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
     * 实验名称
     */
    @NotNull
    private String name;

    /**
     * 客户端实验或者服务端实验
     */
    @NotNull
    private TerminalTypeEnum terminalType;

    /**
     * 实验key
     */
    @NotNull
    private String paramKey;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 层ID
     */
    @NotNull
    private Long layerId;
    @NotNull
    private String layerName;

    /**
     * 桶数
     */
    private Integer bucketNum;

    /**
     * 实验组剩余桶数
     */
    private Integer surplusBucketNum;

    /**
     * 剩余桶ID,逗号拼接
     */

    @TableField(strategy = FieldStrategy.IGNORED)
    private String surplusBucketIds;

    /**
     * 实验组所有桶
     */
    private String bucketIds;
    /**
     * 描述
     */
    @NotNull
    private String description;

    @TableField(exist = false)
    @Size(min = 1, max = 10, message = "实验参数必须大于等于1 小于等于10")
    private List<ExperimentItem> items;

    @TableField(exist = false)
    private Layer layer;

    @TableField(exist = false)
    private List<Page> pages;

    @TableField(exist = false)
    @NotNull(message = "流量占比不能为空")
    @Digits(integer = 2, fraction = 2)
    private Double rateFlow;

    private GroupStatusEnum status;

    @NotNull(message = "流量分配策略只能为AVG或者CUSTOM")
    private RateFlowStrategy rateFlowStrategy;

    @TableField(exist = false)
    private List<ExperimentFilter> filters;

    private PageCategoryEnum bisLine;

    private Date startTime;

    private Long duration;

    private String bisLineName;
}
