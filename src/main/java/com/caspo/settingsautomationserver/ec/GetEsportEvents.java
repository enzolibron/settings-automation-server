package com.caspo.settingsautomationserver.ec;

import com.caspo.settingsautomationserver.enums.EcUrl;
import com.caspo.settingsautomationserver.daos.CompetitionGroupSettingDao;
import com.caspo.settingsautomationserver.daos.EventDao;
import com.caspo.settingsautomationserver.daos.ParentChildSettingDao;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.models.ParentChildSetting;
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
    private final ParentChildSettingDao parentChildSettingDao;
    private final CompetitionGroupSettingDao competitionGroupSettingDao;
    private final EventDao eventDao;

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
            if ((!jsonObject.get("gmmID").toString().isEmpty() && computeKickoffPeriod(jsonObject.get("eventDate").toString()) > 0L) || eventDao.get(jsonObject.get("gmmID").toString()) != null) {
                Event newEvent = new Event();
                newEvent.setEventId(jsonObject.get("gmmID").toString());
                newEvent.setEventDate(jsonObject.get("eventDate").toString().replaceAll("/", "-"));
                if (jsonObject.get("isRB").toString().equalsIgnoreCase("Yes")) {
                    newEvent.setIsRB(1);
                } else {
                    newEvent.setIsRB(2);
                }
                newEvent.setCompetitionId(jsonObject.get("gmmCompetitionID").toString());
                newEvent.setCompetitionName(jsonObject.get("gmmCompetition").toString());
                newEvent.setAway(jsonObject.get("gmmAway").toString());
                newEvent.setHome(jsonObject.get("gmmHome").toString());

                newEvent.setType("parent");
                ParentChildSetting parentChildSetting = parentChildSettingDao.getParentChildSettingByCompetitionIdAndTypeAndSportId(Long.valueOf(newEvent.getCompetitionId()), newEvent.getType(), 23);

                if (parentChildSetting != null) {
                    CompetitionGroupSetting competitionGroupSettingParent = competitionGroupSettingDao.get(parentChildSetting.getCompetitionGroupSettingName());

                    if (competitionGroupSettingParent != null) {
                        newEvent.setCompetitionGroupSetting(competitionGroupSettingParent);
                        eventList.add(newEvent);

                    }
                }

            }
        }

        return eventList;
    }

}
