package com.hupu.themis.admin.system.util;

import cn.hutool.core.util.ReUtil;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;

public class SpmUtils {
    private static final String PAGE_REGEX = "([A-Z]{2})([A-Z]{2})([0-9]{4})";

    public static PageCategoryEnum getCategory(String pageSpm) {
        String pageCategoryStr = ReUtil.get(PAGE_REGEX, pageSpm, 2);
        return PageCategoryEnum.getEnumByKeyword(pageCategoryStr);
    }

    public static String getTerminalPrefix(String pageSpm) {
        return ReUtil.get(PAGE_REGEX, pageSpm, 1);
    }

}
