package com.vkbot.vk;

import com.vkbot.httpclient.Client;
import com.vkbot.httpclient.Request;
import com.vkbot.httpclient.Response;
import com.vkbot.util.FindUtil;
import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class VkAuthorization {
    private final Client client;
    private long captchaSid;
    private String loginLink;

    public VkAuthorization() {
        client = Client.getInstance();
    }

     /*
     returns
     -1 if email or password or captcha value are incorrect
      0 if captcha needed
      1 if authentication is successful
     */
    public byte login(String email, String pass) {
        //getting login params
        Response page = client.doGet("https://m.vk.com");
        loginLink = FindUtil.getLink(page.getEntityAsString());

        Request request = new Request();
        request.addPairToEntity("email", email);
        request.addPairToEntity("pass", pass);
        request.addHeader("origin", "https://m.vk.com");

        //login.vk.com http request
        Response response = client.doPost(loginLink, request);

        String locationLink = "";
        for (Header h : response.getHeaders()) {
            if (h.getName().equalsIgnoreCase("location")) {
                locationLink = h.getValue();
                break;
            }
        }

        if ( locationLink.contains("&email=") ) {
            Response pageWithCaptcha = client.doGet(locationLink);
            String captchaLink = FindUtil.matchesFinder("captcha.php", "[0-9]\"", pageWithCaptcha.getEntityAsString());

            if (captchaLink != null) {
                String sid = captchaLink.substring(captchaLink.lastIndexOf("=") + 1);
                captchaSid = Long.parseLong(sid);

                captchaLink = "https://m.vk.com/" + captchaLink;
                Response captchaImage = client.doGet(captchaLink);
                downloadCaptcha(captchaImage);
                return 0;
            } else {
                return -1;
            }

        } else if ( locationLink.contains("_hash=") ) {
            return 1;
        } else {
            return -1;
        }
    }

    public int getId() {
        int id;
        String token; //TODO get user id for initializing actor
        Request req = new Request();
        Response info = client.doPost("https://api.vk.com/method/users.get?access_token=&v=5.131", req);
//        id = info.getJsonProperty(id);
        return 0;
    }
    public byte loginWithCaptcha(String email, String pass, String captchaKey) {
        Request request = new Request();
        request.addPairToEntity("email", email);
        request.addPairToEntity("pass", pass);
        request.addPairToEntity("captcha_sid", captchaSid + "");
        request.addPairToEntity("captcha_key", captchaKey);
        request.addHeader("origin", "https://m.vk.com");

        //login.vk.com http request
        Response response = client.doPost(loginLink, request);

        String locationLink = "";
        for (Header h : response.getHeaders()) {
            if (h.getName().equalsIgnoreCase("location")) {
                locationLink = h.getValue();
                break;
            }
        }

        if ( locationLink.contains("&email=") ) {
            Response pageWithCaptcha = client.doGet(locationLink);
            String captchaLink = FindUtil.matchesFinder("captcha.php", "[0-9]\"", pageWithCaptcha.getEntityAsString());

            if (captchaLink != null) {
                String sid = captchaLink.substring(captchaLink.lastIndexOf("=") + 1);
                captchaSid = Long.parseLong(sid);

                captchaLink = "https://m.vk.com/" + captchaLink;
                Response captchaImage = client.doGet(captchaLink);
                downloadCaptcha(captchaImage);
                return 0;
            } else {
                return -1;
            }

        } else if ( locationLink.contains("_hash=") ) {
            return 1;
        } else {
            return -1;
        }
    }


    public String getAccessToken() {
        String clientId = "6287487";
        Request tokenRequest = new Request("https://login.vk.com/?act=web_token");
        tokenRequest.addPairToEntity("version", "1");
        tokenRequest.addPairToEntity("app_id", clientId);
        tokenRequest.addPairToEntity("access_token", "");
        tokenRequest.addHeader("content-type", "application/x-www-form-urlencoded");
        tokenRequest.addHeader("origin", "https://vk.com");

        Response tokenResponse = client.doPost("https://login.vk.com/?act=web_token", tokenRequest);
        String entity = tokenResponse.getEntityAsString();

        String token = FindUtil.getToken("\"access_token\":\"", "\"", entity);
        int index = token.indexOf(":");
        if (index != -1) {
            token = token.substring(token.indexOf(":") + 1);
            token = token.replaceAll("\"", "");
        }
        return token;
    }


    private void downloadCaptcha(Response response) {
        byte[] bytes = response.getEntityAsBytes();
        File captchaImage = new File("src/main/resources/com/vkbot/temp/captcha.jpg");
        try (OutputStream os = new FileOutputStream(captchaImage)) {
            os.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
