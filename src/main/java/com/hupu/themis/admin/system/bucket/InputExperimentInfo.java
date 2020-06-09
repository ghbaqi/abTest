package com.hupu.themis.admin.system.bucket;

import com.hupu.themis.admin.system.enums.RateFlowStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputExperimentInfo {
    private Double groupRateFlow;
    private RateFlowStrategy rateFlowStrategy;
    private List<ExperimentItemBucketInfo> submitItems;

    private LayerBucketInfo oldLayer;
    private ExperimentGroupBucketInfo oldGroup;
    private List<ExperimentItemBucketInfo> oldItems;
}
