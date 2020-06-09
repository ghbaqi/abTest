package com.hupu.themis.admin.modules.monitor.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.StringUtils;
import com.hupu.themis.admin.modules.monitor.domain.Logging;
import com.hupu.themis.admin.modules.monitor.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @date 2019-11-24
 */
@ApiIgnore
@RestController
@RequestMapping("api")
public class LoggingController {

    @Autowired
    private LoggingService loggingService;

    @GetMapping(value = "logs")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity getLogs(Logging logging, Pageable pageable) {
        IPage<Logging> loggingIPage = loggingService.page(new Page<>(pageable.getPageNumber(), pageable.getPageSize()), Wrappers.<Logging>lambdaQuery()
                .like(StringUtils.isNotBlank(logging.getUsername()), Logging::getUsername, logging.getUsername())
                .eq(StringUtils.isNotBlank(logging.getLogType()), Logging::getLogType, logging.getLogType())
                .orderByDesc(Logging::getId));
        return new ResponseEntity(PageUtil.toPage(loggingIPage), HttpStatus.OK);
    }
}
