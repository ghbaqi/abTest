package com.hupu.themis.admin.system.service.mapstruct;

import com.hupu.themis.admin.modules.common.mapper.BaseMapStruct;
import com.hupu.themis.admin.system.domain.User;
import com.hupu.themis.admin.system.service.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 *
 * @date 2019-11-23
 */
@Mapper(componentModel = "spring",uses = {RoleMapStruct.class},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapStruct extends BaseMapStruct<UserDTO, User> {

}
