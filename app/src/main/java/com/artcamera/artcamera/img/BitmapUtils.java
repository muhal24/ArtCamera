package com.artcamera.artcamera.img;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtils {

    public static Bitmap setTakePicktrueOrientation(int id, Bitmap bitmap) {

//        if (bitmap.getWidth() < bitmap.getHeight()) {
//            return bitmap;
//        }
        Camera.CameraInfo info = new Camera.CameraInfo();
//        Camera.getCameraInfo(id, info);
//        bitmap = rotaingImageView(id, info.orientation, bitmap);
        bitmap = rotaingImageView(0,id, bitmap);
        return bitmap;
    }

    private static Bitmap rotaingImageView(int id, int angle, Bitmap bitmap) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        if (id == 1) {
            matrix.postScale(-1, 1);
        }

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static boolean SaveJpg(ImageView view) {
        try {
            Drawable drawable = view.getDrawable();
            if (drawable == null) {
                return false;
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri dataUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri fileUri = view.getContext().getContentResolver().insert(dataUri, values);
            if (fileUri == null) {
                return false;
            }
            OutputStream outStream = view.getContext().getContentResolver().openOutputStream(fileUri);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(fileUri);
            view.getContext().sendBroadcast(intent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveBitmapJpg(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            boolean b = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isCompressBitmapToFile(Bitmap image, File file, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int quality = 90;
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        Log.d("Bitmap", "before==" + baos.toByteArray().length);

        while (baos.toByteArray().length / (1024 * 1024) > maxSize) {
            baos.reset();
            quality -= 10;

            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        Log.d("Bitmap", "after==" + baos.toByteArray().length / (1024 * 1024));
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static boolean sizeCompress(Bitmap bmp, File file) {

        int ratio = 2;

        int width = bmp.getWidth()/ratio;
        int height = bmp.getHeight()/ratio;

        while (width>=4096 || height>=4096){
            ratio++;
            width = bmp.getWidth()/ratio;
            height = bmp.getHeight()/ratio;

        }
        if (width<256 || height<256){
            width = 256;
            height = 256;

        }


//        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
//        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        Rect rect = new Rect(0, 0, width, height);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e("TAG", "readPictureDegree: orientation-------->"+orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return degree;
    }
}
