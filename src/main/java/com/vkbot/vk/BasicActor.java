package com.vkbot.vk;


import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.objects.messages.responses.GetConversationsByIdResponse;
import com.vk.api.sdk.objects.messages.responses.GetConversationsResponse;
import com.vk.api.sdk.objects.messages.responses.GetLongPollHistoryResponse;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import com.vk.api.sdk.queries.messages.MessagesGetConversationsByIdQuery;
import com.vkbot.httpclient.Client;
import com.vkbot.observer.Observable;
import com.vkbot.observer.Observer;
import com.vkbot.vk.longpoll.UserLongPollApi;

import java.io.InputStream;
import java.net.URI;
import java.util.*;


public class BasicActor implements Observable {
    private static BasicActor basicActor;
    private final UserActor actor;
    private final VkApiClient vkClient;

    private final Map<Integer, ConversationWithMessages> conversations = new HashMap<>();

    private Observer observer;


    private BasicActor(int actorId, String token) {
        TransportClient transportClient = new HttpTransportClient();
        vkClient = new VkApiClient(transportClient);

        actor = new UserActor(actorId, token);
        setConversations();
        Thread listenerThread = new Thread(this::messagesListener);
        listenerThread.start();
    }


    public List<ConversationWithMessages> getSortedConversations() {
        List<ConversationWithMessages> conversationsList = new ArrayList<>(conversations.values());
        conversationsList.sort( (ConversationWithMessages a, ConversationWithMessages b) -> b.getSortId() - a.getSortId() );

        return conversationsList;
    }


    private Conversation doConversationRequest(int peerId) {
        MessagesGetConversationsByIdQuery conversationsById = new MessagesGetConversationsByIdQuery(vkClient, actor, peerId);
        try {
            GetConversationsByIdResponse response = conversationsById.execute();
            List<Conversation> list = response.getItems();
            if (list != null) {
                return list.get(0);
            }
        } catch (ApiException | ClientException e) {
            e.printStackTrace();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            doConversationRequest(peerId);
        }

        return null;
    }

    //todo attachments
    private GetLongPollHistoryResponse messagesListener() {
        new UserLongPollApi() {
            @Override
            protected void messageNew(Message message) {
                int peerId = message.getPeerId();
                int sortId = message.getId();

                //update conversations
                if (!conversations.containsKey(peerId)) {
                    Conversation conversation = doConversationRequest(peerId);

                    assert conversation != null;
                    addToConversationsMap(conversation);
                }
                conversations.get(peerId).setSortId(sortId);

                //update messages
                conversations.get(peerId).addMessageToConversationStart(message);
                notifyObserver();
            }
        }.run(actor);

//        int ts = 0;
//        try {
//            ts = vkClient.messages().getLongPollServer(actor).lpVersion(3).execute().getTs();
//        } catch (ApiException | ClientException e) {
//            e.printStackTrace();
//        }
//
//        while (true) {
//
//            try {
//                GetLongPollHistoryResponse historyResponse = new GetLongPollHistoryResponse();
//                MessagesGetLongPollServerQuery ms = new MessagesGetLongPollServerQuery(vkClient, actor);
////                ms.
////                vkClient.e
////                historyResponse
//                ts = vkClient.messages().getLongPollServer(actor).lpVersion(3).execute().getTs();
//
//                if (!historyResponse.getMessages().getItems().isEmpty()) {
//
//                        //update conversations
//                        List<Conversation> conversationsResponse = historyResponse.getConversations();
//                        for (Conversation conversation : conversationsResponse) {
//                            int peerId = conversation.getPeer().getId();
//                            int sortId = conversation.getSortId().getMinorId();
//
//                            if (!conversations.containsKey(peerId)) {
//                                addToConversationsMap(conversation);
//                            }
//
//                            conversations.get(peerId).setSortId(sortId);
//                        }
//
//                        //update messages
//                        List<Message> messages = historyResponse.getMessages().getItems();
//                        for (Message message : messages) {
//                            int peerId = message.getPeerId();
//                            conversations.get(peerId).addMessageToConversationStart(message);
//                        }
//                        notifyObserver();
//                    }
//
//                Thread.sleep(1000);
//            } catch (ApiException | ClientException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//

        return null;
    }

