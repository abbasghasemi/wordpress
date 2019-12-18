/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import qasemi.abbas.wordpress.BuildConfig;
import qasemi.abbas.wordpress.Luancher;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.TinyData;

public class Menu extends BaseFragment {

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(Builder.getItem(R.layout.menu, R.layout.d_menu), null));
        if (!Builder.enablePage) {
            findViewById(R.id.pages).setVisibility(View.GONE);
            findViewById(R.id.pages_divider).setVisibility(View.GONE);
        }
        findViewById(R.id.pages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new Page());
            }
        });
        if (!Builder.enableDate) {
            findViewById(R.id.date_name).setVisibility(View.GONE);
            findViewById(R.id.date_name_divider).setVisibility(View.GONE);
        }
        findViewById(R.id.date_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new Date());
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Builder.Share();
            }
        });

        if (!Builder.enablePhone) {
            findViewById(R.id.call).setVisibility(View.GONE);
            findViewById(R.id.call_divider).setVisibility(View.GONE);
        }
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + Builder.phone));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "هیچ برنامه ای نمی تواند این کار را انجام دهد.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{Builder.mail});
                i.putExtra(Intent.EXTRA_SUBJECT, "V:" + BuildConfig.VERSION_NAME + " " + getResources().getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "هیچ برنامه ای نمی تواند این کار را انجام دهد.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.telegram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Builder.telegram));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "هیچ برنامه ای نمی تواند این کار را انجام دهد.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Builder.instagram));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "هیچ برنامه ای نمی تواند این کار را انجام دهد.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext(), Builder.getItem(5, 4))
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.msg_service))
                        .setNegativeButton("لغو", null)
                        .show();
            }
        });
        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new AboutUs());
            }
        });

        SwitchCompat switchCompat = findViewById(R.id.setting);
        switchCompat.setChecked(TinyData.getInstanse().getString("is_dark_theme").equals("1"));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TinyData.getInstanse().putString("is_dark_theme", "1");
                } else {
                    TinyData.getInstanse().putString("is_dark_theme", "");
                }
                startActivity(new Intent(getContext(), Luancher.class));
                getContext().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                getContext().finish();
            }
        });
    }

}
