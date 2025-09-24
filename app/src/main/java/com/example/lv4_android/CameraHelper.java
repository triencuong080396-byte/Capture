package com.example.lv4_android;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

public class CameraHelper {
    public interface OnBitmapCaptureListener{
        void onBitmapCapture(Bitmap bitmap);
        void onError(String message);
    }
    Context context;
    PreviewView previewView;
    ImageCapture imageCapture;

    public CameraHelper(Context context, PreviewView previewView) {
        this.context = context;
        this.previewView = previewView;

    }
    public void startCamera() {
        if(ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(context, new String[]{
//                    Manifest.permission.CAMERA}, 100);
            Toast.makeText(context,"Chua co quyen Camera",Toast.LENGTH_SHORT).show();
            return;
        }
        // Mã bắt đầu camera ở đây, ví dụ như khởi tạo camera, mở màn hình camera
        // startCameraIntent();  // Ví dụ về hàm mở camera
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture =
                ProcessCameraProvider.getInstance(context);
        cameraProviderListenableFuture.addListener(()-> {
            try {
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) context,
                        cameraSelector,
                        preview,
                        imageCapture);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context,"Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(context));
    }
    public void capture(OnBitmapCaptureListener listener){
        if(imageCapture==null){

            Toast.makeText(context,"Camera chua san sang!", Toast.LENGTH_SHORT).show();
        }
        imageCapture.takePicture(ContextCompat.getMainExecutor(context),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image){
                        //Bitmap bitmap = imageProxyToBitmap(image);
                        Bitmap bitmap = ImageHelper.imageProxyToBitmap(image);

                        //Xoay anh
                        int deg = image.getImageInfo().getRotationDegrees();
                        bitmap = ImageHelper.rotaeBitmap(bitmap, deg);

                        //Bat su kien onBitmapCaptured
                        listener.onBitmapCapture(bitmap);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        listener.onError("Loi chup anh: " + exception.getMessage());
                    }
                });
    }

}
