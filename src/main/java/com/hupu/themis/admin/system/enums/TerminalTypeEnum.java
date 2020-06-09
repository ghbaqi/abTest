package com.hupu.themis.admin.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import org.apache.commons.lang3.StringUtils;

public enum TerminalTypeEnum implements IEnum<String> {
    SERVER("SERVER", "服务端"),
    CLIENT("CLIENT", "客户端"),
    H5("H5", "内嵌H5"),
    M("M", "M站"),
    PC("PC", "PC端");

    @EnumValue
    private String keyword;
    private String comment;

    TerminalTypeEnum(String keyword, String comment) {
        this.keyword = keyword;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getKeyword() {
        return keyword;
    }

    public static TerminalTypeEnum getEnumByValue(String value) {
        TerminalTypeEnum[] values = TerminalTypeEnum.values();
        for (TerminalTypeEnum e : values) {
            if (StringUtils.equalsIgnoreCase(e.toString(), value)) {
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
