package com.hupu.themis.admin.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayerOption {
    private String label;
    private Long value;
    private Double surplusRateFlow;
}
