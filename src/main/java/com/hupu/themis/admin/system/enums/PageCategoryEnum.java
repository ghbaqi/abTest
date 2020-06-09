package com.hupu.themis.admin.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import org.apache.commons.lang3.StringUtils;

public enum PageCategoryEnum implements IEnum<String> {
    BS("BS", "社区"),
    PB("PB", "基线"),
    BB("BB", "篮球"),
    SC("SC", "足球"),
    MK("MK", "创新变现"),
    UK("UK", "路人王"),
    ES("ES", "电竞"),
    MV("MV", "影视");
    @EnumValue
    private String keyword;
    private String name;

    PageCategoryEnum(String keyword, String name) {
        this.keyword = keyword;
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static PageCategoryEnum getEnumByKeyword(String keyword) {
        PageCategoryEnum[] values = PageCategoryEnum.values();
        for (PageCategoryEnum e : values) {
            if (StringUtils.equalsIgnoreCase(e.keyword, keyword)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String getValue() {
        return this.keyword;
    }
}
