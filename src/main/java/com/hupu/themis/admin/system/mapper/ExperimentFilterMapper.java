package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.ExperimentFilter;
import com.hupu.themis.admin.system.service.dto.ExperimentFilterDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 实验过滤表 Mapper 接口
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-27
 */
public interface ExperimentFilterMapper extends BaseMapper<ExperimentFilter> {

    @Select("select count(1) from experiment_filter u ${ew.customSqlSegment}")
    long queryAllCount(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select * from experiment_filter ${ew.customSqlSegment}")
    List<ExperimentFilter> queryAll(@Param(Constants.WRAPPER) LambdaQueryWrapper<ExperimentFilter> queryWrapper);

    @Delete("delete from experiment_filter where experiment_group_id = #{experimentGroupId}")
    void deleteByExperimentGroupId(@Param("experimentGroupId") Long experimentGroupId);

    @Select("select * from experiment_filter where experiment_group_id =  #{experimentGroupId}")
    List<ExperimentFilterDTO> findByExperimentGroupId(@Param("experimentGroupId") Long experimentGroupId);
}
