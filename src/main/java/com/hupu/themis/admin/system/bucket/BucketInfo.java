package com.hupu.themis.admin.system.bucket;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BucketInfo {
    private LayerBucketInfo layerBucketInfo;
    private ExperimentGroupBucketInfo experimentGroupBucketInfo;
    private List<ExperimentItemBucketInfo> experimentItemBucketInfos;
    private List<ExperimentItemBucketInfo> oldItems;

    private List<ExperimentItemBucketInfo> delItemIds;
}
