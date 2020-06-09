package com.hupu.themis.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.system.domain.ExperimentFilter;
import com.hupu.themis.admin.system.mapper.ExperimentFilterMapper;
import com.hupu.themis.admin.system.service.IExperimentFilterService;
import com.hupu.themis.admin.system.service.dto.ExperimentFilterDTO;
import com.hupu.themis.admin.system.service.mapstruct.ExperimentFilterMapStruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * 实验过滤表 服务实现类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-27
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExperimentFilterServiceImpl extends ServiceImpl<ExperimentFilterMapper, ExperimentFilter> implements IExperimentFilterService {
    @Autowired
    private ExperimentFilterMapStruct experimentFilterMapStruct;

    @Override
    public ExperimentFilterDTO findById(long id) {
        ExperimentFilter experimentFilter = baseMapper.selectById(id);
        Optional<ExperimentFilter> opt = Optional.ofNullable(experimentFilter);
        ValidationUtil.isNull(opt, "ExperimentFilter", "id", id);
        return experimentFilterMapStruct.toDto(opt.get());
    }

    @Override
    public Map queryAll(ExperimentFilterDTO resources, Pageable pageable) {
        LambdaQueryWrapper<ExperimentFilter> queryWrapper = Wrappers.<ExperimentFilter>lambdaQuery()
                .like(!ObjectUtils.isEmpty(resources.getTagColumnName()), ExperimentFilter::getTagColumnName, resources.getTagColumnName())
                .like(!ObjectUtils.isEmpty(resources.getTagTableName()), ExperimentFilter::getTagTableName, resources.getTagTableName());
        long count = baseMapper.queryAllCount(queryWrapper);
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order idOrder = sort.getOrderFor("modify_time");
            if (idOrder != null) {
                queryWrapper.orderByDesc(ExperimentFilter::getModifyTime);
            }
        }
        queryWrapper.last("limit " + pageable.getOffset() + "," + pageable.getPageSize());
        List<ExperimentFilterDTO> experimentFilterDTOS = experimentFilterMapStruct.toDto(baseMapper.queryAll(queryWrapper));
        return PageUtil.toPage(experimentFilterDTOS, count, pageable.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExperimentFilterDTO create(ExperimentFilter resources) {
        Date cDate = new Date();
        resources.setCreateTime(cDate);
        resources.setModifyTime(cDate);
        int insert = baseMapper.insert(resources);
        if (insert > 0) {
            return experimentFilterMapStruct.toDto(resources);
        }
        throw new BadRequestException("添加过滤条件失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExperimentFilter resources) {
        Optional<ExperimentFilter> opt = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(opt, "ExperimentFilter", "id", resources.getId());
        ExperimentFilter experimentFilter = opt.get();

        BeanUtil.copyProperties(resources, experimentFilter);
        experimentFilter.setModifyTime(new Date());

        int update = baseMapper.updateById(experimentFilter);
        if (update <= 0) {
            throw new BadRequestException("修改过滤条件[" + resources.getId() + "]失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByExperimentGroupId(Long experimentGroupId) {
        baseMapper.deleteByExperimentGroupId(experimentGroupId);
    }

    @Override
    public List<ExperimentFilterDTO> findByExperimentGroupId(Long experimentGroupId) {
        return baseMapper.findByExperimentGroupId(experimentGroupId);
    }
}
