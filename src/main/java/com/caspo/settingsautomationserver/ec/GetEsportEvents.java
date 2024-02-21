package com.caspo.settingsautomationserver.ec;

import com.caspo.settingsautomationserver.daos.CompetitionGroupSettingDao;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.models.Event;
import static com.caspo.settingsautomationserver.services.EventSettingService.computeKickoffPeriod;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Component
public class GetEsportEvents {

    private final int SPORTID = 23;
    private final String GMMURL = EcUrl.UAT.url;
    private final JSONParser parser = new JSONParser();
    private final CompetitionGroupSettingDao competitionGroupSettingDao;

    public List<Event> getEvents() throws IOException {

        List<Event> eventList = null;

        HttpGet request = new HttpGet(GMMURL + SPORTID);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(request);

        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String entityString = EntityUtils.toString(entity);

                try {
                    //to catch error 1002 
                    JSONObject result = (JSONObject) parser.parse(entityString);
                    if (Integer.parseInt(result.get("error code").toString()) == 1002) {
                        Logger.getLogger(GetEsportEvents.class.getName()).log(Level.INFO, "GetEsportEvents: error 1002 from ec, request limit exceeded");
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

            //add to list if event has gmmID and hasn't started
            if (!jsonObject.get("gmmID").toString().isEmpty() && computeKickoffPeriod(jsonObject.get("eventDate").toString()) > 0L) {
                Event newEvent = new Event();
                newEvent.setEcEventID(jsonObject.get("gmmID").toString());
                newEvent.setEventDate(jsonObject.get("eventDate").toString().replaceAll("/", "-"));
                newEvent.setIsRB(jsonObject.get("isRB").toString());
                newEvent.setCompetitionId(jsonObject.get("gmmCompetitionID").toString());
                newEvent.setCompetitionName(jsonObject.get("gmmCompetition").toString());

                CompetitionGroupSetting competitionGroupSetting = competitionGroupSettingDao.getCompetitionSettingByCompetitionId(Long.valueOf(newEvent.getCompetitionId()));
                if (competitionGroupSetting != null) {
                    newEvent.setCompetitionGroupSetting(competitionGroupSetting);
                    eventList.add(newEvent);
                }

            }
        }

        return eventList;
    }

}
