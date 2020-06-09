package com.hupu.themis.admin.system.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hupu.themis.admin.system.bucket.BucketInfo;
import com.hupu.themis.admin.system.bucket.ExperimentItemBucketInfo;
import com.hupu.themis.admin.system.bucket.LayerBucketInfo;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.service.dto.ExperimentGroupDTO;
import com.hupu.themis.admin.system.service.dto.ExperimentItemDTO;
import com.hupu.themis.admin.system.service.dto.PageDTO;
import com.hupu.themis.admin.system.util.BucketUtils;
import com.hupu.themis.admin.system.util.LayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RedisBucketInfo {
    private String whiteListKey;
    private Map<String, String> whiteListHash;
    private Long layerId;
    private TerminalTypeEnum terminalType;
    private Map<String, String> bucket2Params;

    private List<String> needDelWhiteList;
    private List<String> needDelBucket;

    public RedisBucketInfo(BucketInfo bucketInfo, String pages, GroupStatusEnum groupStatus) {
        this.whiteListHash = Maps.newHashMap();
        this.bucket2Params = Maps.newHashMap();
        this.needDelBucket = Lists.newArrayList();
        this.needDelWhiteList = Lists.newArrayList();

        LayerBucketInfo layerBucketInfo = bucketInfo.getLayerBucketInfo();
        String layerName = layerBucketInfo.getName();
        this.terminalType = LayerUtils.getTerminalType(layerName);
        this.layerId = layerBucketInfo.getId();
        this.whiteListKey = getWhiteListKey(terminalType, layerId);
        List<ExperimentItemBucketInfo> items = bucketInfo.getExperimentItemBucketInfos();
        for (ExperimentItemBucketInfo itemBucketInfo : items) {
            String whiteList = itemBucketInfo.getWhiteList();
            String paramKey = itemBucketInfo.getParamKey();
            String paramValue = itemBucketInfo.getParamValue();
            String param = createParam(terminalType, paramKey, paramValue, pages);
            if (StringUtils.isNotBlank(whiteList)) {
                String[] cltArr = whiteList.split(",");
                for (String clt : cltArr) {
                    whiteListHash.put(clt, param);
                }
            }
            String bucketIds = itemBucketInfo.getBucketIds();
            List<String> bucketList = BucketUtils.parseBucketList(bucketIds);
            for (String id : bucketList) {
                String bucketKey = getBucketKey(terminalType, layerId, id);
                bucket2Params.put(bucketKey, param);
            }
        }
        List<ExperimentItemBucketInfo> delItem = bucketInfo.getDelItemIds();
        if (delItem != null) {
            for (ExperimentItemBucketInfo bucket : delItem) {
                String id = bucket.getId().toString();
                needDelBucket.add(getBucketKey(terminalType, layerId, id));
                String whiteList = bucket.getWhiteList();
                if (StringUtils.isNotBlank(whiteList)) {
                    String[] cltArr = whiteList.split(",");
                    for (String clt : cltArr) {
                        needDelWhiteList.add(clt);
                    }
                }
            }
        }
        List<ExperimentItemBucketInfo> oldItems = bucketInfo.getOldItems();
        if (oldItems != null) {
            for (ExperimentItemBucketInfo itemBucketInfo : oldItems) {
                String whiteList = itemBucketInfo.getWhiteList();
                if (StringUtils.isNotBlank(whiteList)) {
                    String[] cltArr = whiteList.split(",");
                    for (String clt : cltArr) {
                        needDelWhiteList.add(clt);
                    }
                }
            }
        }
    }

    public RedisBucketInfo(ExperimentGroupDTO experimentGroupDTO, GroupStatusEnum status) {
        this.whiteListHash = Maps.newHashMap();
        this.bucket2Params = Maps.newHashMap();
        this.needDelBucket = Lists.newArrayList();
        this.needDelWhiteList = Lists.newArrayList();
        String pages = experimentGroupDTO.getPages()
                .parallelStream()
                .map(PageDTO::getSpm)
                .collect(Collectors.joining(","));
        TerminalTypeEnum terminalType = experimentGroupDTO.getTerminalType();
        Long layerId = experimentGroupDTO.getLayer().getId();
        this.whiteListKey = getWhiteListKey(terminalType, layerId);

        for (ExperimentItemDTO item : experimentGroupDTO.getItems()) {
            String whiteList = item.getWhiteList();
            String paramKey = item.getParamKey();
            String paramValue = item.getParamValue();
            if (status == GroupStatusEnum.STOP ) {
                if (StringUtils.isNotBlank(whiteList)) {
                    String[] cltArr = whiteList.split(",");
                    for (String clt : cltArr) {
                        needDelWhiteList.add(clt);
                    }
                }
                if (status == GroupStatusEnum.STOP) {
                    String bucketIds = experimentGroupDTO.getBucketIds();
                    List<String> bucketList = BucketUtils.parseBucketList(bucketIds);
                    for (String bucketId : bucketList) {
                        needDelBucket.add(getBucketKey(terminalType, layerId, bucketId));
                    }
                }
            }
            if (status == GroupStatusEnum.TEST || status == GroupStatusEnum.RUNNING) {
                String param = createParam(terminalType, paramKey, paramValue, pages);
                if (StringUtils.isNotBlank(whiteList)) {
                    String[] cltArr = whiteList.split(",");
                    for (String clt : cltArr) {
                        whiteListHash.put(clt, param);
                    }
                }
                String bucketIds = item.getBucketIds();
                List<String> bucketList = BucketUtils.parseBucketList(bucketIds);
                for (String id : bucketList) {
                    String bucketKey = getBucketKey(terminalType, layerId, id);
                    bucket2Params.put(bucketKey, param);
                }
            }
        }
    }


    public String createParam(TerminalTypeEnum terminalType, String key, String value, String pgs) {
        if (terminalType == TerminalTypeEnum.SERVER) {
            return new RedisParam(key, value).toJson();
        } else {
            if (StringUtils.isBlank(pgs)) {
                pgs = "ALL";
            }
            return new RedisParam(key, value, pgs).toJson();
        }
    }

    public String getWhiteListKey(TerminalTypeEnum terminalType, Long layerId) {
        return "wl" + ":" + terminalType.getKeyword() + ":" + layerId;
    }

    public String getBucketKey(TerminalTypeEnum terminalType, Long layerId, String bucketId) {
        return "bi" + ":" + terminalType.getKeyword() + ":" + layerId + ":" + bucketId;
    }

    public String getTerminalKey() {
        return "tk" + ":" + this.terminalType.getKeyword();
    }
}