package com.hupu.themis.admin.modules.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.quartz.domain.QuartzLog;
import com.hupu.themis.admin.modules.quartz.mapper.QuartzLogMapper;
import com.hupu.themis.admin.modules.quartz.service.QuartzLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @date 2019-01-07
 */
@Service(value = "quartzLogService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class QuartzLogServiceImpl extends ServiceImpl<QuartzLogMapper,QuartzLog> implements QuartzLogService {
    

}
