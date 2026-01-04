package com.hicham.xmljson.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConverterWithoutApi {

    // ===== XML → JSON avec support imbriqué =====
    public static String xmlToJson(String xml) {
        xml = xml.replaceAll(">\\s+<", "><").trim();
        return toJsonObject(xml, 0, xml.length());
    }

    // Méthode récursive pour convertir XML imbriqué en JSON
    private static String toJsonObject(String xml, int start, int end) {
        StringBuilder json = new StringBuilder("{");

        Pattern pattern = Pattern.compile("<(\\w+)>(.*?)</\\1>");
        Matcher matcher = pattern.matcher(xml.substring(start, end));

        boolean first = true;
        while (matcher.find()) {
            if (!first) json.append(",");
            first = false;

            String tag = matcher.group(1);
            String content = matcher.group(2).trim();

            // Vérifier si le contenu contient des balises imbriquées
            if (content.matches(".*<\\w+>.*")) {
                json.append("\"").append(tag).append("\": ")
                    .append(toJsonObject(content, 0, content.length()));
            } else {
                json.append("\"").append(tag).append("\": ")
                    .append("\"").append(content).append("\"");
            }
        }

        json.append("}");
        return json.toString();
    }

    // ===== JSON → XML avec support imbriqué =====
    public static String jsonToXml(String json) {
        json = json.replaceAll("\\s+", "");
        return toXmlObject(json);
    }

    // Méthode récursive pour convertir JSON imbriqué en XML
    private static String toXmlObject(String json) {
        StringBuilder xml = new StringBuilder();

        if (json.startsWith("{") && json.endsWith("}")) {
            String inner = json.substring(1, json.length() - 1);

            int braceCount = 0;
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            boolean inKey = true;

            for (int i = 0; i < inner.length(); i++) {
                char c = inner.charAt(i);
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;

                if (inKey) {
                    if (c == ':') {
                        inKey = false;
                    } else {
                        key.append(c);
                    }
                } else {
                    value.append(c);
                    // Fin de valeur : soit fin de JSON imbriqué, soit fin d'élément
                    if ((braceCount == 0 && i + 1 == inner.length()) || (braceCount == 0 && inner.charAt(i + 1) == ',')) {
                        String k = key.toString().replaceAll("\"", "");
                        String v = value.toString();
                        if (v.startsWith("{")) {
                            xml.append("<").append(k).append(">")
                               .append(toXmlObject(v))
                               .append("</").append(k).append(">");
                        } else {
                            xml.append("<").append(k).append(">")
                               .append(v.replaceAll("\"", ""))
                               .append("</").append(k).append(">");
                        }
                        key.setLength(0);
                        value.setLength(0);
                        inKey = true;
                        if (i + 1 < inner.length() && inner.charAt(i + 1) == ',') i++;
                    }
                }
            }
        }

        return xml.toString();
    }

}
