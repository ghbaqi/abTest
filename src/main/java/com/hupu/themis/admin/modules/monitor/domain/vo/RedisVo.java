package com.hupu.themis.admin.modules.monitor.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @date 2019-12-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisVo {

    @NotBlank
    private String key;

    @NotBlank
    private String value;
}
