package com.hupu.themis.admin.system.rest;


import com.hupu.themis.admin.modules.common.aop.log.Log;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.system.domain.Layer;
import com.hupu.themis.admin.system.domain.vo.CheckVO;
import com.hupu.themis.admin.system.domain.vo.LayerOption;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.service.ILayerService;
import com.hupu.themis.admin.system.service.dto.LayerDTO;
import com.hupu.themis.admin.system.util.BucketUtils;
import io.swagger.annotations.Api;
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
 * 层配置 前端控制器
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Api(value = "/api/layer", description = "层模块")
@RestController
@RequestMapping("api/layer")
public class LayerController {
    @Autowired
    private ILayerService layerService;


    private static final String ENTITY_NAME = "layer";

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_SELECT')")
    public ResponseEntity getLayer(@PathVariable Long id) {
        return new ResponseEntity(layerService.findById(id), HttpStatus.OK);
    }

    @Log(description = "查询层")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_SELECT')")
    public ResponseEntity list(LayerDTO resources, Pageable pageable) {
        return new ResponseEntity(layerService.queryAll(resources, pageable), HttpStatus.OK);
    }

    @Log(description = "新增层")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_CREATE')")
    public ResponseEntity create(@Validated @RequestBody Layer resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        return new ResponseEntity(layerService.create(resources), HttpStatus.CREATED);
    }

    @Log(description = "修改层")
    @PostMapping(value = "/edit")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_EDIT')")
    public ResponseEntity update(@Validated @RequestBody Layer resources) {
        if (resources.getId() == null) {
            throw new BadRequestException(ENTITY_NAME + " ID Can not be empty");
        }
        layerService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log(description = "删除层")
    @GetMapping(value = "/del/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_DELETE')")
    public ResponseEntity delete(@PathVariable Long id) {
        layerService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log(description = "查询层(不分页)")
    @GetMapping(value = "/option/list")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_SELECT')")
    public ResponseEntity list(@RequestParam(value = "terminalValue") TerminalTypeEnum terminalValue,
                               @RequestParam(value = "bisLineValue") PageCategoryEnum bisLineValue) {
        List<LayerDTO> layerDTOS = layerService.listAll(bisLineValue, terminalValue);
        List<LayerOption> collect = layerDTOS.parallelStream()
                .map(l -> {
                    LayerOption option = new LayerOption();
                    option.setValue(l.getId());
                    option.setLabel(l.getName());
                    Double aDouble = BucketUtils.computeRateFlow(l.getSurplusBucketNum());
                    option.setSurplusRateFlow(aDouble);
                    return option;
                })
                .collect(Collectors.toList());
        return new ResponseEntity(collect, HttpStatus.OK);
    }

    @Log(description = "查询层流量")
    @GetMapping(value = "/getSurplusRateFlow")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_SELECT')")
    public ResponseEntity getSurplusRateFlow(@RequestParam(value = "terminalValue") Long id) {
        LayerDTO layerDTO = layerService.findById(id);
        Double aDouble = BucketUtils.computeRateFlow(layerDTO.getSurplusBucketNum());
        return new ResponseEntity(aDouble, HttpStatus.OK);
    }

    @Log(description = "是否层中相同的白名单")
    @GetMapping(value = "/white_list/exists")
    @PreAuthorize("hasAnyRole('ADMIN','LAYER_ALL','LAYER_SELECT')")
    public ResponseEntity existsWhiteList(
            @RequestParam(value = "layerId") Long layerId,
            @RequestParam(value = "whiteList") String whiteList,
            @RequestParam(value = "experimentItemId", required = false) Long experimentItemId) {
        CheckVO whiteCheckVO = layerService.existsWhiteList(layerId, whiteList,experimentItemId);
        return new ResponseEntity(whiteCheckVO, HttpStatus.OK);
    }

}
