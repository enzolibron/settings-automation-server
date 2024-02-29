package com.caspo.settingsautomationserver.services;

import com.caspo.settingsautomationserver.ScheduledEventsStorage;
import com.caspo.settingsautomationserver.daos.CompetitionGroupSettingDao;
import com.caspo.settingsautomationserver.daos.EventDao;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.models.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventDao eventDao;
    private final EventSettingService eventSettingService;
    private final CompetitionGroupSettingDao competitionGroupSettingDao;

    public Event updateEvent(Event event) {
        Event existing = eventDao.get(event.getEventId());

        Event eventFromScheduledEventStorage = ScheduledEventsStorage.get().getEvents().stream()
                .filter(item -> item.getEventId().equalsIgnoreCase(event.getEventId()))
                .findFirst()
                .orElse(null);

        int scheduledEventIndex = ScheduledEventsStorage.get().getIndex(eventFromScheduledEventStorage);

        if (existing == null && eventFromScheduledEventStorage == null) {
            return null;
        } else {

            if (event.getCompetitionGroupSettingName() != null) {
                CompetitionGroupSetting cgs = competitionGroupSettingDao.get(event.getCompetitionGroupSettingName());
                event.setCompetitionGroupSetting(cgs);
            }

            Event updatedEvent = eventDao.update(event, event.getEventId());
            ScheduledEventsStorage.get().updateEvent(scheduledEventIndex, updatedEvent);

            //cancel previous scheduled task
            if (eventFromScheduledEventStorage.getKickoffTimeMinusTodayScheduledTask() != null) {
                eventFromScheduledEventStorage.getKickoffTimeMinusTodayScheduledTask().cancel(false);
            }

            if (eventFromScheduledEventStorage.getKickoffTimeScheduledTask() != null) {
                eventFromScheduledEventStorage.getKickoffTimeScheduledTask().cancel(false);
            }

            //set default settings
            eventSettingService.setNewMatchSetting(updatedEvent);

            //set today scheduled task
            eventFromScheduledEventStorage.setKickoffTimeMinusTodayScheduledTask(eventSettingService.setKickoffTimeMinusTodayScheduledTask(updatedEvent));

            //set kickoff scheduled task
            eventFromScheduledEventStorage.setKickoffTimeScheduledTask(eventSettingService.setKickoffTimeScheduledTask(updatedEvent));

            if (updatedEvent == null) {
                return null;
            } else {
                return updatedEvent;
            }
        }

    }
}
