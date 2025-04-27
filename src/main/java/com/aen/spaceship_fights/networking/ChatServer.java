package com.aen.spaceship_fights.networking;

import com.aen.spaceship_fights.Config;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

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

    private static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
