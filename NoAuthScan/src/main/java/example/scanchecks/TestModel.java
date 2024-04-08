package example.scanchecks;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import example.scanchecks.model.View_model;


import example.scanchecks.model.Config_model;
import example.scanchecks.utils.Yaml_Util;


public class TestModel {
    public MontoyaApi api;
    public HttpRequestResponse baseRequestResponse;
    public Config_model config_model = new Config_model();

    public TestModel(MontoyaApi api, HttpRequestResponse baseRequestResponse) {
        this.api = api;
        this.baseRequestResponse = baseRequestResponse;
    }

    public HttpRequestResponse test_engine(MontoyaApi api, HttpRequestResponse baseRequestResponse, View_model tableModel, Boolean flag) {
        //以下if分支为默认扫描逻辑，在未启用配置文件下生效
        if (!flag) {
            HttpRequest request = baseRequestResponse.request();
            for (HttpHeader i : baseRequestResponse.request().headers()) {
                if (i.name().equals("Cookie")) {
                    request = request.withUpdatedHeader("Cookie", "");
                    HttpRequest httprequest = baseRequestResponse.request().withUpdatedHeader("Cookie", "");
                    HttpRequestResponse later_package = this.api.http().sendRequest(httprequest);
                    //该分支判断回包是否为空
                    if (later_package.response() == null) {
                        return null;
                    }
                    if (later_package.response().statusCode() / 100 == 2) {
                        tableModel.add(new View_model.ListTree(later_package));
                        return later_package;
                    }
                }
            }
            return null;
        }


        String file_path = System.getProperty("user.dir") + "/" + "auth_config.yaml";

        //以下逻辑判断扫描域名是否在黑名单内
        Object result1 = Yaml_Util.find_config(file_path, config_model.black_lists);
        Boolean flag1 = config_model.url_analyze(api, result1, baseRequestResponse);
        if (!flag1) {
            return null;
        }

        //以下逻辑判断扫描目标是否是黑名单内的文件格式
        Object result2 = Yaml_Util.find_config(file_path, config_model.black_file_suffix);
        Boolean flag2 = config_model.file_suffix_analyze(api, result2, baseRequestResponse);
        if (!flag2) {
            return null;
        }

        Logging logging = api.logging();
        HttpRequest send_package = baseRequestResponse.request();
        logging.logToOutput("Scanning " + send_package.url());
        HttpRequestResponse later_package = this.api.http().sendRequest(send_package);
        return poc_sender(api, baseRequestResponse, tableModel, file_path);
    }

    public HttpRequestResponse poc_sender(MontoyaApi api, HttpRequestResponse baseRequestResponse, View_model tableModel, String file_path) {
        //判断是否有鉴权字段
        Object result1 = Yaml_Util.find_config(file_path, config_model.request_headers);
        HttpRequest request_package = config_model.auth_analyze(result1, baseRequestResponse);
        if (request_package == null) {
            return null;
        }

        HttpRequestResponse later_package = this.api.http().sendRequest(request_package);
        //该分支判断回包是否为空
        if (later_package.response() == null) {
            return null;
        }

        if (later_package.response().statusCode() / 100 == 2) {
            //返回包请求头字段分析
            Object result2 = Yaml_Util.find_config(file_path, config_model.response_headers);
            Boolean flag1 = config_model.response_headers_analyze(result2, baseRequestResponse);
            if (!flag1) {
                return null;
            }

            //分析返回包中的json数据进一步判断是否未授权
            Object result3 = Yaml_Util.find_config(file_path, config_model.body_json_string);
            Boolean flag2 = config_model.json_analyze(api, result3, baseRequestResponse);
            if (!flag2) {
                return null;
            }
            tableModel.add(new View_model.ListTree(later_package));
            return later_package;
        }
        return null;
    }
}

