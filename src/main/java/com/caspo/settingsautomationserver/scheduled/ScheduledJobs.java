package com.caspo.settingsautomationserver.scheduled;

import com.caspo.settingsautomationserver.ScheduledEventsStorage;
import com.caspo.settingsautomationserver.daos.CompetitionDao;
import com.caspo.settingsautomationserver.daos.EventDao;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.utils.DateUtil;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@RequiredArgsConstructor
@Component
public class ScheduledJobs {

    private final static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    static final long DAY = 24 * 60 * 60 * 1000;

    private final EventDao eventDao;

    private final CompetitionDao competitionDao;

    //every hour
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void deleteEventsThatArePast24HrsFromTheirKickoff() {

        //delete event in ScheduledEventStorage
        ScheduledEventsStorage.get().getEvents().stream().forEach(event -> {
            try {
                if (isDatePast24Hrs(DateUtil.add12HoursToDate(formatter.parse(event.getEventDate())))) {
                    Logger.getLogger(ScheduledJobs.class.getName()).log(Level.INFO, "Removing event from ScheduledEventStorage: {0}", event);
                    ScheduledEventsStorage.get().remove(event);
                }
            } catch (ParseException ex) {
                Logger.getLogger(ScheduledJobs.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        //delete event in event DB
        eventDao.getAll().stream().forEach((Event event) -> {
            try {
                if (isDatePast24Hrs(DateUtil.add12HoursToDate(formatter.parse(event.getEventDate())))
                        || competitionDao.get(Long.valueOf(event.getCompetitionId())) == null) {
                    Logger.getLogger(ScheduledJobs.class.getName()).log(Level.INFO, "Removing event from db: {0}", event);
                    eventDao.delete(event.getEventId());
                }

            } catch (ParseException ex) {
                Logger.getLogger(ScheduledJobs.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public boolean isDatePast24Hrs(Date aDate) {
        return aDate.getTime() > System.currentTimeMillis() - DAY;
    }

}
