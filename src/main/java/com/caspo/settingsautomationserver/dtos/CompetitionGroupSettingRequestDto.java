package com.caspo.settingsautomationserver.dtos;

import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class CompetitionGroupSettingRequestDto implements Dto<CompetitionGroupSetting> {
    private String name;
    private String straight;
    private Integer today;
    private String propositionToday;
    private String straightToday;
    
    @Override
    public CompetitionGroupSetting dtoToEntity() {
        CompetitionGroupSetting entity = new CompetitionGroupSetting();
        
        entity.setName(this.getName());
        entity.setPropositionToday(this.getPropositionToday());
        entity.setStraight(this.getStraight());
        entity.setStraightToday(this.getStraightToday());
        entity.setToday(this.getToday());
        
        return entity;
    }

}
