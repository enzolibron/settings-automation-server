package com.caspo.settingsautomationserver.models;

import com.caspo.settingsautomationserver.models.attributeconverter.CompetitionGroupSettingConverter;
import com.caspo.settingsautomationserver.models.attributeconverter.ScheduledFutureConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "events")
public class Event {

    @Id
    private String eventId;

    @Column(name = "event_date")
    private String eventDate;

    @Column(name = "is_rb")
    private String isRB;

    @Column(name = "competition_id")
    private String competitionId;

    @Column(name = "competition_name")
    private String competitionName;
    
    @Transient
    private ScheduledFuture<?> kickoffTimeMinusTodayScheduledTask;
    
    @Transient
    private ScheduledFuture<?> kickoffTimeScheduledTask;

    @Convert(converter = CompetitionGroupSettingConverter.class)
    @Column(name = "competition_group_setting")
    private CompetitionGroupSetting competitionGroupSetting;

    @Column(name = "away")
    private String away;

    @Column(name = "home")
    private String home;

}
