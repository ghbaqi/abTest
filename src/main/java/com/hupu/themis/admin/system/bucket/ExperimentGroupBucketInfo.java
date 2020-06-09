package com.hupu.themis.admin.system.bucket;

import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExperimentGroupBucketInfo {
    private Long id;
    private Integer bucketNum;
    private String bucketIds;
    private Integer surplusBucketNum;
    private String surplusBucketIds;
    private GroupStatusEnum status;
    private List<String> bucketIdList;

}
