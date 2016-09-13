package com.marceme.hpifitness.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Marcel on 9/12/2016.
 */
public class Helper {

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
}
