package com.hupu.themis.admin.modules.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hupu.themis.admin.modules.quartz.domain.QuartzJob;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @date 2019-01-07
 */
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

    /**
     * 更新状态
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Update(value = "update quartz_job set is_pause = 1 where id = #{id}")
    void updateIsPause(Long id);

    /**
     * 查询不是启用的任务
     * @return
     */
    List<QuartzJob> findByIsPauseIsFalse();
}
