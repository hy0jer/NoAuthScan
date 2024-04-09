package example.scanchecks.utils;

import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;

public class packeage_util {
    public static String search_target_string(String str, HttpRequestResponse requestresponse) {
        for (HttpHeader i : requestresponse.response().headers()) {

            if (i.name().equals(str)) {
                return i.value();
            }
        }
        return null;
    }
}
