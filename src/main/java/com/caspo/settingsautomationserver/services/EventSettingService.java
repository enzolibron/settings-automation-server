package com.caspo.settingsautomationserver.services;

import com.caspo.settingsautomationserver.dtos.EventBetholdRequestDto;
import com.caspo.settingsautomationserver.enums.SportId;
import com.caspo.settingsautomationserver.models.BetType;
import com.caspo.settingsautomationserver.models.ChildEvent;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.repositories.BetTypeRepository;
import com.caspo.settingsautomationserver.utils.DateUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Service
public class EventSettingService {

    private final JSONParser jsonParser = new JSONParser();
    private final BetTypeRepository betTypeRepository;
    private final GmmService gmmService;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private EventSettingService(GmmService gmmService, BetTypeRepository betTypeRepository) {
        this.betTypeRepository = betTypeRepository;
        this.gmmService = gmmService;
    }

    public void setNewMatchSetting(Event event) {
        gmmService.setEventByMtsgp(Integer.valueOf(event.getEcEventID()), event.getCompetitionGroupSetting().getStraight());
        setMarginByMarketType(event);
        setMarginByMarketLineName(event);
    }

    public ScheduledFuture<?> setKickoffTimeMinusTodayScheduledTask(Event event) {

        Integer settingToday = event.getCompetitionGroupSetting().getToday();

        //schedule task for kickoff time - today
        Runnable kickoffTimeMinusTodaytask = () -> {
            System.out.println(new Date() + ": Running kickoffTimeMinusTodaytask for event: " + event.getEcEventID());
            setMtsgpByMtsgforToday(event, event.getCompetitionGroupSetting());
            setMarginByMarketType(event);
            setMarginByMarketLineName(event);
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
                System.out.println(new Date() + ": Running kickoffTask for event: " + event.getEcEventID());
                TimeUnit.SECONDS.sleep(10);
                setEventBetHold(event, event.getCompetitionGroupSetting());
                setMarginByMarketType(event);
                setMarginByMarketLineName(event);
            } catch (InterruptedException ex) {
                Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        Long kickoffTimeTaskPeriod = computeKickoffPeriod(event.getEventDate());

        ScheduledFuture<?> kickoffTimeTaskScheduledTask = scheduler.schedule(kickoffTask, kickoffTimeTaskPeriod, TimeUnit.MILLISECONDS);

        return kickoffTimeTaskScheduledTask;

    }

    private void getPropositionsAndSetMargin(String eventId, Boolean isEventStarted) {
        try {

            String[] propositions = gmmService.getPropositions(eventId);
            JSONObject resultJsonObject = (JSONObject) jsonParser.parse(propositions[1]);
            JSONObject responseJSONObject = (JSONObject) resultJsonObject.get("Response");

            if (responseJSONObject != null) {

                JSONArray propsJsonArray = (JSONArray) jsonParser.parse(responseJSONObject.get("props").toString());

                for (Object item : propsJsonArray) {
                    JSONObject jsonObject = (JSONObject) item;

                    if (isEventStarted) {
                        gmmService.setMarginByMarketLineName(Integer.valueOf(eventId), jsonObject.get("propositionName").toString(), 671, 1.02d);
                    } else {
                        gmmService.setMarginByMarketLineName(Integer.valueOf(eventId), jsonObject.get("propositionName").toString(), 670, 1.03d);
                    }
                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(EventSettingService.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setMarginByMarketLineName(Event event) {
        List<ChildEvent> childEvent = gmmService.getChildEvent(event.getEcEventID());

        //get eventIDs
        List<Integer> eventIds = childEvent.stream()
                .map(item -> item.getEventID()).collect(Collectors.toList());

        //get parent event propositions and set margin
        getPropositionsAndSetMargin(event.getEcEventID(), isEventRB(event));

        //get child event propositions and set margin
        eventIds.stream().forEach(childEventId -> {
            getPropositionsAndSetMargin(childEventId.toString(), isEventRB(event));
        });

    }

    private void setMtsgpByMtsgforToday(Event event, CompetitionGroupSetting competitionGroupSetting) {
        //setMtsgpByMtsg for mtsgName STRAIGHT
        gmmService.setMtsgpByMtsg(Integer.valueOf(event.getEcEventID()), competitionGroupSetting.getStraightToday(), "Straight");

        //setMtsgpByMtsg for mtsgName Proposition
        setMtsgpByMtsgForProposition(event, competitionGroupSetting.getPropositionToday());
    }

    private void setMtsgpByMtsgForProposition(Event event, String mtsgpName) {
        List<ChildEvent> childEvent = gmmService.getChildEvent(event.getEcEventID());

        //get eventIDs
        List<Integer> eventIds = childEvent.stream()
                .map(item -> item.getEventID()).collect(Collectors.toList());

        //add parent eventId
        eventIds.add(Integer.valueOf(event.getEcEventID()));

        eventIds.stream().forEach(eventId -> {
            try {
                String[] propositions = gmmService.getPropositions(eventId.toString());
                JSONObject resultJsonObject = (JSONObject) jsonParser.parse(propositions[1]);
                JSONObject responseJSONObject = (JSONObject) resultJsonObject.get("Response");

                if (responseJSONObject != null) {
                    gmmService.setMtsgpByMtsg(eventId, mtsgpName, "Proposition");
                }

            } catch (ParseException ex) {
                Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    private void setMarginByMarketType(Event event) {
        try {
            String[] orgSpread = gmmService.getOrgSpread(event.getEcEventID());
            JSONObject resultJsonObject = (JSONObject) jsonParser.parse(orgSpread[1]);
            JSONObject responseJSONObject = (JSONObject) resultJsonObject.get("Response");

            if (responseJSONObject != null) {

                JSONArray marketLines = (JSONArray) jsonParser.parse(responseJSONObject.get("withoutSpreadMarketline").toString());

                for (Object item : marketLines) {
                    JSONObject betTypeJsonObject = (JSONObject) item;

                    Optional<BetType> betType = betTypeRepository.findById((Long) betTypeJsonObject.get("bettypeId"));

                    if (betType.isPresent()) {
                        gmmService.setMarginByMarketType(event.getEcEventID(), Integer.parseInt(betType.get().getMarketTypeId()), 1.02);
                    } else {
                        Logger.getLogger(EventSettingService.class.getName()).log(Level.INFO, "bet type: {0} not found", betTypeJsonObject.get("bettypeId"));
                    }

                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setEventBetHold(Event event, CompetitionGroupSetting competitionGroupSetting) {

        EventBetholdRequestDto request = new EventBetholdRequestDto();
        request.setEventid(Integer.valueOf(event.getEcEventID()));
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

    public Boolean isEventRB(Event event) {
        return computeKickoffPeriod(event.getEventDate()) < 0L;
    }

}
