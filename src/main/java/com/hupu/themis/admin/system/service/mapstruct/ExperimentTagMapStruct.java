package com.hupu.themis.admin.system.service.mapstruct;

import com.hupu.themis.admin.modules.common.mapper.BaseMapStruct;
import com.hupu.themis.admin.system.domain.ExperimentTag;
import com.hupu.themis.admin.system.service.dto.ExperimentTagDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExperimentTagMapStruct extends BaseMapStruct<ExperimentTagDTO, ExperimentTag> {

}
