package com.hupu.themis.admin.system.domain;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedisParam {
    private String k;
    private String v;
    private String pgs;

    RedisParam(String k, String v) {
        this.k = k;
        this.v = v;
    }

    RedisParam(String k, String v, String pgs) {
        this.k = k;
        this.v = v;
        this.pgs = pgs;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
