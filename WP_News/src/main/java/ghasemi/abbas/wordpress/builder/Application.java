/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.builder;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import br.vince.easysave.EasySave;
import ghasemi.abbas.wordpress.R;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class Application extends android.app.Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    @SuppressLint("StaticFieldLeak")
    public static EasySave easySave;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        easySave = new EasySave(context);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/" + BuildApp.fontName + ".ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        checkNightMode();

        distoryCashe();
    }

    private void checkNightMode() {
        String nightMode = TinyData.getInstance().getString("nightMode", "system");
        if (nightMode.equals("system")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (nightMode.equals("off")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void distoryCashe() {
        long timeNow = System.currentTimeMillis();
        String keys = TinyData.getInstance().getString("cashe_info");
        if (keys.isEmpty()) {
            return;
        }
        long timer = BuildApp.timeDistoryCashe * 30 * 1000;
        String[] data = keys.split("::");
        StringBuilder newKeys = new StringBuilder();
        for (String key : data) {
            key = key.replace(":", "");
            String time = TinyData.getInstance().getString("time_last_" + key);
            if (timeNow - Long.parseLong(time) >= timer) {
                easySave.saveModel(key, null);
            } else {
                newKeys.append(":").append(key).append(":");
            }
        }
        TinyData.getInstance().putString("cashe_info", newKeys.toString());
    }
}

