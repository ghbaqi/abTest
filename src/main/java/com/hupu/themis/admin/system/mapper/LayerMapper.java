package com.hupu.themis.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hupu.themis.admin.system.domain.Layer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 层配置 Mapper 接口
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
public interface LayerMapper extends BaseMapper<Layer> {
    @Select("select * from layer u where u.id = #{id}")
    Layer findById(@Param("id") long id);

    @Select("select count(1) from layer u ${ew.customSqlSegment}")
    long queryAllCount(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select * from layer ${ew.customSqlSegment}")
    List<Layer> queryAll(@Param(Constants.WRAPPER) LambdaQueryWrapper<Layer> queryWrapper);

    @Select("select * from layer u where u.name = #{name}")
    Layer findByLayerName(@Param("name") String layerName);

    @Select("SELECT ei.white_list FROM layer AS l" +
            " INNER JOIN experiment_group AS eg ON l.id=eg.layer_id" +
            " INNER JOIN experiment_item AS ei ON eg.id=ei.experiment_group_id" +
            " WHERE l.id = #{layerId}" +
            " AND eg.status <> 'STOP'" +
            " AND ei.id <> #{experimentItemId}")
    List<String> findWhiteListById(@Param("layerId") Long layerId, @Param("experimentItemId") Long experimentItemId);

    @Select("SELECT ei.white_list FROM layer AS l" +
            " INNER JOIN experiment_group AS eg ON l.id=eg.layer_id" +
            " INNER JOIN experiment_item AS ei ON eg.id=ei.experiment_group_id" +
            " WHERE l.id = #{layerId}" +
            " AND eg.status <> 'STOP'")
    List<String> findWhiteListByLayerId(@Param("layerId") Long layerId);
}
