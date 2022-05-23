package com.vkbot.vk.longpoll;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.LongPollServer;
import com.vk.api.sdk.objects.messages.responses.GetLongPollServerResponse;
import org.apache.http.ConnectionClosedException;

import java.util.concurrent.Executors;

public abstract class UserLongPollApi extends UpdatesHandler {

    TransportClient transportClient = HttpTransportClient.getInstance();
    VkApiClient client = new VkApiClient(transportClient);
    boolean isRunning;

    private void handleUpdates(LongPollServer lpServer, UserActor actor) throws ConnectionClosedException {
        isRunning = true;
        try {
            String ts = lpServer.getTs();

            while (isRunning) {
                GetLongPollQuery query = new GetLongPollQuery(client, lpServer.getServer(), lpServer.getKey(), ts);
                query.mode(10).waitTime(25).version(14);

                GetLongPollResponse response = query.execute();
                ts = response.getTs();

                for (LongPollEvent event : response.getLongPollEvents(actor)) {
                    parse(event);
                }
            }
        } catch (ApiException | ClientException e) {
            /*
            Actually instead of GetLongPollEventsResponse there might be returned error like:
            {"failed":1,"ts":30} or {"failed":2}, but it directly handled in execute() method.
            There are 2 ways: deserialize manually response from string OR do reconnection in each
            error case. There is second way - keep use typed object and reconnect when any error.
            */
            throw new ConnectionClosedException();
        }
        isRunning = false;
    }


    public void run(UserActor actor) {
        Executors.newSingleThreadExecutor().execute(
                () -> {
                    try {
                        LongPollServer lpServer = new LongPollServer();
                        GetLongPollServerResponse response = client.messages().getLongPollServer(actor).execute();
                        lpServer.setServer("https://" + response.getServer());
                        lpServer.setKey(response.getKey());
                        lpServer.setTs(response.getTs() + "");

                        handleUpdates(lpServer, actor);
                    } catch (ConnectionClosedException e) {
                        run(actor);
                    } catch (ClientException | ApiException e) {
                        e.printStackTrace();
                    }
                }
        );

    }
}
