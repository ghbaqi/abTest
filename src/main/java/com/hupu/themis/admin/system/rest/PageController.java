package com.hupu.themis.admin.system.rest;


import cn.hutool.core.bean.BeanUtil;
import com.hupu.themis.admin.modules.common.aop.log.Log;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.system.domain.CustomPage;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.domain.Page;
import com.hupu.themis.admin.system.domain.vo.PageOption;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.service.IPageService;
import com.hupu.themis.admin.system.service.dto.PageDTO;
import com.hupu.themis.admin.system.util.BucketUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 页面配置 前端控制器
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Api(value = "/api/page", description = "页面模块")
@RestController
@RequestMapping("api/page")
public class PageController {
    @Autowired
    private IPageService pageService;


    private static final String ENTITY_NAME = "page";

    @ApiOperation(value = "根据ID查询")
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PAGE_ALL','PAGE_SELECT')")
    public ResponseEntity getPage(@PathVariable Long id) {
        return new ResponseEntity(pageService.findById(id), HttpStatus.OK);
    }

    @ApiOperation(value = "查询页面")
    @Log(description = "查询页面")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAnyRole('ADMIN','PAGE_ALL','PAGE_SELECT')")
    public ResponseEntity list(PageDTO resources, Pageable pageable) {
        return new ResponseEntity(pageService.queryAll(resources, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "新增页面")
    @Log(description = "新增页面")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ADMIN','PAGE_ALL','PAGE_CREATE')")
    public ResponseEntity create(@Validated @RequestBody Page resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        return new ResponseEntity(pageService.create(resources), HttpStatus.CREATED);
    }

    @ApiOperation(value = "修改页面")
    @Log(description = "修改页面")
    @PostMapping(value = "/edit")
    @PreAuthorize("hasAnyRole('ADMIN','PAGE_ALL','PAGE_EDIT')")
    public ResponseEntity update(@Validated @RequestBody Page resources) {
        if (resources.getId() == null) {
            throw new BadRequestException(ENTITY_NAME + " ID Can not be empty");
        }
        pageService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "删除页面")
    @Log(description = "删除页面")
    @GetMapping(value = "/del/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PAGE_ALL','PAGE_DELETE')")
    public ResponseEntity delete(@PathVariable Long id) {
        pageService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "获取页面ID")
    @GetMapping(value = "/option/list")
    @PreAuthorize("hasAnyRole('ADMIN','PAGE_ALL','PAGE_SELECT')")
    public ResponseEntity<List<PageOption>> listByTerminalAndBusinessLine(@RequestParam(value = "terminalValue", required = false) TerminalTypeEnum terminalValue,
                                                                          @RequestParam(value = "bisLineValue", required = false) PageCategoryEnum bisLineValue) {
        List<PageDTO> pageDTOList = pageService.listByTerminalAndBusinessLine(terminalValue, bisLineValue);
        List<PageOption> result = pageDTOList.parallelStream()
                .map(d ->
                        PageOption.builder()
                                .label(d.getDescription())
                                .spm(d.getSpm())
                                .value(d.getId())
                                .isEmploy(d.getIsEmploy())
                                .build()
                )
                .collect(Collectors.toList());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @ApiOperation(value = "根据ID获取页面占用信息")
    @GetMapping(value = "/employ/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PAGE_ALL','PAGE_SELECT')")
    public ResponseEntity getEmployInfo(@PathVariable Long id) {
        List<ExperimentGroup> employPage = pageService.findEmployPage(id);
        List<CustomPage> pages = employPage.parallelStream().map(a -> {
            CustomPage customPage = new CustomPage();
            BeanUtil.copyProperties(a, customPage);
            Double rateFlow = BucketUtils.computeRateFlow(a.getBucketNum());
            customPage.setRateFlow(rateFlow);
            customPage.setExperimentName(a.getName());
            customPage.setExperimentStatus(a.getStatus());
            customPage.setExperimentStatusName(customPage.getExperimentStatus().getComment());
            customPage.setTerminalTypeName(customPage.getTerminalType().getComment());
            customPage.setBisLineName(customPage.getBisLine().getName());
            return customPage;
        }).collect(Collectors.toList());
        return new ResponseEntity(pages, HttpStatus.OK);
    }


}
