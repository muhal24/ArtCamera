package com.artcamera.artcamera.tool;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;


import com.artcamera.artcamera.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class FaceLoading extends Dialog {
    private View view;
    private ImageView loadingImageView;
    private Context context;
    public FaceLoading(@NonNull Context context) {
        super(context, R.style.FaceDialogStyle);
        this.context = context;
        view = View.inflate(context, R.layout.face_loading, null);
        initUI();
        show();
    }

    private void initUI(){

        loadingImageView = view.findViewById(R.id.iv_loading);
        Glide.with(context).load(R.drawable.face_loading).diskCacheStrategy(DiskCacheStrategy.NONE).into(loadingImageView);
        setContentView(view);
//        setCancelable(false);
        show();
    }
}
