package com.caspo.settingsautomationserver.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GmmMarginBaseRequestDto extends GmmBaseRequestDto {

    private Integer eventId;
    private Integer marketTypeId;
    private String marketLineName;
    private Double profitMargin;
}
