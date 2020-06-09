package com.hupu.themis.admin.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;

public enum RateFlowStrategy implements IEnum<String> {
    AVG("AVG", "平均分配"),
    CUSTOM("CUSTOM", "自定义分配");
    @EnumValue
    private String keyword;
    private String comment;

    RateFlowStrategy(String keyword, String comment) {
        this.keyword = keyword;
        this.comment = comment;
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
