package com.adsforce.sdk.utils;

import android.text.TextUtils;

import com.adsforce.sdk.security.AESUtils;
import com.adsforce.sdk.security.Md5Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by t.wang on 2018/4/17.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class Utils {
    public static ArrayList getShuffleList(Collection collection) {
        if (collection == null) {
            return new ArrayList();
        } else {
            ArrayList list = new ArrayList(collection);
            Collections.shuffle(list);
            return list;
        }
    }

    public static boolean isUrl(String pInput) {
        if (pInput == null) {
            return false;
        }
        String regEx = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]" +
                "+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))" +
                "(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(pInput);
        return matcher.matches();
    }

    public static String convertMapToString(Map<String, String> parameters) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, String>> es = parameters.entrySet();
        Iterator<Map.Entry<String, String>> it = es.iterator();

        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String k = entry.getKey();
            String v = entry.getValue();
            if (!TextUtils.isEmpty(v)) {
                if (!hasUrlEncoded(v)) {
                    try {
                        v = URLEncoder.encode(v, Constants.ENCODE_UTF8);
                    } catch (UnsupportedEncodingException e) {
                        v = "";
                    }
                }
                sb.append("&" + k + "=" + v);
            } else {
                sb.append("&" + k + "=" + "");
            }
        }

        String string = sb.toString().replaceFirst("&", "");
        return string;
    }

    public static String convertMapToJson(Map<String, String> parameters) {
        try {
            JSONObject jsonObject = new JSONObject();
            Set<Map.Entry<String, String>> es = parameters.entrySet();
            Iterator<Map.Entry<String, String>> it = es.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String k = entry.getKey();
                String v = entry.getValue();
                jsonObject.put(k, v);
            }
            return URLEncoder.encode(jsonObject.toString(), Constants.ENCODE_UTF8);
        } catch (Throwable throwable) {

        }
        return "";
    }

    public static String convertListToJson(ArrayList<String> list) {
        try {
            JSONArray jsonArray = new JSONArray(list);
            return URLEncoder.encode(jsonArray.toString(), Constants.ENCODE_UTF8);
        } catch (Throwable throwable) {

        }
        return "";
    }

    public static String getMd5Uuid() {
        UUID uuid = UUID.randomUUID();
        try {
            return Md5Utils.textOfMd5(uuid.toString());
        } catch (Throwable throwable) {
            try {
                return AESUtils.getRandomString(16);
            } catch (Throwable t) {

            }
        }
        return "";
    }

    public static boolean hasClass(String name) {
        try {
            Class.forName(name);
        } catch (Throwable throwable) {
            return false;
        }
        return true;
    }

    public static String getEncodeReferrer(String referrer) {
        String encodeReferrer = "";
        if (!TextUtils.isEmpty(referrer)) {
            boolean isReferrerEncoded = Utils.hasUrlEncoded(referrer);
            if (!isReferrerEncoded) {
                try {
                    encodeReferrer = URLEncoder.encode(referrer, Constants.ENCODE_UTF8);
                } catch (Throwable throwable) {

                }
            }
        }

        return encodeReferrer;
    }

    private static boolean hasUrlEncoded(String str) {
        BitSet dontNeedEncoding = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            dontNeedEncoding.set(i);
        }
        dontNeedEncoding.set('+');
        dontNeedEncoding.set('-');
        dontNeedEncoding.set('_');
        dontNeedEncoding.set('.');
        dontNeedEncoding.set('*');

        boolean needEncode = false;
        for (i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (dontNeedEncoding.get((int) c)) {
                continue;
            }
            if (c == '%' && (i + 2) < str.length()) {
                char c1 = str.charAt(++i);
                char c2 = str.charAt(++i);
                if (isDigit16Char(c1) && isDigit16Char(c2)) {
                    continue;
                }
            }
            needEncode = true;
            break;
        }

        return !needEncode;
    }

    private static boolean isDigit16Char(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F');
    }
}
