package com.caspo.settingsautomationserver.repositories;

import com.caspo.settingsautomationserver.models.Margin;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Repository
public interface MarginRepository extends JpaRepository<Margin, Long> {
    
    List<Margin> findAllByMarginGroupName(String groupName);
    
    void deleteAllByMarginGroupName(String groupName);
    
    List<Margin> findAllByOrderByMarginGroupNameAsc();
}
