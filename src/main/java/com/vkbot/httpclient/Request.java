package com.vkbot.httpclient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class Request extends HttpEntityEnclosingRequestBase {
    private final List<BasicNameValuePair> entity = new ArrayList<>();
    private String method;


    public Request(String URI) {
        this.setURI(java.net.URI.create(URI));
    }
    public Request() {

    }

    public void addHeader(String name, String value) {
        this.addHeader(new BasicHeader(name, value));
    }


    public List<BasicNameValuePair> getPairsFromEntity() {
        return this.entity;
    }

    public void addPairToEntity(String name, String value) {
        this.entity.add(new BasicNameValuePair(name, value));
    }

    public String getEntityInString() {
        HttpEntity entity = this.getEntity();
        String s = "";
        try {
            s = EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public void showHeaders() {
        Header[] headers = this.getAllHeaders();
        for (Header h : headers) {
            System.out.printf("%s: %s", h.getName(), h.getValue());
        }
    }

    @Override
    public String getMethod() {
        return this.method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
}
