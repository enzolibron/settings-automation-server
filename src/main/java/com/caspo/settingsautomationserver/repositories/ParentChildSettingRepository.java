package com.caspo.settingsautomationserver.repositories;

import com.caspo.settingsautomationserver.models.ParentChildSetting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Repository
public interface ParentChildSettingRepository extends JpaRepository<ParentChildSetting, Long> {

    ParentChildSetting findByTypeIgnoreCaseAndSettingNameIgnoreCaseAndSportId(String type, String settingName, Integer sportId);
    
    List<ParentChildSetting> findAllBySettingNameIgnoreCaseAndSportId(String settingName, Integer sportId);
    
}
