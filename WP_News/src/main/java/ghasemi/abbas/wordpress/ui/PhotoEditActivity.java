/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import androidx.core.content.FileProvider;

import ghasemi.abbas.wordpress.R;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ghasemi.abbas.wordpress.BuildApp;

public class PhotoEditActivity extends AppCompatActivity {

    static Bitmap bitmap;
    PhotoEditorView mPhotoEditorView;
    PhotoEditor mPhotoEditor;
    AppCompatEditText editText;
    RelativeLayout colors;
    ImageView back, share, pen, markerPen, a, undo, redo, eraser, red, orange, yellow,
            green, jadeGreen, blue, mauve, close, done, markerPenColor, aColor, penColor;
    int color;
    Animation show, hide;
    int status;

    public static void loadBitmapFromView(View v) {
        bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        v.requestLayout();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        init();

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + BuildApp.fontName + ".ttf"))
                .build();

        mPhotoEditorView.getSource().setImageBitmap(bitmap);
//        mPhotoEditorView.getSource().setScaleType(ImageView.ScaleType.FIT_XY);

        toolbar();

        center();

        colors();

        bottombar();
    }

    private void colors() {
        red.setOnClickListener(new colors());
        orange.setOnClickListener(new colors());
        yellow.setOnClickListener(new colors());
        green.setOnClickListener(new colors());
        jadeGreen.setOnClickListener(new colors());
        blue.setOnClickListener(new colors());
        mauve.setOnClickListener(new colors());
    }

    private void init() {

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        editText = findViewById(R.id.edit_text);
        a = findViewById(R.id.a);
        aColor = findViewById(R.id.a_color);
        close = findViewById(R.id.close);
        done = findViewById(R.id.done);
        colors = findViewById(R.id.colors);
        eraser = findViewById(R.id.eraser);
        back = findViewById(R.id.back);
        share = findViewById(R.id.share);
        pen = findViewById(R.id.pen);
        penColor = findViewById(R.id.pen_color);
        markerPen = findViewById(R.id.marker_pen);
        markerPenColor = findViewById(R.id.marker_pen_color);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        red = findViewById(R.id.red);
        mauve = findViewById(R.id.mauve);
        blue = findViewById(R.id.blue);
        jadeGreen = findViewById(R.id.jade_green);
        green = findViewById(R.id.green);
        yellow = findViewById(R.id.yellow);
        orange = findViewById(R.id.orange);

        show = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        hide = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.clearAnimation();
                colors.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.clearAnimation();
                colors.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void center() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (editText.getText().toString().trim().isEmpty()) {
                    editText.setText("");
                    return false;
                }
                mPhotoEditor.addText(editText.getText().toString(), color);
                close();
                return true;
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().isEmpty()) {
                    editText.setText("");
                    return;
                }
                mPhotoEditor.addText(editText.getText().toString(), color);
                close();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    private void close() {
        if (editText.getVisibility() == View.GONE) {
            return;
        }
        keyboard(false);
        editText.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
        editText.setText("");
    }

    private void keyboard(boolean show) {
        try {
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            if (show) {
                editText.startAnimation(this.show);
                imm.showSoftInput(editText, 0);
                buttonEnabled(false);
            } else {
                editText.startAnimation(hide);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                buttonEnabled(true);
            }
        } catch (Exception e) {
            //
        }
    }

    private void bottombar() {

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colors.getVisibility() == View.VISIBLE) {
                    colors.startAnimation(hide);
                    colors.setVisibility(View.GONE);
                }
                aColor.setVisibility(View.INVISIBLE);
                markerPenColor.setVisibility(View.INVISIBLE);
                penColor.setVisibility(View.INVISIBLE);
                mPhotoEditor.brushEraser();
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.undo();
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.redo();
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 1;
                setColors();
            }
        });
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 0;
                setColors();
            }
        });
        markerPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 2;
                setColors();
            }
        });
    }

    private void setColors() {
        if (colors.getVisibility() == View.GONE) {
            colors.startAnimation(show);
            colors.setVisibility(View.VISIBLE);
        } else {
            colors.startAnimation(hide);
            colors.setVisibility(View.GONE);
        }
    }

    private void buttonEnabled(boolean isEnabled) {
        pen.setEnabled(isEnabled);
        a.setEnabled(isEnabled);
        markerPen.setEnabled(isEnabled);
        undo.setEnabled(isEnabled);
        eraser.setEnabled(isEnabled);
        redo.setEnabled(isEnabled);
    }

    private void toolbar() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            boolean doing = false;

            @Override
            public void onClick(View v) {
                if (doing) {
                    return;
                }
                doing = true;
                mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {
                        try {
                            File file = new File(getExternalFilesDir(null), getString(R.string.app_name) + ".png");
                            if (file.exists()) {
                                Log.e("Sdsd", "sd");
                                file.delete();
                            }
                            file.createNewFile();
                            FileOutputStream outputStream = new FileOutputStream(file);
                            saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(PhotoEditActivity.this, getPackageName() + ".provider", file));
                            startActivity(Intent.createChooser(intent, "اشتراک خبر ..."));
                        } catch (Exception e) {
                            //
                        }
                        doing = false;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        doing = false;
                        Toast.makeText(PhotoEditActivity.this, "با عرض پوزش،خطائی پیش آمده است", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }

    private float getDP(int i) {
        return getResources().getDisplayMetrics().density * i;
    }

    class colors implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.red:
                    color = 0xffE53935;
                    break;
                case R.id.orange:
                    color = 0xffFB8C00;
                    break;
                case R.id.yellow:
                    color = 0xffFDD835;
                    break;
                case R.id.green:
                    color = 0xff43A047;
                    break;
                case R.id.jade_green:
                    color = 0xff00ACC1;
                    break;
                case R.id.blue:
                    color = 0xff1E88E5;
                    break;
                case R.id.mauve:
                    color = 0xff8E24AA;
                    break;
            }
            setColors();
            if (status == 0) {
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setBrushColor(color);
                mPhotoEditor.setBrushSize(getDP(5));
                mPhotoEditor.setOpacity(100);
                penColor.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                penColor.setVisibility(View.VISIBLE);
                markerPenColor.setVisibility(View.INVISIBLE);
                aColor.setVisibility(View.INVISIBLE);
                close();
            } else if (status == 1) {
                if (editText.getVisibility() == View.VISIBLE) {
                    return;
                }
                mPhotoEditor.setBrushDrawingMode(false);
                editText.setVisibility(View.VISIBLE);
                done.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                editText.requestFocus();
                aColor.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                aColor.setVisibility(View.VISIBLE);
                markerPenColor.setVisibility(View.INVISIBLE);
                penColor.setVisibility(View.INVISIBLE);
                keyboard(true);
            } else {
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setBrushColor(color);
                mPhotoEditor.setBrushSize(getDP(15));
                mPhotoEditor.setOpacity(50);
                markerPenColor.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                markerPenColor.setVisibility(View.VISIBLE);
                aColor.setVisibility(View.INVISIBLE);
                penColor.setVisibility(View.INVISIBLE);
                close();
            }
        }
    }
}
