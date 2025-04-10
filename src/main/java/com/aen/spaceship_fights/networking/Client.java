package com.aen.spaceship_fights.networking;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public Client(Socket socket, String username) {
        try{
            this.socket = socket;
            this.username = username;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch(IOException e){
            cleanUp(socket, in, out);
        }

    }

    public void sendMessage(String message)  {

        while(socket.isConnected()) {
            try {
                out.write(username + " : " + message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public Node receiveMessage() {
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(10));
        messageBox.setStyle(
                """
                
                        -fx-padding: 10;
                        -fx-border-color: transparent;
                        -fx-border-width: 1px;
                        -fx-border-radius: 10px;
                        -fx-background-color: transparent;
                """
        );
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;



                while(socket.isConnected()) {
                    try{
                        messageFromChat = in.readLine();

                        Label messageLabel = new Label(messageFromChat);
                        messageLabel.setStyle("""
                                -fx-text-fill: #1e1e1e;
                                -fx-font-weight: normal;
                                -fx-font-size: 14px;
                                -fx-background-color: #e1eaf5;
                                -fx-background-radius: 12px;
                                -fx-padding: 8px 12px;
                        """);
                        Platform.runLater(()->{
                            messageBox.getChildren().add(messageLabel);
                        });
                    }catch(IOException e){
                        cleanUp(socket, in, out);
                    }

                }
            }
        }).start();

        return messageBox;
    }

    public void cleanUp(Socket socket, BufferedReader in, BufferedWriter out) {
        try{
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
            if(socket != null) {
                socket.close();
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }

}
