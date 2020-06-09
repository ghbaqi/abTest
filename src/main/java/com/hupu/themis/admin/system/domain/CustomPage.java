package com.hupu.themis.admin.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hupu.themis.admin.system.enums.GroupStatusEnum;
import com.hupu.themis.admin.system.enums.PageCategoryEnum;
import com.hupu.themis.admin.system.enums.TerminalTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 页面配置
 * </p>
 *
 * @author BobLiu
 * @since 2020-02-24
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CustomPage implements Serializable {
    private String experimentName;
    private GroupStatusEnum experimentStatus;
    private String experimentStatusName;
    private PageCategoryEnum bisLine;
    private String bisLineName;
    private TerminalTypeEnum terminalType;
    private String terminalTypeName;
    private String layerName;
    private Double rateFlow;
    private String userName;
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;
}
