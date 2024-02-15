package com.caspo.settingsautomationserver;

import com.caspo.settingsautomationserver.models.Event;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
public class EventStorage {

    private static EventStorage eventStorage_instance = null;

    private final List<Event> events;

    private EventStorage() {
        events = new ArrayList();
    }

    public static synchronized EventStorage get() {
        if (eventStorage_instance == null) {
            eventStorage_instance = new EventStorage();
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
    
    public List<Event> getEvents() {
        return events;
    }

}
