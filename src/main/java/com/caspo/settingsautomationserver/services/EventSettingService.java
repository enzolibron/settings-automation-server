package com.caspo.settingsautomationserver.services;

import com.caspo.settingsautomationserver.dtos.EventBetholdRequestDto;
import com.caspo.settingsautomationserver.enums.SportId;
import com.caspo.settingsautomationserver.models.BetType;
import com.caspo.settingsautomationserver.models.ChildEvent;
import com.caspo.settingsautomationserver.models.Competition;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.repositories.BetTypeRepository;
import com.caspo.settingsautomationserver.repositories.CompetitionGroupSettingRepository;
import com.caspo.settingsautomationserver.repositories.CompetitionRepository;
import com.caspo.settingsautomationserver.utils.DateUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private final CompetitionGroupSettingRepository competitionGroupSettingRepository;
    private final CompetitionRepository competitionRepository;
    private final BetTypeRepository betTypeRepository;
    private final GmmService gmmService;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private EventSettingService(CompetitionGroupSettingRepository competitionGroupSettingRepository, CompetitionRepository competitionRepository, GmmService gmmService, BetTypeRepository betTypeRepository) {
        this.competitionGroupSettingRepository = competitionGroupSettingRepository;
        this.competitionRepository = competitionRepository;
        this.betTypeRepository = betTypeRepository;
        this.gmmService = gmmService;
    }

    public void setScheduledTask(Event event) {

        CompetitionGroupSetting competitionGroupSetting = getCompetitionSettingByCompetitionId(Long.valueOf(event.getCompetitionId()));

        if (competitionGroupSetting != null) {
//            System.out.println(event);
            Integer settingToday = competitionGroupSetting.getToday();

            //schedule task for kickoff time - today
            Runnable kickoffTimeMinusTodaytask = () -> {
                System.out.println(new Date() + " running kickoffTimeMinusTodaytask for event: " + event.getEcEventID());
                setMtsgpByMtsgforToday(event, competitionGroupSetting);
                setMarginByMarketType(event);
                setMarginByMarketLineName(event.getEcEventID());
            };

            //schedule task for kickoff
            Runnable kickoffTask = () -> {
                System.out.println(new Date() + " running kickoffTask for event: " + event.getEcEventID());
                setEventBetHold(event, competitionGroupSetting);
                setMarginByMarketType(event);
                setMarginByMarketLineName(event.getEcEventID());
            };

            //compute period
            Long kickoffTimeMinusTodayTaskPeriod = computeKickoffMinusTodayPeriod(event.getEventDate(), settingToday);
            Long kickoffTimeTaskPeriod = computeKickoffPeriod(event.getEventDate());
            scheduler.schedule(kickoffTimeMinusTodaytask, kickoffTimeMinusTodayTaskPeriod, TimeUnit.MILLISECONDS);
            scheduler.schedule(kickoffTask, kickoffTimeTaskPeriod, TimeUnit.MILLISECONDS);
        }

    }

    public void setNewMatchSetting(Event event) {

        CompetitionGroupSetting competitionGroupSetting = getCompetitionSettingByCompetitionId(Long.valueOf(event.getCompetitionId()));
        if (competitionGroupSetting != null) {

            gmmService.setEventByMtsgp(Integer.valueOf(event.getEcEventID()), competitionGroupSetting.getStraight());
            setMarginByMarketType(event);
            setMarginByMarketLineName(event.getEcEventID());

            event.getSetting().setNewMatchSettingComplete(Boolean.TRUE);
        }

    }

    private CompetitionGroupSetting getCompetitionSettingByCompetitionId(Long id) {

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

            System.out.println(eventDateTime.getTime() - currentTimeMillis);
            return eventDateTime.getTime() - currentTimeMillis;

        } catch (java.text.ParseException ex) {
            Logger.getLogger(EventSettingService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void getPropositionsAndSetMargin(String eventId) {
        try {

            String[] propositions = gmmService.getPropositions(eventId);
            JSONObject resultJsonObject = (JSONObject) jsonParser.parse(propositions[1]);
            JSONObject responseJSONObject = (JSONObject) resultJsonObject.get("Response");

            if (responseJSONObject != null) {

                JSONArray propsJsonArray = (JSONArray) jsonParser.parse(responseJSONObject.get("props").toString());

                for (Object item : propsJsonArray) {
                    JSONObject jsonObject = (JSONObject) item;

                    if (jsonObject.get("isRB").toString().equalsIgnoreCase("1")) {
                        gmmService.setMarginByMarketLineName(Integer.valueOf(eventId), jsonObject.get("propositionName").toString(), 671, 1.02d);

                    } else if (jsonObject.get("isRB").toString().equalsIgnoreCase("2")) {
                        gmmService.setMarginByMarketLineName(Integer.valueOf(eventId), jsonObject.get("propositionName").toString(), 670, 1.03d);

                    }
                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(EventSettingService.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setMarginByMarketLineName(String eventId) {
        List<ChildEvent> childEvent = gmmService.getChildEvent(eventId);

        //get eventIDs
        List<Integer> eventIds = childEvent.stream()
                .map(item -> item.getEventID()).collect(Collectors.toList());

        //get parent event propositions and set margin
        getPropositionsAndSetMargin(eventId);

        //get child event propositions and set margin
        eventIds.stream().forEach(childEventId -> {
            getPropositionsAndSetMargin(childEventId.toString());
        });

    }

    private void setMtsgpByMtsgforToday(Event event, CompetitionGroupSetting competitionGroupSetting) {
        System.out.println(event);
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

                    BetType betType = betTypeRepository.getOne((Long) betTypeJsonObject.get("bettypeId"));

                    //TODO: getting of profit margin, 
                    if (betTypeJsonObject.get("isRB").toString().equalsIgnoreCase("1")) {
                        gmmService.setMarginByMarketType(event.getEcEventID(), Integer.parseInt(betType.getMarketTypeId()), 1.01);
                    } else if (betTypeJsonObject.get("isRB").toString().equalsIgnoreCase("2")) {
                        gmmService.setMarginByMarketType(event.getEcEventID(), Integer.parseInt(betType.getMarketTypeId()), 1.02);
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

}
