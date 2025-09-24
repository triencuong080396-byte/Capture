package com.example.lv4_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbable = result.getMostProbableActivity();
            String activityName = getActivityName(mostProbable.getType());
            int confidence = mostProbable.getConfidence();

            // 🔹 Gửi dữ liệu về cho Recognition Activity
            Intent updateIntent = new Intent("ACTIVITY_UPDATE");
            updateIntent.putExtra("activity", activityName);
            updateIntent.putExtra("confidence", confidence);
            context.sendBroadcast(updateIntent);
        }
    }

    private String getActivityName(int type) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE:   return "Trong xe";
            case DetectedActivity.ON_BICYCLE:   return "Đi xe đạp";
            case DetectedActivity.ON_FOOT:      return "Đi bộ";
            case DetectedActivity.RUNNING:      return "Chạy bộ";
            case DetectedActivity.STILL:        return "Đứng yên";
            case DetectedActivity.TILTING:      return "Nghiêng thiết bị";
            case DetectedActivity.WALKING:      return "Đi bộ";
            case DetectedActivity.UNKNOWN:
            default: return "Không xác định";
        }
    }
}
