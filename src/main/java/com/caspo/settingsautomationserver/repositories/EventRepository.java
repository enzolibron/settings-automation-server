package com.caspo.settingsautomationserver.repositories;

import com.caspo.settingsautomationserver.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    
}
