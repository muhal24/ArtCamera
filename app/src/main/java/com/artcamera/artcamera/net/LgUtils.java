package com.artcamera.artcamera.net;

import android.content.Context;

import com.artcamera.artcamera.clr.s.DataTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LgUtils {
    private static int monkey = 432000;
    private static int second = 1000;

    private static String getCurrentTime(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentTime = dateFormat.format(date);
        return currentTime;
    }

    public static boolean isLgXXX(Context context){
        String oldTime = DataTool.readData(context,"lgTime");

        long longOld = timetoInt(oldTime);
        long longNew = timetoInt(getCurrentTime());
        long longExpend = longNew - longOld;
        longExpend = longExpend/second;

        if (longExpend<monkey){

            return false;
        }
        return true;

    }

    private static long timetoInt(String timeStr) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(timeStr);
            returnMillis = d.getTime();
        } catch (ParseException e) {

        }
        return returnMillis;
    }
}
