package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.domain.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 页面配置 Mapper 接口
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
public interface PageMapper extends BaseMapper<Page> {
    @Select("select count(1) from page u ${ew.customSqlSegment}")
    long queryAllCount(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select * from page ${ew.customSqlSegment}")
    List<Page> queryAll(@Param(Constants.WRAPPER) LambdaQueryWrapper<Page> queryWrapper);

    @Select("select p.* from experiment_group as e " +
            " inner join experiments_pages as ep on e.id = ep.experiment_group_id" +
            " inner join page as p on p.id = ep.page_id" +
            " where e.id = #{experimentGroupId}")
    List<Page> findByExperimentGroupId(@Param("experimentGroupId") Long experimentGroupId);

    List<ExperimentGroup> findEmployPage(@Param("pageId") Long pageId);

    void bindExperimentGroupId(@Param("pageIds") List<Long> pageIds, @Param("experimentGroupId") Long experimentGroupId);

    void unbindExperimentGroupId(@Param("pageIds") List<Long> oldPageIds, @Param("experimentGroupId") Long experimentGroupId);
}
