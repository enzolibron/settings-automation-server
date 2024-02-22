package com.caspo.settingsautomationserver.daos;

import com.caspo.settingsautomationserver.models.ParentChildSetting;
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
            if (t.getSpecials() != null) {
                existing.setSpecials(t.getSpecials());
            }

            if (t.getParent() != null) {
                existing.setParent(t.getParent());
            }

            if (t.getKills() != null) {
                existing.setKills(t.getKills());
            }

            if (t.getHomeKills() != null) {
                existing.setHomeKills(t.getHomeKills());
            }

            if (t.getAwayKills() != null) {
                existing.setAwayKills(t.getAwayKills());
            }

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
        }    }

}
