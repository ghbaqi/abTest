package com.hupu.themis.admin.system.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum PageStatus implements IEnum<String> {
    AVAILABLE("AVAILABLE", "可用的"),
    NOT_AVAILABLE("NOT_AVAILABLE", "不可用的");

    PageStatus(String keyword, String comment) {
        this.keyword = keyword;
        this.comment = comment;
    }

    private String keyword;
    private String comment;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getValue() {
        return this.keyword;
    }
}
