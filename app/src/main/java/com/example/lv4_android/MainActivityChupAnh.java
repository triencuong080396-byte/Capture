package com.example.lv4_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.task.vision.detector.Detection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivityChupAnh extends AppCompatActivity {
    PreviewView previewView;
    Button btnChupAnh;
    ImageView imgResult;
    CameraHelper cameraHelper;
    YOLODetector yoloDetector;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_chup_anh);

        // Linking UI
        previewView = findViewById(R.id.preview);
        btnChupAnh = findViewById(R.id.btn_ChupAnh);
        imgResult = findViewById(R.id.imgResult);  // Cần thêm trong XML

        // Kiểm tra quyền CAMERA
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        // Khởi tạo Camera
        cameraHelper = new CameraHelper(this, previewView);
        cameraHelper.startCamera();

        // Khởi tạo YOLO Detector
        try {
            yoloDetector = new YOLODetector(this);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo YOLO Detector", Toast.LENGTH_LONG).show();
            return;
        }

        // Bắt sự kiện nút chụp
        btnChupAnh.setOnClickListener(v -> {
            cameraHelper.capture(new CameraHelper.OnBitmapCaptureListener() {
                @Override
                public void onBitmapCapture(Bitmap bitmap) {
                    ImageHelper.saveBitmap(MainActivityChupAnh.this, bitmap);
                    List<Detection> results = yoloDetector.detect(bitmap);
                    Bitmap output = drawDetections(bitmap, results);
                    runOnUiThread(() -> imgResult.setImageBitmap(output));
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivityChupAnh.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private Bitmap drawDetections(Bitmap bitmap, List<Detection> results) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(40);

        for (Detection detection : results) {
            RectF box = detection.getBoundingBox();
            canvas.drawRect(box, paint);
            String label = detection.getCategories().get(0).getLabel() +
                    "(" + String.format("%.2f", detection.getCategories().get(0).getScore()) + ")";
            canvas.drawText(label, box.left, box.top - 10, textPaint);
        }

        return mutableBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền CAMERA", Toast.LENGTH_SHORT).show();
                cameraHelper.startCamera();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền CAMERA để sử dụng chức năng này", Toast.LENGTH_LONG).show();
            }
        }
    }
}
