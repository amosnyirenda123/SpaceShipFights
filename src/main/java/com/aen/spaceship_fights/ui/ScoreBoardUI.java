package com.aen.spaceship_fights.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ScoreBoardUI {

    public static class PlayerScore {
        private final String name;
        private final int score;
        private final int rank;
        private final String achievement;

        public PlayerScore(String name, int score, int rank, String achievement) {
            this.name = name;
            this.score = score;
            this.rank = rank;
            this.achievement = achievement;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
        public int getRank() { return rank; }
        public String getAchievement() { return achievement; }
    }

    public Parent showScoreBoard() {
        TableView<PlayerScore> table = new TableView<>();
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Columns
        TableColumn<PlayerScore, Number> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createIntegerBinding(() -> cellData.getValue().getRank()));

        TableColumn<PlayerScore, String> nameCol = new TableColumn<>("Player Name");
        nameCol.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().getName()));

        TableColumn<PlayerScore, Number> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createIntegerBinding(() -> cellData.getValue().getScore()));

        TableColumn<PlayerScore, String> achievementCol = new TableColumn<>("Achievement");
        achievementCol.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().achievement));

        table.getColumns().addAll(rankCol, nameCol, scoreCol, achievementCol);

        // Sample data (replace with real savedData)
        ObservableList<PlayerScore> players = FXCollections.observableArrayList(
                new PlayerScore("Alice", 9500, 1, "Master Scorer"),
                new PlayerScore("Bob", 8500, 2, "Sharp Shooter"),
                new PlayerScore("Charlie", 7200, 3, "Quick Learner"),
                new PlayerScore("Diana", 6100, 4, "Rising Star")
        );

        table.setItems(players);

        // Optional: Style top player
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(PlayerScore player, boolean empty) {
                super.updateItem(player, empty);
                if (player != null && player.getRank() == 1) {
                    setStyle("-fx-background-color: gold; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });


        Text title = new Text("Score Board");
        title.setFont(Font.loadFont(getClass().getResourceAsStream("/assets/fonts/BruceForever.ttf"), 20));

        LinearGradient gradientFill = new LinearGradient(
                0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0.1, Color.color(0.5, 0.0, 0.0, 1)),  // Dark Blood Red
                new Stop(0.5, Color.color(0.6, 0.1, 0.1, 1)),  // Deep Crimson
                new Stop(0.9, Color.color(0.4, 0.0, 0.0, 1))   // Darker Red
        );
        title.setFill(gradientFill);

        VBox root = new VBox(15, title, table, new MenuButton("Go Back", getGameController()::gotoGameMenu));
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: white;");
        root.setPrefSize(getAppWidth() / 2.0 , getAppHeight() / 2.0);
        root.setTranslateX(360);
        root.setTranslateY(100);
        return root;
    }
}

