package com.vkbot.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private final CloseableHttpClient httpclient;
    private final HttpClientContext context;
    private static Client client;


    private Client() {
        httpclient = HttpClients.custom().
                setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).
                build();

        context = HttpClientContext.create();
    }

    public static Client getInstance() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }
    public Response doGet(String URI) {
        if (URI == null) return null;
        final HttpGet httpGet = new HttpGet(URI);
        Response simpleResponse = null;

        try ( CloseableHttpResponse response = httpclient.execute(httpGet, context) ) {
            byte[] entityAsBytes = response.getEntity().getContent().readAllBytes();
            simpleResponse = new Response(response.getAllHeaders(), entityAsBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return simpleResponse;
    }

    public Response doPost(String URI, Request request) {
        request.setMethod("POST");
        if (URI == null) return null;
        request.setURI(java.net.URI.create(URI));
        List<BasicNameValuePair> params = request.getPairsFromEntity();

        //set entity
        if (params == null) {
            params = new ArrayList<>();
            params.add(new BasicNameValuePair("", ""));
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(params));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Response simpleResponse = null;
        try ( CloseableHttpResponse response = httpclient.execute(request, context) ) {
            byte[] entityAsBytes = response.getEntity().getContent().readAllBytes();
            simpleResponse = new Response(response.getAllHeaders(), entityAsBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return simpleResponse;
    }

    public void showCookies() {
        try {
            CookieStore cookieStore = context.getCookieStore();
            List<Cookie> cookies = cookieStore.getCookies();
            System.out.println("Cookies:");
            for (Cookie c : cookies) {
                System.out.printf("%s: %s\n", c.getName(), c.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
