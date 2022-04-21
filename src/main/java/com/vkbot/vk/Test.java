package com.vkbot.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetLongPollServerResponse;
import com.vkbot.vk.longpoll.LongPollEvent;
import com.vkbot.vk.longpoll.UserLongPollApi;

public class Test {
    private static Object LongPollEventType;

    public static void main(String[] args) throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = HttpTransportClient.getInstance();

        VkApiClient vk = new VkApiClient(transportClient);
        UserActor actor = new UserActor(258252603, "ed4d514739fa8769f09221761ec8845f453314b9724ff39614c64f2b4d5c1815262828ae679e92babfb69");
        GetLongPollServerResponse server = vk.messages().getLongPollServer(actor).lpVersion(3).execute();

        int ts = server.getTs();


//        UserLongPollApi api = new UserLongPollApi(vk, actor, 1, 25) {
//            @Override
//            protected void messageNew(Integer groupId, Message message) {
//                System.out.println("зп пришла!!");
//            }
//        };
        UserLongPollApi api = new UserLongPollApi() {
            @Override
            public void messageNew(Message message) {
                System.out.println(message.getText());
                System.out.println(message.getFromId());
                System.out.println(message.getPeerId());
                System.out.println(message.getId());
            }

        };
        api.run(actor);



//        api.run();
//        while (true) {
//            Thread.sleep(100);
//
//        }


//        MessagesGetLongPollHistoryQuery query = vk.messages().getLongPollHistory(actor).lpVersion(3).unsafeParam("wait", "25").ts(ts);
//        GetLongPollHistoryResponse response = query.execute();
//        System.out.println(response);


//        Client c = new Client();
//        Response ab = c.doGet("https://im.vk.com/nim258252603?act=a_check&key=1b4f42efcbf64ec9975c22570788847ed084454e&ts=1863717145&wait=25&mode=2&version=3");
//        ab.showEntity();
    }

}
