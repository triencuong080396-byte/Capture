package com.example.lv4_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OutputFileOptions;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.task.vision.detector.Detection;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivityChupAnh extends AppCompatActivity {
    PreviewView previewView;
    Button btnChupAnh;
    CameraHelper cameraHelper;
    ImageCapture imageCapture;
    Detection detection;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_chup_anh);

        //Linking
        previewView = findViewById(R.id.preview);
        btnChupAnh = findViewById(R.id.btn_ChupAnh);

//        cameraHelper = new CameraHelper(this,previewView)

        //Check quyen chup anh
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA}, 100);
            return;
        }
        cameraHelper = new CameraHelper(this, previewView);
        cameraHelper.startCamera();

        btn_ChupAnh.setOnClickListener(v -> cameraHelper.capture(new CameraHelper.OnBitmapCaptureListener() {
            @Override
            public void onBitmapCapture(Bitmap bitmap) {
                ImageHelper.saveBitmap(MainActivityChupAnh.this,bitmap);
                List<Detection> results = yoloDetection.detect(bitmap);
                Bitmap output = drawDetections(bitmap,results);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivityChupAnh.this,message,Toast.LENGTH_LONG).show();
            }
        }));

    }

    private Bitmap drawDetections(Bitmap bitmap, List<Detection> results) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(40);

        for (Detection detection: results);
        RectF box = detection.getBoundingBox();
        canvas.drawRect(box,paint);
        String label = detection.getCategories().get(0).getLabel() + "(" + String.format("%.2f",detection.getCategories().get(0).getScore()) + ")";
        canvas.drawText(label,box.left,box.top-10,textPaint);

        return bitmap;
    }



    private void capture() {
        if(imageCapture==null) return;
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss",
                Locale.US).format(new Date());
        File photoFile = new File(getFilesDir(),"IMG_"+timeStamp+".jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.@org.jspecify.annotations.NonNull OutputFileResults outputFileResults) {
                        Toast.makeText(MainActivityChupAnh.this,"Saved Picture: "+photoFile.getAbsolutePath(),Toast.LENGTH_LONG).show();;
                    }

                    @Override
                    public void onError(@org.jspecify.annotations.NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> Toast.makeText(MainActivityChupAnh.this,
                                "Error: " + exception.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }
        );
    }


    // Hàm xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            // Kiểm tra quyền camera có được cấp không
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền CAMERA", Toast.LENGTH_SHORT).show();
                cameraHelper.startCamera(); // Bắt đầu camera sau khi có quyền
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền CAMERA để sử dụng chức năng này", Toast.LENGTH_LONG).show();
                // Có thể tắt activity hoặc disable chức năng camera ở đây
            }
        }
    }

}