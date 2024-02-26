package com.caspo.settingsautomationserver.dtos;

import com.caspo.settingsautomationserver.models.Event;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class EventDto implements Dto<Event> {
    
    private String eventId;
    private String eventDate;
    private String isRB;
    private String competitionId;
    private String competitionName;
    private String settingName;
    private String away;
    private String home;
    
    public static EventDto buildDto(Event event) {
        EventDto dto = new EventDto();
        dto.setAway(event.getAway());
        dto.setHome(event.getHome());
        dto.setCompetitionId(event.getCompetitionId());
        dto.setCompetitionName(event.getCompetitionName());
        dto.setEventDate(event.getEventDate());
        dto.setEventId(event.getEventId());
        dto.setIsRB(event.getIsRB());
        dto.setSettingName(event.getCompetitionGroupSetting().getName());
        
        return dto;
    }

    @Override
    public Event dtoToEntity() {
        Event entity = new Event();
        entity.setAway(this.away);
        entity.setCompetitionId(this.competitionId);
        entity.setCompetitionName(this.competitionName);
        entity.setEventDate(this.eventDate);
        entity.setEventId(this.eventId);
        entity.setHome(this.home);
        entity.setIsRB(this.isRB);
        
        return entity;
    }

    
}
