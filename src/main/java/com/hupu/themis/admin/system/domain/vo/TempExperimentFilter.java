package com.hupu.themis.admin.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempExperimentFilter {
    private List<FilterItemVO> filters;
    private Integer groupSort;
}
