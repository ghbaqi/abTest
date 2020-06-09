package com.hupu.themis.admin.system.bucket;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LayerBucketInfo {
    private Integer surplusBucketNum;
    private String surplusBucketIds;
    private String name;
    private Long id;
}
