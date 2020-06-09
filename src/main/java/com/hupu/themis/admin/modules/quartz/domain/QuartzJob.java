package com.hupu.themis.admin.modules.quartz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @date 2019-01-07
 */
@Data
@TableName("quartz_job")
public class QuartzJob implements Serializable {

    public static final String JOB_KEY = "JOB_KEY";

    @TableId(value = "id" ,type = IdType.AUTO)
    private Long id;

    /**
     * 定时器名称
     */
    private String jobName;

    /**
     * Bean名称
     */
    @NotBlank
    private String beanName;

    /**
     * 方法名称
     */
    @NotBlank
    private String methodName;

    /**
     * 参数
     */
    private String params;

    /**
     * cron表达式
     */
    @NotBlank
    private String cronExpression;

    /**
     * 状态
     */
    private Boolean isPause = false;

    /**
     * 备注
     */
    @NotBlank
    private String remark;

    /**
     * 创建日期
     */
    private Timestamp updateTime;
}