package com.vkbot.vk.longpoll;

import com.vk.api.sdk.objects.messages.Message;

public abstract class UpdatesHandler {

    protected void parse(LongPollEvent event) {
        switch (event.getType()) {
            case MESSAGE_NEW:
                messageNew(event.getMessage());
                break;
        }

    }

    protected abstract void messageNew(Message message);
}
