package com.example.lv4_android;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.List;


public class YOLODetector {
    ObjectDetector objectDetector;
    public YOLODetector(Context context) throws IOException {
        ObjectDetector.ObjectDetectorOptions options =
                ObjectDetector.ObjectDetectorOptions.builder()
                        .setMaxResults(5)
                        .setScoreThreshold(0.3f)
                        .build();
        objectDetector = ObjectDetector.createFromFileAndOptions(context,
                "yolo11s_f32.tflite", options);
    }

    public List<Detection> detect (Bitmap bitmap){
        TensorImage image = TensorImage.fromBitmap(bitmap);
        return  objectDetector.detect(image);
    }
}
