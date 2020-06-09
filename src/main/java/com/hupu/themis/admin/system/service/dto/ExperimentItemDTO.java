package com.hupu.themis.admin.system.service.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hupu.themis.admin.system.enums.ExperimentItemTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
public class ExperimentItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 实验key(冗余)
     */
    private String paramKey;

    /**
     * 值
     */
    private String paramValue;

    /**
     * 桶ID,逗号拼接
     */
    @JsonIgnore
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

    private Double rateFlow;

    private String whiteList;

    private String name;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentItemDTO that = (ExperimentItemDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
