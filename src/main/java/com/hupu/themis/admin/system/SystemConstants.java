package com.hupu.themis.admin.system;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class SystemConstants {
    private static final int NUM = 10000;
    public static final int TOTAL_BUCKET_NUM = NUM;
    public static final Double TOTAL_BUCKET_NUM_DOUBLE = Double.parseDouble(NUM + ".00");
    public static final String SUPER_ADMIN_AUTH_CODE = "ce3d1e23f8a04520a36af77752ebbe77";

    public static String BUCKET_LIST_STR;

    static {
        List<String> temp = Lists.newArrayList();
        for (int i = 1; i <= NUM; i++) {
            temp.add(i + "");
        }
        BUCKET_LIST_STR = temp.parallelStream().collect(Collectors.joining(","));
    }

}
