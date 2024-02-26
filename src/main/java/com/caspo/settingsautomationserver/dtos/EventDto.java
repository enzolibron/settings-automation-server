package com.caspo.settingsautomationserver.dtos;

import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class EventDto {
    
    private String eventId;
    private String eventDate;
    private String isRB;
    private String competitionId;
    private String competitionName;
    private String settingName;
    private String away;
    private String home;
    
}
