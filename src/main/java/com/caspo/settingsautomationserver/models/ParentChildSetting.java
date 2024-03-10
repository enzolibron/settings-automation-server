package com.caspo.settingsautomationserver.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sport_id")
    private Integer sportId;

    @Column(name = "type")
    private String type;

    @Column(name = "setting_name")
    private String settingName;

    @Column(name = "competition_group_setting_name")
    private String competitionGroupSettingName;

}
