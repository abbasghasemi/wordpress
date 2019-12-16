/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.CheckNetworkStatus;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static qasemi.abbas.wordpress.builder.Builder.ShowSplash;
import static qasemi.abbas.wordpress.builder.Builder.TimeSplash;

public class Start extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle superSave) {
        Builder.setTheme(this);
        super.onCreate(superSave);

        if (!ShowSplash) {
            main();
            return;
        }

        setContentView(Builder.getItem(R.layout.start,R.layout.d_start));
        if (!CheckNetworkStatus.isOnline())
            Toast.makeText(this, "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                main();
            }
        }, TimeSplash * 1000);
    }

    private void main() {
        if (isFinishing()) {
            return;
        }
        startActivity(new Intent(Start.this, Luancher.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}