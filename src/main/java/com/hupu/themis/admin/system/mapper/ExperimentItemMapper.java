package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hupu.themis.admin.system.domain.ExperimentItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 实验详情 Mapper 接口
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
public interface ExperimentItemMapper extends BaseMapper<ExperimentItem> {
    void updateBatch(@Param("list") List<ExperimentItem> list);

    @Select("select * from experiment_item u where u.experiment_group_id = #{experimentGroupId}")
    List<ExperimentItem> findByExperimentGroupId(@Param("experimentGroupId") Long experimentGroupId);

    @Delete("delete from experiment_item u where u.experiment_group_id = #{experimentGroupId} ")
    void deleteByExperimentGroupId(@Param("experimentGroupId") Long experimentGroupId);

    @Update("update experiment_item set white_list=null,bucket_num=0,bucket_ids=null,experiment_group_bucket_num=0 where experiment_group_id = #{experimentGroupId}")
    void clearBucketIds(@Param("experimentGroupId")Long experimentGroupId);

    @Update("update experiment_item set white_list=null where experiment_group_id = #{experimentGroupId}")
    void clearWhiteList(Long experimentGroupId);
}
