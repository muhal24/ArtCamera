package com.artcamera.artcamera.clr.t;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomTime {
    private static int monkey = 432000;
    private static int second = 1000;

    public static String getCurrentTime(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentTime = dateFormat.format(date);
        return currentTime;
    }

    public static boolean timeDifferent(String oldTime, String newTime){

        long longOld = TimeStrtoInt(oldTime);
        long longNew = TimeStrtoInt(newTime);
        long longExpend = longNew - longOld;
        longExpend = longExpend/second;

        if (longExpend<monkey){

            return false;
        }
        return true;

    }

    private static long TimeStrtoInt(String timeStr) {
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
