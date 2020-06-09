package com.hupu.themis.admin.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageOption {
    @ApiModelProperty(value = "展示值")
    private String label;
    @ApiModelProperty(value = "字段Id")
    private Long value;
    @ApiModelProperty(value = "spm码")
    private String spm;
    @ApiModelProperty(value = "是否被暂用")
    private Boolean isEmploy;
}
