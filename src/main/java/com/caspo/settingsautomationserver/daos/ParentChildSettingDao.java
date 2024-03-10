package com.caspo.settingsautomationserver.daos;

import com.caspo.settingsautomationserver.models.Competition;
import com.caspo.settingsautomationserver.models.ParentChildSetting;
import com.caspo.settingsautomationserver.repositories.CompetitionRepository;
import com.caspo.settingsautomationserver.repositories.ParentChildSettingRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@RequiredArgsConstructor
@Service
public class ParentChildSettingDao implements Dao<ParentChildSetting> {

    private final ParentChildSettingRepository parentChildSettingRepository;
    private final CompetitionRepository competitionRepository;

    @Override
    public ParentChildSetting get(Object name) {
        Optional<ParentChildSetting> result = parentChildSettingRepository.findById((String) name);
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    @Override
    public List<ParentChildSetting> getAll() {
        return parentChildSettingRepository.findAll();
    }

    @Override
    public ParentChildSetting save(ParentChildSetting t) {
        return parentChildSettingRepository.save(t);
    }

    @Override
    public ParentChildSetting update(ParentChildSetting t, Object param) {
        ParentChildSetting existing = get(param);

        if (existing != null) {

            return parentChildSettingRepository.save(existing);
        }
        return null;
    }

    @Override
    public String delete(Object name) {
        ParentChildSetting existing = get(name);
        if (existing == null) {
            return null;
        } else {
            parentChildSettingRepository.delete(existing);
            return "Deleted successfully.";
        }
    }
    

    public ParentChildSetting getParentChildSettingByCompetitionIdAndType(Long id, String type, Integer sportId) {

        Optional<Competition> competition = competitionRepository.findById(id);

        if (competition.isPresent()) {
            ParentChildSetting setting = parentChildSettingRepository.findByTypeIgnoreCaseAndSettingNameIgnoreCaseAndSportId(type, competition.get().getSettings(), sportId);

            if (setting != null) {
                return setting;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

}
