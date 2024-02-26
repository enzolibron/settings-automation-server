package com.caspo.settingsautomationserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EcPushFeedEventDto {
    private String eventid;
    private String ecEventID;
    private String isRB;
    private String eventDate;
    private EcPushFeedCompetitionDto competition;
    private EcPushFeedAwayHomeDto away;
    private EcPushFeedAwayHomeDto home;
}
