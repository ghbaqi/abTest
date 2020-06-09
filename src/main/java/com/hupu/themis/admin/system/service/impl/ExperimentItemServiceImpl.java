package com.hupu.themis.admin.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.system.domain.ExperimentItem;
import com.hupu.themis.admin.system.mapper.ExperimentItemMapper;
import com.hupu.themis.admin.system.service.IExperimentItemService;
import com.hupu.themis.admin.system.service.dto.ExperimentItemDTO;
import com.hupu.themis.admin.system.service.mapstruct.ExperimentItemMapStruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 实验详情 服务实现类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExperimentItemServiceImpl extends ServiceImpl<ExperimentItemMapper, ExperimentItem> implements IExperimentItemService {
    @Autowired
    private ExperimentItemMapStruct experimentItemMapStruct;

    @Override
    public ExperimentItemDTO findById(long id) {
        return null;
    }

    @Override
    public Map queryAll(ExperimentItemDTO resources, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExperimentItemDTO create(ExperimentItem resources) {
        Date cDate = new Date();
        resources.setCreateTime(cDate);
        resources.setModifyTime(cDate);
        baseMapper.insert(resources);
        return experimentItemMapStruct.toDto(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExperimentItem resources) {
        Date cDate = new Date();
        resources.setModifyTime(cDate);
        baseMapper.updateById(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<ExperimentItem> list) {
        baseMapper.updateBatch(list);
    }

    @Override
    public List<ExperimentItem> findByExperimentGroupId(Long experimentGroupId) {
        return baseMapper.findByExperimentGroupId(experimentGroupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByExperimentGroupId(Long experimentGroupId) {
        baseMapper.deleteByExperimentGroupId(experimentGroupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> delItemIds) {
        if (delItemIds != null) {
            for (Long id : delItemIds) {
                baseMapper.deleteById(id);
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearBucketIds(Long experimentGroupId) {
        baseMapper.clearBucketIds(experimentGroupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearWhiteList(Long experimentGroupId) {
        baseMapper.clearWhiteList(experimentGroupId);
    }
}
