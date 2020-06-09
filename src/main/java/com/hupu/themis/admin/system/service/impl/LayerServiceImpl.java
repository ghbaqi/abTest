package com.hupu.themis.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.exception.EntityExistException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.modules.monitor.service.RedisService;
import com.hupu.themis.admin.system.SystemConstants;
import com.hupu.themis.admin.system.domain.Layer;
import com.hupu.themis.admin.system.domain.vo.CheckVO;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.mapper.LayerMapper;
import com.hupu.themis.admin.system.service.ILayerService;
import com.hupu.themis.admin.system.service.dto.LayerDTO;
import com.hupu.themis.admin.system.service.mapstruct.LayerMapStruct;
import com.hupu.themis.admin.system.util.LayerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 层配置 服务实现类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LayerServiceImpl extends ServiceImpl<LayerMapper, Layer> implements ILayerService {
    @Autowired
    private LayerMapStruct layerMapStruct;
    @Autowired
    private RedisService redisService;

    @Override
    public LayerDTO findById(long id) {
        Layer layer = baseMapper.selectById(id);
        Optional<Layer> opt = Optional.ofNullable(layer);
        ValidationUtil.isNull(opt, "Layer", "id", id);
        return layerMapStruct.toDto(opt.get());
    }

    @Override
    public Map queryAll(LayerDTO layerDTO, Pageable pageable) {
        LambdaQueryWrapper<Layer> queryWrapper = Wrappers.<Layer>lambdaQuery()
                .like(!ObjectUtils.isEmpty(layerDTO.getName()), Layer::getName, layerDTO.getName())
                .like(!ObjectUtils.isEmpty(layerDTO.getDescription()), Layer::getDescription, layerDTO.getDescription());
        long count = baseMapper.queryAllCount(queryWrapper);
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order idOrder = sort.getOrderFor("modify_time");
            if (idOrder != null) {
                queryWrapper.orderByDesc(Layer::getModifyTime);
            }
        }
        queryWrapper.last("limit " + pageable.getOffset() + "," + pageable.getPageSize());
        List<LayerDTO> layerDTOs = layerMapStruct.toDto(baseMapper.queryAll(queryWrapper));
        return PageUtil.toPage(layerDTOs, count, pageable.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LayerDTO create(Layer resources) {
        Layer existLayer = baseMapper.findByLayerName(resources.getName());
        if (existLayer != null) {
            throw new EntityExistException(Layer.class, "name", resources.getName());
        }
        String name = resources.getName();
        LayerUtils.check(name);

        Date cDate = new Date();
        resources.setCreateTime(cDate);
        resources.setModifyTime(cDate);
        resources.setSurplusBucketIds(SystemConstants.BUCKET_LIST_STR);
        resources.setSurplusBucketNum(SystemConstants.TOTAL_BUCKET_NUM);
        int insert = baseMapper.insert(resources);
        if (insert > 0) {
            //sync to redis
            redisService.sadd(LayerUtils.getRedisKey(name), resources.getId().toString());
            return layerMapStruct.toDto(resources);
        }
        throw new BadRequestException("添加层[" + resources.getName() + "]失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Layer resources) {
        Optional<Layer> opt = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(opt, "Layer", "id", resources.getId());
        Layer layer = opt.get();

        Layer existLayer = baseMapper.findByLayerName(resources.getName());
        if (existLayer != null && !layer.getId().equals(existLayer.getId())) {
            throw new EntityExistException(Layer.class, "name", resources.getName());
        }
        resources.setModifyTime(new Date());
        int update = baseMapper.updateById(resources);
        if (update <= 0) {
            throw new BadRequestException("修改层[" + resources.getName() + "]失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public List<LayerDTO> listAll(PageCategoryEnum bisLineValue, TerminalTypeEnum terminalValue) {
        LambdaQueryWrapper<Layer> queryWrapper = Wrappers.<Layer>lambdaQuery();
        queryWrapper.orderByDesc(Layer::getModifyTime);
        queryWrapper
                .like(true, Layer::getName, LayerUtils.getPrefix(bisLineValue, terminalValue));
        List<Layer> layers = baseMapper.selectList(queryWrapper);
        return layerMapStruct.toDto(layers);
    }

    @Override
    public List<LayerDTO> listAll() {
        List<Layer> layers = baseMapper.selectList(Wrappers.<Layer>lambdaQuery());
        return layerMapStruct.toDto(layers);
    }

    @Override
    public CheckVO existsWhiteList(Long layerId, String whiteList, Long experimentItemId) {
        if (StringUtils.isBlank(whiteList)) {
            return CheckVO.builder().exists(false).build();
        }
        List<String> whiteLists = experimentItemId == null ? baseMapper.findWhiteListByLayerId(layerId) :
                baseMapper.findWhiteListById(layerId, experimentItemId);
        List<String> allWhiteLists = whiteLists.parallelStream().flatMap(x -> {
            ArrayList<String> result = Lists.newArrayList();
            if (x != null) {
                String[] split = x.split(",");
                result = Lists.newArrayList(split);
                return result.parallelStream();
            }
            return result.parallelStream();
        }).collect(Collectors.toList());
        ArrayList<String> submitWhiteLists = Lists.newArrayList(whiteList.split(","));
        for (String sw : submitWhiteLists) {
            if (allWhiteLists.contains(sw)) {
                return CheckVO.builder().exists(true).msg("白名单[" + sw + "]在该层中已存在").build();
            }
        }
        return CheckVO.builder().exists(false).build();
    }
}
