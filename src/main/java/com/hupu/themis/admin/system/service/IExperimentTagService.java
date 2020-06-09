package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.ExperimentTag;
import com.hupu.themis.admin.system.service.dto.ExperimentTagDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-27
 */
@CacheConfig(cacheNames = "experiment_tag")
public interface IExperimentTagService extends IService<ExperimentTag> {
    /**
     * get
     *
     * @param id
     * @return
     */
    ExperimentTagDTO findById(long id);

    /**
     * 分页查询
     *
     * @param resources
     * @param pageable
     * @return
     */
    Map queryAll(ExperimentTagDTO resources, Pageable pageable);

    /**
     * create
     *
     * @param resources
     * @return
     */
    ExperimentTagDTO create(ExperimentTag resources);

    /**
     * update
     *
     * @param resources
     */
    void update(ExperimentTag resources);

    /**
     * delete
     *
     * @param id
     */
    void delete(Long id);

    List<ExperimentTagDTO> findAll();

}
