/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
  */
package ghasemi.abbas.wordpress.fragment;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import ghasemi.abbas.wordpress.BuildConfig;
import ghasemi.abbas.wordpress.R;


public class AboutUs extends BaseFragment {

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment,@LayoutRes int id) {
        super.onCreateView(baseFragment, R.layout.fragment_about_us);
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
