package com.caspo.settingsautomationserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.concurrent.ScheduledFuture;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    private String ecEventID;
    private String eventDate;
    private String isRB;
    private String competitionId;
    private String competitionName;
    private ScheduledFuture<?> kickoffTimeMinusTodayScheduledTask;
    private ScheduledFuture<?> kickoffTimeScheduledTask;
    private CompetitionGroupSetting competitionGroupSetting;

}
