package com.aen.spaceship_fights.ui;

import javafx.scene.control.Button;

public class CustomButton extends Button {
    public CustomButton(String name, Runnable action) {
        super(name);
        this.setStyle("-fx-background-color: black; -fx-text-fill: #8B0000; -fx-border-color: #8B0000; " +
                "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-font-family: 'Courier New'; -fx-font-size: 14px;");
        this.setOnAction(e -> action.run());
    }
}
