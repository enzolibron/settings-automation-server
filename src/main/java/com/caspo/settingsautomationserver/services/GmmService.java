package com.caspo.settingsautomationserver.services;


import com.caspo.settingsautomationserver.connector.GmmConnector;
import com.caspo.settingsautomationserver.dto.GmmBaseRequestDto;
import com.caspo.settingsautomationserver.dto.GmmMarginBaseRequestDto;
import com.caspo.settingsautomationserver.dto.GmmMtsgpBaseRequestDto;
import com.caspo.settingsautomationserver.enums.SportId;
import com.caspo.settingsautomationserver.kafka.KProducer;
import com.caspo.settingsautomationserver.models.ChildEvent;
import com.caspo.settingsautomationserver.utils.DateUtil;
import com.caspo.settingsautomationserver.utils.ParserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class GmmService {

    private final GmmConnector gmmConnector;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JSONParser jsonParser = new JSONParser();
    private final KProducer kproducer;

    private GmmService(GmmConnector gmmConnector, KProducer kproducer) {
        this.gmmConnector = gmmConnector;
        this.kproducer = kproducer;
    }
    

    public String[] setEventByMtsgp(Integer eventId, String mtsgpName) {
        try {
            GmmMtsgpBaseRequestDto mtsgpByEventRequestDto = new GmmMtsgpBaseRequestDto();
            mtsgpByEventRequestDto.setEventId(eventId);
            mtsgpByEventRequestDto.setSportId(SportId.ESPORT.id);
            mtsgpByEventRequestDto.setIsActive(1);
            mtsgpByEventRequestDto.setMtsgpName(mtsgpName);

            String[] response = gmmConnector.setEventMtsgp(objectMapper.writeValueAsString(mtsgpByEventRequestDto));
            sendLogToKafka(response, objectMapper.writeValueAsString(mtsgpByEventRequestDto), mtsgpByEventRequestDto.getEventId().toString());
            return response;

        } catch (JsonProcessingException ex) {
            Logger.getLogger(EventSettingService.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String[] setMtsgpByMtsg(Integer eventId, String mtsgpName, String mtsgName) {
        try {
            GmmMtsgpBaseRequestDto mtsgpByEventRequestDto = new GmmMtsgpBaseRequestDto();
            mtsgpByEventRequestDto.setEventId(eventId);
            mtsgpByEventRequestDto.setSportId(SportId.ESPORT.id);
            mtsgpByEventRequestDto.setIsActive(1);
            mtsgpByEventRequestDto.setMtsgpName(mtsgpName);
            mtsgpByEventRequestDto.setMtsgName(mtsgName);
            String[] response = gmmConnector.setEventMtsgpByMtsg(objectMapper.writeValueAsString(mtsgpByEventRequestDto));
            sendLogToKafka(response, objectMapper.writeValueAsString(mtsgpByEventRequestDto), mtsgpByEventRequestDto.getEventId().toString());
            return response;

        } catch (JsonProcessingException ex) {
            Logger.getLogger(EventSettingService.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String[] setMarginByMarketLineName(Integer eventId, String marketLineName, int marketTypeId, double profitMargin) {
        try {
            GmmMarginBaseRequestDto setMarginRequestDto = new GmmMarginBaseRequestDto();
            setMarginRequestDto.setEventId(eventId);
            setMarginRequestDto.setMarketLineName(marketLineName);
            setMarginRequestDto.setMarketTypeId(marketTypeId);
            setMarginRequestDto.setProfitMargin(profitMargin);
            setMarginRequestDto.setSportId(SportId.ESPORT.id);

            String[] response = gmmConnector.setMarginByMarketLineName(objectMapper.writeValueAsString(setMarginRequestDto));
            sendLogToKafka(response, objectMapper.writeValueAsString(setMarginRequestDto), setMarginRequestDto.getEventId().toString());
            return response;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GmmService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String[] setMarginByMarketType(String eventId, int marketTypeId, double profitMargin) {
        try {
            GmmMarginBaseRequestDto setMarginRequestDto = new GmmMarginBaseRequestDto();
            setMarginRequestDto.setEventId(Integer.valueOf(eventId));
            setMarginRequestDto.setMarketTypeId(marketTypeId);
            setMarginRequestDto.setProfitMargin(profitMargin);
            setMarginRequestDto.setSportId(SportId.ESPORT.id);

            String[] response = gmmConnector.setMarginByMarketType(objectMapper.writeValueAsString(setMarginRequestDto));
            sendLogToKafka(response, objectMapper.writeValueAsString(setMarginRequestDto), setMarginRequestDto.getEventId().toString());
            return response;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GmmService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String[] getPropositions(String eventId) {
        try {
            GmmBaseRequestDto request = new GmmBaseRequestDto();
            request.setEventid(Integer.valueOf(eventId));
            request.setSportId(SportId.ESPORT.id);
            String[] response = gmmConnector.getPropositions(objectMapper.writeValueAsString(request));
            sendLogToKafka(response, objectMapper.writeValueAsString(request), request.getEventid().toString());
            return response;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GmmService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String[] getOrgSpread(String eventId) {
        try {
            GmmBaseRequestDto request = new GmmBaseRequestDto();
            request.setEventid(Integer.valueOf(eventId));
            request.setSportId(SportId.ESPORT.id);
            String[] response = gmmConnector.getOrgSpread(objectMapper.writeValueAsString(request));
            sendLogToKafka(response, request.toString(), eventId);
            return response;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GmmService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public List<ChildEvent> getChildEvent(String eventId) {
        try {
            String[] getChildEventInfoResponse = gmmConnector.getChildEventInfo(eventId);
            JSONObject resultJsonObject = (JSONObject) jsonParser.parse(getChildEventInfoResponse[1]);
            JSONArray resultJsonArray = (JSONArray) jsonParser.parse(resultJsonObject.get("Response").toString());
            List<ChildEvent> childEvents = ParserUtil.parseJsonArrayToChildEvent(resultJsonArray);
            sendLogToKafka(getChildEventInfoResponse, eventId, eventId);
            return childEvents;

        } catch (ParseException ex) {
            Logger.getLogger(EventSettingService.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void sendLogToKafka(String[] response, String request, String eventId) {
        if (!response[0].equals("")) {
            JSONObject kafkaLog = new JSONObject();
            String gmmid = eventId;
            kafkaLog.put("gmmid", Integer.valueOf(gmmid));
            kafkaLog.put("send_time", Long.valueOf(response[3]));
            kafkaLog.put("response_time", Long.valueOf(response[4]));
            kafkaLog.put("response_code", response[0].trim());
            kafkaLog.put("response", response[1].trim());
            kafkaLog.put("message", request);
            kafkaLog.put("url", response[2]);
            kafkaLog.put("appname", "ta-esports-setting-automation");
            kafkaLog.put("log_time", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));

            System.out.println("sending to kafka");
            kproducer.sendToKafka(kafkaLog.toString());
        }
    }
}
