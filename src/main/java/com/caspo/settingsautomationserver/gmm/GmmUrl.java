package com.caspo.settingsautomationserver.gmm;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
public enum GmmUrl {
    UAT("http://spi-gmmpub.188.uat/api"),
    PROD("http://gmm-spi-sbk-ext.sbk808.prod/api");
    
    public final String url;
    
    private GmmUrl(String url) {
        this.url = url;
    }
    
}
