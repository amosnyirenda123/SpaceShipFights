package com.aen.spaceship_fights.networking;

import com.aen.spaceship_fights.utils.Selection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatServiceFXGL extends VBox {

    private PrintWriter out;
    private TextArea messageArea;
    private TextField inputField;
    private VBox notificationPane;
    private final ObservableList<String> activeUsers = FXCollections.observableArrayList();

    public ChatServiceFXGL() {

    }

    public VBox showChatUI(){
        VBox chatUI = new VBox();

        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPrefHeight(200);
        messageArea.setWrapText(true);

        messageArea.setStyle(
                "-fx-control-inner-background: #1e1e1e;; " +
                        "-fx-text-fill: #ffff; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-font-size: 15px; " +
                        "-fx-background-color: black; " +
                        "-fx-border-color: #ff0033; " +
                        "-fx-border-width: 2px; " +
                        "-fx-effect: dropshadow(gaussian, #ff0033, 10, 0.5, 0, 0);"
        );



        inputField = new TextField();
        inputField.setPromptText("Write your message here...");
        inputField.setStyle("-fx-control-inner-background: black; " +
                "-fx-font-family: 'Consolas'; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #8B0000; " +
                "-fx-prompt-text-fill: darkred; " +
                "-fx-background-color: black; " +
                "-fx-border-color: #8B0000; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;");

        inputField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                inputField.setStyle(
                        "-fx-control-inner-background: black; " +
                                "-fx-font-family: 'Consolas'; " +
                                "-fx-font-size: 14px; " +
                                "-fx-text-fill: #8B0000; " +
                                "-fx-prompt-text-fill: darkred; " +
                                "-fx-background-color: black; " +
                                "-fx-border-color: #8B0000; " +
                                "-fx-border-width: 2px; " +
                                "-fx-border-radius: 5px; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-effect: dropshadow(gaussian, #8B0000, 10, 0.5, 0, 0);"
                );
            } else {
                inputField.setStyle(
                        "-fx-control-inner-background: black; " +
                                "-fx-font-family: 'Consolas'; " +
                                "-fx-font-size: 14px; " +
                                "-fx-text-fill: #8B0000; " +
                                "-fx-prompt-text-fill: darkred; " +
                                "-fx-background-color: black; " +
                                "-fx-border-color: #8B0000; " +
                                "-fx-border-width: 2px; " +
                                "-fx-border-radius: 5px; " +
                                "-fx-background-radius: 5px;"
                );
            }
        });
        inputField.setOnAction(e -> {
            sendMessage();
        });


        chatUI.getChildren().addAll(messageArea, inputField);
        chatUI.setVisible(false);

        return chatUI;

    }

    private void addInvitationNotification(String inviter) {
        HBox invitationBox = new HBox(10);
        invitationBox.setAlignment(Pos.CENTER_LEFT);
        invitationBox.setStyle("-fx-background-color: #2e2e2e; -fx-padding: 10px; -fx-border-color: #8B0000;");

        Label label = new Label(inviter + " invited you to a game.");
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Consolas", 11));

        Button acceptBtn = new Button("Accept");
        Button declineBtn = new Button("Decline");

        acceptBtn.setOnAction(e -> {
//            out.println("INVITE_ACCEPTED:" + inviter);
            notificationPane.getChildren().remove(invitationBox);
        });

        declineBtn.setOnAction(e -> {
//            out.println("INVITE_DECLINED:" + inviter);
            notificationPane.getChildren().remove(invitationBox);
        });


        if(notificationPane != null) {
            invitationBox.getChildren().addAll(label, acceptBtn, declineBtn);
            notificationPane.getChildren().add(invitationBox);
        }

    }

    public VBox showNotificationPane(){
        notificationPane = new VBox();
        notificationPane.setSpacing(10);
        notificationPane.setPrefSize(400, 200);
        notificationPane.setStyle("-fx-background-color: #1e1e1e;"+
                "-fx-border-color: #ff0033; "+
                "-fx-border-width: 2px; " +
                "-fx-effect: dropshadow(gaussian, #ff0033, 10, 0.5, 0, 0);");
        notificationPane.setVisible(false);
        return notificationPane;
    }

    public void connectToServer(String serverAddress, int port) {
        try {
            Socket socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(Selection.getUsername());
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message;
                        String usersArrayStart = "USERS:";
                        String invitationMessageStart = "INVITATION:";
                        if(finalMessage.startsWith(usersArrayStart)) {
                            String[] users = finalMessage.substring(usersArrayStart.length()).split(",");

                            Platform.runLater(()-> {
                                activeUsers.setAll(Arrays.asList(users));
                            });
                        }else if(finalMessage.startsWith(invitationMessageStart)) {
                            String inviter = finalMessage.substring(invitationMessageStart.length());
                            Platform.runLater(()->{
                                addInvitationNotification(inviter);
                            });
                        }else{
                            Platform.runLater(() -> {
                                if(messageArea != null) {
                                    messageArea.appendText(finalMessage + "\n");
                                }
                            });
                        }

                    }
                } catch (IOException ignored) {}
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(Selection.getUsername()+ ": " + message + "\n");
            inputField.clear();
        }
    }

    public void sendInvitation(String receiver){
        if(out != null) {
            out.println("RECEIVER:" + receiver + ":" + Selection.getUsername());
        }else{
            System.err.println("Error: Not connected to server. 'out' is null.");
        }

    }

    public static void toggleVisibility(VBox box) {
        box.setVisible(!box.isVisible());
    }

    public ObservableList<String> getUsersList(){
        return activeUsers;
    }
}
