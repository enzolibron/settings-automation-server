package com.caspo.settingsautomationserver.enums;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
public enum EcUrl {
    UAT("http://uat-gmm-extended-spi-sbk-ext-cache.sbk808.com/api/getevents?sportid="),
    PROD("http://gmm-extended-spi-sbk-ext-cache.sbk808.com/api/getevents?sportid=");

    public final String url;

    private EcUrl(String url) {
        this.url = url;
    }
}
