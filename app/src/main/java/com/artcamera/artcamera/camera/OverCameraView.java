package com.artcamera.artcamera.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.List;

public class OverCameraView extends AppCompatImageView {
    private Context context;

    private Rect touchFocusRect;
    private Paint touchFocusPaint;

    private boolean isFoucuing;

    public OverCameraView(Context context) {
        this(context, null, 0);
    }

    public OverCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        touchFocusPaint = new Paint();
        touchFocusPaint.setColor(Color.GREEN);
        touchFocusPaint.setStyle(Paint.Style.STROKE);
        touchFocusPaint.setStrokeWidth(3);
    }

    public boolean isFoucuing() {
        return isFoucuing;
    }

    public void setFoucuing(boolean foucuing) {
        isFoucuing = foucuing;
    }


    public void setTouchFoucusRect(Camera camera, Camera.AutoFocusCallback autoFocusCallback, float x, float y) {

        touchFocusRect = new Rect((int) (x - 100), (int) (y - 100), (int) (x + 100), (int) (y + 100));


        int left = touchFocusRect.left * 2000 / getWindowWidth(context) - 1000;
        int top = touchFocusRect.top * 2000 / getWindowHeight(context) - 1000;
        int right = touchFocusRect.right * 2000 / getWindowWidth(context) - 1000;
        int bottom = touchFocusRect.bottom * 2000 / getWindowHeight(context) - 1000;

        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        final Rect targetFocusRect = new Rect(left, top, right, bottom);


        doTouchFocus(camera, autoFocusCallback, targetFocusRect);

        postInvalidate();
    }

    public void doTouchFocus(Camera camera, Camera.AutoFocusCallback autoFocusCallback, final Rect tfocusRect) {
        if (camera == null || isFoucuing) {
            return;
        }
        try {
            final List<Camera.Area> focusList = new ArrayList<>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters para = camera.getParameters();
            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);
            para.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            camera.cancelAutoFocus();
            camera.setParameters(para);
            camera.autoFocus(autoFocusCallback);
            isFoucuing = true;
        } catch (Exception e) {
            Log.e("hey", e.getMessage());
        }
    }


    public void disDrawTouchFocusRect() {

        touchFocusRect = null;

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawTouchFocusRect(canvas);
        super.onDraw(canvas);
    }


    public static int getWindowHeight(Context cxt) {
        WindowManager wm = (WindowManager) cxt
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();

    }


    public static int getWindowWidth(Context cxt) {
        WindowManager wm = (WindowManager) cxt
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();

    }

    private void drawTouchFocusRect(Canvas canvas) {
        if (null != touchFocusRect) {


            canvas.drawRect(touchFocusRect.left - 2, touchFocusRect.bottom, touchFocusRect.left + 20, touchFocusRect.bottom + 2, touchFocusPaint);
            canvas.drawRect(touchFocusRect.left - 2, touchFocusRect.bottom - 20, touchFocusRect.left, touchFocusRect.bottom, touchFocusPaint);

            canvas.drawRect(touchFocusRect.left - 2, touchFocusRect.top - 2, touchFocusRect.left + 20, touchFocusRect.top, touchFocusPaint);
            canvas.drawRect(touchFocusRect.left - 2, touchFocusRect.top, touchFocusRect.left, touchFocusRect.top + 20, touchFocusPaint);

            canvas.drawRect(touchFocusRect.right - 20, touchFocusRect.top - 2, touchFocusRect.right + 2, touchFocusRect.top, touchFocusPaint);
            canvas.drawRect(touchFocusRect.right, touchFocusRect.top, touchFocusRect.right + 2, touchFocusRect.top + 20, touchFocusPaint);

            canvas.drawRect(touchFocusRect.right - 20, touchFocusRect.bottom, touchFocusRect.right + 2, touchFocusRect.bottom + 2, touchFocusPaint);
            canvas.drawRect(touchFocusRect.right, touchFocusRect.bottom - 20, touchFocusRect.right + 2, touchFocusRect.bottom, touchFocusPaint);
        }
    }
}
