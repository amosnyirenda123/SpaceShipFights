package com.aen.spaceship_fights.networking;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> handlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;

        try{
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientName = in.readLine();
            handlers.add(this);

            broadcastMessage(clientName+ " has joined the stream");


        }catch(IOException e){
            cleanUp(socket, in, out);
        }


    }

    @Override
    public void run() {
        String messageFromClient = null;

        while(socket.isConnected()) {
            try{
                messageFromClient = in.readLine();
                broadcastMessage(messageFromClient);
            }catch(IOException e){
                cleanUp(socket, in, out);
                break;
            }
        }

    }

    public void broadcastMessage(String message) {
        for(ClientHandler client : handlers) {
            try{
                if(!client.clientName.equals(this.clientName)) {
                    client.out.write(message);
                    client.out.newLine();
                    client.out.flush();
                }
            }catch(IOException e){
                cleanUp(socket, in, out);
            }
        }
    }

    public void disconnectClient() {
        handlers.remove(this);
        broadcastMessage(this.clientName+ " has left.");
    }

    public void cleanUp(Socket socket, BufferedReader in, BufferedWriter out) {
        disconnectClient();
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
            e.printStackTrace();
        }

    }
}
