package com.artcamera.artcamera.tool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadTool {

     public static String saveImageToGallery(Context context,Bitmap mBitmap) {
         String result = "success";
         if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

             result = "sdcard not working";
             return result;
         }

         File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
         if (!appDir.exists()) {
             appDir.mkdir();
         }
         String fileName = System.currentTimeMillis() + ".jpg";
         File file = new File(appDir, fileName);
         try {
             FileOutputStream fos = new FileOutputStream(file);
             mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
             fos.flush();
             fos.close();
         } catch (FileNotFoundException e) {
             e.printStackTrace();
             result = e.toString();
             return result;
         } catch (IOException e) {
             e.printStackTrace();
             result = e.toString();
             return result;
         }

         try {
             MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
         } catch (FileNotFoundException e) {
             e.printStackTrace();
             result = e.toString();
             return result;
         }

         context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")));
         return result;
       }
}
