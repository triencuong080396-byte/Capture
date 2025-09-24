package com.example.lv4_android;

import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lv4_android.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText edtMST;
    Button btnTraCuu;
    TextView textViewTenCT, textViewDiaChi, textViewResponse;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // anh xa
        edtMST = findViewById(R.id.edtTextMST);
        btnTraCuu = findViewById(R.id.btnTraCuu);
        textViewDiaChi = findViewById(R.id.textViewDiaChi);
        textViewTenCT = findViewById(R.id.textViewTenCT);
        textViewResponse = findViewById(R.id.textViewResponse);

        client = new OkHttpClient();

        // Bat su kien
        edtMST.setText("2300325764");

        btnTraCuu.setOnClickListener(v -> {
            String mst = edtMST.getText().toString();
            new Thread(() -> traCuu(mst)).start();
        });
    }

    private void traCuu(String mst) {

        String url = "https://api.vietqr.io/v2/business/" + mst;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String body = response.body()!=null?response.body().string() : "";
            if (!response.isSuccessful()){
                runOnUiThread(()->textViewResponse.setText("Lỗi http: " + response.code()));
                return;
            }
            runOnUiThread(()->textViewResponse.setText(body));
            ApiData apiData = new Gson().fromJson(body,ApiData.class);
            runOnUiThread(()-> {
                if(apiData.getCode().equals("00")) {
                    textViewTenCT.setText("Ten Cong Ty: " + apiData.getData().getName());
                    textViewDiaChi.setText("Dia chi: " + apiData.getData().getAddress());
                }else {
                    textViewTenCT.setText(apiData.getDesc());
                }
            });
        } catch (IOException e){
            runOnUiThread(()->textViewResponse.setText("Lỗi: "+ e.getMessage()));
        }

    }
}