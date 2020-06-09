package com.hupu.themis.admin.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;

public enum ExperimentItemTypeEnum implements IEnum<String> {
    COMPARE("COMPARE", "对照组"),
    EXPERIMENT("EXPERIMENT", "实验组");

    @EnumValue
    private String keyword;
    private String comment;

    ExperimentItemTypeEnum(String keyword, String comment) {
        this.keyword = keyword;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String getValue() {
        return this.keyword;
    }
}
