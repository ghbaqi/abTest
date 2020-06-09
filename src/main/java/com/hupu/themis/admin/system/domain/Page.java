package com.hupu.themis.admin.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 页面配置
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Page implements Serializable {

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
     * 页面SPM
     */
    private String spm;

    /**
     * 页面描述
     */
    private String description;

    /**
     * 页面分类
     */
    private PageCategoryEnum category;

    /**
     * client、m、pc、h5
     */
    private TerminalTypeEnum terminalType;

    /**
     * 排序用的
     */
    @TableField(exist = false)
    private int score;

    @TableField(exist = false)
    private Boolean isEmploy;
}
