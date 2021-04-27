package com.artcamera.artcamera.clr.s;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DataTool {
    public static void saveData(Context context,String key, String data){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, data);
        editor.commit();
    }

    public static String readData(Context context,String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String content = sharedPreferences.getString(key,"0");
        return content;
    }
}
