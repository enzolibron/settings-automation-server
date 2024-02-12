package com.caspo.settingsautomationserver.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EventBetholdRequestDto extends GmmBaseRequestDto {
    
    private Integer isHoldBet;
    private Integer holdDuration;
    private Integer holdAmount;

}
