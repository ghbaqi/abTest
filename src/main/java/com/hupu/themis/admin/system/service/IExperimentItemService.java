package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.ExperimentItem;
import com.hupu.themis.admin.system.service.dto.ExperimentItemDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实验详情 服务类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
public interface IExperimentItemService extends IService<ExperimentItem> {
    /**
     * get
     *
     * @param id
     * @return
     */
    ExperimentItemDTO findById(long id);

    /**
     * 分页查询
     *
     * @param resources
     * @param pageable
     * @return
     */
    Map queryAll(ExperimentItemDTO resources, Pageable pageable);

    /**
     * create
     *
     * @param resources
     * @return
     */
    ExperimentItemDTO create(ExperimentItem resources);

    /**
     * update
     *
     * @param resources
     */
    void update(ExperimentItem resources);

    /**
     * delete
     *
     * @param id
     */
    void delete(Long id);

    void updateBatch(List<ExperimentItem> list);

    List<ExperimentItem> findByExperimentGroupId(Long experimentGroupId);

    void deleteByExperimentGroupId(Long experimentGroupId);

    void deleteBatch(List<Long> delItemIds);

    void clearBucketIds(Long experimentGroupId);

    void clearWhiteList(Long experimentGroupId);

}
