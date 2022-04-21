package com.vkbot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindUtil {

    public static String valueFinder(String word, String string) {
        Pattern pattern = Pattern.compile(word);
        Matcher matcher = pattern.matcher(string);

        String result = null;
        while (matcher.find()) {
            result = string.substring(matcher.start(), matcher.end() - 1);
        }
        if (result != null) {
            int index = result.indexOf("=");
            if (index != -1) {
                result = result.substring(index + 1);
            }
        }

        return result;
    }

    public static String matchesFinder(String start, String end, String string) {
        Pattern pattern = Pattern.compile(start + ".*" + end);
        Matcher matcher = pattern.matcher(string);

        String result = null;
        while (matcher.find()) {
            result = string.substring(matcher.start(), matcher.end() - 1);
        }

        return result;
    }

    public static String getToken(String start, String end, String content) {
        Pattern pattern = Pattern.compile(start + "[a-zA-Z0-9]*" + end);
        Matcher matcher = pattern.matcher(content);

        String result = null;
        while (matcher.find()) {
            result = content.substring(matcher.start(), matcher.end());
        }
        if (result != null) {
            int index = result.indexOf("=");
            if (index != -1) {
                result = result.substring(index + 1);
            }
        } else {
            result = "";
        }

        return result;
    }

    public static String getLink(String response) {
        Pattern pattern = Pattern.compile("http.*&ip_h.*com");
        Matcher matcher = pattern.matcher(response);
        String result = null;
        while (matcher.find()) {
            result = response.substring(matcher.start(), matcher.end());
        }
        return result;
    }


}
