package example.scanchecks.model;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static example.scanchecks.utils.Json_Util.serach_stringInJson;
import static example.scanchecks.utils.packeage_util.search_target_string;

public class Config_model {
    public List<String> black_lists;
    public List<String> request_headers;
    public List<String> body_json_string;
    public List<String> black_file_suffix;
    public List<String> response_headers;

    public Config_model() {
        this.black_lists = new ArrayList<>();
        black_lists.add("black_lists");

        this.request_headers = new ArrayList<>();
        request_headers.add("request_package");
        request_headers.add("headers");

        this.body_json_string = new ArrayList<>();
        body_json_string.add("response_package");
        body_json_string.add("body_json_string");

        this.black_file_suffix = new ArrayList<>();
        black_file_suffix.add("request_package");
        black_file_suffix.add("black_file_suffix");

        this.response_headers = new ArrayList<>();
        response_headers.add("response_package");
        response_headers.add("headers");
    }


    public HttpRequest auth_analyze(Object config_list, HttpRequestResponse requestresponse) {
        List<String> finding_config = (List<String>) config_list;
        int sum = 0;
        HttpRequest request = requestresponse.request();
        for (HttpHeader i : requestresponse.request().headers()) {
            for (String e : finding_config) {
                if (i.name().equals(e)) {
                    sum += 1;
                    request = request.withUpdatedHeader(e, "");
                }
            }
        }
        if (sum <= 0) {
            return null;
        }
        return request;
    }

    public Boolean response_headers_analyze(Object config_list, HttpRequestResponse requestresponse) {
        List<String> finding_config = (List<String>) config_list;
        for (HttpHeader i : requestresponse.response().headers()) {
            for (String e : finding_config) {
                if (i.name().equals(e)) {
                   return false;
                }
            }
        }
        return true;
    }


    public Boolean json_analyze(MontoyaApi api, Object config_list, HttpRequestResponse requestresponse) {
        if (search_target_string("Content-Type", requestresponse).contains("application/json")) {
            String json_string = requestresponse.response().bodyToString();
            Map<String, Object> map = (Map<String, Object>) config_list;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (serach_stringInJson(key, json_string) == value) {
                    return false;
                }
            }
        }
        return true;
    }


    public Boolean file_suffix_analyze(MontoyaApi api, Object config_list, HttpRequestResponse requestresponse) {
        List<String> finding_config1 = (List<String>) config_list;
        HttpRequest request = requestresponse.request();
        String[] path1 = request.pathWithoutQuery().split("\\.");
        for (String e : finding_config1) {
            if (path1.length <= 1) {
                break;
            }
            if (path1[path1.length - 1].equals(e)) {
                api.logging().logToOutput("Ignore scan url: " + request.url());
                return false;
            }
        }
        return true;
    }


    public Boolean url_analyze(MontoyaApi api, Object config_list, HttpRequestResponse requestresponse) {
        String domain = requestresponse.request().url().split("/")[2];
        String[] domain_char = domain.split("\\.");
        List<String> finding_config = (List<String>) config_list;
        for (String e : finding_config) {
            String[] config_char1 = e.split("\\.");
            int num = 1;
            for (int ii = config_char1.length - 1; ii >= 0; ii--) {
                if (config_char1[ii].equals(domain_char[domain_char.length - num]) || config_char1[ii].equals("*")) {
                    num += 1;
                } else {
                    break;
                }
                if (num >= domain_char.length || num >= config_char1.length) {
                    api.logging().logToOutput("Ignore scan url: " + requestresponse.request().url());
                    return false;
                }
            }
        }
        return true;
    }


}
