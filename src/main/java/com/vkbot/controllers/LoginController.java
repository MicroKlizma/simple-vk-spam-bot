package com.vkbot.controllers;

import com.vkbot.vk.VkAuthorization;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.io.FileInputStream;

public class LoginController {
    @FXML
    private TextField email;
    @FXML
    private PasswordField pass;
    @FXML
    private TextField captchaKey;
    @FXML
    private TextField token;
    @FXML
    private Label infoLabel;
    @FXML
    private Button authWithLoginButton;
    @FXML
    private Button tokenButton;
    @FXML
    private Button authWithTokenButton;
    @FXML
    private Button backButton;

    @FXML
    private BorderPane borderPane;
    @FXML
    private Pane loginPane;
    @FXML
    private Pane description;
    @FXML
    private Pane tokenPane;
    @FXML
    private Pane tokenDescription;

    @FXML
    private ImageView captcha;

    private static String validToken;
    private VkAuthorization vkAuth;
    private boolean isCaptchaNeeded;

    // TODO: 02.04.2022 null log & pass fields
    @FXML
    private void initialize() {
        vkAuth = new VkAuthorization();

        tokenButton.getStyleClass().add("key-button");
        tokenButton.setPickOnBounds(true);

        Region keyIcon = new Region();
        keyIcon.getStyleClass().add("key");

        tokenButton.setGraphic(keyIcon);
//        Image im = new Image("resources/com/vkbot/images/border.png");
//        System.out.println(im.getUrl());
    }

    private boolean login() {
        authWithLoginButton.setDisable(true);

        byte loginState;
        String email = this.email.getText();
        String pass = this.pass.getText();

        if (isCaptchaNeeded) {
            String captchaKey = this.captchaKey.getText();
            loginState = vkAuth.loginWithCaptcha(email, pass, captchaKey);
        } else {
            loginState = vkAuth.login(email, pass);
        }


        switch (loginState) {
            case 1:
                String s = vkAuth.getAccessToken();
                Platform.runLater(() -> infoLabel.setText(s));
                break;
            case -1:
                Platform.runLater(() -> infoLabel.setText("Неверный логин или пароль"));
                isCaptchaNeeded = false;
                break;
            case 0:
                Platform.runLater(() -> infoLabel.setText("Введите капчу"));
                isCaptchaNeeded = true;
                try {
                    Image captchaImage = new Image(new FileInputStream("src/main/resources/com/vkbot/temp/captcha.jpg"));
                    Platform.runLater(() -> captcha.setImage(captchaImage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        authWithLoginButton.setDisable(false);
        return true;
    }

    //todo токен дескрибшн переход по ссылке в браузер
    //todo change name action and login
    public void action() {
        Thread thread = new Thread(this::login);
        thread.start();
    }


    @FXML
    private void setTokenPane() {
        loginPane.setVisible(false);
        description.setVisible(false);
        borderPane.setLeft(tokenPane);
        borderPane.setCenter(tokenDescription);
        tokenPane.setVisible(true);
        tokenDescription.setVisible(true);

    }

    @FXML
    private void authWithToken() {

    }
    @FXML
    private void goBack() {
        tokenPane.setVisible(false);
        tokenDescription.setVisible(false);
        borderPane.setLeft(loginPane);
        borderPane.setCenter(description);
        loginPane.setVisible(true);
        description.setVisible(true);
    }
    public static String getToken() {
        return validToken;
    }
}


