package com.hupu.themis.admin.modules.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hupu.themis.admin.modules.monitor.domain.Logging;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @date 2019-11-24
 */
public interface LoggingMapper extends BaseMapper<Logging> {

    /**
     * 获取一个时间段的IP记录
     * @param date1
     * @param date2
     * @return
     */
    @Select("select count(*) FROM (select requestIp FROM log where createTime between #{data1} and #{data2} GROUP BY requestIp) as s")
    Long findIp(@Param("data1") String date1, @Param("data2") String date2);
}
