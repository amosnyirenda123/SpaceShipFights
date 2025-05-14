package com.aen.spaceship_fights.networking;

import com.aen.spaceship_fights.Config;
import com.aen.spaceship_fights.utils.Selection;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, PrintWriter> userWriterMap = new ConcurrentHashMap<>();
    private static Map<String, BufferedReader> userReaderMap = new ConcurrentHashMap<>();
    private static Map<String, Set<String>> friendsMap = new ConcurrentHashMap<>();


    private static ObservableList<String> activeUsers = FXCollections.observableArrayList();



    public static void main(String[] args) throws IOException {
        System.out.println("Server started...");
        ServerSocket serverSocket = new ServerSocket(Config.PORT_NUMBER);

        activeUsers.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    System.out.println("Updated active users: " + activeUsers);
                }
            }
        });

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
            String username = in.readLine();
            synchronized (clientWriters) {
                clientWriters.add(out);
                activeUsers.add(username);
                userWriterMap.put(username, out);
                userReaderMap.put(username, in);
                broadcastUserList();
            }



            String message;
            while ((message = in.readLine()) != null) {
                String recep = "RECEIVER:";
                String inviteAccepted = "INVITE_ACCEPTED:";
                String o_Score = "O_SCORE:";
                if(message.startsWith(recep)) {
                    String[] parts = message.split(":");
                    if(parts.length == 3) {
                        String receiver = parts[1];
                        String sender = parts[2];
                        PrintWriter writer = userWriterMap.get(receiver);

                        friendsMap.computeIfAbsent(sender, k -> ConcurrentHashMap.newKeySet()).add(receiver);
                        friendsMap.computeIfAbsent(receiver, k -> ConcurrentHashMap.newKeySet()).add(sender);
                        //add sender to own list of friends to update the ui
                        friendsMap.computeIfAbsent(sender, k -> ConcurrentHashMap.newKeySet()).add(sender);
                        friendsMap.computeIfAbsent(receiver, k -> ConcurrentHashMap.newKeySet()).add(receiver);

                        if(writer != null) {
                            writer.println("INVITATION:" + sender);
                        }
                    }

                }else if (message.startsWith(inviteAccepted)) {
                    String[] parts = message.split(":");

                    if(parts.length == 2) {
                        PrintWriter writer = userWriterMap.get(parts[1]);
                        if(writer != null) {
                            System.out.println("Message sent");
                            writer.println("I_ACCEPTED:" + parts[1]);
                        }
                    }

                }else if(message.startsWith(o_Score)) {
                    String[] parts = message.split(":");
                    PrintWriter writer = userWriterMap.get(parts[1]);
                    if(writer != null) {
                        writer.println("SCORE:" + parts[2]);
                    }
                }else{
                    broadcast(username, message);
                }

            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }


    private static void broadcast(String sender, String message) {
        Set<String> friends = friendsMap.getOrDefault(sender, Collections.emptySet());

        for (String friend : friends) {
            PrintWriter writer = userWriterMap.get(friend);
            if (writer != null) {
                writer.println(message);
            }
        }
    }

    private static void broadcastUserList() {
        String users = "USERS:" + String.join(",", activeUsers);
        for (PrintWriter writer : clientWriters) {
            writer.println(users);
        }
    }

}
