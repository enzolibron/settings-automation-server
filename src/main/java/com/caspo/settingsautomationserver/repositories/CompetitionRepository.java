package com.caspo.settingsautomationserver.repositories;

import com.caspo.settingsautomationserver.models.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long>{
    
}
