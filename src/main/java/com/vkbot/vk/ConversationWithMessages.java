package com.vkbot.vk;


import com.vk.api.sdk.objects.messages.Message;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ConversationWithMessages {
    private final URI conversationImageURI;
    private final String name;
    private final int id;
    private int sortId;

    private List<Message> messages = new ArrayList<>();



    public ConversationWithMessages(String name, int id, URI conversationImageURI, int sortId) {
        this.name = name;
        this.id = id;
        this.conversationImageURI = conversationImageURI;
        this.sortId = sortId;
    }



    public URI getConversationImageURI() {
        return conversationImageURI;
    }

    public String getName() {
        return name;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addMessage(List<Message> messages) {
        this.messages.addAll(messages);
    }

    public void addMessageToConversationStart(Message message) {
        messages.add(0, message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }
}
