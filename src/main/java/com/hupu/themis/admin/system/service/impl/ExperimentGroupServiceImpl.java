package com.hupu.themis.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
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
import com.hupu.themis.admin.system.bucket.*;
import com.hupu.themis.admin.system.domain.*;
import com.hupu.themis.admin.system.domain.vo.CheckVO;
import com.hupu.themis.admin.system.domain.vo.TempLayer;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.mapper.ExperimentGroupMapper;
import com.hupu.themis.admin.system.service.*;
import com.hupu.themis.admin.system.service.dto.*;
import com.hupu.themis.admin.system.service.mapstruct.ExperimentGroupMapStruct;
import com.hupu.themis.admin.system.service.mapstruct.LayerMapStruct;
import com.hupu.themis.admin.system.util.BucketUtils;
import com.hupu.themis.admin.system.util.LayerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 实验组配置 服务实现类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExperimentGroupServiceImpl extends ServiceImpl<ExperimentGroupMapper, ExperimentGroup> implements IExperimentGroupService {
    @Autowired
    private ExperimentGroupMapStruct experimentGroupMapStruct;
    @Autowired
    private ILayerService layerService;
    @Autowired
    private IExperimentItemService experimentItemService;
    @Autowired
    private LayerMapStruct layerMapStruct;
    @Autowired
    private IExperimentFilterService experimentFilterService;
    @Autowired
    private IPageService pageService;
    @Autowired
    private DefaultAllocateStrategy defaultAllocateStrategy;
    @Autowired
    private RedisService redisService;
    @Autowired
    private IExperimentTagService experimentTagService;


    @Override
    public ExperimentGroupDTO findById(long id) {
        ExperimentGroup experimentConf = baseMapper.findById(id);
        Optional<ExperimentGroup> opt = Optional.ofNullable(experimentConf);
        ValidationUtil.isNull(opt, "Experiment", "id", id);
        ExperimentGroup experimentGroup = opt.get();
        List<ExperimentFilterDTO> experimentFilterDTOS = experimentFilterService
                .findByExperimentGroupId(experimentGroup.getId());
        ExperimentGroupDTO experimentGroupDTO = experimentGroupMapStruct.toDto(experimentConf);
        if (GroupStatusEnum.RUNNING == experimentGroupDTO.getStatus() && experimentGroupDTO.getStartTime() != null) {
            experimentGroupDTO.setDuration(DateUtil.between(experimentGroupDTO.getStartTime(), new Date(), DateUnit.SECOND));
        }
        experimentGroupDTO.setFilters(experimentFilterDTOS);
        List<ExperimentItemDTO> items = experimentGroupDTO.getItems();
        for (ExperimentItemDTO i : items) {
            if (StringUtils.isBlank(i.getWhiteList())) {
                i.setWhiteList(null);
            }
            i.setRateFlow(BucketUtils.computeRateFlow(i.getBucketNum(), i.getExperimentGroupBucketNum()));
        }
        LayerDTO layer = experimentGroupDTO.getLayer();
        layer.setSurplusRateFlow(BucketUtils.computeRateFlow(layer.getSurplusBucketNum()));
        experimentGroupDTO.setRateFlow(BucketUtils.computeRateFlow(experimentGroupDTO.getBucketNum()));
        experimentGroupDTO.setLayerId(experimentGroupDTO.getLayer().getId());
        return experimentGroupDTO;
    }

    @Override
    public Map queryAll(ExperimentGroupDTO experimentConfDTO, Pageable pageable) {
        LambdaQueryWrapper<ExperimentGroup> queryWrapper = Wrappers.<ExperimentGroup>lambdaQuery();
        String searchKeyword = experimentConfDTO.getSearchKeyword();
        if (StringUtils.isNotBlank(searchKeyword)) {
            queryWrapper.like(ExperimentGroup::getName, searchKeyword)
                    .or()
                    .like(ExperimentGroup::getBisLineName, searchKeyword)
                    .or()
                    .like(ExperimentGroup::getUserName, searchKeyword)
                    .or()
                    .in(ExperimentGroup::getStatus, GroupStatusEnum.getByComment(searchKeyword));
        }
        long count = baseMapper.queryAllCount(queryWrapper);
        queryWrapper.orderByDesc(ExperimentGroup::getStartTime)
                .last("limit " + pageable.getOffset() + "," + pageable.getPageSize());
        List<ExperimentGroup> experimentGroups = baseMapper.queryAll(queryWrapper);
        List<ExperimentGroupDTO> experimentConfDTOs = experimentGroupMapStruct.toDto(experimentGroups);
        List<ExperimentGroupDTO> collect = experimentConfDTOs.parallelStream()
                .map(e -> {
                    List<ExperimentFilterDTO> experimentFilterDTOS = experimentFilterService.findByExperimentGroupId(e.getId());
                    e.setFilters(experimentFilterDTOS);
                    List<ExperimentItemDTO> items = e.getItems();
                    for (ExperimentItemDTO i : items) {
                        if (StringUtils.isBlank(i.getWhiteList())) {
                            i.setWhiteList(null);
                        }
                        i.setRateFlow(BucketUtils.computeRateFlow(i.getBucketNum(), i.getExperimentGroupBucketNum()));
                    }
                    LayerDTO layer = e.getLayer();
                    layer.setSurplusRateFlow(BucketUtils.computeRateFlow(layer.getSurplusBucketNum()));

                    e.setRateFlow(BucketUtils.computeRateFlow(e.getBucketNum()));
                    if (GroupStatusEnum.RUNNING == e.getStatus() && e.getStartTime() != null) {
                        e.setDuration(DateUtil.between(e.getStartTime(), new Date(), DateUnit.SECOND));
                    }
                    return e;
                })
                .collect(Collectors.toList());

        return PageUtil.toPage(collect, count, pageable.getPageSize());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized ExperimentGroupDTO create(ExperimentGroup resources) {
        GroupStatusEnum status = resources.getStatus();
        if (status != GroupStatusEnum.RUNNING && status != GroupStatusEnum.TEST) {
            throw new BadRequestException("更新实验状态只能为Running或者Test");
        }
        Optional<LayerDTO> optionalLayerDTO = Optional.ofNullable(layerService.findById(resources.getLayerId()));
        ValidationUtil.isNull(optionalLayerDTO, "Layer", "id", resources.getLayerId());
        LayerDTO layerDTO = optionalLayerDTO.get();

        List<ExperimentFilter> submitFilter = resources.getFilters();
        List<ExperimentFilter> newFilter = submitFilter.parallelStream().map(f -> {
            ExperimentTagDTO existsTag = experimentTagService.findById(f.getTagId());
            if (existsTag == null) {
                throw new BadRequestException("提交的过滤条件标签不存在");
            }
            f.setTagColumnName(existsTag.getColumnName());
            f.setTagTableName(existsTag.getTableName());
            return f;
        }).collect(Collectors.toList());


        List<PageDTO> existPages = Lists.newArrayList();
        if (resources.getTerminalType() == TerminalTypeEnum.SERVER) {
            List<Page> pages = resources.getPages();
            if (pages != null && pages.size() > 0) {
                throw new BadRequestException("分端类型为服务端时不能选择页面信息");
            }
        } else {
            List<Page> pages = resources.getPages();
            if (pages == null || pages.size() <= 0) {
                throw new BadRequestException("分端类型为非服务端时至少选择一个页面");
            } else {
                for (Page p : pages) {
                    PageDTO pageDTO = pageService.findById(p.getId());
                    if (pageDTO == null) {
                        throw new BadRequestException("关联的页面信息不存在");
                    } else {
                        existPages.add(pageDTO);
                    }
                }
            }
        }
        String pages = existPages.parallelStream()
                .map(PageDTO::getSpm)
                .collect(Collectors.joining(","));
        List<Long> pageIds = existPages.parallelStream()
                .map(PageDTO::getId)
                .collect(Collectors.toList());

        Double rateFlow = resources.getRateFlow();
        int needBucketNum = BucketUtils.computeBucketNum(rateFlow);

        if (needBucketNum > layerDTO.getSurplusBucketNum()) {
            throw new BadRequestException("流量[" + resources.getRateFlow() + "]大于剩余总流量[" +
                    BucketUtils.computeRateFlow(layerDTO.getSurplusBucketNum()) + "]越界");
        }

        List<ExperimentItem> items = resources.getItems();
        //获取唯一的key
        String key = items.parallelStream().map(ExperimentItem::getParamKey)
                .distinct()
                .collect(Collectors.toList()).get(0);
        ExperimentGroup existExperiment = baseMapper.findByParamKey(key);
        if (existExperiment != null) {
            throw new EntityExistException(ExperimentGroup.class, "key", key);
        }

        List<ExperimentItemBucketInfo> itemBucketInfos = items.parallelStream().map(i -> {
            ExperimentItemBucketInfo experimentItemBucketInfo = ExperimentItemBucketInfo.builder()
                    .inputRateFlow(i.getRateFlow())
                    .build();
            BeanUtil.copyProperties(i, experimentItemBucketInfo);
            return experimentItemBucketInfo;
        }).collect(Collectors.toList());

        LayerBucketInfo oldLayer = new LayerBucketInfo();
        BeanUtil.copyProperties(layerDTO, oldLayer);

        BucketInfo bucketInfo = defaultAllocateStrategy.allocate(InputExperimentInfo.builder()
                .groupRateFlow(rateFlow)
                .oldLayer(oldLayer)
                .submitItems(itemBucketInfos)
                .rateFlowStrategy(resources.getRateFlowStrategy())
                .build());
        log.info("开始创建实验,共[5]步>>>>>>>>>>");

        //sync to redis
        RedisBucketInfo redisBucketInfo = new RedisBucketInfo(bucketInfo, pages, resources.getStatus());
        sync2Redis(redisBucketInfo);
        log.info("创建[" + resources.getName() + "]实验Step1:同步Redis成功！");

        Date cDate = new Date();
        resources.setCreateTime(cDate);
        resources.setModifyTime(cDate);
        if (status == GroupStatusEnum.RUNNING) {
            resources.setStartTime(cDate);
        }
        ExperimentGroupBucketInfo groupBucketInfo = bucketInfo.getExperimentGroupBucketInfo();
        //当前实验总桶数
        resources.setBucketNum(groupBucketInfo.getBucketNum());
        //当前桶ID列表
        resources.setBucketIds(groupBucketInfo.getBucketIds());
        //当前剩余
        resources.setSurplusBucketNum(groupBucketInfo.getSurplusBucketNum());
        //当前剩余ids
        resources.setSurplusBucketIds(groupBucketInfo.getSurplusBucketIds());

        resources.setLayerName(layerDTO.getName());
        resources.setParamKey(key);
        resources.setBisLineName(resources.getBisLine().getName());
        int insert = baseMapper.insert(resources);
        if (insert <= 0) {
            throw new BadRequestException("开启实验失败，请稍后重试");
        }
        //-----------更新item
        List<ExperimentItemBucketInfo> newItemBucketInfos = bucketInfo.getExperimentItemBucketInfos();
        List<ExperimentItem> itemList = newItemBucketInfos.parallelStream().map(n -> {
            ExperimentItem item = new ExperimentItem();
            BeanUtil.copyProperties(n, item);
            item.setExperimentGroupId(resources.getId());
            item.setExperimentGroupBucketNum(n.getGroupBucketNum());
            item.setCreateTime(cDate);
            item.setModifyTime(cDate);
            return item;
        }).collect(Collectors.toList());
        experimentItemService.saveBatch(itemList);
        log.info("创建[" + resources.getName() + "]实验Step2:批量保存实验组成功！");
        //-----------更新层
        LayerBucketInfo layerBucketInfo = bucketInfo.getLayerBucketInfo();
        Layer layer = Layer.builder()
                .id(resources.getLayerId())
                .modifyTime(cDate)
                .surplusBucketIds(layerBucketInfo.getSurplusBucketIds())
                .surplusBucketNum(layerBucketInfo.getSurplusBucketNum())
                .build();
        layerService.update(layer);
        log.info("创建[" + resources.getName() + "]实验Step3:更新所属层剩余桶数[" + layerBucketInfo.getSurplusBucketNum() + "]成功!");
        //----------保存过滤条件
        for (ExperimentFilter f : newFilter) {
            f.setExperimentGroupId(resources.getId());
            experimentFilterService.create(f);
        }
        log.info("创建[" + resources.getName() + "]实验Step4:保存实验过滤条件成功！");
        //更新页面的分层
        if (resources.getTerminalType() != TerminalTypeEnum.SERVER) {
            pageService.bindExperimentGroupId(pageIds, resources.getId());
        }
        log.info("创建[" + resources.getName() + "]实验Step5:关联页面成功！");
        log.info("创建实验成功,共[5]步<<<<<<<<<<");
        return experimentGroupMapStruct.toDto(resources);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void update(ExperimentGroup resources) {
        //检查 实验是否存在
        Optional<ExperimentGroupDTO> optionalConf = Optional.ofNullable(findById(resources.getId()));
        ValidationUtil.isNull(optionalConf, "Experiment", "id", resources.getId());
        ExperimentGroupDTO experimentConfDTO = optionalConf.get();

        GroupStatusEnum status = resources.getStatus();
        if (status != null && status != experimentConfDTO.getStatus()) {
            throw new BadRequestException("不能修改实验状态");
        }
        //检查层信息是否存在
        Optional<LayerDTO> optionalLayerDTO = Optional.ofNullable(layerService.findById(resources.getLayerId()));
        ValidationUtil.isNull(optionalLayerDTO, "Layer", "id", resources.getLayerId());
        LayerDTO layerDTO = optionalLayerDTO.get();

        List<ExperimentFilter> submitFilter = resources.getFilters();
        List<ExperimentFilter> newFilter = submitFilter.parallelStream().map(f -> {
            ExperimentTagDTO existsTag = experimentTagService.findById(f.getTagId());
            if (existsTag == null) {
                throw new BadRequestException("提交的过滤条件标签不存在");
            }
            f.setTagColumnName(existsTag.getColumnName());
            f.setTagTableName(existsTag.getTableName());
            return f;
        }).collect(Collectors.toList());

        List<PageDTO> existPages = Lists.newArrayList();
        if (resources.getTerminalType() == TerminalTypeEnum.SERVER) {
            List<Page> pages = resources.getPages();
            if (pages != null && pages.size() > 0) {
                throw new BadRequestException("分端类型为服务端时不能选择页面信息");
            }
        } else {
            List<Page> pages = resources.getPages();
            if (pages == null || pages.size() <= 0) {
                throw new BadRequestException("分端类型为非服务端时至少选择一个页面");
            } else {
                for (Page p : pages) {
                    PageDTO pageDTO = pageService.findById(p.getId());
                    if (pageDTO == null) {
                        throw new BadRequestException("关联的页面信息不存在");
                    } else {
                        existPages.add(pageDTO);
                    }
                }
            }
        }
        String pages = existPages.parallelStream()
                .map(PageDTO::getSpm)
                .collect(Collectors.joining(","));
        List<Long> pageIds = existPages.parallelStream()
                .map(PageDTO::getId)
                .collect(Collectors.toList());

        List<ExperimentItem> items = resources.getItems();

        List<ExperimentItemBucketInfo> submitItems = items.parallelStream().map(i -> {
            ExperimentItemBucketInfo experimentItemBucketInfo = ExperimentItemBucketInfo.builder()
                    .inputRateFlow(i.getRateFlow())
                    .build();
            BeanUtil.copyProperties(i, experimentItemBucketInfo);
            return experimentItemBucketInfo;
        }).collect(Collectors.toList());

        List<ExperimentItemDTO> oldItemDTOs = experimentConfDTO.getItems();

        List<ExperimentItemBucketInfo> oldItems = oldItemDTOs.parallelStream().map(o -> {
            ExperimentItemBucketInfo experimentItemBucketInfo = ExperimentItemBucketInfo.builder()
                    .build();
            BeanUtil.copyProperties(o, experimentItemBucketInfo);
            return experimentItemBucketInfo;
        }).collect(Collectors.toList());

        ExperimentGroupBucketInfo oldGroup = ExperimentGroupBucketInfo.builder().build();
        BeanUtil.copyProperties(experimentConfDTO, oldGroup);

        LayerBucketInfo oldLayer = new LayerBucketInfo();
        BeanUtil.copyProperties(layerDTO, oldLayer);

        BucketInfo bucketInfo = defaultAllocateStrategy.allocate(InputExperimentInfo.builder()
                .groupRateFlow(resources.getRateFlow())
                .oldLayer(oldLayer)
                .oldItems(oldItems)
                .oldGroup(oldGroup)
                .submitItems(submitItems)
                .rateFlowStrategy(resources.getRateFlowStrategy())
                .build());

        Date cDate = new Date();
        List<ExperimentItemBucketInfo> experimentItemBucketInfos = bucketInfo.getExperimentItemBucketInfos();
        List<ExperimentItem> itemList = experimentItemBucketInfos.parallelStream().map(n -> {
            ExperimentItem item = new ExperimentItem();
            BeanUtil.copyProperties(n, item);
            item.setExperimentGroupId(resources.getId());
            item.setExperimentGroupBucketNum(n.getGroupBucketNum());
            item.setModifyTime(cDate);
            return item;
        }).collect(Collectors.toList());

        //获取唯一的key
        String key = itemList.parallelStream().map(ExperimentItem::getParamKey)
                .distinct()
                .collect(Collectors.toList()).get(0);
        ExperimentGroup existExperiment = baseMapper.findByParamKey(key);
        if (existExperiment != null && !resources.getId().equals(existExperiment.getId())) {
            throw new EntityExistException(ExperimentGroup.class, "key", resources.getParamKey());
        }


        log.info("更新实验开始,共[10]步>>>>>>>>>>");
        //sync to redis
        RedisBucketInfo redisBucketInfo = new RedisBucketInfo(bucketInfo, pages, resources.getStatus());
        sync2Redis(redisBucketInfo);
        log.info("更新[" + resources.getName() + "]实验Step1:同步Redis成功！");

        //----------- 更新Item
        List<Long> delItemIds = bucketInfo.getDelItemIds().parallelStream().map(b -> b.getId())
                .collect(Collectors.toList());
        experimentItemService.deleteBatch(delItemIds);
        log.info("更新[" + resources.getName() + "]实验Step2:删除多余实验组成功！");
        List<ExperimentItem> updateItemList = itemList.parallelStream().filter(i -> i.getId() != null)
                .collect(Collectors.toList());
        if (!updateItemList.isEmpty()) {
            experimentItemService.updateBatch(updateItemList);
        }
        log.info("更新[" + resources.getName() + "]实验Step3:更新实验组成功！");
        List<ExperimentItem> addItemList = itemList.parallelStream().filter(i -> i.getId() == null)
                .collect(Collectors.toList());
        if (!addItemList.isEmpty()) {
            experimentItemService.saveBatch(addItemList);
        }
        log.info("更新[" + resources.getName() + "]实验Step4:添加实验组成功！");
        //----------- 更新Group
        ExperimentGroupBucketInfo experimentGroupBucketInfo = bucketInfo.getExperimentGroupBucketInfo();
        resources.setBucketIds(experimentGroupBucketInfo.getBucketIds());
        resources.setBucketNum(experimentGroupBucketInfo.getBucketNum());
        resources.setSurplusBucketIds(experimentGroupBucketInfo.getSurplusBucketIds());
        resources.setSurplusBucketNum(experimentGroupBucketInfo.getSurplusBucketNum());
        resources.setParamKey(key);
        PageCategoryEnum bisLine = resources.getBisLine();
        if (bisLine != null) {
            resources.setBisLineName(bisLine.getName());
        }
//        if (status == GroupStatusEnum.RUNNING) {
//            resources.setStartTime(cDate);
//        }
        baseMapper.updateById(resources);
        log.info("更新[" + resources.getName() + "]实验Step5:更新实验成功！");
        //----------- 更新过滤条件
        experimentFilterService.deleteByExperimentGroupId(experimentConfDTO.getId());
        log.info("更新[" + resources.getName() + "]实验Step6:删除过滤条件成功！");
        for (ExperimentFilter f : newFilter) {
            f.setExperimentGroupId(experimentConfDTO.getId());
            experimentFilterService.create(f);
        }
        log.info("更新[" + resources.getName() + "]实验Step7:创建过滤条件成功！");
        //------------更新绑定的页面信息
        if (resources.getTerminalType() != TerminalTypeEnum.SERVER) {
            List<Long> oldPageIds = experimentConfDTO.getPages().parallelStream()
                    .map(PageDTO::getId)
                    .collect(Collectors.toList());
            pageService.unbindExperimentGroupId(oldPageIds, experimentConfDTO.getId());
            log.info("更新[" + resources.getName() + "]实验Step8:解绑页面成功！");
            pageService.bindExperimentGroupId(pageIds, experimentConfDTO.getId());
            log.info("更新[" + resources.getName() + "]实验Step9:关联页面成功！");
        }
        //-----------更新层
        LayerBucketInfo layerBucketInfo = bucketInfo.getLayerBucketInfo();
        layerDTO.setSurplusBucketIds(layerBucketInfo.getSurplusBucketIds());
        layerDTO.setSurplusBucketNum(layerBucketInfo.getSurplusBucketNum());
        layerService.update(layerMapStruct.toEntity(layerDTO));
        log.info("更新[" + resources.getName() + "]实验Step10:更新所属层剩余桶数[" + layerBucketInfo.getSurplusBucketNum() + "]成功!");
        log.info("更新实验成功,共[10]步<<<<<<<<<<");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void delete(Long id) {
        ExperimentGroupDTO dto = findById(id);
        if (dto == null) {
            throw new BadRequestException("ID为[" + id + "]的实验组不存在");
        }
        experimentItemService.deleteByExperimentGroupId(id);
        baseMapper.deleteById(id);
        Integer bucketNum = dto.getBucketNum();
        String bucketIds = dto.getBucketIds();
        LayerDTO layer = dto.getLayer();
        layer.setSurplusBucketNum(layer.getSurplusBucketNum() + bucketNum);
        layer.setSurplusBucketIds(BucketUtils.union(layer.getSurplusBucketIds(), bucketIds));
        layerService.update(layerMapStruct.toEntity(layer));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, GroupStatusEnum status) {
        //检查 实验是否存在
        Optional<ExperimentGroupDTO> optionalGroup = Optional.ofNullable(findById(id));
        ValidationUtil.isNull(optionalGroup, "Experiment", "id", id);
        ExperimentGroupDTO experimentGroupDTO = optionalGroup.get();
        if (status == experimentGroupDTO.getStatus()) {
            throw new BadRequestException("非法请求,当前状态为：" + experimentGroupDTO.getStatus());
        }
        if (status == GroupStatusEnum.TEST) {
            throw new BadRequestException("非法请求,当前状态为：" + experimentGroupDTO.getStatus());
        }
        if (status == GroupStatusEnum.RUNNING && (experimentGroupDTO.getStatus() != GroupStatusEnum.STOP &&
                experimentGroupDTO.getStatus() != GroupStatusEnum.TEST)) {
            throw new BadRequestException("非法请求,当前状态为：" + experimentGroupDTO.getStatus());
        }
        if (status == GroupStatusEnum.STOP && (experimentGroupDTO.getStatus() != GroupStatusEnum.RUNNING &&
                experimentGroupDTO.getStatus() != GroupStatusEnum.TEST)) {
            throw new BadRequestException("非法请求,当前状态为：" + experimentGroupDTO.getStatus());
        }
        Long duration = null;
        if (status == GroupStatusEnum.STOP) {
            if (experimentGroupDTO.getStatus() == GroupStatusEnum.RUNNING) {
                duration = DateUtil.between(experimentGroupDTO.getStartTime(), new Date(), DateUnit.SECOND);
            }
            //    experimentItemService.clearBucketIds(experimentGroupDTO.getId());
            clearBucketIds(experimentGroupDTO.getId());
            Integer bucketNum = experimentGroupDTO.getBucketNum();
            String bucketIds = experimentGroupDTO.getBucketIds();
            LayerDTO oldLayer = experimentGroupDTO.getLayer();
            Integer surplusBucketNum = oldLayer.getSurplusBucketNum();
            String surplusBucketIds = oldLayer.getSurplusBucketIds();
            String union = BucketUtils.union(surplusBucketIds, bucketIds);
            Layer newLayer = new Layer();
            newLayer.setId(oldLayer.getId());
            newLayer.setSurplusBucketNum(bucketNum + surplusBucketNum);
            newLayer.setSurplusBucketIds(union);
            layerService.update(newLayer);
        }
        RedisBucketInfo redisBucketInfo = new RedisBucketInfo(experimentGroupDTO, status);
        //sync redis
        sync2Redis(redisBucketInfo);
        Date cdate = new Date();
        if (status == GroupStatusEnum.RUNNING) {
            // experimentItemService.clearWhiteList(id);
            baseMapper.changeStatus(id, status, cdate, cdate, duration);
        } else {
            baseMapper.changeStatus(id, status, cdate, null, duration);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync2Redis(RedisBucketInfo redisBucketInfo) {
        redisService.multi(multi -> {
            String whiteListKey = redisBucketInfo.getWhiteListKey();
            //删除hash 结构 白名单
            List<String> needDelWhiteList = redisBucketInfo.getNeedDelWhiteList();
            if (needDelWhiteList != null && !needDelWhiteList.isEmpty()) {
                multi.hdel(whiteListKey, needDelWhiteList.toArray(new String[needDelWhiteList.size()]));
            }
            //删除桶信息
            List<String> needDelBucket = redisBucketInfo.getNeedDelBucket();
            if (needDelBucket != null && !needDelBucket.isEmpty()) {
                for (String bk : needDelBucket) {
                    multi.del(bk);
                }
            }
            //创建或者修改 层对应的白名单
            Map<String, String> whiteListHash = redisBucketInfo.getWhiteListHash();
            if (whiteListHash != null && !whiteListHash.isEmpty()) {
                multi.hmset(redisBucketInfo.getWhiteListKey(), whiteListHash);
            }
            //创建或者修改 桶对应的 参数
            Map<String, String> bucket2Params = redisBucketInfo.getBucket2Params();
            if (bucket2Params != null) {
                bucket2Params.forEach((k, v) -> {
                    multi.set(k, v);
                });
            }
        });
    }

    @Override
    public CheckVO existsParamKey(String paramKey) {
        ExperimentGroup experimentGroup = baseMapper.findByParamKey(paramKey);
        if (experimentGroup != null) {
            return CheckVO.builder().exists(true)
                    .msg("已存在key:[" + paramKey + "]")
                    .build();
        }
        return CheckVO.builder().exists(false).build();
    }

    @Override
    public List<HistoryDTO> history(Long id) {
        return baseMapper.history(id);
    }

    @Override
    public String flushCache(String authCode) {
        if (SystemConstants.SUPER_ADMIN_AUTH_CODE.equals(authCode)) {
            redisService.delByPrefix("wl:*");
            redisService.delByPrefix("bi:*");
            redisService.delByPrefix("terminal:*");
            //重建层
            List<LayerDTO> layerDTOS = layerService.listAll();
            layerDTOS.parallelStream().map(l -> {
                String name = l.getName();
                String redisKey = LayerUtils.getRedisKey(name);
                return TempLayer.builder().id(l.getId())
                        .redisKey(redisKey).build();
            }).collect(Collectors.groupingBy(i -> i.getRedisKey(), TreeMap::new, Collectors.toList()))
                    .forEach((key, value) -> {
                        List<String> collect = value.parallelStream()
                                .map(t -> t.getId().toString())
                                .collect(Collectors.toList());
                        String[] ids = new String[collect.size()];
                        redisService.sadd(key, collect.toArray(ids));
                    });
            LambdaQueryWrapper<ExperimentGroup> queryWrapper = Wrappers.<ExperimentGroup>lambdaQuery();
            queryWrapper.in(ExperimentGroup::getStatus, GroupStatusEnum.TEST, GroupStatusEnum.RUNNING);
            List<ExperimentGroup> experimentGroups = baseMapper.queryAll(queryWrapper);
            for (ExperimentGroup e : experimentGroups) {
                ExperimentGroupDTO experimentGroupDTO = experimentGroupMapStruct.toDto(e);
                RedisBucketInfo redisBucketInfo = new RedisBucketInfo(experimentGroupDTO, experimentGroupDTO.getStatus());
                sync2Redis(redisBucketInfo);
            }
            return "SUCCESS";
        }
        return "FAIL";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearBucketIds(Long id) {
        baseMapper.clearBucketIds(id);
    }

}
