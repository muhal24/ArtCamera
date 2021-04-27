package com.artcamera.artcamera.skin;

import android.graphics.Bitmap;
import android.util.Log;

public class SkinTool {

    private static int[] detectSkin(int[] pixels, int width, int height){
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                int pixel = pixels[j*width + i];
                int red = (pixel>>16) & 0xff;
                int green = (pixel>>8) & 0xff;
                int blue = pixel & 0xff;

                if ((red>95 && green>40 && blue>20 && Math.abs(red-green)>15 &&
                        ((max(red, green, blue) - min(red, green, blue))>15) &&
                        red > green && red > blue) ||
                        (red>220 && green>210 && blue>170 &&
                                Math.abs(red-green)<=15 && red>blue && green>blue)) {

                    int orgin = pixels[j*width + i];
                    Log.e("TAG", "detectSkin: "+red+":"+green+":"+blue );
                    pixels[j*width + i] = -1;

                    String skin = "";
                    if (red>220 && green>220 && blue>220){
                        skin = "white";
                    }else {

                    }
                }
            }
        }
        return pixels;
    }


    private static int max(int x, int y, int z){
        int max = x;
        if(y > max){
            max = y;
        }
        if(z > max){
            max = z;
        }
        return max;
    }


    private static int min(int x, int y, int z){
        int min = x;
        if(y < min){
            min = y;
        }
        if(z < min){
            min = z;
        }
        return min;
    }


    public static Bitmap doDetect(Bitmap bitmap){
        if(bitmap == null){
//            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        pixels = detectSkin(pixels, width, height);
        Bitmap dst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        dst.setPixels(pixels, 0, width, 0,0, width, height);
//        mHandler.obtainMessage(CODE_SHOW, dst).sendToTarget();
        return dst;
    }
}
