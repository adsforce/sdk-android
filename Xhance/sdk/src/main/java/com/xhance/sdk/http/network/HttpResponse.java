package com.xhance.sdk.http.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class HttpResponse {

    private static HttpURLConnection sConn;

    public HttpResponse(HttpURLConnection conn) throws Exception {
        if (conn == null)
            throw new IOException("HttpURLConnection is null");
        this.sConn = conn;
    }

    public int getResponseCode() throws Exception {
        int code = sConn.getResponseCode();

        return code;
    }

    public String getBody() throws Exception {
        String body = "";
        try {
            InputStream is = sConn.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int size = 1024;
            byte[] bf = new byte[size];
            int count;
            while ((count = is.read(bf)) != -1) {
                os.write(bf, 0, count);
            }
            body = os.toString("UTF-8");
            is.close();
            os.close();
        } catch (Throwable e) {

        } finally {
            free();
        }

        return body;
    }

    public void free() {
        try {
            sConn.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
