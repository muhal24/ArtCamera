package com.artcamera.artcamera.log;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.artcamera.artcamera.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class FaceSheetDialog extends BottomSheetDialog {

    private FaceSheetD faceSheetD;
    private View view;

    public FaceSheetDialog(@NonNull Context context,FaceSheetD faceSheetD) {
        super(context, R.style.FaceDialogStyle);
        this.faceSheetD = faceSheetD;
        view = View.inflate(context, R.layout.face_sheet_dialog, null);
        setContentView(view);
        initView();
        show();
    }
    private void initView() {

        view.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faceSheetD!=null){
                    faceSheetD.faceSheetType("0");
                }
                dismiss();
            }
        });
        view.findViewById(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faceSheetD!=null){
                    faceSheetD.faceSheetType("1");
                }
                dismiss();
            }
        });

    }
}
