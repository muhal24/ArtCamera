package com.artcamera.artcamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;


import com.artcamera.artcamera.img.BitmapUtils;
import com.artcamera.artcamera.img.ContentUriUtils;
import com.artcamera.artcamera.log.FaceSheetD;
import com.artcamera.artcamera.log.FaceSheetDialog;
import com.artcamera.artcamera.net.LgUtils;
import com.artcamera.artcamera.tool.FaceLoading;
import com.artcamera.artcamera.tool.PermisstionToll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private ImageView fingerImageView;
    private ImageView startImageView;
    private FaceLoading faceLoading;
    private Handler handler;
    private String imagePath;
    private boolean isContinue;//need login
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        showLoading();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 200) {
                   hideLoading();
                   if (imagePath != null) {

                       Intent intent = new Intent(MainActivity.this, FaceActivity.class);
                       intent.putExtra("imagePath",imagePath);
                       intent.putExtra("isContinue",resultForLg());
                       startActivity(intent);
                   }
                }else if(message.what == 210){
//                    isContinue = (boolean)message.obj;
                    isContinue = true;
                    hideLoading();
                }else if(message.what == 211 || message.what == 212){
                    hideLoading();
                } else {
                    Toast.makeText(MainActivity.this, "Image processing failed!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hideLoading();
//            }
//        },2000);
        initUI();
        creatAnimation();

//        calculate();
    }



    private void initUI(){
        fingerImageView = findViewById(R.id.iv_finger);
        startImageView = findViewById(R.id.iv_start);

        startImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermisstionToll.checkPermission(MainActivity.this) == false){

                    return;
                }

                fingerImageView.clearAnimation();
                fingerImageView.setVisibility(View.GONE);

                FaceSheetDialog faceSheetDialog = new FaceSheetDialog(MainActivity.this, new FaceSheetD() {
                    @Override
                    public void faceSheetType(String type) {
                        if (type.equals("0")){
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, FaceCameraActivity.class);

                            startActivityForResult(intent,1000);
                        }else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            //  intent.putExtra("crop", true);
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, 0);
                        }
                    }
                });

//                startActivity(new Intent(MainActivity.this,FaceActivity.class));
            }
        });

    }

    private void creatAnimation(){
        ScaleAnimation animation = new ScaleAnimation(0.8f, 1.2f, 0.8f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f,1, 0.5f);
        animation.setDuration(800);

        animation.setFillAfter(true);

        animation.setRepeatCount(1000);

        fingerImageView.startAnimation(animation);

    }

    private void showLoading(){
        faceLoading = new FaceLoading(this);
    }
    private void hideLoading(){
        if (faceLoading!=null){
            faceLoading.dismiss();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                showLoading();
                imagePath = ContentUriUtils.getPath(this, data.getData());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileInputStream fis;
                        try {
                            fis = new FileInputStream(imagePath);
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            final int degree = BitmapUtils.readPictureDegree(imagePath);
                            Bitmap retBitmap = BitmapUtils.setTakePicktrueOrientation(degree, bitmap);
                            String newPath = getExternalCacheDir() + File.separator + System.currentTimeMillis() + "temp.jpg";
                            boolean isCompress = BitmapUtils.isCompressBitmapToFile(retBitmap, new File(newPath), 2);

                            Message message = new Message();
                            if (isCompress) {
                                String newPath2 = getExternalCacheDir() + File.separator + System.currentTimeMillis() + "temp2.jpg";
                                boolean pressImg = BitmapUtils.sizeCompress(retBitmap,new File(newPath2));
                                if (pressImg) {
                                    imagePath = newPath2;
                                    message.what = 200;
                                }
                            } else {
                                message.what = 500;
                            }
                            handler.sendMessage(message);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }else if(requestCode == 1000 && resultCode == 1001){
            imagePath = data.getStringExtra("imagePath");
            Message message = new Message();
            message.what = 200;
            handler.sendMessage(message);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

//    private void calculate(){
//        for (int i =0;i<7;i++){
//            String result = "";
//            for (int j =0;j<7;j++){
//                result += (new Random().nextInt(9)+",");
//            }
//            Log.e("TAG", "calculate: " +result );
//        }
//    }

    private boolean resultForLg(){
        if (isContinue){
            return LgUtils.isLgXXX(this);
        }else {
            return false;
        }

    }
}