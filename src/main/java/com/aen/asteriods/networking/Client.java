package com.aen.asteriods.networking;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

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
                "-fx-background-color: #e0f7fa;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #b2ebf2;" +
                        "-fx-border-width: 1;"
        );
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;



                while(socket.isConnected()) {
                    try{
                        messageFromChat = in.readLine();

                        Text text = new Text(messageFromChat);
                        text.setStyle("-fx-color: #e0f7fa;");
                        messageBox.getChildren().add(text);
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
