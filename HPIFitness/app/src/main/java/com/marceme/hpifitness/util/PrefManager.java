package com.marceme.hpifitness.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class PrefManager {

    public static final String USER_ID = "user_id";
    public static final String USER_WALK = "user_walk";

    private static SharedPreferences PrefController;

    public static void initSharedPref(Context context) {
        PrefController = context.getSharedPreferences("auth_hpi_fitness", Context.MODE_PRIVATE);
    }

    private static SharedPreferences getAuthCredentials() {
        return PrefController;
    }

    private static SharedPreferences.Editor editSharedPrefs() {
        return getAuthCredentials().edit();
    }

    @Nullable
    public static String getID(String key) {
        return PrefController.getString(key, null);
    }

    public static boolean getUserWalk(String key) {
        return PrefController.getBoolean(key, false);
    }

    public static void setID(String key, String value) {
        editSharedPrefs().putString(key, value).commit();
    }

    public static void setUserWalk(String key, boolean value) {
        editSharedPrefs().putBoolean(key, value).commit();
    }

    public static boolean isAuthorized() {
        return getID(USER_ID) != null;
    }

    public static boolean isUserWalking() {
        return getUserWalk(USER_WALK);
    }























}
