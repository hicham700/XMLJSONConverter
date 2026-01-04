package com.hicham.xmljson.service;

import org.json.JSONObject;
import org.json.XML;

public class ConverterWithApi {

    public static String xmlToJson(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        return jsonObject.toString(4);
    }

    public static String jsonToXml(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return XML.toString(jsonObject);
    }
}
