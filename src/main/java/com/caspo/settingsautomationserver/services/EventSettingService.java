package com.caspo.settingsautomationserver.services;

import com.caspo.settingsautomationserver.ScheduledEventsStorage;
import com.caspo.settingsautomationserver.daos.CompetitionGroupSettingDao;
import com.caspo.settingsautomationserver.daos.EventDao;
import com.caspo.settingsautomationserver.daos.MarginDao;
import com.caspo.settingsautomationserver.dtos.EventBetholdRequestDto;
import com.caspo.settingsautomationserver.ec.GetEsportEvents;
import com.caspo.settingsautomationserver.enums.SportId;
import com.caspo.settingsautomationserver.models.ChildEvent;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.models.Margin;
import com.caspo.settingsautomationserver.models.ParentChildSetting;
import com.caspo.settingsautomationserver.utils.DateUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@RequiredArgsConstructor
@Service
public class EventSettingService {

    private final JSONParser jsonParser = new JSONParser();
    private final GmmService gmmService;
    private final GetEsportEvents getEsportEvents;
    private final EventDao eventDao;
    private final MarginDao marginDao;
    private final CompetitionGroupSettingDao competitionGroupSettingDao;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void setNewMatchSetting(Event event) {
        gmmService.setEventByMtsgp(Integer.valueOf(event.getEventId()), event.getCompetitionGroupSetting().getMtsgp());
        //non-proposition
        setMarginByMarketType(event, event.getCompetitionGroupSetting().getStraightMarginGroupName());
        //proposition
        setMarginByMarketLineName(event.getEventId(), event.getCompetitionGroupSetting().getStraightMarginGroupName());

    }

    public ScheduledFuture<?> setKickoffTimeMinusTodayScheduledTask(Event event) {

        Integer settingToday = event.getCompetitionGroupSetting().getToday();

        //schedule task for kickoff time - today
        Runnable kickoffTimeMinusTodaytask = () -> {

            System.out.println(new Date() + ": Running kickoffTimeMinusTodaytask for event: " + event.getEventId());
            setMtsgpByMtsgforToday(event, event.getCompetitionGroupSetting());
            //non-proposition
            setMarginByMarketType(event, event.getCompetitionGroupSetting().getStraightTodayMarginGroupName());
            //proposition
            setMarginByMarketLineName(event.getEventId(), event.getCompetitionGroupSetting().getStraightTodayMarginGroupName());
        };

        //compute period
        Long kickoffTimeMinusTodayTaskPeriod = computeKickoffMinusTodayPeriod(event.getEventDate(), settingToday);
        ScheduledFuture<?> kickoffTimeMinusTodayScheduledTask = scheduler.schedule(kickoffTimeMinusTodaytask, kickoffTimeMinusTodayTaskPeriod, TimeUnit.MILLISECONDS);

        return kickoffTimeMinusTodayScheduledTask;

    }

