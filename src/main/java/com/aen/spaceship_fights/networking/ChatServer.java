package com.aen.spaceship_fights.networking;

import com.aen.spaceship_fights.Config;
import com.aen.spaceship_fights.utils.Selection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    private static ObservableList<String> activeUsers = FXCollections.observableArrayList();



    public static void main(String[] args) throws IOException {
        System.out.println("Server started...");
        ServerSocket serverSocket = new ServerSocket(Config.PORT_NUMBER);

        while (true) {
            Socket clientSocket = serverSocket.accept();

            System.out.println("Client connected!");
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            synchronized (clientWriters) {
                clientWriters.add(out);
                activeUsers.add(Selection.getUsername());
            }

            String message;
            while ((message = in.readLine()) != null) {
                broadcast(message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    public static ObservableList<String> getActiveUsers() {
        ObservableList<String> newActiveUsers = FXCollections.observableArrayList();
        for (String user : activeUsers) {
            if (!Selection.getUsername().equals(user)) {
                newActiveUsers.add(user);
            }
        }
        return newActiveUsers;
    }

    private static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
