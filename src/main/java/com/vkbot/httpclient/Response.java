package com.vkbot.httpclient;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;


public class Response {

    private final Header[] headers;
    private String entity;
    private final byte[] entityAsBytes;

    public Response(Header[] headers, byte[] entityAsBytes) {
        this.headers = headers;
        this.entityAsBytes = entityAsBytes;
        if (entityAsBytes.length != 0) {
            entity = new String(this.entityAsBytes, StandardCharsets.UTF_8);
        } else {
            entity = new String("");
        }
    }

    public void showHeaders() {
        for (Header header : headers) {
            System.out.println(header.getName() + ": " + header.getValue());
        }
    }

    public void showEntity() {
        System.out.println(entity);
    }
    public Header[] getHeaders() {
        return headers;
    }

    public byte[] getEntityAsBytes() {
        return entityAsBytes;
    }

    public String getEntityAsString() {
        if (entity == null) {
            return "";
        } else {
            return entity;
        }
    }
    public ByteArrayInputStream getInputStream() {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(entityAsBytes)) {
            return byteArrayInputStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
