package com.example.lv4_android;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

public class Recognition extends AppCompatActivity {
    TextView tvActivity;
    ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent pendingIntent;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String  activity = intent.getStringExtra("activity");
            int confidence = intent.getIntExtra("confidence",0);
            tvActivity.setText("Bạn đang: " + activity + " ("+ confidence+"%)");
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        tvActivity = findViewById(R.id.tvKetQua);
        activityRecognitionClient = ActivityRecognition.getClient(this);
            startActivityUpdates();
        }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startActivityUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACTIVITY_RECOGNITION},100);
            return;
    }
        Intent intent = new Intent(this,ActivityRecognitionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

         activityRecognitionClient.requestActivityUpdates(3000,pendingIntent).addOnSuccessListener(x->{
                Toast.makeText(this,"Bắt đầu: ", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e->{Toast.makeText(this,"Lỗi: ",Toast.LENGTH_SHORT).show();
            });
    }
}