package com.artcamera.artcamera.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class BeautifulFilter {
    public BeautifulFilter() {
    }

    public static Bitmap changeToLomo(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap returnBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas canvas = new Canvas(returnBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float scaleValue = 0.7480315F;
        ColorMatrix scaleMatrix = new ColorMatrix();
        scaleMatrix.reset();
        scaleMatrix.setScale((float)((double)scaleValue + 0.1D), (float)((double)scaleValue + 0.2D), (float)((double)scaleValue + 0.1D), 0.9F);
        ColorMatrix satMatrix = new ColorMatrix();
        satMatrix.reset();
        satMatrix.setSaturation(0.85F);
        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.reset();
        hueMatrix.setRotate(0, 8.0F);
        hueMatrix.setRotate(1, 8.0F);
        hueMatrix.setRotate(2, 8.0F);
        ColorMatrix allMatrix = new ColorMatrix();
        allMatrix.reset();
        allMatrix.postConcat(scaleMatrix);
        allMatrix.postConcat(satMatrix);
        paint.setColorFilter(new ColorMatrixColorFilter(allMatrix));
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);
        double radius = (double)(width / 2) * 95.0D / 100.0D;
        double centerX = (double)((float)width / 2.0F);
        double centerY = (double)((float)height / 2.0F);
        int[] pixels = new int[width * height];
        returnBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        double pixelsFalloff = 3.5D;

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                double dis = Math.sqrt(Math.pow(centerX - (double)x, 2.0D) + Math.pow(centerY - (double)y, 2.0D));
                int currentPos = y * width + x;
                if (dis > radius) {
                    int pixR = Color.red(pixels[currentPos]);
                    int pixG = Color.green(pixels[currentPos]);
                    int pixB = Color.blue(pixels[currentPos]);
                    double scaler = scaleFunction(radius, dis, pixelsFalloff);
                    scaler = Math.abs(scaler);
                    Log.d("Ragnarok", "scaler=" + scaler);
                    int newR = (int)((double)pixR - scaler);
                    int newG = (int)((double)pixG - scaler);
                    int newB = (int)((double)pixB - scaler);
                    newR = Math.min(255, Math.max(0, newR));
                    newG = Math.min(255, Math.max(0, newG));
                    newB = Math.min(255, Math.max(0, newB));
                    pixels[currentPos] = Color.argb(255, newR, newG, newB);
                }
            }
        }

        returnBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return returnBitmap;
    }

    private static double scaleFunction(double radius, double dis, double pixelsFallof) {
        return 1.0D - Math.pow((dis - radius) / pixelsFallof, 2.0D);
    }
}
