package com.hupu.themis.admin.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import org.apache.commons.lang3.StringUtils;

public enum SqlKeywordEnum implements IEnum<String> {
    AND("AND", "且"),
    OR("OR", "或"),
    IN("IN", "存在"),
    NOT("NOT", "不存在"),
    LIKE("LIKE", "包含"),
    NOT_LIKE("NOT LIKE", "不包含"),
    EQ("=", "等于"),
    NE("<>", "不等于"),
    GT(">", "大于"),
    GE(">=", "大于等于"),
    LT("<", "小于"),
    LE("<=", "小于等于"),
    IS_NULL("IS NULL", "为空"),
    IS_NOT_NULL("IS NOT NULL", "不为空"),
    GROUP_BY("GROUP BY", "group by"),
    HAVING("HAVING", "having"),
    ORDER_BY("ORDER BY", "order by"),
    EXISTS("EXISTS", "exists"),
    BETWEEN("BETWEEN", "between"),
    ASC("ASC", "正序"),
    DESC("DESC", "倒序");

    @EnumValue
    private String keyword;
    private String viewName;

    SqlKeywordEnum(String keyword, String viewName) {
        this.keyword = keyword;
        this.viewName = viewName;
    }

    public static SqlKeywordEnum getEnumByKeyword(String keyword) {
        SqlKeywordEnum[] values = SqlKeywordEnum.values();
        for (SqlKeywordEnum e : values) {
            if (StringUtils.equalsIgnoreCase(e.keyword, keyword)) {
                return e;
            }
        }
        return null;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public String getValue() {
        return this.keyword;
    }
}
