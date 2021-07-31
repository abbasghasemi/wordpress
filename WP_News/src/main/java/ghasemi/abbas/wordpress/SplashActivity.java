/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.widget.Toast;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ghasemi.abbas.wordpress.api.CheckNetworkStatus;

import static ghasemi.abbas.wordpress.builder.BuildApp.showSplash;
import static ghasemi.abbas.wordpress.builder.BuildApp.splashTime;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle superSave) {
        super.onCreate(superSave);

        if (!showSplash) {
            main();
            return;
        }

        setContentView(R.layout.activity_splash);
        if (!CheckNetworkStatus.isOnline())
            Toast.makeText(this, "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                main();
            }
        }, splashTime * 1000);
    }

    private void main() {
        if (isFinishing()) {
            return;
        }
        startActivity(new Intent(SplashActivity.this, LuancherActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}