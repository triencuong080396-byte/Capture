package com.example.lv4_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TestMQTT extends AppCompatActivity {
    String BROKER_URL = "tcp://broker.emqx.io:1883";
    String SUB_TOPIC = "ss/android";
    String PUB_TOPIC = "ss/android";
    int PUB_QOS = 1;
    MqttAsyncClient mqttClient;
    EditText edtMsgToPubLish;
    Button btnPubLish;
    TextView tvReceivedMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mqtt);

        edtMsgToPubLish = findViewById(R.id.edtMsgToPubLish);
        btnPubLish = findViewById(R.id.btnPublish);
        tvReceivedMsg = findViewById(R.id.tvMsgReceived);

        btnPubLish.setEnabled(false); // Chỉ bật khi đã connect

        String clientId = MqttClient.generateClientId();
        try {
            mqttClient = new MqttAsyncClient(BROKER_URL, clientId);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    runOnUiThread(() ->
                            Toast.makeText(TestMQTT.this, "Mất kết nối", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String msg = new String(message.getPayload());
                    runOnUiThread(() ->
                            tvReceivedMsg.append(topic + ": " + msg + "\n")
                    );
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    runOnUiThread(() ->
                            Toast.makeText(TestMQTT.this, "Gửi thành công", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        } catch (MqttException e) {
            Toast.makeText(TestMQTT.this, "Khởi tạo client thất bại", Toast.LENGTH_SHORT).show();
        }

        btnPubLish.setOnClickListener(v -> {
            String msg = edtMsgToPubLish.getText().toString().trim();
            if (msg.isEmpty()) return;
            MqttMessage mqttMessage = new MqttMessage(msg.getBytes());
            mqttMessage.setQos(PUB_QOS);
            try {
                mqttClient.publish(PUB_TOPIC, mqttMessage);
            } catch (MqttException e) {
                Toast.makeText(TestMQTT.this,
                        "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        mqttConnect();
    }

    private void mqttConnect() {
        try {
            IMqttToken token = mqttClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    runOnUiThread(() -> {
                        Toast.makeText(TestMQTT.this,
                                "Kết nối thành công", Toast.LENGTH_LONG).show();
                        btnPubLish.setEnabled(true);
                    });
                    mqttSubscribe(); // Chỉ subscribe sau khi kết nối thành công
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    runOnUiThread(() ->
                            Toast.makeText(TestMQTT.this,
                                    "Kết nối thất bại", Toast.LENGTH_LONG).show()
                    );
                }
            });
        } catch (MqttException e) {
            Toast.makeText(TestMQTT.this,
                    "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void mqttSubscribe() {
        try {
            mqttClient.subscribe(SUB_TOPIC, PUB_QOS);
        } catch (MqttException e) {
            Toast.makeText(TestMQTT.this,
                    "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
