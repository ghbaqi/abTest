package com.hupu.themis.admin.system.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.RateFlowStrategy;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
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
@ApiModel(description = "实验实体")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExperimentGroupDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "创建时间")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifyTime;

    /**
     * 实验名称
     */
    @ApiModelProperty(value = "实验名称")
    private String name;

    /**
     * 客户端实验或者服务端实验
     */
    @ApiModelProperty(value = "分端类型")
    private TerminalTypeEnum terminalType;

    /**
     * 实验key
     */
    @ApiModelProperty(hidden = true)
    private String paramKey;

    /**
     * 用户ID
     */
    @ApiModelProperty(hidden = true)
    private Long userId;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户")
    private String userName;

    /**
     * 层ID
     */
    @JsonIgnore
    private Long layerId;

    private String layerName;

    /**
     * 桶数
     */
    @ApiModelProperty(hidden = true)
    private Integer bucketNum;

    /**
     * 实验组剩余桶数
     */
    @ApiModelProperty(hidden = true)
    private Integer surplusBucketNum;

    /**
     * 剩余桶ID,逗号拼接
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String surplusBucketIds;

    /**
     * 实验组所有桶
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String bucketIds;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "实验参数配置")
    private List<ExperimentItemDTO> items;

    @ApiModelProperty(value = "实验所属层信息")
    private LayerDTO layer;

    @ApiModelProperty(value = "实验关联页面信息")
    private List<PageDTO> pages;

    @ApiModelProperty(value = "状态")
    private GroupStatusEnum status;

    @NotNull(message = "流量分配策略只能为AVG或者CUSTOM")
    private RateFlowStrategy rateFlowStrategy;

    @ApiModelProperty(value = "过滤条件")
    private List<ExperimentFilterDTO> filters;

    private PageCategoryEnum bisLine;

    @ApiModelProperty(value = "查询关键字")
    private String searchKeyword;

    private Date startTime;

    private Double rateFlow;

    private Long duration;

    private String bisLineName;
}
