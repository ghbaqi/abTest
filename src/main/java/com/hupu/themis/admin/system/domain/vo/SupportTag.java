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
public class SupportTag {
    @ApiModelProperty(value = "展示值")
    private String label;
    @ApiModelProperty(value = "字段Id")
    private Long value;
    @ApiModelProperty(value = "字段英文名")
    private String columnName;
}
