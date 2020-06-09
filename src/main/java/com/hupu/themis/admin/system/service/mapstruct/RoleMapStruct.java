package com.hupu.themis.admin.system.service.mapstruct;

import com.hupu.themis.admin.modules.common.mapper.BaseMapStruct;
import com.hupu.themis.admin.system.domain.Role;
import com.hupu.themis.admin.system.service.dto.RoleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 *
 * @date 2019-11-23
 */
@Mapper(componentModel = "spring", uses = {PermissionMapStruct.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapStruct extends BaseMapStruct<RoleDTO, Role> {

}
