package com.caspo.settingsautomationserver.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@Entity(name = "competition_group_settings")
public class CompetitionGroupSetting {

    @Id
    private String name;

    @Column(name = "mtsgp")
    private String mtsgp;

    @Column(name = "today")
    private Integer today;

    @Column(name = "proposition_today")
    private String propositionToday;

    @Column(name = "straight_today")
    private String straightToday;
    
    @Column(name = "obt_today")
    private String obtToday;
    
    @Column(name = "straight_margin_group_name")
    private String straightMarginGroupName;
    
    @Column(name = "straight_today_margin_group_name")
    private String straightTodayMarginGroupName;
    
    @Column(name = "ip_margin_group_name")
    private String ipMarginGroupName;

    @Column(name = "bethold_amount")
    private Integer betholdAmount;

    @Column(name = "bethold_duration")
    private Integer betholdDuration;
}
