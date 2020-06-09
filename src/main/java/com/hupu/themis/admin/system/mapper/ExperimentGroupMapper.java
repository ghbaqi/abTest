package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.service.dto.HistoryDTO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 实验组配置 Mapper 接口
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
public interface ExperimentGroupMapper extends BaseMapper<ExperimentGroup> {
    @Select("select count(1) from experiment_group u ${ew.customSqlSegment}")
    long queryAllCount(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Results({
            @Result(id = true, property = "id", column = "id")
            , @Result(column = "id", property = "items", one = @One(select = "com.hupu.themis.admin.system.mapper.ExperimentItemMapper.findByExperimentGroupId"))
            , @Result(column = "layer_id", property = "layer", one = @One(select = "com.hupu.themis.admin.system.mapper.LayerMapper.findById"))
            , @Result(column = "id", property = "pages", one = @One(select = "com.hupu.themis.admin.system.mapper.PageMapper.findByExperimentGroupId"))})
    @Select("select * from experiment_group ${ew.customSqlSegment}")
    List<ExperimentGroup> queryAll(@Param(Constants.WRAPPER) LambdaQueryWrapper<ExperimentGroup> queryWrapper);

    @Results({
            @Result(id = true, property = "id", column = "id")
            , @Result(column = "id", property = "items", one = @One(select = "com.hupu.themis.admin.system.mapper.ExperimentItemMapper.findByExperimentGroupId"))
            , @Result(column = "layer_id", property = "layer", one = @One(select = "com.hupu.themis.admin.system.mapper.LayerMapper.findById"))
            , @Result(column = "id", property = "pages", one = @One(select = "com.hupu.themis.admin.system.mapper.PageMapper.findByExperimentGroupId"))})
    @Select("select * from experiment_group u where u.id = #{id}")
    ExperimentGroup findById(@Param("id") long id);

    void changeStatus(@Param("id") Long id, @Param("status") GroupStatusEnum status, @Param("cdate") Date cdate, @Param("startTime") Date startTime, @Param("duration") Long duration);

    @Select("select * from experiment_group where param_key = #{paramKey}")
    ExperimentGroup findByParamKey(@Param("paramKey") String paramKey);


    List<HistoryDTO> history(@Param("id") Long id);

    @Update("update experiment_group set bucket_num = 0,bucket_ids = null,surplus_bucket_num = 0,surplus_bucket_ids = null where id=#{id}")
    void clearBucketIds(@Param("id") Long id);
}
