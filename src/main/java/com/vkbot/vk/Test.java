package com.vkbot.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetLongPollServerResponse;
import com.vk.api.sdk.objects.photos.Image;
import com.vk.api.sdk.objects.photos.responses.GetByIdLegacyResponse;
import com.vkbot.vk.longpoll.LongPollEvent;
import com.vkbot.vk.longpoll.UserLongPollApi;

import java.util.List;

public class Test {
    private static Object LongPollEventType;

    public static void main(String[] args) throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = HttpTransportClient.getInstance();

        VkApiClient vk = new VkApiClient(transportClient);
        UserActor actor = new UserActor(258252603, "c9cb1bb38afd58718fb031e414d1bcd377f2ecb2234704c0c393299502b58e8a6e054717c48a5dc8b5cfe");
        Message mes = vk.messages().getById(actor, 258252603, 2216348).execute().getItems().get(0);

        GetLongPollServerResponse server = vk.messages().getLongPollServer(actor).lpVersion(3).execute();
        List<GetByIdLegacyResponse> resp = vk.photos().getByIdLegacy(actor, "258252603_457273092").execute();
        Image image = resp.get(0).getImages().get(0);

        int ts = server.getTs();


//        UserLongPollApi api = new UserLongPollApi(vk, actor, 1, 25) {
//            @Override
//            protected void messageNew(Integer groupId, Message message) {
//                System.out.println("зп пришла!!");
//            }
//        };
//        UserLongPollApi api = new UserLongPollApi() {
//            @Override
//            public void messageNew(Message message) {
//                System.out.println(message.getText());
//                System.out.println(message.getFromId());
//                System.out.println(message.getPeerId());
//                System.out.println(message.getId());
//            }
//
//        };
//        api.run(actor);



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
