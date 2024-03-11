package com.caspo.settingsautomationserver.daos;

import com.caspo.settingsautomationserver.models.Competition;
import com.caspo.settingsautomationserver.models.ParentChildSetting;
import com.caspo.settingsautomationserver.repositories.CompetitionRepository;
import com.caspo.settingsautomationserver.repositories.ParentChildSettingRepository;
import java.util.List;
import java.util.Objects;
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
public class ParentChildSettingDao implements Dao<ParentChildSetting> {

    private final ParentChildSettingRepository parentChildSettingRepository;
    private final CompetitionRepository competitionRepository;

    @Override
    public ParentChildSetting get(Object id) {
        Optional<ParentChildSetting> result = parentChildSettingRepository.findById((Long) id);
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }
    
    public List<ParentChildSetting> getBySettingNameAndSportId(String name, Integer sportId) {
        return parentChildSettingRepository.findAllBySettingNameIgnoreCaseAndSportId(name, sportId);
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
    public ParentChildSetting update(ParentChildSetting t, Object id) {
        ParentChildSetting existing = get(t.getId());

        if (existing != null) {
            
            if(t.getCompetitionGroupSettingName() != null) {
                existing.setCompetitionGroupSettingName(t.getCompetitionGroupSettingName());
            }
            
            if(t.getSettingName() != null) {
                existing.setSettingName(t.getSettingName());
            }
            
            if(t.getSportId() != null) {
                existing.setSportId(t.getSportId());
            }
            
            if(t.getType() != null) {
                existing.setType(t.getType());
            }

            return parentChildSettingRepository.save(existing);
        }
        return null;
    }

    public List<ParentChildSetting> batchUpdate(List<ParentChildSetting> t) {
        return t.stream().map(item -> update(item, item.getId())).filter(Objects::nonNull).collect(Collectors.toList());
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

    public ParentChildSetting getParentChildSettingByCompetitionIdAndTypeAndSportId(Long id, String type, Integer sportId) {

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
