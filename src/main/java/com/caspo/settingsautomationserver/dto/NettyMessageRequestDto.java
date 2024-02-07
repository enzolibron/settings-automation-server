package com.caspo.settingsautomationserver.dto;

import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class NettyMessageRequestDto {

    private String type;
    private Object data;
    private Object param;
}