    //todo implement List<>
    private void addToConversationsMap(Conversation conversation) {
        try {
            ConversationPeer peer = conversation.getPeer();
            int sortId = conversation.getSortId().getMinorId();
            int peerId = peer.getId();

            //add conversation name, sort peerId and image uri to current user's conversation list
            switch (peer.getType()) {
                case CHAT:
                    Thread.sleep(200);
                    ChatSettings chatSettings = conversation.getChatSettings();
                    conversations.put(peerId, new ConversationWithMessages(chatSettings.getTitle(),
                            peerId,
                            chatSettings.getPhoto().getPhoto50(),
                            sortId));
                    break;
                case GROUP:
                    Thread.sleep(200);
                    List<GetByIdObjectLegacyResponse> groups = vkClient.groups().getByIdObjectLegacy(actor).groupId(peer.getLocalId() + "").execute();
                    for (GetByIdObjectLegacyResponse group : groups) {
                        conversations.put(peerId, new ConversationWithMessages(group.getName(), peerId, group.getPhoto50(), sortId));
                    }
                    break;
                case USER:
                    Thread.sleep(200);
                    List<GetResponse> list = vkClient.users().get(actor).userIds(peer.getLocalId() + "").fields(Fields.PHOTO_50).execute();
                    for (GetResponse res : list) {
                        conversations.put(peerId, new ConversationWithMessages(res.getFirstName() + " " + res.getLastName(),
                                peerId, res.getPhoto50(), sortId));
                    }
                    break;
            }

            //add message history in conversation
            List<Message> messages = vkClient.messages().getHistory(actor).peerId(peerId).execute().getItems();
            conversations.get(peerId).addMessage(messages);

        } catch (InterruptedException | ClientException | ApiException e) {
            e.printStackTrace();
        }
    }


    public static BasicActor getInstance(int actorId, String token) {
        if (basicActor != null) {
            return basicActor;
        }
        basicActor = new BasicActor(actorId, token);
        return basicActor;
    }

    public static BasicActor getInstance() {
        if (basicActor != null) {
            return basicActor;
        }
        System.out.println("Basic actor is not initialized");
        return null;
    }


//    public ConversationWithMessages getConversation(int id) {
//        if (conversations.containsKey(id)) {
//            return conversations.get(id);
//        }
//
//        try {
//            GetConversationsByIdResponse a = vkClient.messages().getConversationsById(actor, id).execute();
//            ConversationWithMessage a = new Conversation();
//            a.get
//        } catch (ApiException | ClientException e) {
//            e.printStackTrace();
//        }
//
//
//    }


    //todo 1 request, delete all thread.sleep, rename method
    public void setConversations() {
        try {
            GetConversationsResponse response = vkClient.messages().getConversations(actor).execute();
            for (ConversationWithMessage conversationsWithMessage : response.getItems()) {
                addToConversationsMap(conversationsWithMessage.getConversation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setMembersImages(Set<String> fromIds, ConversationWithMessages conversation) {
        try {
            Client client = Client.getInstance();
            List<String> listedFromIds = new ArrayList<>(fromIds);
            List<GetResponse> responses = vkClient.users().get(actor).userIds(listedFromIds).fields(Fields.PHOTO_50).execute();


            for (int i = 0; i < listedFromIds.size(); i++) {
                GetResponse response = responses.get(i);
                byte[] imageBytes = client.doGet(response.getPhoto50().toString()).getInputStream().readAllBytes();
                int fromId = Integer.parseInt(listedFromIds.get(i));

                Member member = new Member(fromId);
                member.setImage(imageBytes);
                conversation.addMember(fromId, member);
            }

        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }

    }

    public Map<Integer, ConversationWithMessages> getConversations() {
        return conversations;
    }

    @Override
    public void addObserver(Observer observer) {
        this.observer = observer;
    }

    @Override
    public void notifyObserver() {
        observer.handleEvent();
    }
}
