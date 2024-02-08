package com.caspo.settingsautomationserver.connector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.text.SimpleDateFormat;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Component
public class GmmConnector {

    private final Aes aes;
    
    public GmmConnector(Aes aes){
        this.aes = aes;
    }

    SimpleDateFormat sd = new SimpleDateFormat("yyMMddHHmmss");

    private final String KEY = "Sportsbook";
    private final String IV = "1234567890ABCDEF";
    private final String USER = "User1";
    private final String GMMURL = GmmUrl.UAT.url;

    private String[] send(String json, String u) {

        try {
//            System.out.println("json:\t" + json);

            HttpPost httppost = new HttpPost(u);
            httppost.setHeader(new BasicHeader("Authorization-UserCode", USER));
            httppost.setHeader(new BasicHeader("Authorization-Token", getAuthToken()));
            httppost.setHeader(new BasicHeader("Accept", "application/json"));
            httppost.setHeader(new BasicHeader("Content-type", "application/json"));

            String encjson = aes.encrypt(json, KEY, IV);

            httppost.setEntity(new StringEntity(encjson));
            HttpClient httpclient = HttpClients.createDefault();
            final Long sendTime = System.currentTimeMillis();
            HttpResponse response = httpclient.execute(httppost);
            final Long responseTime = System.currentTimeMillis();
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
//            System.out.println("res:\t" + response.getStatusLine());
            return new String[]{response.getStatusLine() + "", content, u, sendTime.toString(), responseTime.toString()};

        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(GmmConnector.class.getName()).log(Level.SEVERE, null, ex);
            return new String[]{"Error sending", "", ""};
        } catch (IOException ex) {
            Logger.getLogger(GmmConnector.class.getName()).log(Level.SEVERE, null, ex);
            return new String[]{"Error sending", "", ""};
        }
    }

    public String[] getEventBetHold(String requestBody) {
        String u = GMMURL + "/GMMForTA/GetEventHoldBet";
        return send(requestBody, u);
    }

    public String[] setEventBetHold(String requestBody) {
        String u = GMMURL + "/GMMForTA/SetEventHoldBet";
        return send(requestBody, u);
    }

    public String[] getEventMtsgp(String requestBody) {
        String u = GMMURL + "/GMMForTA/GetMtsgpByEvent";
        return send(requestBody, u);
    }

    public String[] setEventMtsgp(String requestBody) {
        String u = GMMURL + "/GMMForTA/SetMtsgpByEvent";
        return send(requestBody, u);
    }

    public String[] setEventMtsgpByMtsg(String requestBody) {
        String u = GMMURL + "/GMMForTA/SetMtsgpByMtsg";
        return send(requestBody, u);
    }

    public String[] setMarginByMarketType(String requestBody) {
        String u = GMMURL + "/GMMForTA/SetMarginByMarketType";
        return send(requestBody, u);
    }

    public String[] setMarginByMarketLineName(String requestBody) {
        String u = GMMURL + "/GMMForTA/SetMarginByMarketLineName";
        return send(requestBody, u);
    }

    public String[] getPropositions(String requestBody) {
        String u = GMMURL + "/GMMForTA/GetPropositions";
        return send(requestBody, u);
    }

    public String[] getChildEventInfo(String requestBody) {
        String u = GMMURL + "/GMMForTA/GetChildEventInfo";
        return send(requestBody, u);
    }

    public String[] getOrgSpread(String requestBody) {
        String u = GMMURL + "/GMMForTA/GetOrgSpread";
        return send(requestBody, u);
    }

    private String getAuthToken() {
        String timestamp = sd.format(new Date(System.currentTimeMillis() + 600000));
        String enctimestamp = TokenUtil.encrypt(timestamp);
        String token;
        try {
            token = aes.encrypt(USER + "|" + enctimestamp, KEY, IV);
            return token;
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(GmmConnector.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

    }

}
