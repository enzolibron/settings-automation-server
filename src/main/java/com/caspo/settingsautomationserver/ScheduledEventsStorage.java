package com.caspo.settingsautomationserver;

import com.caspo.settingsautomationserver.models.Event;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
public class ScheduledEventsStorage {

    private static ScheduledEventsStorage eventStorage_instance = null;

    private final List<Event> events;

    private ScheduledEventsStorage() {
        events = new ArrayList();
    }

    public static synchronized ScheduledEventsStorage get() {
        if (eventStorage_instance == null) {
            eventStorage_instance = new ScheduledEventsStorage();
        }

        return eventStorage_instance;
    }

    public List<Event> updateEvent(int index, Event updatedEvent) {
        events.set(index, updatedEvent);
        return events;
    }

    public int getIndex(Event event) {
        return events.indexOf(event);
    }

    public void addAll(List<Event> newEvents) {
        events.addAll(newEvents);

    }

    public void add(Event newEvent) {
        events.add(newEvent);
    }
    
    public void remove(Event event) {
        int eventIndex = getIndex(event);
        
        events.remove(eventIndex);
    }
    
    public List<Event> getEvents() {
        return events;
    }

}
