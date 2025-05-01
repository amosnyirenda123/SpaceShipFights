package com.aen.spaceship_fights.networking;

import com.aen.spaceship_fights.utils.Selection;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;


import java.io.*;
import java.net.Socket;
import java.util.*;

public class ChatServiceFXGL extends VBox {

    private PrintWriter out;
    private TextArea messageArea;
    private TextField inputField;
    private VBox notificationPane;
    private Timeline timeline;
    private String opponent;//TODO: make list to allow multiple opponents
    private Map<String, Integer> opponentScores = new HashMap<>();
    private VBox gameTimerUI;
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
        opponent = inviter; //TODO: Add oponent to list
        HBox invitationBox = new HBox(10);
        invitationBox.setAlignment(Pos.CENTER_LEFT);
        invitationBox.setStyle("-fx-background-color: #2e2e2e; -fx-padding: 10px; -fx-border-color: #8B0000;");

        Label label = new Label(inviter + " invited you to a game.");
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Consolas", 11));

        Button acceptBtn = new Button("Accept");
        Button declineBtn = new Button("Decline");

        acceptBtn.setOnAction(e -> {
            out.println("INVITE_ACCEPTED:" + inviter);
            startTimer(timeline);
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
    private void pushNotification(String message) {
        HBox notificationBox = new HBox(10);
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        notificationBox.setStyle("-fx-background-color: #2e2e2e; -fx-padding: 10px; -fx-border-color: #8B0000;");

        Label label = new Label(message);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Consolas", 11));

        Button cancelBtn = new Button("Cancel");

        cancelBtn.setOnAction(e -> {
            notificationPane.getChildren().remove(notificationBox);
        });
        notificationBox.getChildren().addAll(label, cancelBtn);
        notificationPane.getChildren().add(notificationBox);
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



    public VBox showGameTimerUI(){
        VBox gameTimerUI = new VBox();
        Label timerLabel = new Label();
        timerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        gameTimerUI.getChildren().add(timerLabel);


        final int[] timeLeft = {30};


            timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int minutes = timeLeft[0] / 60;
            int seconds = timeLeft[0] % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

            timeLeft[0]--;

            if (timeLeft[0] < 0) {
                timeLeft[0] = 0;
                int my_score = FXGL.geti("score");
                out.println("O_SCORE:" + opponent + ":" + my_score);


                FXGL.runOnce(() ->{
                    FXGL.getDialogService().showConfirmationBox(
                            "Your Score: "+ my_score + "\n"+
                                    "Opponent Score: " + opponentScores.get(opponent) +
                                    "\nClick Yes to go to the main menu.", yes ->{
                                if(yes){
                                    FXGL.getGameController().gotoMainMenu();
                                }else{
                                    FXGL.getGameController().exit();
                                }
                            });
                }, Duration.seconds(1));


            }


        }));

        timeline.setCycleCount(31);

        return gameTimerUI;
    }

    public void startTimer(Timeline timeline){
        timeline.play();
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
                        String opponentScoreStart = "SCORE:"; //TODO: make it more safe and unique
                        String invitationMessageStart = "INVITATION:";
                        String invitationAccepted = "I_ACCEPTED:";
                        if(finalMessage.startsWith(usersArrayStart)) {
                            String[] users = finalMessage.substring(usersArrayStart.length()).split(",");

                            Platform.runLater(() -> {
                                List<String> uniqueUsers = new ArrayList<>(new LinkedHashSet<>(Arrays.asList(users)));
                                activeUsers.setAll(uniqueUsers);
                            });
                        }else if(finalMessage.startsWith(invitationMessageStart)) {
                            String inviter = finalMessage.substring(invitationMessageStart.length());
                            Platform.runLater(()->{
                                addInvitationNotification(inviter);
                            });
                        }else if(finalMessage.startsWith(invitationAccepted)) {

                            Platform.runLater(()->{
                                pushNotification("Invitation Accepted.");
                                startTimer(timeline);
                            });
                        }else if(finalMessage.startsWith(opponentScoreStart)){
                            String score = finalMessage.substring(opponentScoreStart.length());
                            opponentScores.put(opponent, Integer.parseInt(score));
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

    public ObservableList<String> getUsersList() {
        return activeUsers;
    }
}
