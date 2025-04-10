package com.aen.spaceship_fights.networking;


public class NetworkManager {

    private static Client client;
    private static Server server;

    public static void setClient(Client c) {
        client = c;
    }

    public static Client getClient() {
        return client;
    }

    public static void setServer(Server s) {
        server = s;
    }

    public static Server getServer() {
        return server;
    }
}

