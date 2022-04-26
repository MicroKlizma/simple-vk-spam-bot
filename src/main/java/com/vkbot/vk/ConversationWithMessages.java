package com.vkbot.vk;


import com.vk.api.sdk.objects.messages.Message;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationWithMessages {
    private final URI conversationImageURI;
    private final String name;



    private final int peerId;
    private int sortId;

    private final Map<Integer, Member> members = new HashMap<>();
    private List<Message> messages = new ArrayList<>();



    public ConversationWithMessages(String name, int peerId, URI conversationImageURI, int sortId) {
        this.name = name;
        this.peerId = peerId;
        this.conversationImageURI = conversationImageURI;
        this.sortId = sortId;
    }


    public boolean containsMember(int fromId) {
        return members.containsKey(fromId);
    }

    public void addMember(int fromId, Member member) {
        members.put(fromId, member);
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

    public Member getMember(int fromId) {
        return members.get(fromId);
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
    public int getPeerId() {
        return peerId;
    }

}



