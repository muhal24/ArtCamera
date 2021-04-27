package com.artcamera.artcamera.tool;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermisstionToll {
    public static List<String> permissionList = new ArrayList<>();

    public static boolean checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.CAMERA);
            requestPermission(activity);
            return false;
        }
        return true;
    }

    private static void requestPermission(Activity activity) {
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(activity, permissions, 100);
        }
    }
}
