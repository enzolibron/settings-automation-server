package com.caspo.settingsautomationserver.dtos;

import com.caspo.settingsautomationserver.models.ParentChildSetting;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class ParentChildSettingDto implements Dto<ParentChildSetting> {

    private Long id;
    private Integer sportId;
    private String type;
    private String settingName;
    private String competitionGroupSettingName;

    @Override
    public ParentChildSetting dtoToEntity() {
        ParentChildSetting entity = new ParentChildSetting();

        entity.setId(this.getId());
        entity.setSportId(this.getSportId());
        entity.setType(this.getType());
        entity.setSettingName(this.getSettingName());
        entity.setCompetitionGroupSettingName(this.getCompetitionGroupSettingName());

        return entity;
    }

}
