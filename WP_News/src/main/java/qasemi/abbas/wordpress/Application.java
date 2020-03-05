/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress;

import android.annotation.SuppressLint;
import android.content.Context;

import br.vince.easysave.EasySave;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.TinyData;

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
                                .setDefaultFontPath("fonts/" + Builder.nameFont + ".ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        distoryCashe();
    }

    private void distoryCashe() {
        long timeNow = System.currentTimeMillis();
        String keys = TinyData.getInstanse().getString("cashe_info");
        if (keys.isEmpty()) {
            return;
        }
        long timer = Builder.TimeDistoryCashe * 30 * 1000;
        String[] data = keys.split("::");
        StringBuilder newKeys = new StringBuilder();
        for (String key : data) {
            key = key.replace(":", "");
            String time = TinyData.getInstanse().getString("time_last_" + key);
            if (timeNow - Long.parseLong(time) >= timer) {
                easySave.saveModel(key, null);
            } else {
                newKeys.append(":").append(key).append(":");
            }
        }
        TinyData.getInstanse().putString("cashe_info", newKeys.toString());
    }
}

