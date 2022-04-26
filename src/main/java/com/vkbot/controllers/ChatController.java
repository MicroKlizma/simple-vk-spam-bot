package com.vkbot.controllers;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import com.vkbot.httpclient.Client;
import com.vkbot.httpclient.Response;
import com.vkbot.observer.Observer;
import com.vkbot.vk.BasicActor;
import com.vkbot.vk.ConversationWithMessages;
import com.vkbot.vk.Member;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable, Observer {

    @FXML
    private ScrollPane messagesScrollPane;
    @FXML
    private VBox conversationsList;
    @FXML
    private VBox messageList;

    private ConversationWithMessages currentConversation;
    private BasicActor actor;
    private final Client client;

    public ChatController() {
        client = Client.getInstance();
    }


    private void displayMessages(ConversationWithMessages conversation) {
        List<String> members = new ArrayList<>();

        for (Message message : conversation.getMessages()) {
            int fromId = message.getFromId();

            if (!conversation.containsMember(fromId)) { //add user image to message if not added yet
                Member member = new Member(fromId);
                if (fromId == conversation.getPeerId()) {
                    byte[] imageBytes = client.doGet(conversation.getConversationImageURI().toString()).getInputStream().readAllBytes();
                    member.setImage(imageBytes);
                    conversation.addMember(fromId, member);
                } else {
                    members.add("" + fromId);
                }
            }
        }
        actor.getUserImages(members, conversation);

        for (Message message : conversation.getMessages()) {


//            textArea.setWrapText(true);
//            System.out.println(textArea.);
//            ScrollBar scrollBar = new ScrollBar();
//            scrollBar = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
//            scrollBar.setDisable(true);
//            WebView webView = new WebView();
//            WebEngine webEngine = webView.getEngine();
//            webEngine.loadContent("<body style='background : rgba(0,0,0,0);font-size: 70px;padding: 10;attachType-align:center;'>Test Transparent</body>");
//            webView.setStyle("-fx-background-color: red");
//            webView.setPrefHeight(100);
//            TextArea
//            messageList.getChildren().add(0, webView);

            AnchorPane messagePane = new AnchorPane();
            messagePane.setMinHeight(40);

            StringBuilder attachType = new StringBuilder("");
            List<MessageAttachment> attachments = message.getAttachments();
            if (attachments != null) {
                for (MessageAttachment attachment : attachments) {
                    switch (attachment.getType()) {
                        case PHOTO:
                            attachType.append("<ФОТО>");
                            break;
                        case AUDIO_MESSAGE:
                            attachType.append("<ГОЛОСОВОЕ СООБЩЕНИЕ>");
                            break;
                        case WALL:
                            attachType.append("<ПОСТ>");
                            break;
                        case AUDIO:
                            attachType.append("<МУЗЫКА>");
                            break;
                        case VIDEO:
                            attachType.append("<ВИДЕО>");
                            break;
                        case STICKER:
                            attachType.append("<СТИКЕР>");
                            break;
                        case GRAFFITI:
                            attachType.append("<ГРАФФИТИ>");
                            break;
                        case GIFT:
                            attachType.append("<ПОДАРОК>");
                            break;
                        case DOC:
                            attachType.append("<ДОКУМЕНТ>");
                            break;
                        case ARTICLE:
                            attachType.append("<СТАТЬЯ>");
                            break;
                    }
                }


                //todo reply and forwarded

                if (!attachType.toString().equals("")) {
                    attachType.append(" ");
                }
                Label messageText = new Label(attachType + message.getText());

                messageText.setWrapText(true);

                //add user image to message pane
                ByteArrayInputStream inputStream = new ByteArrayInputStream(conversation.getMember(message.getFromId()).getImageBytes());
                Image image = new Image(inputStream);
                Circle userImage = new Circle(18);
                ImagePattern pattern = new ImagePattern(image);
                userImage.setFill(pattern);


                messagePane.getChildren().add(messageText);
                messagePane.getChildren().add(userImage);

                AnchorPane.setLeftAnchor(messageText, 60.0);
                AnchorPane.setTopAnchor(messageText, 19.0);

                AnchorPane.setLeftAnchor(userImage, 10.0);
                AnchorPane.setTopAnchor(userImage, 10.0);

                messageList.getChildren().add(0, messagePane);
            }
        }


    }

    public void conversationClicked(MouseEvent event) {
        messageList.getChildren().clear();
        AnchorPane anchorPane = (AnchorPane) event.getSource();
        currentConversation = (ConversationWithMessages) anchorPane.getUserData();
        displayMessages(currentConversation);

        Platform.runLater( () -> messagesScrollPane.setVvalue(1) );

    }


    //todo token expired add attachements ?!
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actor = BasicActor.getInstance(258252603, "d8c6ed1849d02d9b504cfaa5906c4b796f05bdbf05a6bd7579f7576f3807e37bc02d10380f0375725bcc6");
        List<ConversationWithMessages> bc = actor.getSortedConversations();
        for (ConversationWithMessages conv : bc) {
            addConversationToConversationsList(conv);
        }

//        conversationsList.applyCss();
//        conversationsList.layout();
//        anchorPane.applyCss();
//        anchorPane.layout();

        actor.addObserver(this);
    }

//    public void addMessage() {
//        anchorPane.
//    }


    private void addConversationToConversationsList(ConversationWithMessages conversation) {
        Response response = client.doGet(conversation.getConversationImageURI().toString());
        Image conversationImage = new Image(response.getInputStream());

        Label nameLabel = new Label();
        nameLabel.setText(conversation.getName());
        nameLabel.setMaxWidth(200.0);

        Circle circleWithImage = new Circle(23);
        ImagePattern pattern = new ImagePattern(conversationImage);
        circleWithImage.setFill(pattern);

        AnchorPane conversationPane = new AnchorPane();
        conversationPane.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: black");
        conversationPane.setUserData(conversation);
        conversationPane.getChildren().add(circleWithImage);
        conversationPane.getChildren().add(nameLabel);
        conversationPane.setPadding(new Insets(2, 0, 2, 0));
        conversationPane.setOnMouseClicked(this::conversationClicked);
        conversationPane.applyCss();
        conversationPane.layout();

        AnchorPane.setLeftAnchor(circleWithImage, 10.0);
        AnchorPane.setTopAnchor(circleWithImage, 10.0);
        AnchorPane.setLeftAnchor(nameLabel, 72.0);
        AnchorPane.setTopAnchor(nameLabel, 23.0);

        conversationsList.getChildren().add(conversationPane);
    }

    private void updateMessages() {
        Platform.runLater( () -> messageList.getChildren().clear() );

        if (currentConversation != null) {
            Platform.runLater( () -> displayMessages(currentConversation));
        }

        Platform.runLater( () -> messagesScrollPane.setVvalue(1) );
    }

    private void updateConversations() {
        Platform.runLater( () -> conversationsList.getChildren().clear() );
        List<ConversationWithMessages> sortedConversations = actor.getSortedConversations();
        for (ConversationWithMessages conversation : sortedConversations) {
            Platform.runLater( () -> addConversationToConversationsList(conversation) );
        }
    }

    @Override
    public void handleEvent() {
        updateMessages();
        updateConversations();
    }

}

