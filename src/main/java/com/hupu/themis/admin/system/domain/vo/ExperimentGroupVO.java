package com.hupu.themis.admin.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hupu.themis.admin.system.domain.ExperimentItem;
import com.hupu.themis.admin.system.domain.Page;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.RateFlowStrategy;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.service.dto.LayerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentGroupVO {
    private Long id;
    /**
     * 实验名称
     */
    @NotNull(message = "实验名称不能为空")
    private String name;
    /**
     * 客户端实验或者服务端实验
     */
    @NotNull(message = "分端类型不能为空")
    private TerminalTypeEnum terminalType;

    /**
     * 层ID
     */
    @NotNull(message = "层ID不能为空")
    private Long layerId;
    /**
     * 描述
     */
    private String description;

    @Size(min = 1, message = "至少需要有一个实验参数")
    private List<ExperimentItem> items;

    private List<Page> pages;

    @NotNull(message = "流量占比不能为空")
    @Digits(integer = 1, fraction = 4)
    private Double rateFlow;

    @NotNull(message = "状态不能为空")
    private GroupStatusEnum status;

    @NotNull(message = "流量分配策略只能为AVG或者CUSTOM")
    private RateFlowStrategy rateFlowStrategy;

    private List<List<FilterItemVO>> filters;

    @NotNull(message = "业务线不能为空")
    private PageCategoryEnum bisLine;

    private LayerDTO layer;

    private String userName;

   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    private String bisLineName;

    private String terminalTypeName;

    private Long duration;
}
