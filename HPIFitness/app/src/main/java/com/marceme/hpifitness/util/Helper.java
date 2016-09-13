package com.marceme.hpifitness.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Marcel on 9/12/2016.
 */
public class Helper {

    public static final String ACTION_NAME_SPACE = "com.marceme.hpifitness.LocationService";
    public static final String INTENT_EXTRA_RESULT_CODE = "resultCode";
    public static final String INTENT_USER_LAT_LNG = "userLatLng";

    public static AlertDialog displayMessageToUser(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

    public static ProgressDialog displayProgressDialog(Context context, boolean cancelable, String message){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(cancelable);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static int secondToMinuteConverter(long seconds){
        return (int)seconds/60;
    }
    public static float meterToMileConverter(float meter){
        return meter/1609;
    }

    public static int getNumberOfMilestones(float meter){
        return (int)meter/305; // 1000 feet -> 304.8
    }

    public static float calculatePace(long time, float distance){
        return secondToMinuteConverter(time)/meterToMileConverter(distance); // 1000 feet -> 304.8
    }

    public static String secondToHHMMSS(long secondsCount){

        long seconds = secondsCount %60;
        secondsCount -= seconds;
        long minutesCount = secondsCount / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;
        long hoursCount = minutesCount / 60;
        return "" + hoursCount + ":" + minutes + ":" + seconds;
    }
}
