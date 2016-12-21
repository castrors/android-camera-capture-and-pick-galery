package com.example.rodrigocastro.photopicker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import static com.example.rodrigocastro.photopicker.CameraUtils.requestCameraPermission;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_FILE = 2;
    private static final String TAG_LOG = MainActivity.class.getName();
    private static String APP_FOLDER = "MY_APP";

    private ImageView mImageView;
    private File file;
    private Button mButtonCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        handlePermissions();
    }

    private void setupViews() {
        mImageView = (ImageView) findViewById(R.id.image_view);
        mButtonCapture = (Button) findViewById(R.id.button_capture);
    }

    private void handlePermissions() {
        if (CameraUtils.checkCameraHardware(this)) {
            if (!CameraUtils.checkCameraPermission(this)) {
                mButtonCapture.setEnabled(false);
                requestCameraPermission(this);
                Log.i(TAG_LOG, "dado permissao para usar a camera");
            } else {
                Log.i(TAG_LOG, "ja existe permissao para usar a camera");
            }
        } else {
            Log.e(TAG_LOG, "dispositivo nao contem camera");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                mButtonCapture.setEnabled(true);
            }
        }
    }

    public void capturePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            file = getOutputMediaFile();
        } catch (IOException e) {
            Log.e(TAG_LOG, e.getMessage());
        }

        if (file == null) {
            Log.e(TAG_LOG, "Nao foi possivel criar um diretorio!");
        } else {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(view.getContext(),
                    view.getContext().getApplicationContext().getPackageName() + ".provider", file));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void pickPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_PICK_FILE:
                    onSelectFromGalleryResult(data);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    setImageViewAndAddImageToGallery();
                    break;
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mImageView.setImageBitmap(bitmap);
    }

    private void setImageViewAndAddImageToGallery() {
        final Uri uriForFile = FileProvider.getUriForFile(getBaseContext(),
                getBaseContext().getApplicationContext().getPackageName() + ".provider", file);
        mImageView.setImageURI(uriForFile);
        CameraUtils.addImagemGaleria(file, getBaseContext());
    }

    private static File getOutputMediaFile() throws IOException {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), CameraUtils.getCameraDir(APP_FOLDER));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return File.createTempFile(CameraUtils.IMAGE_NAME, CameraUtils.IMAGE_TYPE, mediaStorageDir);
    }

}
