package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.ExperimentTag;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 标签表 Mapper 接口
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-27
 */
public interface ExperimentTagMapper extends BaseMapper<ExperimentTag> {

    @Select("select count(1) from experiment_tag u ${ew.customSqlSegment}")
    long queryAllCount(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select * from experiment_tag ${ew.customSqlSegment}")
    List<ExperimentTag> queryAll(@Param(Constants.WRAPPER) LambdaQueryWrapper<ExperimentTag> queryWrapper);


}
