package example.scanchecks.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Json_Util {
    public static String serach_stringInJson(String target_string, String json_str) {
        try {
            JsonParser jp = new JsonParser();
            JsonObject jo = jp.parse(json_str).getAsJsonObject();
            return jo.get(target_string).getAsString();
        } catch (Exception e) {
            return null;
        }
    }
}

