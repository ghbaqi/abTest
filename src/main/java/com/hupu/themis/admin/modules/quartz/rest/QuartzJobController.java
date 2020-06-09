package com.hupu.themis.admin.modules.quartz.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.hupu.themis.admin.modules.common.aop.log.Log;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.StringUtils;
import com.hupu.themis.admin.modules.quartz.domain.QuartzJob;
import com.hupu.themis.admin.modules.quartz.domain.QuartzLog;
import com.hupu.themis.admin.modules.quartz.service.QuartzJobService;
import com.hupu.themis.admin.modules.quartz.service.QuartzLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @date 2019-01-07
 */
@Slf4j
@ApiIgnore
@RestController
@RequestMapping("/api/job")
public class QuartzJobController {

    private static final String ENTITY_NAME = "quartzJob";

    @Autowired
    private QuartzJobService quartzJobService;

    @Autowired
    private QuartzLogService quartzLogService;


    @Log(description = "查询定时任务")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAnyRole('ADMIN','JOB_ALL','JOB_SELECT')")
    public ResponseEntity getJobs(QuartzJob resources, Pageable pageable){
        return new ResponseEntity(quartzJobService.queryAll(resources,pageable), HttpStatus.OK);
    }

    /**
     * 查询定时任务日志
     * @param resources
     * @param pageable
     * @return
     */
    @GetMapping(value = "/jobLogs")
    @PreAuthorize("hasAnyRole('ADMIN','JOB_ALL','JOB_SELECT')")
    public ResponseEntity getJobLogs(QuartzLog resources, Pageable pageable){
        IPage<QuartzLog> quartzLogIPage = quartzLogService.page(new Page<>(pageable.getPageNumber(),pageable.getPageSize()), Wrappers.<QuartzLog>query()
                .eq(!ObjectUtils.isEmpty(resources.getIsSuccess()),"isSuccess",resources.getIsSuccess()!=null&& resources.getIsSuccess() ?1:0)
                .like(StringUtils.isNotBlank(resources.getJobName()),"jobName",resources.getJobName())
                .orderByDesc("id"));
        return new ResponseEntity(PageUtil.toPage(quartzLogIPage), HttpStatus.OK);
    }

    @Log(description = "新增定时任务")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ADMIN','JOB_ALL','JOB_CREATE')")
    public ResponseEntity create(@Validated @RequestBody QuartzJob resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity(quartzJobService.create(resources),HttpStatus.CREATED);
    }

    @Log(description = "修改定时任务")
    @PostMapping(value = "/edit")
    @PreAuthorize("hasAnyRole('ADMIN','JOB_ALL','JOB_EDIT')")
    public ResponseEntity update(@Validated @RequestBody QuartzJob resources){
        if (resources.getId() == null) {
            throw new BadRequestException(ENTITY_NAME +" ID Can not be empty");
        }
        quartzJobService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log(description = "更改定时任务状态")
    @PostMapping(value = "/change_pause/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','JOB_ALL','JOB_EDIT')")
    public ResponseEntity updateIsPause(@PathVariable Long id){
        quartzJobService.updateIsPause(quartzJobService.findById(id));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log(description = "执行定时任务")
    @PostMapping(value = "/exec/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','JOB_ALL','JOB_EDIT')")
    public ResponseEntity execution(@PathVariable Long id){
        quartzJobService.execution(quartzJobService.findById(id));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log(description = "删除定时任务")
    @GetMapping(value = "/del/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','JOB_ALL','JOB_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        quartzJobService.delete(quartzJobService.findById(id));
        return new ResponseEntity(HttpStatus.OK);
    }
}
