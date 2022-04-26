module com.vkbot {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires java.sql;
    requires sdk;
    requires com.google.gson;


    opens com.vkbot to javafx.fxml;
    opens com.vkbot.controllers to javafx.fxml;
    opens com.vkbot.vk to javafx.fxml;
    exports com.vkbot;
    opens com.vkbot.vk.longpoll to com.google.gson, javafx.fxml;
}