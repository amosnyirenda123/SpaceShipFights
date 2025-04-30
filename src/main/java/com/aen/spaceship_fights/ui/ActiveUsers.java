package com.aen.spaceship_fights.ui;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class ActiveUsers {


    public Parent showActiveUsers(){
        ListView<String> userListView = new ListView<>();
        BorderPane root = new BorderPane();
        root.setTranslateY(20);
        root.setTranslateX(700);
        VBox userInfoBox = new VBox(10);
        userListView.getItems().addAll("Alice", "Bob", "Charlie", "Amos");
        userListView.setStyle(
                "-fx-control-inner-background: black;" +
                        "-fx-background-insets: 0;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #8B0000;" +
                        "-fx-border-width: 2px;" +
                        "-fx-effect: dropshadow(gaussian, #8B0000, 10, 0.5, 0, 0);"
        );

        userListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: black;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: black; -fx-text-fill: white;");
                }
            }
        });

        userInfoBox.setStyle(
                "-fx-background-color: black;" +
                        "-fx-padding: 10;" +
                        "-fx-border-color: #8B0000;" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-border-width: 2px;" +
                        "-fx-effect: dropshadow(gaussian, #8B0000, 10, 0.5, 0, 0);"
        );

        Label nameLabel = new Label();
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Label statusLabel = new Label("Status: Online");
        statusLabel.setStyle("-fx-text-fill: #8B0000; -fx-font-size: 14px;");

        Label ipLabel = new Label("IP Address: 192.168.1.1");
        ipLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        userInfoBox.getChildren().addAll(nameLabel, statusLabel, ipLabel);

        // On user click, update user info box
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameLabel.setText("Username: " + newVal);
                // Optionally fetch other details dynamically
            }
        });
        CustomButton connectBtn = new CustomButton("Connect", null);
        CustomButton cancelBtn = new CustomButton("Close Pane", closeActiveUsersPane(root));

        HBox buttonBox = new HBox(10, connectBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        VBox infoPane = new VBox(userInfoBox);
        infoPane.setAlignment(Pos.CENTER);
        infoPane.setPadding(new Insets(0,20,0,20));

        VBox rightPane = new VBox(10, infoPane, buttonBox);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setVisible(false);

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                rightPane.setVisible(true);
            }
        });


        root.setLeft(userListView);
        root.setCenter(rightPane);

        root.setStyle("-fx-background-color: black; -fx-padding: 20;");

        return root;
    }

    private Runnable closeActiveUsersPane(Node node){
        return () -> node.setVisible(false);
    }
}
