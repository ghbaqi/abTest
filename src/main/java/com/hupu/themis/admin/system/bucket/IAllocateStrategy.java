package com.hupu.themis.admin.system.bucket;

public interface IAllocateStrategy {
    BucketInfo allocate(InputExperimentInfo input);
}
