package com.caspo.settingsautomationserver.models;

import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class GmmKafkaLogMessage {
    private Integer gmmid;
    private Long send_time;
    private Long response_time;
    private String response_code;
    private String response;
    private String message;
    private String url;
    private String appname;
    private String log_time;
}
