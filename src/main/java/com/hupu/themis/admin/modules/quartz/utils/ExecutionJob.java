package com.hupu.themis.admin.modules.quartz.utils;

import cn.hutool.core.date.DateUtil;
import com.hupu.themis.admin.modules.common.utils.SpringContextHolder;
import com.hupu.themis.admin.modules.common.utils.ThrowableUtil;
import com.hupu.themis.admin.modules.quartz.domain.QuartzJob;
import com.hupu.themis.admin.modules.quartz.domain.QuartzLog;
import com.hupu.themis.admin.modules.quartz.service.QuartzJobService;
import com.hupu.themis.admin.modules.quartz.service.QuartzLogService;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 参考人人开源，https://gitee.com/renrenio/renren-security
 * @author
 * @date 2019-01-07
 */
@Async
public class ExecutionJob extends QuartzJobBean {

    @Resource(name = "scheduler")
    private Scheduler scheduler;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExecutorService executorService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        QuartzJob quartzJob = (QuartzJob) context.getMergedJobDataMap().get(QuartzJob.JOB_KEY);
        // 获取spring bean
        QuartzLogService quartzLogService = SpringContextHolder.getBean("quartzLogService");
        QuartzJobService quartzJobService = SpringContextHolder.getBean("quartzJobService");
        QuartzManage quartzManage = SpringContextHolder.getBean("quartzManage");

        QuartzLog log = new QuartzLog();
        log.setJobName(quartzJob.getJobName());
        log.setBeanName(quartzJob.getBeanName());
        log.setMethodName(quartzJob.getMethodName());
        log.setParams(quartzJob.getParams());
        log.setCreateTime(DateUtil.date().toTimestamp());
        long startTime = System.currentTimeMillis();
        log.setCronExpression(quartzJob.getCronExpression());
        try {
            // 执行任务
            logger.info("任务准备执行，任务名称：{}", quartzJob.getJobName());
            QuartzRunnable task = new QuartzRunnable(quartzJob.getBeanName(), quartzJob.getMethodName(),
                    quartzJob.getParams());
            Future<?> future = executorService.submit(task);
            future.get();
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            // 任务状态
            log.setIsSuccess(true);
            logger.info("任务执行完毕，任务名称：{} 总共耗时：{} 毫秒", quartzJob.getJobName(), times);
        } catch (Exception e) {
            logger.error("任务执行失败，任务名称：{}" + quartzJob.getJobName(), e);
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            // 任务状态 0：成功 1：失败
            log.setIsSuccess(false);
            log.setExceptionDetail(ThrowableUtil.getStackTrace(e));
            //出错就暂停任务
            quartzManage.pauseJob(quartzJob);
            //更新状态
            quartzJobService.updateIsPause(quartzJob);
        } finally {
            quartzLogService.save(log);
        }
    }
}
