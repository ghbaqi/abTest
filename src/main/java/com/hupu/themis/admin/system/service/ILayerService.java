package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.Layer;
import com.hupu.themis.admin.system.domain.vo.CheckVO;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.service.dto.LayerDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 层配置 服务类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
public interface ILayerService extends IService<Layer> {
    /**
     * get
     *
     * @param id
     * @return
     */
    LayerDTO findById(long id);

    /**
     * 分页查询
     *
     * @param layerDTO
     * @param pageable
     * @return
     */
    Map queryAll(LayerDTO layerDTO, Pageable pageable);

    /**
     * create
     *
     * @param resources
     * @return
     */
    LayerDTO create(Layer resources);

    /**
     * update
     *
     * @param resources
     */
    void update(Layer resources);

    /**
     * delete
     *
     * @param id
     */
    void delete(Long id);

    List<LayerDTO> listAll(PageCategoryEnum bisLineValue, TerminalTypeEnum terminalValue);

    List<LayerDTO> listAll();

    CheckVO existsWhiteList(Long layerId, String whiteList, Long experimentItemId);
}
