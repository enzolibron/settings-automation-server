package com.caspo.settingsautomationserver.ec;

import com.caspo.settingsautomationserver.models.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Component
public class GetEsportEvents {

    private final int SPORTID = 23;
    private final String GMMURL = EcUrl.UAT.url;

    JSONParser parser = new JSONParser();;
    
    public List<Event> connect() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<Event> eventList = null;

        HttpGet request = new HttpGet(GMMURL + SPORTID);
        CloseableHttpResponse response = httpClient.execute(request);

        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String entityString = EntityUtils.toString(entity);

                try {
                    //to catch error 1002 
                    JSONObject result = (JSONObject) parser.parse(entityString);
                    if (Integer.parseInt(result.get("error code").toString()) == 1002) {
                        System.out.println("GetEsportEvents: error 1002 from ec, request limit exceeded");
                    }
                    return null;
                } catch (ClassCastException e) {
                    JSONArray result = (JSONArray) parser.parse(entityString);
                    eventList = parseResultToEventList(result);
                }

            }
        } catch (ParseException | IOException | org.apache.http.ParseException ex) {
            Logger.getLogger(GetEsportEvents.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.close();
            } catch (IOException ex) {
                Logger.getLogger(GetEsportEvents.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return eventList;
    }

    private List<Event> parseResultToEventList(JSONArray result) throws ParseException {
        List<Event> eventList = new ArrayList();
        for (Object item : result) {
            JSONObject jsonObject = (JSONObject) item;

            if (!jsonObject.get("gmmID").toString().isEmpty() && jsonObject.get("isRB").toString().equalsIgnoreCase("No")) {
                Event newEvent = new Event();
                newEvent.setEcEventID(jsonObject.get("gmmID").toString());
                newEvent.setEventDate(jsonObject.get("eventDate").toString());
                newEvent.setIsRB(jsonObject.get("isRB").toString());
                newEvent.setCompetitionId(jsonObject.get("gmmCompetitionID").toString());
                newEvent.setCompetitionName(jsonObject.get("gmmCompetition").toString());
                eventList.add(newEvent);
            }
        }

        return eventList;
    }

}
