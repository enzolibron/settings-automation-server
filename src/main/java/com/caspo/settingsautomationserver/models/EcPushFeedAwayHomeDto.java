package com.caspo.settingsautomationserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EcPushFeedAwayHomeDto {

    private String name;
    
}
