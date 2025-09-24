package com.example.lv4_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private int port;
    private ChatListener listener;

    // Cho public để có thể dùng từ ClientServer.java
    public interface ChatListener {
        void onMessegerReceived(String msg);
    }

    public ChatServer(int port, ChatListener listener) {
        this.port = port;
        this.listener = listener;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept(); // chờ client kết nối
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String messeger;
                while ((messeger = in.readLine()) != null) {
                    if (listener != null) {
                        listener.onMessegerReceived(messeger);
                    }
                }
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
