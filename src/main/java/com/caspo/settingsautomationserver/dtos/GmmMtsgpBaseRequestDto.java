package com.caspo.settingsautomationserver.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GmmMtsgpBaseRequestDto extends GmmBaseRequestDto {

    private Integer eventId;
    private Integer isActive;
    private String mtsgpName;
    private String mtsgName;

}
