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

    @Column(name = "straight")
    private String straight;

    @Column(name = "today")
    private Integer today;

    @Column(name = "proposition_today")
    private String propositionToday;

    @Column(name = "straight_today")
    private String straightToday;
}
