package com.vkbot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.responses.GetLongPollServerResponse;
import com.vkbot.httpclient.Client;
import com.vkbot.httpclient.Response;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Kek extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Kek.class.getResource("ui/chat.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("VK BOT");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws ClientException, ApiException {

        TransportClient client = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(client);
        UserActor actor = new UserActor(258252603, "e97a6cd5d7d7d2a16c4721a41bbff1f0c6861ed00721debab0b93ee8f2eac2144cc04345efb833b989a86");
        GetLongPollServerResponse response = vk.messages().getLongPollServer(actor).execute();
        String url = "https://" + response.getServer() + "?act=a_check&key=" + response.getKey() + "&mode=10&ts=" + response.getTs() + "&version=14&wait=50";
        Client c = Client.getInstance();
        Response resp = c.doGet(url);

//        launch();
        String str = resp.getEntityAsString();
        System.out.println(str.replaceAll("[^0-9]", ""));
    }
}
