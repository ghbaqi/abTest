package com.hupu.themis.admin.modules.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页工具
 *
 * @date 2018-12-10
 */
public class PageUtil {


    /**
     * List 分页
     *
     * @param page
     * @param size
     * @param list
     * @return
     */
    public static List toPage(int page, int size, List list) {
        int fromIndex = page * size;
        int toIndex = page * size + size;

        if (fromIndex > list.size()) {
            return new ArrayList();
        } else if (toIndex >= list.size()) {
            return list.subList(fromIndex, list.size());
        } else {
            return list.subList(fromIndex, toIndex);
        }
    }

    /**
     * Page 数据处理，预防redis反序列化报错
     *
     * @param page
     * @return
     */
    public static Map toPage(Page page) {
        Map map = new HashMap(2);
        map.put("content", page.getContent());
        map.put("totalElements", page.getTotalElements());
        map.put("totalPageNum", page.getTotalPages());
        return map;
    }

    public static Map toPage(List list, long count, int pageSize) {
        Map map = new HashMap(2);
        map.put("content", list);
        map.put("totalElements", count);
        map.put("totalPages", count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        return map;
    }

    public static Map toPage(IPage page) {
        Map map = new HashMap(2);
        map.put("content", page.getRecords());
        map.put("totalElements", page.getTotal());
        map.put("totalPageNum", page.getPages());
        return map;
    }
}
