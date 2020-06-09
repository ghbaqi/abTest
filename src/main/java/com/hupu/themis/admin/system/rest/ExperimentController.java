package com.hupu.themis.admin.system.rest;


import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.hupu.themis.admin.modules.common.aop.log.Log;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.utils.SecurityUtils;
import com.hupu.themis.admin.system.domain.ExperimentFilter;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.domain.vo.*;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.service.IExperimentGroupService;
import com.hupu.themis.admin.system.service.dto.ExperimentFilterDTO;
import com.hupu.themis.admin.system.service.dto.ExperimentGroupDTO;
import com.hupu.themis.admin.system.util.IdUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 实验配置 前端控制器
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Api(value = "/api/experiment", description = "实验配置")
@RestController
@RequestMapping("api/experiment")
public class ExperimentController {
    @Autowired
    private IExperimentGroupService experimentGroupService;


    private static final String ENTITY_NAME = "experiment";

    @ApiOperation(value = "根据ID获取实验")
    @Log(description = "通过ID查询实验")
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_SELECT')")
    public ResponseEntity<ExperimentGroupVO> getExperimentGroup(@PathVariable Long id) {
        ExperimentGroupDTO e = experimentGroupService.findById(id);
        ExperimentGroupVO vo = new ExperimentGroupVO();
        BeanUtil.copyProperties(e, vo, "filters");
        List<ExperimentFilterDTO> filters = e.getFilters();

        List<List<FilterItemVO>> filterVOs = filters.parallelStream()
                .collect(Collectors.groupingBy(i -> i.getGroupId(),
                        TreeMap::new, Collectors.toList()))
                .values()
                .parallelStream()
                .map(t -> {
                    Integer groupSort = t.get(0).getGroupSort();
                    List<FilterItemVO> filterItemVOList = Lists.newArrayList();
                    for (ExperimentFilterDTO tt : t) {
                        FilterItemVO fVO = new FilterItemVO();
                        BeanUtil.copyProperties(tt, fVO);
                        fVO.setOperatorName(tt.getOperator().getViewName());
                        filterItemVOList.add(fVO);
                    }
                    TempExperimentFilter experimentFilter = TempExperimentFilter.builder()
                            .filters(filterItemVOList)
                            .groupSort(groupSort)
                            .build();
                    return experimentFilter;
                })
                .sorted(Comparator.comparing(TempExperimentFilter::getGroupSort))
                .map(t -> {
                    return t.getFilters().parallelStream()
                            .sorted(Comparator.comparing(FilterItemVO::getSort))
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());

        vo.setFilters(filterVOs);
        vo.setTerminalTypeName(vo.getTerminalType().getComment());
        vo.setBisLineName(vo.getBisLine().getName());
        return new ResponseEntity<ExperimentGroupVO>(vo, HttpStatus.OK);
    }

    @ApiOperation(value = "分页查询实验")
    @Log(description = "分页查询实验")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_SELECT')")
    public ResponseEntity<Map<String, Object>> list(ExperimentGroupDTO resources, Pageable pageable) {
        Map map = experimentGroupService.queryAll(resources, pageable);
        List<ExperimentGroupDTO> content = (List<ExperimentGroupDTO>) map.get("content");
        List<ExperimentGroupVO> newContent = content.parallelStream().map(e -> {
            ExperimentGroupVO vo = new ExperimentGroupVO();
            BeanUtil.copyProperties(e, vo, "filters");
            List<ExperimentFilterDTO> filters = e.getFilters();

            List<List<FilterItemVO>> filterVOs = filters.parallelStream()
                    .collect(Collectors.groupingBy(i -> i.getGroupId(),
                            TreeMap::new, Collectors.toList()))
                    .values()
                    .parallelStream()
                    .map(t -> {
                        Integer groupSort = t.get(0).getGroupSort();
                        List<FilterItemVO> filterItemVOList = Lists.newArrayList();
                        for (ExperimentFilterDTO tt : t) {
                            FilterItemVO fVO = new FilterItemVO();
                            fVO.setOperatorName(tt.getOperator().getViewName());
                            BeanUtil.copyProperties(tt, fVO);
                            filterItemVOList.add(fVO);
                        }
                        TempExperimentFilter experimentFilter = TempExperimentFilter.builder()
                                .filters(filterItemVOList)
                                .groupSort(groupSort)
                                .build();
                        return experimentFilter;
                    })
                    .sorted(Comparator.comparing(TempExperimentFilter::getGroupSort))
                    .map(t -> {
                        return t.getFilters().parallelStream()
                                .sorted(Comparator.comparing(FilterItemVO::getSort))
                                .collect(Collectors.toList());
                    })
                    .collect(Collectors.toList());

            vo.setFilters(filterVOs);
            vo.setTerminalTypeName(vo.getTerminalType().getComment());
            vo.setBisLineName(vo.getBisLine().getName());
            return vo;
        }).collect(Collectors.toList());
        map.put("content", newContent);
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @ApiOperation(value = "新增实验")
    @Log(description = "新增实验")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_CREATE')")
    public ResponseEntity<ExperimentGroupDTO> create(@Validated @RequestBody ExperimentGroupVO resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        ExperimentGroup experimentGroup = build(resources);
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();
        experimentGroup.setUserId(userId);
        experimentGroup.setUserName(username);
        return new ResponseEntity<ExperimentGroupDTO>(experimentGroupService.create(experimentGroup), HttpStatus.CREATED);
    }

    private ExperimentGroup build(ExperimentGroupVO resources) {
        ExperimentGroup experimentGroup = new ExperimentGroup();
        BeanUtil.copyProperties(resources, experimentGroup);
        List<List<FilterItemVO>> filterGroups = resources.getFilters();
        if (filterGroups != null) {
            List<ExperimentFilter> filters = Lists.newArrayList();
            for (int x = 0; x < filterGroups.size(); x++) {
                List<FilterItemVO> itemList = filterGroups.get(x);
                String groupId = IdUtils.getId();
                for (int y = 0; y < itemList.size(); y++) {
                    FilterItemVO item = itemList.get(y);
                    ExperimentFilter experimentFilter = ExperimentFilter.builder()
                            .groupId(groupId)
                            .groupSort(x)
                            .nextFilterRelation(item.getNextFilterRelation())
                            .operator(item.getOperator())
                            .value(item.getValue())
                            .sort(y)
                            .tagId(item.getTagId())
                            .tagColumnName(item.getTagColumnName())
                            .tagTableName(item.getTagTableName())
                            .build();
                    filters.add(experimentFilter);
                }
            }
            experimentGroup.setFilters(filters);
        }
        return experimentGroup;
    }

    @ApiOperation(value = "修改实验")
    @Log(description = "修改实验")
    @PostMapping(value = "/edit")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_EDIT')")
    public ResponseEntity update(@Validated @RequestBody ExperimentGroupVO resources) {
        if (resources.getId() == null) {
            throw new BadRequestException(ENTITY_NAME + " ID Can not be empty");
        }
        ExperimentGroup experimentGroup = build(resources);
        experimentGroupService.update(experimentGroup);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "删除实验")
    @Log(description = "删除实验")
    @GetMapping(value = "/del/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_DELETE')")
    public ResponseEntity delete(@PathVariable Long id) {
        experimentGroupService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "获取分端列表")
    @GetMapping(value = "/terminal/list")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_SELECT')")
    public ResponseEntity<List<GeneralOption<String>>> getSupportTerminal() {
        TerminalTypeEnum[] values = TerminalTypeEnum.values();
        List<GeneralOption<String>> collect = Lists.newArrayList(values)
                .parallelStream()
                .map(t -> {
                    GeneralOption<String> option = new GeneralOption<String>();
                    option.setLabel(t.getComment());
                    option.setValue(t.getKeyword());
                    return option;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }

    @ApiOperation(value = "获取业务线列表")
    @GetMapping(value = "/business_line/list")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_SELECT')")
    public ResponseEntity<List<GeneralOption<String>>> getSupportBusinessLine() {
        PageCategoryEnum[] values = PageCategoryEnum.values();
        List<GeneralOption<String>> collect = Lists.newArrayList(values)
                .parallelStream()
                .map(t -> {
                    GeneralOption<String> option = new GeneralOption<String>();
                    option.setLabel(t.getName());
                    option.setValue(t.getKeyword());
                    return option;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }

    @ApiOperation(value = "修改实验状态")
    @Log(description = "开启/关闭实验")
    @GetMapping(value = "/change_status")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_EDIT')")
    public ResponseEntity changeStatus(@RequestParam(value = "id") Long id,
                                       @RequestParam(value = "status") GroupStatusEnum status) {
        if (id == null) {
            throw new BadRequestException("ID Can not be empty");
        }
        experimentGroupService.changeStatus(id, status);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "判断key是否存在")
    @GetMapping(value = "/item_key/exists")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_EDIT')")
    public ResponseEntity existsParamKey(@RequestParam(value = "paramKey") String paramKey) {
        CheckVO checkVO = experimentGroupService.existsParamKey(paramKey);
        return ResponseEntity.ok(checkVO);
    }


    @ApiOperation(value = "判断key是否存在")
    @GetMapping(value = "/history/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_EDIT')")
    public ResponseEntity history(@PathVariable Long id) {
        return ResponseEntity.ok(experimentGroupService.history(id));
    }

    @GetMapping(value = "/super/flush")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERIMENT_GROUP_ALL','EXPERIMENT_GROUP_EDIT')")
    public ResponseEntity flushCache(@RequestParam(value = "auth_code") String authCode) {
        String result = experimentGroupService.flushCache(authCode);
        return ResponseEntity.ok(result);
    }
}
