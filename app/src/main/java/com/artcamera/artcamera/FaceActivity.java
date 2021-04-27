package com.artcamera.artcamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.artcamera.artcamera.filter.BeautifulFilter;
import com.artcamera.artcamera.skin.SkinTool;
import com.artcamera.artcamera.tool.DownloadTool;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImage;


public class FaceActivity extends AppCompatActivity {
    private ImageView backImageView, faceImageView, downloadImageView;
    private LinearLayout faceImageLinaer;
    private Handler handler;
    private boolean isContinue;

    private GPUImage gpuImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        initUI();
        initFunc();
        loadLocalPic();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation();
            }
        },100);
    }

    private void loadLocalPic(){
        String path  = getIntent().getStringExtra("imagePath");
        File file = new File(path);

        if(file.exists()){

            Bitmap bm = BitmapFactory.decodeFile(path);

            faceImageView.setImageBitmap(SkinTool.doDetect(bm));


          //Bitmap bm2 = WhiteFilter.changeToGray(bm);
            Bitmap bm2 = BeautifulFilter.changeToLomo(bm);



           faceImageView.setImageBitmap(bm2);

//            gpuImage = new GPUImage(this);
////            gpuImage.setImage(bm);

////
//////            GPUImageSepiaFilter
//////            GPUImageSketchFilter
////            //GPUImageEmbossFilter
////            //GPUImageFalseColorFilter
////            //GPUImageGlassSphereFilter
////            //GPUImageGrayscaleFilter
////            //GPUImageKuwaharaFilter
////            //GPUImageSwirlFilter
////            gpuImage.setFilter(new GPUImageWeakPixelInclusionFilter());
////            Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();

////            faceImageView.setImageBitmap(bitmap);

//            Bitmap bm2 = ImageFilter.applyFilter(bm, ImageFilter.Filter.LIGHT);
//            faceImageView.setImageBitmap(bm2);

        }
    }

    private void initUI() {
        backImageView = findViewById(R.id.iv_back);
        faceImageView = findViewById(R.id.iv_faceImageView);

        downloadImageView = findViewById(R.id.iv_download);
        faceImageLinaer = findViewById(R.id.faceImageLinaer);
        faceImageLinaer.setVisibility(View.INVISIBLE);


        isContinue = getIntent().getBooleanExtra("isContinue",false);

//        changeView();
    }


    private void initFunc(){
        downloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                animation();

//                Resources resources = getResources();
//                Drawable drawableBg = resources.getDrawable(R.drawable.test);

                faceImageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(faceImageView.getDrawingCache());
                faceImageView.setDrawingCacheEnabled(false);
               String result = DownloadTool.saveImageToGallery(FaceActivity.this,bitmap);
               if (result.equals("success")){
                   Toast.makeText(FaceActivity.this,"Picture successfully saved to album",Toast.LENGTH_LONG).show();
               }else {
                   Toast.makeText(FaceActivity.this,result,Toast.LENGTH_LONG).show();
               }
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                animation();
            }
        });


    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void animation(){

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -faceImageLinaer.getHeight(), 0);
        translateAnimation.setDuration(1000);
        faceImageLinaer.setAnimation(translateAnimation);
        faceImageLinaer.startAnimation(translateAnimation);
        faceImageLinaer.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 310 && resultCode == 320){
            isContinue = false;

        }
    }
}