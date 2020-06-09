package com.hupu.themis.admin.system.util;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hupu.themis.admin.system.SystemConstants.TOTAL_BUCKET_NUM;
import static com.hupu.themis.admin.system.SystemConstants.TOTAL_BUCKET_NUM_DOUBLE;

public class BucketUtils {

    public static int computeBucketNum(Double rateFlow) {
        BigDecimal bigDecimal = NumberUtil.roundHalfEven(rateFlow * TOTAL_BUCKET_NUM, 0);
        return bigDecimal.intValue();
    }

    public static int computeBucketNum(Double rateFlow, int baseBucketNum) {
        BigDecimal bigDecimal = NumberUtil.roundHalfEven(rateFlow * baseBucketNum, 0);
        return bigDecimal.intValue();
    }

    public static Double computeRateFlow(int bucketNum) {
        BigDecimal bigDecimal = NumberUtil.roundHalfEven(bucketNum / TOTAL_BUCKET_NUM_DOUBLE, 4);
        if (bigDecimal.doubleValue() > 1.0000d) {
            return 1.0000;
        } else {
            return bigDecimal.doubleValue();
        }
    }

    //计算占比
    public static Double computeRateFlow(int bucketNum, int baseBucketNum) {
        if (baseBucketNum == 0)
            return 0.0;
        BigDecimal bigDecimal = NumberUtil.roundHalfEven(bucketNum / Double.parseDouble(baseBucketNum + ".00"), 4);
        return bigDecimal.doubleValue();
    }

    public static String union(String bucketIds1, String bucketIds2) {
        ArrayList<String> result = Lists.newArrayList(bucketIds1.split(","));
        result.addAll(Lists.newArrayList(bucketIds2.split(",")));
        return result.parallelStream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
    }

    public static String union(String bucketIds, Collection<String> changeIds) {
        ArrayList<String> result = StringUtils.isNotBlank(bucketIds) ? Lists.newArrayList(bucketIds.split(",")) :
                Lists.newArrayList();
        result.addAll(changeIds);
        return result.parallelStream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
    }

    public static int computeBucketNum(String bucketIds, Set<String> changeIds) {
        ArrayList<String> result = Lists.newArrayList(bucketIds.split(","));
        result.addAll(changeIds);
        return result.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()).size();
    }

    public static List<String> parseBucketList(String bucketIds) {
        return StringUtils.isNotBlank(bucketIds) ? Lists.newArrayList(bucketIds.split(",")) :
                Lists.newArrayList();
    }
}
