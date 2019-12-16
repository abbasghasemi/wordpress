/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.builder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.HashMap;
import java.util.Random;

import qasemi.abbas.wordpress.Application;
import qasemi.abbas.wordpress.Luancher;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.support.customtabs.CustomTabsIntent;

/**
 * Created by abbas qasemi on 05/16/2018.
 */

public class Builder {

    public static String nameAuthor = "abbas qasemi";
    public static String mail = "";
    public static boolean enablePhone = true;
    public static String phone = "";
    public static String telegram = ""; // می تونی آدرس پیامرسان یا سایت های دیگری مثل آپارات ، سایت خودتون و ... را قرار دهی.
    public static String instagram = "";// می تونی آدرس پیامرسان یا سایت های دیگری مثل آپارات ، سایت خودتون و ... را قرار دهی
    public static String nameFont = "sans";
    public static String linkDownload = "https://cafebazaar.ir/app/" + Application.context.getPackageName();
    public static String nameSite = "کافه بازار"; // متناسب با linkDownload تغییر نماید.

    // 1 or 2 or 3
    public static int typeShow = 2; // (new Random()).nextInt(3) + 1
    /**
     * formatDate
     * Year:   Y = 1396  &  y = 96
     * Month:  M = مرداد  &  m = 05
     * ِ Day:    D = دوشنبه & d = 21
     * Hour:   H = 13  & h = 1
     * Minute: I = error & i = 22
     * Second: S = sp & s = 47
     * example :
     * 1- format Y-m-d H:i:s --> 2018-05-25 20:50:06
     * 2- format y/m/d --> 18/05/25
     * 3- format Y+M+D h --> 2118+Feb+Wed 08
     */
    public static String formatDate = "Y-m-d"; // default Y-m-d H:i:s
    public static boolean ShowSplash = true; // true or false
    public static int TimeSplash = 1; // Second ,example 1 - 4
    public static boolean DoubleClickToExit = true; // true or false
    public static boolean Divider = false; // true or false
    public static int LimitPage = 10; // 10
    public static int Timeout = 12; // second
    public static int TimeDistoryCashe = 5; // 5 minute
    public static String FilterCategory = ""; // example 1__2__7__10__12
    public static String FilterPage = ""; // example 1__2__7__10__12
    public static String FilterSearch = "attachment"; // page or post or ... , example page__post__...
    public static boolean enableDate = true;
    public static boolean enablePage = true;

    ///////// Don't Change /////////
    public static void Share() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "اپلیکیشن " + Luancher.activity.getResources().getString(R.string.app_name) + " رو از " + Builder.nameSite + " دانلود کن:\n" + Builder.linkDownload);
            intent.setType("text/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Luancher.activity.startActivity(Intent.createChooser(intent, "برنامه ای را جهت ارسال انتخاب نمائید"));
        } catch (Exception e) {
            Log.e("rr", e.toString());
        }
    }

    public static void setViewSite(Activity c, String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(getItem(ContextCompat.getColor(c, R.color.colorPrimary),ContextCompat.getColor(c, R.color.d_colorPrimary)));
        intentBuilder.setSecondaryToolbarColor(getItem(ContextCompat.getColor(c, R.color.colorPrimaryDark),ContextCompat.getColor(c, R.color.d_colorPrimaryDark)));
        intentBuilder.setStartAnimations(c, R.anim.fade_in, R.anim.fade_out);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(c, uri);
    }

    public static void addBookMark(SaveModel saveModel) {
        SaveModel newSaveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (newSaveModel != null) {
            saveModel.hashMapList.addAll(newSaveModel.hashMapList);
        }
        Application.easySave.saveModel("book_mark_info", saveModel);
    }

    public static void removeBookMark(String id) {
        SaveModel saveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (saveModel != null) {
            SaveModel newSaveModel = new SaveModel();
            for (HashMap<String, Object> hashMap : saveModel.hashMapList) {
                if (!hashMap.get("id").equals(id)) {
                    newSaveModel.hashMapList.add(hashMap);
                }
            }
            Application.easySave.saveModel("book_mark_info", newSaveModel.hashMapList.isEmpty() ? null : newSaveModel);
        }
    }

    public static boolean hasID(String id) {
        SaveModel saveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (saveModel != null) {
            for (HashMap<String, Object> hashMap : saveModel.hashMapList) {
                if (hashMap.get("id").equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getCountPx() {
        DisplayMetrics displayMetrics = Application.context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density / 140);
    }

    public static int getItem(int layout1, int layout2){
        if (TinyData.getInstanse().getString("is_dark_theme").equals("1")) {
            return layout2;
        }
        return layout1;
    }

    public static void setTheme(AppCompatActivity activity) {
        if (TinyData.getInstanse().getString("is_dark_theme").equals("1")) {
            activity.setTheme(R.style.AppThemeDark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(0xff1d2122);
                activity.getWindow().setNavigationBarColor(Color.BLACK);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.getColor(R.color.colorPrimary) == 0xffffffff) {
                Window window = activity.getWindow();
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(ColorUtils.blendARGB(activity.getResources().getColor(R.color.colorPrimary), Color.BLACK, 0.2f));
            }
        }
    }
}