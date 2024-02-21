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
@Entity(name = "parent_child_settings")
public class ParentChildSetting {

    @Id
    @Column(name = "setting")
    private String setting;

    @Column(name = "parent")
    private String parent;

    @Column(name = "specials")
    private String specials;

    @Column(name = "kills")
    private String kills;

    @Column(name = "home_kills")
    private String homeKills;

    @Column(name = "away_kills")
    private String awayKills;

}
