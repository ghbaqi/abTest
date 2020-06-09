package com.hupu.themis.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.system.domain.ExperimentTag;
import com.hupu.themis.admin.system.mapper.ExperimentTagMapper;
import com.hupu.themis.admin.system.service.IExperimentTagService;
import com.hupu.themis.admin.system.service.dto.ExperimentTagDTO;
import com.hupu.themis.admin.system.service.mapstruct.ExperimentTagMapStruct;
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
 * 标签表 服务实现类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-27
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExperimentTagServiceImpl extends ServiceImpl<ExperimentTagMapper, ExperimentTag> implements IExperimentTagService {
    @Autowired
    private ExperimentTagMapStruct experimentTagMapStruct;

    @Override
    public ExperimentTagDTO findById(long id) {
        ExperimentTag experimentTag = baseMapper.selectById(id);
        Optional<ExperimentTag> opt = Optional.ofNullable(experimentTag);
        ValidationUtil.isNull(opt, "ExperimentTag", "id", id);
        return experimentTagMapStruct.toDto(opt.get());
    }

    @Override
    public Map queryAll(ExperimentTagDTO resources, Pageable pageable) {
        LambdaQueryWrapper<ExperimentTag> queryWrapper = Wrappers.<ExperimentTag>lambdaQuery()
                .like(!ObjectUtils.isEmpty(resources.getColumnName()), ExperimentTag::getColumnName, resources.getColumnName())
                .like(!ObjectUtils.isEmpty(resources.getTableName()), ExperimentTag::getTableName, resources.getTableName());
        long count = baseMapper.queryAllCount(queryWrapper);
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order idOrder = sort.getOrderFor("modify_time");
            if (idOrder != null) {
                queryWrapper.orderByDesc(ExperimentTag::getModifyTime);
            }
        }
        queryWrapper.last("limit " + pageable.getOffset() + "," + pageable.getPageSize());
        List<ExperimentTagDTO> experimentTagDTOs = experimentTagMapStruct.toDto(baseMapper.queryAll(queryWrapper));
        return PageUtil.toPage(experimentTagDTOs, count, pageable.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExperimentTagDTO create(ExperimentTag resources) {
        Date cDate = new Date();
        resources.setCreateTime(cDate);
        resources.setModifyTime(cDate);
        int insert = baseMapper.insert(resources);
        if (insert > 0) {
            return experimentTagMapStruct.toDto(resources);
        }
        throw new BadRequestException("添加标签失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExperimentTag resources) {
        Optional<ExperimentTag> opt = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(opt, "ExperimentTag", "id", resources.getId());
        ExperimentTag experimentTag = opt.get();

        BeanUtil.copyProperties(resources, experimentTag);
        experimentTag.setModifyTime(new Date());

        int update = baseMapper.updateById(experimentTag);
        if (update <= 0) {
            throw new BadRequestException("修改标签[" + resources.getId() + "]失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public List<ExperimentTagDTO> findAll() {
        LambdaQueryWrapper<ExperimentTag> queryWrapper = Wrappers.<ExperimentTag>lambdaQuery();
        return experimentTagMapStruct.toDto(baseMapper.selectList(queryWrapper));
    }
}
