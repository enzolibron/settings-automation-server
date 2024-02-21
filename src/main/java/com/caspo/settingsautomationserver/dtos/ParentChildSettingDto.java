package com.caspo.settingsautomationserver.dtos;

import com.caspo.settingsautomationserver.models.ParentChildSetting;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class ParentChildSettingDto implements Dto<ParentChildSetting> {

    private String setting;
    private String parent;
    private String specials;
    private String kills;
    private String homeKills;
    private String awayKills;

    @Override
    public ParentChildSetting dtoToEntity() {
        ParentChildSetting entity = new ParentChildSetting();

        entity.setSetting(this.getSetting());
        entity.setParent(this.getParent());
        entity.setSpecials(this.getSpecials());
        entity.setKills(this.getKills());
        entity.setHomeKills(this.getHomeKills());
        entity.setAwayKills(this.getAwayKills());

        return entity;
    }

}
