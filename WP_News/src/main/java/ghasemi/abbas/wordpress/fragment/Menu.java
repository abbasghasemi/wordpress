/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.fragment;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import ghasemi.abbas.wordpress.BuildConfig;
import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.builder.BuildApp;
import ghasemi.abbas.wordpress.builder.TinyData;

public class Menu extends BaseFragment {
    private BottomSheetDialog bottomSheetDialog;
    private TextView nightType;

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(R.layout.fragment_menu, null));
        if (!BuildApp.enablePage) {
            findViewById(R.id.pages).setVisibility(View.GONE);
            findViewById(R.id.pages_divider).setVisibility(View.GONE);
        }
        findViewById(R.id.pages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new Page());
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildApp.Share();
            }
        });

        if (TextUtils.isEmpty(BuildApp.phone)) {
            findViewById(R.id.call).setVisibility(View.GONE);
            findViewById(R.id.call_divider).setVisibility(View.GONE);
        }
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + BuildApp.phone));
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
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{BuildApp.mail});
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
                    intent.setData(Uri.parse(BuildApp.telegram));
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
                    intent.setData(Uri.parse(BuildApp.instagram));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "هیچ برنامه ای نمی تواند این کار را انجام دهد.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildApp.setCustomDialog(new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.msg_service))
                        .setNegativeButton("لغو", null)
                        .show());
            }
        });
        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new AboutUs());
            }
        });


        nightType = findViewById(R.id.night_type);
        String nightMode = TinyData.getInstance().getString("nightMode", "system");
        if (nightMode.equals("system")) {
            nightType.setText("پیشفرض سیستم");
        } else if (nightMode.equals("off")) {
            nightType.setText("غیر فعال");
        }
        findViewById(R.id.l_theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog != null) {
                    return;
                }
                bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.Theme_Design_BottomSheetDialog);
                bottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_select_night_mode, null));
                bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        bottomSheetDialog = null;
                    }
                });
                bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(0);
                bottomSheetDialog.show();
                bottomSheetDialog.findViewById(R.id.night_system).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog = null;
                        String nightMode = TinyData.getInstance().getString("nightMode", "system");
                        if (!nightMode.equals("system")) {
                            TinyData.getInstance().putString("nightMode", "system");
                            nightType.setText("پیشفرض سیستم");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        }
                    }
                });
                bottomSheetDialog.findViewById(R.id.night_off).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog = null;
                        String nightMode = TinyData.getInstance().getString("nightMode", "system");
                        if (!nightMode.equals("off")) {
                            TinyData.getInstance().putString("nightMode", "off");
                            nightType.setText("غیر فعال");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                    }
                });
                bottomSheetDialog.findViewById(R.id.night_on).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog = null;
                        String nightMode = TinyData.getInstance().getString("nightMode", "system");
                        if (!nightMode.equals("on")) {
                            TinyData.getInstance().putString("nightMode", "on");
                            nightType.setText("فعال");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                    }
                });
            }
        });
    }

}
