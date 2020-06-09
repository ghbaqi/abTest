package com.hupu.themis.admin.system.rest;


import com.google.common.collect.Lists;
import com.hupu.themis.admin.system.domain.vo.GeneralOption;
import com.hupu.themis.admin.system.domain.vo.SupportTag;
import com.hupu.themis.admin.system.service.IExperimentTagService;
import com.hupu.themis.admin.system.service.dto.ExperimentTagDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.hupu.themis.admin.system.enums.SqlKeywordEnum.*;

/**
 * <p>
 * 实验过滤表 前端控制器
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-27
 */
@Api(value = "/api/experiment-filter", description = "实验过滤")
@RestController
@RequestMapping("api/experiment-filter")
public class ExperimentFilterController {
    @Autowired
    private IExperimentTagService experimentTagService;

    /**
     * 获取支持的操作符
     */
    @PreAuthorize("hasAnyRole('USER_SELECT')")
    @ApiOperation(value = "获取支持的操作符")
    @GetMapping(value = "/operator/list")
    public ResponseEntity<List<GeneralOption<String>>> listSupportOperator(@RequestParam(value = "tagId", required = false) Long tagId) {
        return new ResponseEntity(Lists.newArrayList(new GeneralOption<String>(EQ.getViewName(), EQ.name()),
                new GeneralOption<String>(NE.getViewName(), NE.name()), new GeneralOption<String>(LIKE.getViewName(), LIKE.name()),
                new GeneralOption<String>(NOT_LIKE.getViewName(), NOT_LIKE.name())), HttpStatus.OK);
    }

    /**
     * 获取支持的标签
     */
    @PreAuthorize("hasAnyRole('USER_SELECT')")
    @ApiOperation(value = "获取支持的标签")
    @GetMapping(value = "/tag/list")
    public ResponseEntity<List<SupportTag>> listSupportTag() {
        List<ExperimentTagDTO> experimentTagDTOS = experimentTagService.findAll();
        List<SupportTag> collect = experimentTagDTOS.parallelStream().map(e -> {
            return SupportTag.builder()
                    .label(e.getDescription())
                    .value(e.getId())
                    .columnName(e.getColumnName())
                    .build();
        }).collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }

    /**
     * 获取支持的值
     */
    @PreAuthorize("hasAnyRole('USER_SELECT')")
    @ApiOperation(value = "获取标签支持的值")
    @GetMapping(value = "/tag/value/list")
    public ResponseEntity<List<Object>> listSupportTagValue(@RequestParam(value = "tagId", required = false) Long tagId) {
        //todo 待处理
        return ResponseEntity.ok(Lists.newArrayList("1", "2", "3", "4", "5", "6"));
    }

}
