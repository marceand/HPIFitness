package com.marceme.hpifitness.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class AuthUtil {

    public static final String USER_ID = "user_id";

    private static SharedPreferences AuthCredential;

    public static void initSharedPref(Context context) {
        AuthCredential = context.getSharedPreferences("auth_hpi_fitness", Context.MODE_PRIVATE);
    }

    private static SharedPreferences getAuthCredentials() {
        return AuthCredential;
    }

    private static SharedPreferences.Editor editSharedPrefs() {
        return getAuthCredentials().edit();
    }

    @Nullable
    public static String getID(String key) {
        return AuthCredential.getString(key, null);
    }

    public static void setID(String key, String value) {
        editSharedPrefs().putString(key, value).commit();
    }

    public static boolean isAuthorized() {
        return getID(USER_ID) != null;
    }
























}
