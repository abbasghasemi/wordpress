/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import android.util.DisplayMetrics;

import java.util.HashMap;

import ghasemi.abbas.wordpress.general.SaveModel;
import ghasemi.abbas.wordpress.ui.MainActivity;
import ghasemi.abbas.wordpress.support.customtabs.CustomTabsIntent;

public class BuildApp {

    public static String authorName = "abbas ghasemi";
    public static String mail = "";
    public static String phone = "";
    public static String telegram = "https://t.me/telegram"; // می تونی آدرس پیامرسان یا سایت های دیگری مثل آپارات ، سایت خودتون و ... را قرار دهی.
    public static String instagram = "https://instagram.com/p/instagram";// می تونی آدرس پیامرسان یا سایت های دیگری مثل آپارات ، سایت خودتون و ... را قرار دهی
    public static String fontName = "sans";
    public static String download = "https://cafebazaar.ir/app/" + Application.context.getPackageName();
    public static String siteName = "کافه بازار"; // متناسب با linkDownload تغییر نماید.
    public static int showType = 2; // 1 or 2 or 3
    public static boolean showSplash = true; // true or false
    public static int splashTime = 1; // Second ,example 1 - 4
    public static boolean doubleClickForExit = true; // true or false
    public static boolean divider = false; // true or false
    public static int limitPage = 10; // 10
    public static int timeout = 12; // second
    public static int timeDistoryCashe = 5; // 5 minute
    public static boolean enablePage = true;

    ///////// Don't Change /////////
    public static void Share() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "اپلیکیشن " + MainActivity.activity.getResources().getString(R.string.app_name) + " رو از " + BuildApp.siteName + " دانلود کن:\n" + BuildApp.download);
            intent.setType("text/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MainActivity.activity.startActivity(Intent.createChooser(intent, "برنامه ای را جهت ارسال انتخاب نمائید"));
        } catch (Exception e) {
            //
        }
    }

    public static void setViewSite(Activity activity, String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(activity.getTheme().getResources().getColor( R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(activity.getTheme().getResources().getColor(R.color.colorPrimaryDark));
        intentBuilder.setStartAnimations(activity, R.anim.fade_in, R.anim.fade_out);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(activity, uri);
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

    public static void setCustomDialog(AlertDialog alertDialog) {
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_bg);
    }
}