package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.ExperimentFilter;
import com.hupu.themis.admin.system.service.dto.ExperimentFilterDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实验过滤表 服务类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-27
 */
public interface IExperimentFilterService extends IService<ExperimentFilter> {
    /**
     * get
     *
     * @param id
     * @return
     */
    ExperimentFilterDTO findById(long id);

    /**
     * 分页查询
     *
     * @param resources
     * @param pageable
     * @return
     */
    Map queryAll(ExperimentFilterDTO resources, Pageable pageable);

    /**
     * create
     *
     * @param resources
     * @return
     */
    ExperimentFilterDTO create(ExperimentFilter resources);

    /**
     * update
     *
     * @param resources
     */
    void update(ExperimentFilter resources);

    /**
     * delete
     *
     * @param id
     */
    void delete(Long id);

    void deleteByExperimentGroupId(Long experimentGroupId);

    List<ExperimentFilterDTO> findByExperimentGroupId(Long experimentGroupId);
}
