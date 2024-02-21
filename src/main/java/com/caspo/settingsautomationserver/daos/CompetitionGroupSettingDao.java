package com.caspo.settingsautomationserver.daos;

import com.caspo.settingsautomationserver.models.Competition;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.repositories.CompetitionGroupSettingRepository;
import com.caspo.settingsautomationserver.repositories.CompetitionRepository;
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
public class CompetitionGroupSettingDao implements Dao<CompetitionGroupSetting> {

    private final CompetitionGroupSettingRepository competitionGroupSettingRepository;
    private final CompetitionRepository competitionRepository;

    @Override
    public CompetitionGroupSetting get(Object id) {
        Optional<CompetitionGroupSetting> result = competitionGroupSettingRepository.findById((String) id);
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }

    }

    @Override
    public List<CompetitionGroupSetting> getAll() {
        return competitionGroupSettingRepository.findAll();
    }

    @Override
    public CompetitionGroupSetting save(CompetitionGroupSetting t) {
        return competitionGroupSettingRepository.save(t);
    }

    @Override
    public CompetitionGroupSetting update(CompetitionGroupSetting t, Object param) {

        CompetitionGroupSetting existing = get(param);

        if (existing != null) {
            if (t.getPropositionToday() != null) {
                existing.setPropositionToday(t.getPropositionToday());
            }

            if (t.getStraight() != null) {
                existing.setStraight(t.getStraight());
            }

            if (t.getStraightToday() != null) {
                existing.setStraightToday(t.getStraightToday());
            }

            if (t.getToday() != null) {
                existing.setToday(t.getToday());
            }

            return competitionGroupSettingRepository.save(existing);
        }
        return null;
    }

    @Override
    public String delete(Object name) {
        CompetitionGroupSetting existing = get(name);
        if (existing == null) {
            return null;
        } else {
            competitionGroupSettingRepository.delete(existing);
            return "Deleted successfully.";
        }

    }

    public CompetitionGroupSetting getCompetitionSettingByCompetitionId(Long id) {

        Optional<Competition> competition = competitionRepository.findById(id);
        if (competition.isPresent()) {
            Optional<CompetitionGroupSetting> setting = competitionGroupSettingRepository.findById(competition.get().getSettings());
            if (setting.isPresent()) {
                return setting.get();
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

}
