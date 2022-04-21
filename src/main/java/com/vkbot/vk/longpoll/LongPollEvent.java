package com.vkbot.vk.longpoll;

import com.google.gson.JsonObject;
import com.vk.api.sdk.objects.messages.Message;

import java.util.List;

public class LongPollEvent {


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public LongPollEventType getType() {
        return type;
    }

    public void setType(LongPollEventType type) {
        this.type = type;
    }

    private LongPollEventType type;
    private Message message;


}

enum LongPollEventType {
    MESSAGE_NEW
}
