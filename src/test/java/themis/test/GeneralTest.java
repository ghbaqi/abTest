package themis.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import com.google.common.collect.Lists;
import com.hupu.themis.admin.system.SystemConstants;
import com.hupu.themis.admin.system.bucket.*;
import com.hupu.themis.admin.system.enums.RateFlowStrategy;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneralTest {
    @Test
    public void addBucketTest() {
        DefaultAllocateStrategy strategy = new DefaultAllocateStrategy();
        InputExperimentInfo input = InputExperimentInfo.builder()
                .groupRateFlow(0.4)
                .oldGroup(null)
                .oldItems(null)
                .oldLayer(LayerBucketInfo.builder()
                        .surplusBucketNum(10)
                        .surplusBucketIds(SystemConstants.BUCKET_LIST_STR)
                        .build())
                .rateFlowStrategy(RateFlowStrategy.CUSTOM)
                .submitItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .inputRateFlow(0.5).build(),
                        ExperimentItemBucketInfo.builder()
                                .inputRateFlow(0.5).build())
                ).build();

        BucketInfo allocate = strategy.allocate(input);
        LayerBucketInfo layerBucketInfo = allocate.getLayerBucketInfo();
        ExperimentGroupBucketInfo groupBucketInfo = allocate.getExperimentGroupBucketInfo();
        List<ExperimentItemBucketInfo> itemBucketInfos = allocate.getExperimentItemBucketInfos();

        System.out.println("层剩余：" + layerBucketInfo.getSurplusBucketIds() + ">>" + layerBucketInfo.getSurplusBucketNum());
        System.out.println("实验：" + groupBucketInfo.getBucketIds() + ">>" + groupBucketInfo.getBucketNum());
        System.out.println("实验剩余：" + groupBucketInfo.getSurplusBucketIds() + ">>" + groupBucketInfo.getSurplusBucketNum());
        itemBucketInfos.forEach(i -> {
            System.out.println("参数：" + i.getBucketIds() + ">>" + i.getBucketNum() + "|" + i.getGroupBucketNum());
        });

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println();
    }

    @Test
    public void updateBucketTest1() {//升
        DefaultAllocateStrategy strategy = new DefaultAllocateStrategy();
        InputExperimentInfo input = InputExperimentInfo.builder()
                .groupRateFlow(0.8)
                .oldLayer(LayerBucketInfo.builder()
                        .surplusBucketNum(6)
                        .surplusBucketIds("5,6,7,8,9,10")
                        .build())
                .oldGroup(ExperimentGroupBucketInfo.builder()
                        .bucketIds("1,2,3,4")
                        .bucketNum(4)
                        .surplusBucketNum(0)
                        .build())
                .oldItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .id(1L)
                                .bucketNum(2)
                                .bucketIds("1,2").build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .bucketNum(2)
                                .bucketIds("3,4")
                                .build()))
                .rateFlowStrategy(RateFlowStrategy.CUSTOM)
                .submitItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .id(1L)
                                .inputRateFlow(0.8).build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .inputRateFlow(0.1).build())
                ).build();

        BucketInfo allocate = strategy.allocate(input);
        LayerBucketInfo layerBucketInfo = allocate.getLayerBucketInfo();
        ExperimentGroupBucketInfo groupBucketInfo = allocate.getExperimentGroupBucketInfo();
        List<ExperimentItemBucketInfo> itemBucketInfos = allocate.getExperimentItemBucketInfos();

        System.out.println("层剩余：" + layerBucketInfo.getSurplusBucketIds() + ">>" + layerBucketInfo.getSurplusBucketNum());
        System.out.println("实验：" + groupBucketInfo.getBucketIds() + ">>" + groupBucketInfo.getBucketNum());
        System.out.println("实验剩余：" + groupBucketInfo.getSurplusBucketIds() + ">>" + groupBucketInfo.getSurplusBucketNum());
        itemBucketInfos.forEach(i -> {
            System.out.println("参数：" + i.getBucketIds() + ">>" + i.getBucketNum() + "|对应实验：" + i.getGroupBucketNum());
        });
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println();
    }


    @Test
    public void updateBucketTest2() {//降
        DefaultAllocateStrategy strategy = new DefaultAllocateStrategy();
        InputExperimentInfo input = InputExperimentInfo.builder()
                .groupRateFlow(1.0)
                .oldLayer(LayerBucketInfo.builder()
                        .surplusBucketNum(0)
                        .build())
                .oldGroup(ExperimentGroupBucketInfo.builder()
                        .bucketIds("1,2,3,4,5,6,7,8,9,10")
                        .bucketNum(10)
                        .surplusBucketNum(0)
                        .build())
                .oldItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .id(1L)
                                .bucketNum(5)
                                .bucketIds("1,2,3,4,5").build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .bucketNum(5)
                                .bucketIds("6,7,8,9,10")
                                .build()))
                .rateFlowStrategy(RateFlowStrategy.CUSTOM)
                .submitItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .id(1L)
                                .inputRateFlow(0.2).build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .inputRateFlow(0.3).build())
                ).build();

        BucketInfo allocate = strategy.allocate(input);
        LayerBucketInfo layerBucketInfo = allocate.getLayerBucketInfo();
        ExperimentGroupBucketInfo groupBucketInfo = allocate.getExperimentGroupBucketInfo();
        List<ExperimentItemBucketInfo> itemBucketInfos = allocate.getExperimentItemBucketInfos();

        System.out.println("层剩余：" + layerBucketInfo.getSurplusBucketIds() + ">>" + layerBucketInfo.getSurplusBucketNum());
        System.out.println("实验：" + groupBucketInfo.getBucketIds() + ">>" + groupBucketInfo.getBucketNum());
        System.out.println("实验剩余：" + groupBucketInfo.getSurplusBucketIds() + ">>" + groupBucketInfo.getSurplusBucketNum());
        itemBucketInfos.forEach(i -> {
            System.out.println("参数：" + i.getBucketIds() + ">>" + i.getBucketNum() + "|对应实验：" + i.getGroupBucketNum());
        });
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println();
    }

    @Test
    public void updateBucketTest3() {//降
        DefaultAllocateStrategy strategy = new DefaultAllocateStrategy();
        InputExperimentInfo input = InputExperimentInfo.builder()
                .groupRateFlow(0.7)
                .oldLayer(LayerBucketInfo.builder()
                        .surplusBucketNum(0)
                        .build())
                .oldGroup(ExperimentGroupBucketInfo.builder()
                        .bucketIds("1,2,3,4,5,6,7,8,9,10")
                        .bucketNum(10)
                        .surplusBucketNum(0)
                        .build())
                .oldItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .id(1L)
                                .bucketNum(5)
                                .bucketIds("1,2,3,4,5").build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .bucketNum(5)
                                .bucketIds("6,7,8,9,10")
                                .build()))
                .rateFlowStrategy(RateFlowStrategy.AVG)
                .submitItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .id(1L)
                                .inputRateFlow(0.2).build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .inputRateFlow(0.3).build())
                ).build();

        BucketInfo allocate = strategy.allocate(input);
        LayerBucketInfo layerBucketInfo = allocate.getLayerBucketInfo();
        ExperimentGroupBucketInfo groupBucketInfo = allocate.getExperimentGroupBucketInfo();
        List<ExperimentItemBucketInfo> itemBucketInfos = allocate.getExperimentItemBucketInfos();

        System.out.println("层剩余：" + layerBucketInfo.getSurplusBucketIds() + ">>" + layerBucketInfo.getSurplusBucketNum());
        System.out.println("实验：" + groupBucketInfo.getBucketIds() + ">>" + groupBucketInfo.getBucketNum());
        System.out.println("实验剩余：" + groupBucketInfo.getSurplusBucketIds() + ">>" + groupBucketInfo.getSurplusBucketNum());
        itemBucketInfos.forEach(i -> {
            System.out.println("参数：" + i.getBucketIds() + ">>" + i.getBucketNum() + "|对应实验：" + i.getGroupBucketNum());
        });
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println();
    }

    @Test
    public void updateBucketTest4() {//添加
        DefaultAllocateStrategy strategy = new DefaultAllocateStrategy();
        InputExperimentInfo input = InputExperimentInfo.builder()
                .groupRateFlow(0.8)
                .oldLayer(LayerBucketInfo.builder()
                        .surplusBucketNum(0)
                        .build())
                .oldGroup(ExperimentGroupBucketInfo.builder()
                        .bucketIds("1,2,3,4,5,6,7,8,9,10")
                        .bucketNum(10)
                        .surplusBucketNum(0)
                        .build())
                .oldItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .id(1L)
                                .bucketNum(5)
                                .bucketIds("1,2,3,4,5").build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .bucketNum(5)
                                .bucketIds("6,7,8,9,10")
                                .build()))
                .rateFlowStrategy(RateFlowStrategy.CUSTOM)
                .submitItems(Lists.newArrayList(
                        ExperimentItemBucketInfo.builder()
                                .inputRateFlow(0.2).build(),
                        ExperimentItemBucketInfo.builder()
                                .inputRateFlow(0.1).build(),
                        ExperimentItemBucketInfo.builder()
                                .id(2L)
                                .inputRateFlow(0.7).build()
                        )
                ).build();

        BucketInfo allocate = strategy.allocate(input);
        LayerBucketInfo layerBucketInfo = allocate.getLayerBucketInfo();
        ExperimentGroupBucketInfo groupBucketInfo = allocate.getExperimentGroupBucketInfo();
        List<ExperimentItemBucketInfo> itemBucketInfos = allocate.getExperimentItemBucketInfos();

        System.out.println("层剩余：" + layerBucketInfo.getSurplusBucketIds() + ">>" + layerBucketInfo.getSurplusBucketNum());
        System.out.println("实验：" + groupBucketInfo.getBucketIds() + ">>" + groupBucketInfo.getBucketNum());
        System.out.println("实验剩余：" + groupBucketInfo.getSurplusBucketIds() + ">>" + groupBucketInfo.getSurplusBucketNum());
        itemBucketInfos.forEach(i -> {
            System.out.println("参数：[ID:" + i.getId() + "]" + i.getBucketIds() + ">>" + i.getBucketNum() + "|对应实验：" + i.getGroupBucketNum());
        });
        List<ExperimentItemBucketInfo> delItemIds = allocate.getDelItemIds();
        System.out.println("删除>>" + delItemIds);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println();
    }

    @Test
    public void test4() {
        String regex = "([0-9|\\.]{1,})\\(([0-9]{1,})\\s*/\\s*([0-9]{1,})\\)";
        String v = "0.888(544748/544748)";
        System.out.println(ReUtil.get(regex, v, 1));
        System.out.println(ReUtil.get(regex, v, 2));
        System.out.println(ReUtil.get(regex, v, 3));
    }

    @Test
    public void test5() {
        try {
            for (int i = 0; i < 10000; i++) {
                ArrayList<String> baseList = Lists.newArrayList("1", "3", "3","3");
                HashSet hashSet = new HashSet(baseList);
                Set<String> strings = RandomUtil.randomEleSet(hashSet, 2);
                System.out.println(strings);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readFileTest6() {
        List<String> strings = FileUtil.readLines("/Users/liubei/Documents/haha.txt", Charset.defaultCharset());
        List<String> list = strings.stream().flatMap(s -> {
            return Lists.newArrayList(s.split(",")).stream();
        }).filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        Map<String, Long> collect = list.stream().sorted()
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        System.out.println(">>>" + collect);


        System.out.println("<<<" + list.size());
        collect.forEach((k, v) -> {
            if (v >= 2) {
                System.out.println(k);
            }
        });
    }

}

