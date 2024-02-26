package com.caspo.settingsautomationserver.services;

import com.caspo.settingsautomationserver.ScheduledEventsStorage;
import com.caspo.settingsautomationserver.daos.EventDao;
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

    public Event updateEvent(Event event) {
        Event existing = eventDao.get(event.getEventId());

        if (existing == null) {
            return null;
        } else {

            //if update newEvent scheduled
            Event eventFromScheduledEventStorage = ScheduledEventsStorage.get().getEvents().stream()
                    .filter(item -> item.getEventId().equalsIgnoreCase(event.getEventId()))
                    .findFirst()
                    .orElse(null);

            int eventIndex = ScheduledEventsStorage.get().getIndex(eventFromScheduledEventStorage);

            //cancel previous scheduled task
            eventFromScheduledEventStorage.getKickoffTimeMinusTodayScheduledTask().cancel(false);
            eventFromScheduledEventStorage.getKickoffTimeScheduledTask().cancel(false);

            //set new scheduled task
            eventFromScheduledEventStorage.setKickoffTimeScheduledTask(eventSettingService.setKickoffTimeScheduledTask(event));

            if (!eventSettingService.isEventAlreadyStarted(event)) {
                eventFromScheduledEventStorage.setKickoffTimeMinusTodayScheduledTask(eventSettingService.setKickoffTimeMinusTodayScheduledTask(event));
            }

            ScheduledEventsStorage.get().updateEvent(eventIndex, event);
            return eventDao.update(event, event.getEventId());
        }

    }
}
