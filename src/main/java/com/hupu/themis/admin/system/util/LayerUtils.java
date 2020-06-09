package com.hupu.themis.admin.system.util;

import cn.hutool.core.util.ReUtil;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;

public class LayerUtils {
    private static final String REGEX = "([A-Z0-9]{1,6})_([A-Z]{2})_([0-9a-zA-Z_]{1,15})";

    public static void check(String layerName) {
        String terminal = ReUtil.get(REGEX, layerName, 1);
        TerminalTypeEnum terminalTypeEnum = TerminalTypeEnum.getEnumByValue(terminal);
        if (terminalTypeEnum == null) {
            throw new BadRequestException("层命名错误,前缀必须以 SERVER|CLIENT|H5|PC|M 开头");
        }
        String pageCategory = ReUtil.get(REGEX, layerName, 2);
        PageCategoryEnum pageCategoryEnum = PageCategoryEnum.getEnumByKeyword(pageCategory);
        if (pageCategoryEnum == null) {
            throw new BadRequestException("层命名错误,模块名必须是 BS|PB|BB|SC|MK|UK|ES|MV 其中一个");
        }
    }

    public static String getPrefix(PageCategoryEnum bisLineValue, TerminalTypeEnum terminalValue) {
        return terminalValue.getKeyword() + "_" + bisLineValue.getKeyword();
    }

    public static TerminalTypeEnum getTerminalType(String layerName) {
        String terminal = ReUtil.get(REGEX, layerName, 1);
        return TerminalTypeEnum.getEnumByValue(terminal);
    }

    public static PageCategoryEnum getBisLineType(String layerName) {
        String pageCategory = ReUtil.get(REGEX, layerName, 2);
        return PageCategoryEnum.getEnumByKeyword(pageCategory);
    }

    public static String getRedisKey(String layerName) {
        String terminal = ReUtil.get(REGEX, layerName, 1);
        TerminalTypeEnum terminalTypeEnum = TerminalTypeEnum.getEnumByValue(terminal);
        return "terminal" + ":" + terminalTypeEnum.getKeyword();
    }
}
