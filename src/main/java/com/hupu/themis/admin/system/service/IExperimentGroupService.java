package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.domain.RedisBucketInfo;
import com.hupu.themis.admin.system.domain.vo.CheckVO;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.service.dto.ExperimentGroupDTO;
import com.hupu.themis.admin.system.service.dto.HistoryDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实验组配置 服务类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
public interface IExperimentGroupService extends IService<ExperimentGroup> {
    /**
     * get
     *
     * @param id
     * @return
     */
    ExperimentGroupDTO findById(long id);

    /**
     * 分页查询
     *
     * @param resources
     * @param pageable
     * @return
     */
    Map queryAll(ExperimentGroupDTO resources, Pageable pageable);

    /**
     * create
     *
     * @param resources
     * @return
     */
    ExperimentGroupDTO create(ExperimentGroup resources);

    /**
     * update
     *
     * @param resources
     */
    void update(ExperimentGroup resources);

    /**
     * delete
     *
     * @param id
     */
    void delete(Long id);


    void changeStatus(Long id, GroupStatusEnum status);

    void sync2Redis(RedisBucketInfo redisBucketInfo);

    CheckVO existsParamKey(String paramKey);

    List<HistoryDTO> history(Long id);

    String flushCache(String authCode);

    void clearBucketIds(Long id);
}
