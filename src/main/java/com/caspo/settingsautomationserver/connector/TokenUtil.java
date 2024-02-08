package com.caspo.settingsautomationserver.connector;

import java.util.TreeMap;
import org.springframework.stereotype.Component;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Component
public class TokenUtil {

    public final static TreeMap<String, String> encrypt = new TreeMap<>();
    public final static TreeMap<String, String> decrypt = new TreeMap<>();

    static {
        for (int i = 0; i < 63; i++) {
            if (i < 10) {
                encrypt.put("0" + i, String.valueOf(i));
                decrypt.put(String.valueOf(i), "0" + i);
            } else if (i >= 10 && i < 10 + 26) {
                String s = String.valueOf((char) ((i - 10) + 65));
                encrypt.put(String.valueOf(i), s);
                decrypt.put(s, String.valueOf(i));
            } else if (i >= 10 + 26 && i < 10 + 26 * 2) {
                String s = String.valueOf((char) (i - (10 + 26) + (65 + 26) + 6));
                encrypt.put(String.valueOf(i), s);
                decrypt.put(s, String.valueOf(i));
            }
        }
    }

    static String encrypt(String date) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < date.length(); i = i + 2) {
            String s = date.substring(i, i + 2);
            sb.append(encrypt.get(s));
        }
        return sb.toString();
    }

    static String decrypt(String date) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < date.length(); i++) {
            String s = date.substring(i, i + 1);
            String s2 = decrypt.get(s);
            sb.append(s2);
            System.out.println(i + "~~~" + s + "~~~" + s2);
            if (i < 2) {
                sb.append("/");
            }
            if (i == 2) {
                sb.append(" ");
            }
            if (i > 2 && i < s.length() - 1) {
                sb.append(":");
            }
        }
        return "20" + sb.toString();
    }

}
