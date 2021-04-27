package com.artcamera.artcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.artcamera.artcamera.camera.CameraPreview;
import com.artcamera.artcamera.camera.OverCameraView;

import com.artcamera.artcamera.img.BitmapUtils;
import com.artcamera.artcamera.tool.FaceLoading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FaceCameraActivity extends AppCompatActivity  {
    private Toolbar toolbar;
    private FrameLayout mPreviewLayout;
    private ImageView mPhotoButton;
    private OverCameraView mOverCameraView;
    private Camera mCamera;
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private byte[] imageData;
    private boolean isTakePhoto;
    private boolean isFocusable;

    private ImageView ivSwitchCamera;

    private ImageView ivConfirmPhoto;
    private ImageView ivAfreshPhoto;
    private int cameraPosition = 1;

    private CameraPreview preview;

    private FaceLoading faceLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_camera);
        initUI();
        initAction();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    //        toolbar.setNavigationIcon(R.drawable.back);
        setTitle(null);
        mPreviewLayout = findViewById(R.id.camera_preview_layout);
        mPhotoButton = findViewById(R.id.take_photo_button);
        ivSwitchCamera = findViewById(R.id.iv_switch_camera);
        ivAfreshPhoto = findViewById(R.id.iv_afresh_photo);
        ivConfirmPhoto = findViewById(R.id.iv_confirm_photo);



//        RelativeLayout.LayoutParams params0 = new RelativeLayout.LayoutParams(DisplayUtils.getScreenWidth(this), DisplayUtils.getScreenWidth(this) / 3 + DisplayUtils.getScreenWidth(this));
    //        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
    //                DisplayUtils.getScreenWidth(this)/2, (DisplayUtils.getScreenWidth(this) / 3 + DisplayUtils.getScreenWidth(this))/2);
    //
    //        findViewById(R.id.mask_img).setLayoutParams(params1);
//        mPreviewLayout.setLayoutParams(params0);
    }

    private void initAction() {
        ivConfirmPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surePhoto();
            }
        });
        ivSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTakePhoto) {
                    takePhoto();
                }
            }
        });
        ivAfreshPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSavePhoto();
            }
        });

    }

    private void surePhoto(){
        faceLoading = new FaceLoading(FaceCameraActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                savePhoto();
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        preview = new CameraPreview(this, mCamera);
        mOverCameraView = new OverCameraView(this);
        mPreviewLayout.addView(preview);
        mPreviewLayout.addView(mOverCameraView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFocusable) {
                float x = event.getX();
                float y = event.getY();
                if (toolbar != null) {
                    y = y-toolbar.getHeight();
                }

                isFocusable = true;
                if (mCamera != null && !isTakePhoto) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                 mRunnable = new Runnable() {
                     @Override
                     public void run() {
            //                        Toast.makeText(CameraActivity.this, "Auto focus timeout, please adjust the appropriate position to shoot!", Toast.LENGTH_SHORT).show();
                         isFocusable = false;
                         mOverCameraView.setFoucuing(false);
                         mOverCameraView.disDrawTouchFocusRect();
                     }
                };
                mHandler.postDelayed(mRunnable, 100);
            }
        }
        return super.onTouchEvent(event);
    }

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFocusable = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();

            mHandler.removeCallbacks(mRunnable);
        }
    };

    private void takePhoto() {
        isTakePhoto = true;
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                ivConfirmPhoto.setVisibility(View.VISIBLE);
                ivAfreshPhoto.setVisibility(View.VISIBLE);
                mPhotoButton.setVisibility(View.INVISIBLE);
                imageData = bytes;
                mCamera.stopPreview();
            }
        });
    }

    private void cancelSavePhoto() {
        ivConfirmPhoto.setVisibility(View.INVISIBLE);
        ivAfreshPhoto.setVisibility(View.INVISIBLE);
        mPhotoButton.setVisibility(View.VISIBLE);
        mCamera.startPreview();
        imageData = null;
        isTakePhoto = false;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 200) {
    //                progressDialogUtil.stopLoad();
                faceLoading.dismiss();
            }
            return false;
        }
    });

    private void switchCamera() {
        int cameraCount;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraPosition == 1) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        //                    mCamera.stopPreview();
        //                    mCamera.release();
        //                    mCamera = null;
                closeCamera();
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                preview = new CameraPreview(this, mCamera);
                mOverCameraView = new OverCameraView(this);
                mPreviewLayout.addView(preview);
                mPreviewLayout.addView(mOverCameraView);
                cameraPosition = 0;
                break;
                }
            } else {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
    //                    mCamera.stopPreview();
    //                    mCamera.release();
    //                    mCamera = null;
                closeCamera();
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                preview = new CameraPreview(this, mCamera);
                mOverCameraView = new OverCameraView(this);
                mPreviewLayout.addView(preview);
                mPreviewLayout.addView(mOverCameraView);
                cameraPosition = 1;
                break;
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePhoto() {
        FileOutputStream fos = null;
        File cameraFolder = new File(Environment.getExternalStorageDirectory(), "Pictures/MagicPhoto");
        if (!cameraFolder.exists()) {
            if (!cameraFolder.mkdirs()) {
                 Log.e("CameraActivity:", "mkdirs error!");
            }
        }
        String imagePath = cameraFolder.getAbsolutePath() + File.separator + "image_" + System.currentTimeMillis() + ".jpeg";
        File imageFile = new File(imagePath);
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
            try {
                final int degree = BitmapUtils.readPictureDegree(imagePath);
                fos.close();
                Bitmap retBitmap = BitmapFactory.decodeFile(imagePath);
                if (cameraPosition == 0){
                     if (degree == 90) {
                            retBitmap = BitmapUtils.setTakePicktrueOrientation(270, retBitmap);
                    }else {
                        retBitmap = BitmapUtils.setTakePicktrueOrientation(180, retBitmap);
                    }
                }else {
                     retBitmap = BitmapUtils.setTakePicktrueOrientation(degree, retBitmap);
                }
                BitmapUtils.saveBitmapJpg(retBitmap, imagePath);

                String newPath = getExternalCacheDir() + File.separator + System.currentTimeMillis() + "temp.jpg";
                boolean isCompress = BitmapUtils.isCompressBitmapToFile(retBitmap, new File(newPath), 2);
                if (isCompress) {
                     imagePath = newPath;
                } else {
                 Toast.makeText(this, "Image processing failed!", Toast.LENGTH_SHORT).show();
             }

    //                    startActivityByPath(DetailActivity.class, imagePath);
            takePhotoFinish(imagePath);
            } catch (IOException e) {
            e.printStackTrace();
            }
            }
            finish();
        }
    }


    private void takePhotoFinish(String path){
        Intent i = new Intent();
        i.putExtra("imagePath", path);
        setResult(1001, i);
    //        finish();
    }

//    private void startActivityByPath(Class<?> mClass, String path) {
//        Intent intent = new Intent();
//        intent.putExtra("imagePath", path);
//        intent.setClass(this, mClass);
//        startActivity(intent);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
        if (faceLoading!=null){
            faceLoading.dismiss();
        }
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void closeCamera(){
        if(mCamera == null){
            return;
        }
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();

        if (preview!=null){
            preview.destroyDrawingCache();
            preview.mCamera = null;
        }

        mPreviewLayout.removeAllViews();
    }
}