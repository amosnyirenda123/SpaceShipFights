package com.aen.spaceship_fights.networking;

import com.aen.spaceship_fights.Config;

public class ChatContext {
    private static ChatServiceFXGL instance;

    public static ChatServiceFXGL getInstance() {
        if (instance == null) {
            instance = new ChatServiceFXGL();
            instance.connectToServer("localhost", Config.PORT_NUMBER);
        }
        return instance;
    }
}
