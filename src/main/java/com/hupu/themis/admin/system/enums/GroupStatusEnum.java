package com.hupu.themis.admin.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum GroupStatusEnum implements IEnum<String> {
    TEST("TEST", "测试中"),
    RUNNING("RUNNING", "运行中"),
    STOP("STOP", "停止");
    @EnumValue
    private String keyword;
    private String comment;

    GroupStatusEnum(String keyword, String comment) {
        this.keyword = keyword;
        this.comment = comment;
    }

    public static List<GroupStatusEnum> getByComment(String comment) {
        List<GroupStatusEnum> results = Lists.newArrayList();
        GroupStatusEnum[] values = GroupStatusEnum.values();
        for (GroupStatusEnum e : values) {
            if (StringUtils.contains(e.comment, comment)) {
                results.add(e);
            }
        }
        return results;
    }

    public String getComment() {
        return comment;
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String getValue() {
        return this.keyword;
    }
}
