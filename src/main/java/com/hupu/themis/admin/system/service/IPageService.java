package com.hupu.themis.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.domain.Page;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.service.dto.PageDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 页面配置 服务类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@CacheConfig(cacheNames = "themis_page")
public interface IPageService extends IService<Page> {

  //  @Cacheable(key = "#p0")
    PageDTO findById(long id);

    //@Cacheable(keyGenerator = "keyGenerator")
    Map queryAll(PageDTO resources, Pageable pageable);

   // @CacheEvict(allEntries = true)
    PageDTO create(Page resources);

    //@CacheEvict(key = "#resources.id")
    void update(Page resources);

   // @CacheEvict(key = "#p0")
    void delete(Long id);

   // @Cacheable(keyGenerator = "keyGenerator")
    List<PageDTO> listByTerminalAndBusinessLine(TerminalTypeEnum terminalValue, PageCategoryEnum bisLineValue);


  //  @CacheEvict(allEntries = true)
    void bindExperimentGroupId(List<Long> pageIds, Long experimentGroupId);

 //   @Cacheable(key = "'experiment_'.concat(#p0)")
    List<ExperimentGroup> findEmployPage(Long pageId);

 //   @CacheEvict(allEntries = true)
    void unbindExperimentGroupId(List<Long> oldPageIds, Long id);

}
