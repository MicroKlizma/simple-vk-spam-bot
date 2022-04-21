package com.vkbot.controllers;

import com.vk.api.sdk.objects.messages.Message;
import com.vkbot.httpclient.Client;
import com.vkbot.httpclient.Response;
import com.vkbot.observer.Observer;
import com.vkbot.vk.BasicActor;
import com.vkbot.vk.ConversationWithMessages;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable, Observer {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox conversationsList;
    @FXML
    private VBox messageList;

    @FXML
    private Button udali;

    private ConversationWithMessages currentConversation;
    private BasicActor actor;

    public ChatController() {

    }

    public void action(MouseEvent event) {
        messageList.getChildren().clear();
        AnchorPane anchorPane = (AnchorPane) event.getSource();
        currentConversation = (ConversationWithMessages) anchorPane.getUserData();

        for (Message message : currentConversation.getMessages()) {
            TextArea textArea = new TextArea(message.getText());
            messageList.getChildren().add(textArea);
        }

    }


    //todo token expired add attachements ?!
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actor = BasicActor.getInstance(258252603, "ed4d514739fa8769f09221761ec8845f453314b9724ff39614c64f2b4d5c1815262828ae679e92babfb69");
        List<ConversationWithMessages> bc = actor.getSortedConversations();
        for (ConversationWithMessages conv : bc) {
            addConversationToConversationsList(conv);
//            conv.getMessages().get(0).getAttachments().get(0).getPhoto().
        }
//        conversationsList.layout();
        conversationsList.applyCss();
        conversationsList.layout();
        //todo add border or learn css!
        anchorPane.applyCss();
        anchorPane.layout();
        System.out.println(conversationsList.getHeight());
//        anchorPane.setPrefHeight(conversationsList.getHeight());
        actor.addObserver(this);

    }

//    public void addMessage() {
//        anchorPane.
//    }


    public void addConversationToConversationsList(ConversationWithMessages conversation) {
        Client client = new Client();
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
        conversationPane.setOnMouseClicked(this::action);
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
            for (Message message : currentConversation.getMessages()) {
                TextArea textArea = new TextArea(message.getText());
                Platform.runLater( () -> messageList.getChildren().add(textArea) );
            }
        }


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

