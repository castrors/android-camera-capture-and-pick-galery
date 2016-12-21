package com.example.rodrigocastro.photopicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;

/**
 * Created by rodrigocastro on 21/12/16.
 */
class CameraUtils {
    static final String IMAGE_NAME = "img";
    static final String IMAGE_TYPE = ".jpg";
    private static final String IMAGE_DIR = "Camera/";


    static boolean checkCameraHardware(Context context) {

        if (context != null) {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        } else {
            return false;
        }
    }

    static boolean checkCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    static void requestCameraPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }


    static void addImagemGaleria(File file, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    static String getCameraDir(final String appFolder) {
        return IMAGE_DIR + appFolder + "/";
    }
}
