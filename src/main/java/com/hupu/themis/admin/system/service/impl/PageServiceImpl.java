package com.hupu.themis.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.modules.common.utils.PageUtil;
import com.hupu.themis.admin.modules.common.utils.ValidationUtil;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.domain.Page;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import com.hupu.themis.admin.system.mapper.PageMapper;
import com.hupu.themis.admin.system.service.IPageService;
import com.hupu.themis.admin.system.service.dto.PageDTO;
import com.hupu.themis.admin.system.service.mapstruct.PageMapStruct;
import com.hupu.themis.admin.system.util.SpmUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 页面配置 服务实现类
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements IPageService {
    @Autowired
    private PageMapStruct pageMapStruct;

    @Override
    public PageDTO findById(long id) {
        Optional<Page> page = Optional.ofNullable(baseMapper.selectById(id));
        ValidationUtil.isNull(page, "Page", "id", id);
        return pageMapStruct.toDto(page.get());
    }

    @Override
    public Map queryAll(PageDTO pageDTO, Pageable pageable) {
        LambdaQueryWrapper<Page> queryWrapper = Wrappers.<Page>lambdaQuery()
                .like(!ObjectUtils.isEmpty(pageDTO.getSpm()), Page::getSpm, pageDTO.getSpm())
                .eq(!ObjectUtils.isEmpty(pageDTO.getCategory()), Page::getCategory, pageDTO.getCategory())
                .like(!ObjectUtils.isEmpty(pageDTO.getDescription()), Page::getDescription, pageDTO.getDescription());
        long count = baseMapper.queryAllCount(queryWrapper);
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order idOrder = sort.getOrderFor("modify_time");
            if (idOrder != null) {
                queryWrapper.orderByDesc(Page::getModifyTime);
            }
        }
        queryWrapper.last("limit " + pageable.getOffset() + "," + pageable.getPageSize());
        List<PageDTO> pageDTOs = pageMapStruct.toDto(baseMapper.queryAll(queryWrapper));
        return PageUtil.toPage(pageDTOs, count, pageable.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO create(Page resources) {
        baseMapper.insert(resources);
        return pageMapStruct.toDto(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Page resources) {
        Optional<Page> opt = Optional.ofNullable(baseMapper.selectById(resources.getId()));
        ValidationUtil.isNull(opt, "Page", "id", resources.getId());
        resources.setModifyTime(new Date());
        int update = baseMapper.updateById(resources);
        if (update <= 0) {
            throw new BadRequestException("修改页面[" + resources.getDescription() + "]失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        throw new RuntimeException("不支持的请求");
    }

    @Override
    public List<PageDTO> listByTerminalAndBusinessLine(TerminalTypeEnum terminalValue,
                                                       PageCategoryEnum bisLineValue) {
        LambdaQueryWrapper<Page> queryWrapper = Wrappers.<Page>lambdaQuery();
        if (terminalValue != null) {
            if (terminalValue == TerminalTypeEnum.CLIENT) {
                queryWrapper.eq(Page::getTerminalType, TerminalTypeEnum.CLIENT)
                        .or().eq(Page::getTerminalType, TerminalTypeEnum.H5);
            } else if (terminalValue == TerminalTypeEnum.H5) {
                queryWrapper.eq(Page::getTerminalType, TerminalTypeEnum.H5)
                        .or().eq(Page::getTerminalType, TerminalTypeEnum.CLIENT);
            } else if (terminalValue == TerminalTypeEnum.M) {
                queryWrapper.eq(Page::getTerminalType, TerminalTypeEnum.M);
            } else if (terminalValue == TerminalTypeEnum.PC) {
                queryWrapper.eq(Page::getTerminalType, TerminalTypeEnum.PC);
            } else {
                log.info(terminalValue + "不支持获取分端页面");
            }
        }
        List<Page> pages = baseMapper.selectList(queryWrapper);
        List<Page> result = pages
                .parallelStream()
                .map(p -> {
                    PageCategoryEnum category = SpmUtils.getCategory(p.getSpm());
                    int score = 0;
                    if (bisLineValue == category) {
                        score += 1000;
                    }
                    String terminalPrefix = SpmUtils.getTerminalPrefix(p.getSpm());
                    if (terminalValue == TerminalTypeEnum.CLIENT) {
                        if ("px".equalsIgnoreCase(terminalPrefix)) {
                            score += 100;
                        } else if ("ph".equalsIgnoreCase(terminalPrefix)) {
                            score += 10;
                        }
                    } else if (terminalValue == TerminalTypeEnum.H5) {
                        if ("px".equalsIgnoreCase(terminalPrefix)) {
                            score += 10;
                        } else if ("ph".equalsIgnoreCase(terminalPrefix)) {
                            score += 100;
                        }
                    }
                    p.setScore(score);
                    List<ExperimentGroup> availablePage = findEmployPage(p.getId());
                    p.setIsEmploy(!availablePage.isEmpty());
                    return p;
                }).sorted(Comparator.comparingInt(Page::getScore).reversed())
                .sorted(Comparator.comparing(Page::getCreateTime).reversed())
                .collect(Collectors.toList());


        return pageMapStruct.toDto(result);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindExperimentGroupId(List<Long> pageIds, Long experimentGroupId) {
        baseMapper.bindExperimentGroupId(pageIds, experimentGroupId);
    }

    @Override
    public List<ExperimentGroup> findEmployPage(Long pageId) {
        return baseMapper.findEmployPage(pageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindExperimentGroupId(List<Long> oldPageIds, Long experimentGroupId) {
        baseMapper.unbindExperimentGroupId(oldPageIds, experimentGroupId);
    }


}