    public ScheduledFuture<?> setKickoffTimeScheduledTask(Event event) {

        //schedule task for kickoff
        Runnable kickoffTask = () -> {
            try {
                System.out.println(new Date() + ": Running kickoffTask for event: " + event.getEventId());
                TimeUnit.SECONDS.sleep(10);

                setEventBetHold(event, event.getCompetitionGroupSetting());
                //non-proposition
                setMarginByMarketType(event, event.getCompetitionGroupSetting().getIpMarginGroupName());
                //proposition
                setMarginByMarketLineName(event.getEventId(), event.getCompetitionGroupSetting().getIpMarginGroupName());
            } catch (InterruptedException ex) {
                Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        Long kickoffTimeTaskPeriod = computeKickoffPeriod(event.getEventDate());
        ScheduledFuture<?> kickoffTimeTaskScheduledTask = scheduler.schedule(kickoffTask, kickoffTimeTaskPeriod, TimeUnit.MILLISECONDS);

        return kickoffTimeTaskScheduledTask;

    }

    private void getPropositionsAndSetMargin(String eventId, List<Margin> margins) {
        try {

            String[] propositions = gmmService.getPropositions(eventId);
            JSONObject resultJsonObject = (JSONObject) jsonParser.parse(propositions[1]);
            JSONObject responseJSONObject = (JSONObject) resultJsonObject.get("Response");

            if (responseJSONObject != null) {

                JSONArray propsJsonArray = (JSONArray) jsonParser.parse(responseJSONObject.get("props").toString());

                for (Object item : propsJsonArray) {
                    JSONObject jsonObject = (JSONObject) item;
                    Optional<Margin> margin = margins.stream()
                            .filter(x -> x.getBetTypeName().equals((String) jsonObject.get("propositionName")))
                            .findAny();

                    if (margin.isPresent()) {
                        gmmService.setMarginByMarketLineName(Integer.valueOf(eventId), margin.get().getBetTypeName(), margin.get().getMarketTypeId(), margin.get().getMargin());

                    } else {
                        Logger.getLogger(EventSettingService.class.getName()).log(Level.INFO, "bet type nae: {0} not found", jsonObject.get("propositionName"));
                    }

                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(EventSettingService.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setMarginByMarketLineName(String eventId, String marginGroupName) {
        List<Margin> margins = marginDao.getMarginsByGroupName(marginGroupName);

        getPropositionsAndSetMargin(eventId, margins);

    }

    private void setMtsgpByMtsgforToday(Event event, CompetitionGroupSetting competitionGroupSetting) {
        //setMtsgp for straight, proposition, and obt TODAY
        gmmService.setMtsgpByMtsg(Integer.valueOf(event.getEventId()), competitionGroupSetting.getStraightToday(), "Straight");
        gmmService.setMtsgpByMtsg(Integer.valueOf(event.getEventId()), competitionGroupSetting.getObtToday(), "OBT");
        gmmService.setMtsgpByMtsg(Integer.valueOf(event.getEventId()), competitionGroupSetting.getPropositionToday(), "Proposition");

    }

    private void setMarginByMarketType(Event event, String marginGroupName) {
        try {
            List<Margin> margins = marginDao.getMarginsByGroupName(marginGroupName);
            String[] orgSpread = gmmService.getOrgSpread(event.getEventId());
            JSONObject resultJsonObject = (JSONObject) jsonParser.parse(orgSpread[1]);
            JSONObject responseJSONObject = (JSONObject) resultJsonObject.get("Response");
            margins.stream().forEach(item -> System.out.println(item));
            if (responseJSONObject != null) {

                JSONArray marketLines = (JSONArray) jsonParser.parse(responseJSONObject.get("withoutSpreadMarketline").toString());

                for (Object item : marketLines) {
                    JSONObject betTypeJsonObject = (JSONObject) item;

                    Optional<Margin> margin = margins.stream()
                            .filter(x -> x.getBetTypeName().equals((String) betTypeJsonObject.get("name")) 
//                                    is not proposition
                                    && ((long) betTypeJsonObject.get("bettypeId")) != 19L 
                                    && ((long) betTypeJsonObject.get("bettypeId")) != 17L)
                            .findAny();

                    if (margin.isPresent()) {
                        gmmService.setMarginByMarketType(event.getEventId(), margin.get().getMarketTypeId(), margin.get().getMargin());
                    } else {
                        Logger.getLogger(EventSettingService.class.getName()).log(Level.INFO, "bet type name: {0} not found", betTypeJsonObject.get("name"));
                    }

                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setEventBetHold(Event event, CompetitionGroupSetting competitionGroupSetting) {

        EventBetholdRequestDto request = new EventBetholdRequestDto();
        request.setEventid(Integer.valueOf(event.getEventId()));
        request.setHoldAmount(competitionGroupSetting.getBetholdAmount());
        request.setHoldDuration(competitionGroupSetting.getBetholdDuration());
        request.setIsHoldBet(1);
        request.setSportId(SportId.ESPORT.ID);
        gmmService.setEventBetHold(request);

    }

    private Long computeKickoffMinusTodayPeriod(String eventDate, Integer settingToday) {
        try {
            long currentTimeMillis = System.currentTimeMillis();

            Date eventDateTime = dateFormat.parse(eventDate);
            //convert to ph time
            eventDateTime = DateUtil.add12HoursToDate(eventDateTime);
            eventDateTime = DateUtil.minusHoursToDate(eventDateTime, settingToday);

            return eventDateTime.getTime() - currentTimeMillis;

        } catch (java.text.ParseException ex) {
            Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Long computeKickoffPeriod(String eventDate) {
        try {
            long currentTimeMillis = System.currentTimeMillis();

            Date eventDateTime = dateFormat.parse(eventDate);
            //convert to ph time
            eventDateTime = DateUtil.add12HoursToDate(eventDateTime);

            return eventDateTime.getTime() - currentTimeMillis;

        } catch (java.text.ParseException ex) {
            Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Boolean isEventAlreadyStarted(String eventDate) {
        return computeKickoffPeriod(eventDate) < 0L;
    }

    public void processEventsFromEc() throws IOException, InterruptedException {
        List<Event> eventListFromEc = null;

        while (eventListFromEc == null) {
            System.out.println(new Date() + " retrying to retrieve events... ");
            eventListFromEc = getEsportEvents.getEvents();
            TimeUnit.SECONDS.sleep(5);
        }
        //Check if event from ec is already existing in events in DB, if not, save
        eventListFromEc.stream().forEach(eventFromEc -> {
            Event existing = eventDao.get(eventFromEc.getEventId());
            if (existing == null) {
                eventDao.save(eventFromEc);
            } else {

                eventFromEc.setCompetitionGroupSetting(existing.getCompetitionGroupSetting());
                eventDao.update(eventFromEc, existing.getEventId());
            }

        });

        //process parent events
        List<Event> toScheduleEventList = eventDao.getAll()
                .stream()
                .map(event -> {

                    setNewMatchSetting(event);
                    event.setKickoffTimeMinusTodayScheduledTask(setKickoffTimeMinusTodayScheduledTask(event));
                    event.setKickoffTimeScheduledTask(setKickoffTimeScheduledTask(event));

                    processChildEvents(event);

                    return event;

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        ScheduledEventsStorage.get().addAll(toScheduleEventList);
    }

    private void processChildEvents(Event parentEvent) {

        gmmService.getChildEvent(parentEvent.getEventId()).stream().forEach(child -> {
            Event newChildEvent = new Event();
            newChildEvent.setEventId(child.getEventID().toString());
            newChildEvent.setAway(parentEvent.getAway());
            newChildEvent.setHome(parentEvent.getHome());
            newChildEvent.setCompetitionName(child.getCompetitionName());
            newChildEvent.setEventDate(parentEvent.getEventDate());

            ParentChildSetting parentChildSetting = competitionGroupSettingDao.getParentChildSettingByCompetitionId(Long.valueOf(parentEvent.getCompetitionId()));

            if (parentChildSetting != null) {
                System.out.println(parentChildSetting);
                CompetitionGroupSetting competitionGroupSettingParent = competitionGroupSettingDao.get(parentChildSetting.getParent());

                if (competitionGroupSettingParent != null) {
                    System.out.println(competitionGroupSettingParent);
                    newChildEvent.setCompetitionGroupSetting(competitionGroupSettingParent);

                }
            }

            setNewMatchSetting(newChildEvent);
            newChildEvent.setKickoffTimeMinusTodayScheduledTask(setKickoffTimeMinusTodayScheduledTask(newChildEvent));
            newChildEvent.setKickoffTimeScheduledTask(setKickoffTimeScheduledTask(newChildEvent));
        });
    }

}
