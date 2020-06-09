package com.hupu.themis.admin.system.bucket;

import com.hupu.themis.admin.system.enums.ExperimentItemTypeEnum;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExperimentItemBucketInfo {
    //更新存在ID,新增不存在ID
    private Long id;
    private Double inputRateFlow;

    private Integer bucketNum;
    private String bucketIds;
    private Integer groupBucketNum;

    private int score;

    private String paramKey;
    private String paramValue;
    private Integer experimentGroupBucketNum;
    private ExperimentItemTypeEnum itemType;
    private Long experimentGroupId;
    private String whiteList;
    private String name;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentItemBucketInfo that = (ExperimentItemBucketInfo) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
