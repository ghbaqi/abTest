package com.hupu.themis.admin.system.service.mapstruct;

import com.hupu.themis.admin.modules.common.mapper.BaseMapStruct;
import com.hupu.themis.admin.system.domain.ExperimentGroup;
import com.hupu.themis.admin.system.service.dto.ExperimentGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {ExperimentItemMapStruct.class, LayerMapStruct.class, PageMapStruct.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExperimentGroupMapStruct extends BaseMapStruct<ExperimentGroupDTO, ExperimentGroup> {

}
