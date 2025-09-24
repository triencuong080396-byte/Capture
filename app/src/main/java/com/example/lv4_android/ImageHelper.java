package com.example.lv4_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;

import androidx.camera.core.ImageProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageHelper {
    public  static Bitmap imageProxyToBitmap(ImageProxy image){
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        return bitmap;
    }
    public static Bitmap rotaeBitmap(Bitmap bitmap, int rotationDegress){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegress);
        return Bitmap.createBitmap(bitmap,0,0,
                bitmap.getWidth(),bitmap.getHeight(),
                matrix, true);
    }
    public static File saveBitmap(Context context, Bitmap bitmap) {
        File photoFile = new File(context.getFilesDir(),
                "Photo_"+System.currentTimeMillis()+".jpg");
        try (FileOutputStream out = new FileOutputStream(photoFile)){
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            Toast.makeText(context,
                    "Saved: " + photoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            return photoFile;
        }catch (IOException e) {
            Toast.makeText(context,
                    "Save image Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
