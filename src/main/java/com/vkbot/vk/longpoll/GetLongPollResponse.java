package com.vkbot.vk.longpoll;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.messages.Message;
import com.vkbot.vk.BasicActor;

import java.util.ArrayList;
import java.util.List;

public class GetLongPollResponse {

    @SerializedName("ts")
    private String ts;

    @SerializedName("updates")
    private List<JsonArray> updates;

    private List<LongPollEvent> events;



    public String getTs() {
        return ts;
    }

    public List<LongPollEvent> getLongPollEvents(UserActor actor) {
        if (events != null) {
            return events;
        }

        events = new ArrayList<>();

        for (JsonArray array : updates) {
            if (array.get(0).getAsInt() == 4) {
                parseNewMessage(array, actor);
            }
        }


        return events;
    }

    private void parseNewMessage(JsonArray messageArray, UserActor actor) {
        LongPollEvent event = new LongPollEvent();

        Message message = new Message();
        event.setType(LongPollEventType.MESSAGE_NEW);

        message.setId( messageArray.get(1).getAsInt() );
        message.setPeerId( messageArray.get(4).getAsInt() );
        message.setText( messageArray.get(6).getAsString() );

        JsonElement from = messageArray.get(7).getAsJsonObject().get("from");
        if (from != null) {
            message.setFromId(Integer.valueOf(from.getAsString().replaceAll("[0-9]^", "")));
        } else {
            int flag = messageArray.get(2).getAsInt() & 2;
            //outgoing message
            if (flag == 2) {
                message.setFromId(actor.getId());
            } else { //incoming message
                message.setFromId(message.getPeerId());
            }
        }
        event.setMessage(message);
        events.add(event);
        //todo attachments

    }
}

