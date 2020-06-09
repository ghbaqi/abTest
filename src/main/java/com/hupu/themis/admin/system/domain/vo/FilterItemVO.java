package com.hupu.themis.admin.system.domain.vo;

import com.hupu.themis.admin.system.enums.SqlKeywordEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterItemVO {
    private Long id;
    private Long tagId;
    private String tagTableName;
    private String tagColumnName;
    private SqlKeywordEnum operator;
    private String value;
    private SqlKeywordEnum nextFilterRelation;
    private Integer sort;
    private String operatorName;
}
