/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.builder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import qasemi.abbas.wordpress.Application;

public class TinyData {

    private static TinyData tinyData;
    private SharedPreferences sharedPreferences;

    public static TinyData getInstanse() {
        if (tinyData == null) {
            tinyData = new TinyData(Application.context);
        }
        return tinyData;
    }

    private TinyData(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void putString(String key, String v) {
        sharedPreferences.edit().putString(key, v).apply();
    }

    public void putStringCashe(String key) {
        String data = getString("cashe_info");
        if (!data.contains(key)) {
            String k = data + ":" + key + ":";
            putString("cashe_info", k);
        }
        putString("time_last_" + key, String.valueOf(System.currentTimeMillis()));
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }
}
