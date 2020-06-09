package com.hupu.themis.admin.system.service.mapstruct;

import com.hupu.themis.admin.modules.common.mapper.BaseMapStruct;
import com.hupu.themis.admin.system.domain.Menu;
import com.hupu.themis.admin.system.service.dto.MenuDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 *
 */
@Mapper(componentModel = "spring", uses = {RoleMapStruct.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapStruct extends BaseMapStruct<MenuDTO, Menu> {

}
