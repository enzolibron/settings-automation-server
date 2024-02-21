package com.caspo.settingsautomationserver.daos;

import com.caspo.settingsautomationserver.models.Competition;
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
public class CompetitionDao implements Dao<Competition> {

    private final CompetitionRepository competitionRepository;

    @Override
    public Competition get(Object id) {
        Optional<Competition> result = competitionRepository.findById((Long) id);
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Competition> getAll() {
        return competitionRepository.findAll();
    }

    @Override
    public Competition save(Competition t) {
        return competitionRepository.save(t);
    }

    @Override
    public Competition update(Competition t, Object param) {
        Competition existing = get(param);

        if (existing != null) {
            if (t.getCompetition() != null) {
                existing.setCompetition(t.getCompetition());
            }

            if (t.getSettings() != null) {
                existing.setSettings(t.getSettings());
            }

            return competitionRepository.save(existing);
        }

        return null;

    }

    @Override
    public String delete(Object id) {
        Competition existing = get(id);

        if (existing == null) {
            return null;
        } else {
            competitionRepository.delete(existing);
            return "Deleted successfully";
        }
    }

}
