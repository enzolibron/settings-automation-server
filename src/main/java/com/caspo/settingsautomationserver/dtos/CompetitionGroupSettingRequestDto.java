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
    private String mtsgp;
    private Integer today;
    private String propositionToday;
    private String straightToday;
    private String obtToday;
    private String straightMarginGroupName;
    private String straightTodayMarginGroupName;
    private String ipMarginGroupName;
    private Integer betholdAmount;
    private Integer betholdDuration;

    @Override
    public CompetitionGroupSetting dtoToEntity() {
        CompetitionGroupSetting entity = new CompetitionGroupSetting();

        entity.setName(this.name);
        entity.setMtsgp(this.mtsgp);
        entity.setToday(this.today);
        entity.setPropositionToday(this.propositionToday);
        entity.setStraightToday(this.straightToday);
        entity.setObtToday(this.obtToday);
        entity.setStraightMarginGroupName(this.straightMarginGroupName);
        entity.setStraightTodayMarginGroupName(this.straightTodayMarginGroupName);
        entity.setIpMarginGroupName(this.ipMarginGroupName);
        entity.setBetholdAmount(this.betholdAmount);
        entity.setBetholdDuration(this.betholdDuration);
        
        return entity;
    }

}
