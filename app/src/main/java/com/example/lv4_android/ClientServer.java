package com.example.lv4_android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ClientServer extends AppCompatActivity {

    EditText editTextIP, editTextPort, editTextMessenger;
    TextView textViewChat;
    Button buttonConnect, buttonSend;
    ScrollView scrollView;

    ChatClient client;
    ChatServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatmesseger);

        // ánh xạ view
        editTextIP = findViewById(R.id.edtIP);
        editTextPort = findViewById(R.id.edtPort);
        editTextMessenger = findViewById(R.id.edtMessenger);
        textViewChat = findViewById(R.id.tvHistory);
        buttonConnect = findViewById(R.id.btnKetNoi);
        buttonSend = findViewById(R.id.btnGui);
        scrollView = findViewById(R.id.scrollView);

        // xử lý nút Kết nối
        buttonConnect.setOnClickListener(v -> {
            int port = Integer.parseInt(editTextPort.getText().toString().trim());
            String ip = editTextIP.getText().toString().trim();

            // khởi tạo server (chỉ 1 lần)
            if (server == null) {
                server = new ChatServer(port, msg -> {
                    runOnUiThread(() -> appendMessage("Friend: " + msg));
                });
                new Thread(server::run).start();
            }

            // khởi tạo client
            client = new ChatClient(ip, port);
            new Thread(() -> client.connect()).start();  // cần gọi connect
        });

        // xử lý nút Gửi
        buttonSend.setOnClickListener(v -> {
            String msg = editTextMessenger.getText().toString().trim();
            if (!msg.isEmpty() && client != null) {
                new Thread(() -> client.sendMessage(msg)).start();
                appendMessage("Me: " + msg);
                editTextMessenger.setText("");
            }
        });
    }

    // hàm append tin nhắn và tự động scroll xuống cuối
    private void appendMessage(String msg) {
        textViewChat.append(msg + "\n");
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
