package com.caspo.settingsautomationserver.utils;

import com.caspo.settingsautomationserver.models.ChildEvent;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
public class ParserUtil {

    public static List<ChildEvent> parseJsonArrayToChildEvent(JSONArray jsonArray) {
        List<ChildEvent> childEventList = new ArrayList();

        for (Object item : jsonArray) {
            JSONObject jsonObject = (JSONObject) item;

            ChildEvent childEvent = new ChildEvent();
            childEvent.setEventName(jsonObject.get("eventName").toString());
            childEvent.setEventID(Integer.parseInt(jsonObject.get("eventID").toString()));
            childEvent.setCompetitionName(jsonObject.get("competitionName").toString());
            childEvent.setChildEventType(jsonObject.get("childEventType").toString());

            childEventList.add(childEvent);
        }
        return childEventList;
    }
}
