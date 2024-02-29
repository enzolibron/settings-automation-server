package com.caspo.settingsautomationserver.daos;

import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.repositories.EventRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@RequiredArgsConstructor
@Service
public class EventDao implements Dao<Event> {
    
    private final EventRepository eventRepository;
    
    @Override
    public Event get(Object id) {
        Optional<Event> result = eventRepository.findById((String) id);
        
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }
    
    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }
    
    @Override
    public Event save(Event t) {
        return eventRepository.save(t);
    }
    
    public List<Event> saveAll(List<Event> events) {
        return events.stream().map(event -> {
            Event saved = save(event);
            return saved;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Event update(Event t, Object id) {
        Event existing = get(id);
        
        if (existing != null) {
            
            if (t.getCompetitionGroupSetting() != null) {
                existing.setCompetitionGroupSetting(t.getCompetitionGroupSetting());
            }
            
            if (t.getEventDate()!= null) {
                existing.setEventDate(t.getEventDate());
            }
           
            return eventRepository.save(existing);
        }
        
        return null;
    }
    
    @Override
    public String delete(Object id) {
        Event existing = get(id);
        
        if (existing == null) {
            return null;
        } else {
            eventRepository.delete(existing);
            return "Deleted successfully.";
        }
    }
    
}
