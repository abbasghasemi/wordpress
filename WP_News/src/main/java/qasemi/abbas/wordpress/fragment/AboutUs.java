/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import qasemi.abbas.wordpress.BuildConfig;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Builder;


public class AboutUs extends BaseFragment {

    @Override
    public void onCreateView(@NonNull  BaseFragment baseFragment,@LayoutRes int id) {
        super.onCreateView(baseFragment, Builder.getItem(R.layout.about_us,R.layout.d_about_us));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
        TextView textView = findViewById(R.id.about_text);
        textView.setText(getResources().getString(R.string.msg_about));
        TextView version = findViewById(R.id.version);
        version.setText(String.format("%s نسخه %s", getResources().getString(R.string.app_name), BuildConfig.VERSION_NAME));
    }
}
