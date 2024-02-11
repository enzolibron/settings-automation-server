package com.caspo.settingsautomationserver.dtos;

import com.caspo.settingsautomationserver.models.Competition;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class CompetitionRequestDto implements Dto<Competition> {
    
    private Long id;
    private String competition;
    private String settings;

    @Override
    public Competition dtoToEntity() {
        Competition entity = new Competition();
        
        entity.setId(this.id);
        entity.setCompetition(this.competition);
        entity.setSettings(this.settings);
        
        return entity;
    }
    
}
