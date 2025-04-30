package com.aen.spaceship_fights.networking;

import com.aen.spaceship_fights.utils.Selection;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatServiceFXGL extends VBox {

    private PrintWriter out;
    private TextArea messageArea;
    private TextField inputField;

    public ChatServiceFXGL() {

    }

    public VBox showChatUI(){
        VBox chatUI = new VBox();

        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPrefHeight(200);
        messageArea.setWrapText(true);

        messageArea.setStyle(
                "-fx-control-inner-background: black; " +
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

    public void connectToServer(String serverAddress, int port) {
        try {
            Socket socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message;
                        Platform.runLater(() -> messageArea.appendText(finalMessage + "\n"));
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

    public static void toggleVisibility(VBox box) {
        box.setVisible(!box.isVisible());
    }
}
