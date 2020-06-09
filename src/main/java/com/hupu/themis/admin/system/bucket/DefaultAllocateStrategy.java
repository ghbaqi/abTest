package com.hupu.themis.admin.system.bucket;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hupu.themis.admin.system.enums.RateFlowStrategy;
import com.hupu.themis.admin.system.util.BucketUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultAllocateStrategy implements IAllocateStrategy {

    @Override
    public synchronized BucketInfo allocate(InputExperimentInfo input) {
        BucketInfo bucketInfo = new BucketInfo();
        processLayerAndGroup(bucketInfo, input);
        processItems(bucketInfo, input);
        bucketInfo.setOldItems(input.getOldItems());
        return bucketInfo;
    }

    private void processLayerAndGroup(BucketInfo bucketInfo, InputExperimentInfo input) {
        int needNum = BucketUtils.computeBucketNum(input.getGroupRateFlow());

        ExperimentGroupBucketInfo oldGroup = input.getOldGroup();
        LayerBucketInfo oldLayer = input.getOldLayer();
        ExperimentGroupBucketInfo newGroup = null;
        LayerBucketInfo newLayer = null;

        if (oldGroup == null) {//新创建
            String surplusBucketIds = oldLayer.getSurplusBucketIds();

            Set<String> baseSet = Sets.newHashSet(surplusBucketIds.split(","))
                    .stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());

            needNum = Math.min(baseSet.size(), needNum);
            Set<String> needSet = RandomUtil.randomEleSet(baseSet, needNum);
            String bucketIds = needSet.parallelStream().collect(Collectors.joining(","));

            newGroup = ExperimentGroupBucketInfo.builder()
                    .bucketIds(bucketIds)
                    .bucketNum(needSet.size())
                    .bucketIdList(Lists.newArrayList(needSet))
                    .build();

            List<String> newLayerSurplusList = baseSet.parallelStream()
                    .filter(b -> !needSet.contains(b))
                    .collect(Collectors.toList());
            String newLayerSurplusIds = newLayerSurplusList.parallelStream()
                    .collect(Collectors.joining(","));
            int newLayerSurplusNum = newLayerSurplusList.size();

            newLayer = LayerBucketInfo.builder()
                    .name(oldLayer.getName())
                    .id(oldLayer.getId())
                    .surplusBucketIds(newLayerSurplusIds)
                    .surplusBucketNum(newLayerSurplusNum)
                    .build();
            log.info("新建一个实验>>>");
        } else {
            int oldGroupBucketNum = oldGroup.getBucketNum();
            //--------------------处理layer 和 group
            if (needNum > oldGroupBucketNum) {//变大
                String surplusBucketIds = oldLayer.getSurplusBucketIds();

                Set<String> baseSet = StringUtils.isNotBlank(surplusBucketIds) ?
                        Sets.newHashSet(surplusBucketIds.split(","))
                                .stream()
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toSet()) : Sets.newHashSet();

                int changeNum = needNum - oldGroupBucketNum;

                changeNum = Math.min(baseSet.size(), changeNum);
                Set<String> tempSet = RandomUtil.randomEleSet(baseSet, changeNum);
                String oldGroupIds = oldGroup.getBucketIds();

                String newGroupBucketIds = BucketUtils.union(oldGroupIds, tempSet);
                int newGroupBucketNum = BucketUtils.computeBucketNum(oldGroupIds, tempSet);

                newGroup = ExperimentGroupBucketInfo.builder()
                        .bucketIds(newGroupBucketIds)
                        .bucketNum(newGroupBucketNum)
                        .bucketIdList(Lists.newArrayList(tempSet))//实验流量变大后桶信息
                        .id(oldGroup.getId())
                        .build();

                List<String> newLayerSurplusList = baseSet.parallelStream()
                        .filter(b -> !tempSet.contains(b))
                        .collect(Collectors.toList());
                String newLayerSurplusIds = newLayerSurplusList.parallelStream()
                        .collect(Collectors.joining(","));
                int newLayerSurplusNum = newLayerSurplusList.size();

                newLayer = LayerBucketInfo.builder()
                        .name(oldLayer.getName())
                        .id(oldLayer.getId())
                        .surplusBucketIds(newLayerSurplusIds)
                        .surplusBucketNum(newLayerSurplusNum)
                        .build();

                log.info("实验分流调大[" + BucketUtils.computeRateFlow(oldGroupBucketNum) + "]-->[" + input.getRateFlowStrategy() + "]");
            } else if (needNum < oldGroupBucketNum) {//变小
                String oldGroupIds = oldGroup.getBucketIds();
                Set<String> baseSet = StringUtils.isNotBlank(oldGroupIds) ?
                        Sets.newHashSet(oldGroupIds.split(",")) : Sets.newHashSet();

                needNum = Math.min(baseSet.size(), needNum);
                Set<String> tempSet = RandomUtil.randomEleSet(baseSet, needNum);

                List<String> subIds = baseSet.stream()
                        .filter(b -> !tempSet.contains(b))
                        .collect(Collectors.toList());

                String newGroupBucketIds = tempSet.parallelStream().collect(Collectors.joining(","));
                int newGroupBucketNum = tempSet.size();

                newGroup = ExperimentGroupBucketInfo.builder()
                        .bucketIds(newGroupBucketIds)
                        .bucketNum(newGroupBucketNum)
                        .bucketIdList(Lists.newArrayList(tempSet))
                        .id(oldGroup.getId())
                        .build();

                String newLayerSurplusIds = BucketUtils.union(oldGroup.getSurplusBucketIds(), subIds);
                int newLayerSurplusNum = oldLayer.getSurplusBucketNum() + subIds.size();

                newLayer = LayerBucketInfo.builder()
                        .name(oldLayer.getName())
                        .id(oldLayer.getId())
                        .surplusBucketIds(newLayerSurplusIds)
                        .surplusBucketNum(newLayerSurplusNum)
                        .build();
                log.info("实验分流调小[" + BucketUtils.computeRateFlow(oldGroupBucketNum) + "]-->[" + input.getRateFlowStrategy() + "]");
            } else {
                newGroup = oldGroup;
                newLayer = oldLayer;
                if (newGroup.getBucketIdList() == null) {
                    newGroup.setBucketIdList(BucketUtils.parseBucketList(newGroup.getSurplusBucketIds()));
                }
            }
        }
        bucketInfo.setLayerBucketInfo(newLayer);
        bucketInfo.setExperimentGroupBucketInfo(newGroup);
    }

    private void processItems(BucketInfo bucketInfo, InputExperimentInfo input) {
        ExperimentGroupBucketInfo newGroup = bucketInfo.getExperimentGroupBucketInfo();

        List<ExperimentItemBucketInfo> oldItems = input.getOldItems();
        List<ExperimentItemBucketInfo> newItem = Lists.newArrayList();

        List<ExperimentItemBucketInfo> submitItems = input.getSubmitItems();

        int groupBucketNum = newGroup.getBucketNum();
        String groupBucketIds = newGroup.getBucketIds();
        //计算平均值
        double avgRateFlow = BucketUtils.computeRateFlow(1, submitItems.size());
        int avgNeedNum = BucketUtils.computeBucketNum(avgRateFlow, groupBucketNum);

        //新基准
        Set<String> groupBucketIdSet = newGroup.getBucketIdList() != null ? Sets.newHashSet(newGroup.getBucketIdList()) : Sets.newHashSet();
        if (oldItems == null) {//新加入
            for (ExperimentItemBucketInfo d : submitItems) {
                int needNum = input.getRateFlowStrategy() == RateFlowStrategy.AVG ? avgNeedNum :
                        BucketUtils.computeBucketNum(d.getInputRateFlow(), groupBucketNum);

                needNum = Math.min(groupBucketIdSet.size(), needNum);
                Set<String> tempSet = RandomUtil.randomEleSet(groupBucketIdSet, needNum);
                String itemBucketIds = tempSet.parallelStream().collect(Collectors.joining(","));

                ExperimentItemBucketInfo a = ExperimentItemBucketInfo.builder()
                        .build();
                BeanUtil.copyProperties(d, a);
                a.setBucketIds(itemBucketIds);
                a.setBucketNum(tempSet.size());
                a.setGroupBucketNum(groupBucketNum);
                newItem.add(a);

                //循环修改基准数量
                groupBucketIdSet = groupBucketIdSet
                        .parallelStream()
                        .filter(b -> !tempSet.contains(b))
                        .collect(Collectors.toSet());
            }
            //更新group
            String groupSurplusBucketIds = groupBucketIdSet.stream().collect(Collectors.joining(","));
            newGroup.setSurplusBucketIds(groupSurplusBucketIds);
            newGroup.setSurplusBucketNum(groupBucketIdSet.size());

            log.info("新建实验参数>>>");
        } else {
            //基准
            Set<String> baseSet = groupBucketIdSet;

            //step1 释放需要删除的
            List<ExperimentItemBucketInfo> delItemIds = oldItems.parallelStream()
                    .filter(i -> !submitItems.contains(i))
                    .collect(Collectors.toList());
            bucketInfo.setDelItemIds(delItemIds);


            //step2 更新交集
            TreeMap<Long, List<ExperimentItemBucketInfo>> oldItemMap = oldItems.parallelStream()
                    .collect(Collectors.groupingBy(i -> i.getId(),
                            TreeMap::new, Collectors.toList()));

            //先缩小后扩充
            List<ExperimentItemBucketInfo> updateItemList = submitItems.parallelStream()
                    .filter(i -> i.getId() != null && oldItems.contains(i))
                    .map(s -> {
                        ExperimentItemBucketInfo oldItem = oldItemMap.get(s.getId()).get(0);
                        int submitBucketNum = BucketUtils.computeBucketNum(s.getInputRateFlow(), groupBucketNum);
                        if (submitBucketNum < oldItem.getBucketNum()) {
                            s.setScore(-999);
                        } else {
                            s.setScore(999);
                        }
                        return s;
                    })
                    .sorted(Comparator.comparingInt(ExperimentItemBucketInfo::getScore))
                    .collect(Collectors.toList());

            if (updateItemList.size() > 0) {
                log.info("实验[ID:" + newGroup.getId() + "]更新实验参数>>>");
            }

            for (ExperimentItemBucketInfo submitItem : updateItemList) {
                int submitBucketNum = input.getRateFlowStrategy() == RateFlowStrategy.AVG ? avgNeedNum :
                        BucketUtils.computeBucketNum(submitItem.getInputRateFlow(), groupBucketNum);

                ExperimentItemBucketInfo oldItem = oldItemMap.get(submitItem.getId()).get(0);
                String oldBucketIds = oldItem.getBucketIds();
                Set<String> oldItemBucketSet = StringUtils.isNotBlank(oldBucketIds) ?
                        Sets.newHashSet(oldBucketIds.split(","))
                        : Sets.newHashSet();
                Integer oldBucketNum = oldItem.getBucketNum();
                if (submitBucketNum < oldBucketNum) {//减少

                    submitBucketNum = Math.min(oldItemBucketSet.size(), submitBucketNum);
                    Set<String> needIds = RandomUtil.randomEleSet(oldItemBucketSet, submitBucketNum);
                    String itemBucketIds = needIds.parallelStream().collect(Collectors.joining(","));

                    ExperimentItemBucketInfo a = ExperimentItemBucketInfo.builder().build();
                    BeanUtil.copyProperties(submitItem, a);
                    a.setBucketIds(itemBucketIds);
                    a.setBucketNum(needIds.size());
                    a.setGroupBucketNum(groupBucketNum);
                    a.setId(oldItem.getId());
                    newItem.add(a);

                    //循环修改基准数量
                    List<String> freeBuckets = oldItemBucketSet.parallelStream()
                            .filter(b -> !needIds.contains(b))
                            .collect(Collectors.toList());
                    baseSet.addAll(freeBuckets);
                } else {//增加
                    int changeNum = submitBucketNum - oldBucketNum;
                    Set<String> filterAfterBaseSet = baseSet.parallelStream()
                            .filter(i -> !oldItemBucketSet.contains(i))
                            .collect(Collectors.toSet());

                    changeNum = Math.min(filterAfterBaseSet.size(), changeNum);
                    Set<String> needIds = RandomUtil.randomEleSet(filterAfterBaseSet, changeNum);
                    oldItemBucketSet.addAll(needIds);
                    String itemBucketIds = oldItemBucketSet.parallelStream()
                            .collect(Collectors.joining(","));

                    ExperimentItemBucketInfo a = ExperimentItemBucketInfo.builder().build();
                    BeanUtil.copyProperties(submitItem, a);
                    a.setBucketIds(itemBucketIds);
                    a.setBucketNum(oldItemBucketSet.size());
                    a.setGroupBucketNum(groupBucketNum);
                    a.setId(oldItem.getId());
                    newItem.add(a);

                    //循环修改基准数量
                    baseSet = baseSet.stream()
                            .filter(i -> !oldItemBucketSet.contains(i))
                            .collect(Collectors.toSet());
                }
            }
            //step3 添加增加的
            List<ExperimentItemBucketInfo> addItemList = submitItems.parallelStream()
                    .filter(i -> i.getId() == null)
                    .collect(Collectors.toList());

            if (addItemList.size() > 0) {
                log.info("实验[ID:" + newGroup.getId() + "]添加实验参数>>>");
            }
            for (ExperimentItemBucketInfo submitItem : addItemList) {
                int needNum = input.getRateFlowStrategy() == RateFlowStrategy.AVG ? avgNeedNum :
                        BucketUtils.computeBucketNum(submitItem.getInputRateFlow(), groupBucketNum);

                needNum = Math.min(baseSet.size(), needNum);
                Set<String> needIds = RandomUtil.randomEleSet(baseSet, needNum);

                String itemBucketIds = needIds.parallelStream()
                        .collect(Collectors.joining(","));

                ExperimentItemBucketInfo a = ExperimentItemBucketInfo.builder().build();
                BeanUtil.copyProperties(submitItem, a);
                a.setBucketIds(itemBucketIds);
                a.setBucketNum(needIds.size());
                a.setGroupBucketNum(groupBucketNum);
                newItem.add(a);

                //循环修改基准数量
                baseSet = baseSet.stream()
                        .filter(i -> !needIds.contains(i))
                        .collect(Collectors.toSet());
            }

            //更新group
            String groupSurplusBucketIds = baseSet.parallelStream().collect(Collectors.joining(","));
            newGroup.setSurplusBucketIds(groupSurplusBucketIds);
            newGroup.setSurplusBucketNum(baseSet.size());
        }
        bucketInfo.setExperimentItemBucketInfos(newItem);
    }

}
