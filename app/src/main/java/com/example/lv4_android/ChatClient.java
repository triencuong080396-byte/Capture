package com.example.lv4_android;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Gọi hàm này để kết nối tới server
    public void connect() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Gửi tin nhắn qua kết nối đã mở
    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    // Đóng kết nối khi không dùng nữa
    public void disconnect() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
