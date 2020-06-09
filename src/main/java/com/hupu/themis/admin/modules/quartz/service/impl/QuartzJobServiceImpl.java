package com.hupu.themis.admin.modules.quartz.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.StringUtils;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.modules.quartz.domain.QuartzJob;
import com.hupu.themis.admin.modules.quartz.mapper.QuartzJobMapper;
import com.hupu.themis.admin.modules.quartz.service.QuartzJobService;
import com.hupu.themis.admin.modules.quartz.utils.QuartzManage;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @date 2019-01-07
 */
@Service(value = "quartzJobService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class QuartzJobServiceImpl extends ServiceImpl<QuartzJobMapper,QuartzJob> implements QuartzJobService {
    
    @Autowired
    private QuartzManage quartzManage;

    @Override
    public QuartzJob findById(Long id) {
        Optional<QuartzJob> quartzJob = Optional.ofNullable(baseMapper.selectById(id));
        ValidationUtil.isNull(quartzJob,"QuartzJob","id",id);
        return quartzJob.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuartzJob create(QuartzJob resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())){
            throw new BadRequestException("cron表达式格式错误");
        }
        resources.setUpdateTime(DateUtil.date().toTimestamp());
        baseMapper.insert(resources);
        quartzManage.addJob(resources);
        return resources;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QuartzJob resources) {
        if(resources.getId().equals(1L)){
            throw new BadRequestException("该任务不可操作");
        }
        if (!CronExpression.isValidExpression(resources.getCronExpression())){
            throw new BadRequestException("cron表达式格式错误");
        }
        baseMapper.updateById(resources);
        quartzManage.updateJobCron(resources);
    }

    @Override
    public void updateIsPause(QuartzJob quartzJob) {
        if(quartzJob.getId().equals(1L)){
            throw new BadRequestException("该任务不可操作");
        }
        if (quartzJob.getIsPause()) {
            quartzManage.resumeJob(quartzJob);
            quartzJob.setIsPause(false);
        } else {
            quartzManage.pauseJob(quartzJob);
            quartzJob.setIsPause(true);
        }
        baseMapper.updateById(quartzJob);
    }

    @Override
    public void execution(QuartzJob quartzJob) {
        if(quartzJob.getId().equals(1L)){
            throw new BadRequestException("该任务不可操作");
        }
        quartzManage.runAJobNow(quartzJob);
    }

    @Override
    public Map queryAll(QuartzJob quartzJob, Pageable pageable) {
        IPage<QuartzJob> quartzJobIPage = baseMapper.selectPage(new Page<>(pageable.getPageNumber(), pageable.getPageSize()), Wrappers.<QuartzJob>lambdaQuery()
                .like(StringUtils.isNotBlank(quartzJob.getJobName()), QuartzJob::getJobName, quartzJob.getJobName()));
        return PageUtil.toPage(quartzJobIPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(QuartzJob quartzJob) {
        if(quartzJob.getId().equals(1L)){
            throw new BadRequestException("该任务不可操作");
        }
        quartzManage.deleteJob(quartzJob);
        baseMapper.deleteById(quartzJob);
    }
}
