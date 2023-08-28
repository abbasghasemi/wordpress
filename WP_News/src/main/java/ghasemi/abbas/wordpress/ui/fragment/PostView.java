/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ghasemi.abbas.wordpress.ui.PhotoEditActivity;
import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.BuildApp;
import ghasemi.abbas.wordpress.api.CheckNetworkStatus;
import ghasemi.abbas.wordpress.general.SaveModel;

public class PostView extends BaseFragment {
    private ImageView image;
    private TextView titleToolbar, author, date;
    private WebView contextWeb, web;
    private NestedScrollView nestedScrollView;
    private RelativeLayout toolbar;
    private boolean isMark, showWeb;
    private View view;
    private ProgressBar progressBar;
    private int tolbarColor;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment, R.layout.fragment_show_post);

        init();

        if (getDataArguments().get("url_image").toString().isEmpty()) {
            image.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        } else {
            Glide.with(getContext()).load(getDataArguments().get("url_image").toString()).into(image);
            titleToolbar.setVisibility(View.GONE);
            toolbar.setBackgroundColor(0);
            startScroll();
        }

        button();

        show();
    }

    private void startScroll() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (showWeb) {
                    return;
                }
                float y = image.getHeight();
                if (i1 <= y) {
                    if (titleToolbar.getVisibility() == View.VISIBLE) {
                        titleToolbar.setVisibility(View.GONE);
                    }
                    int x = (int) (i1 * 255 / y);
                    toolbar.setBackgroundColor(Color.argb(x, Color.red(tolbarColor), Color.green(tolbarColor), Color.blue(tolbarColor)));
                } else if (titleToolbar.getVisibility() == View.GONE) {
                    toolbar.setBackgroundColor(tolbarColor);
                    titleToolbar.setVisibility(View.VISIBLE);
                    requestTitle();
                }

            }
        });
    }

    private void requestTitle() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                titleToolbar.requestFocus();
            }
        }, 100);
    }

    private void button() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void show() {
        titleToolbar.setText(getDataArguments().get("title").toString());
        author.setText(String.format("نویسنده: %s", BuildApp.authorName));
        date.setText(String.format("%s", getDataArguments().get("date").toString()));
        contextWeb.getSettings().setJavaScriptEnabled(true);
        contextWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                BuildApp.setViewSite(getContext(), url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (CheckNetworkStatus.isOnline()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            web.reload();
                        }
                    }, 1000);
                }
            }


        });
        contextWeb.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                BuildApp.setViewSite(getContext(), url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!url.equals("about:blank")) {
                    view.goBack();
                    contextWeb.loadDataWithBaseURL(
                            "",
                            "<html>" +
                                    "<head>" +
                                    "<style type=\"text/css\">" +
                                    "@font-face {" +
                                    "font-family: sans;src: url(\"file:///android_asset/fonts/" + BuildApp.fontName + ".ttf\")}body {font-family: sans !important;font-size: light;text-align: justify;}*{max-width:100%}" +
                                    "</style>" +
                                    "</head>" +
                                    "<body dir='rtl'>" +
                                    getDataArguments().get("content").toString() +
                                    "</body></html>",
                            "text/html",
                            "UTF-8",
                            "");

                }
            }
        });
        contextWeb.loadDataWithBaseURL(
                "",
                "<html>" +
                        "<head>" +
                        "<style type=\"text/css\">" +
                        "@font-face {" +
                        "font-family: sans;src: url(\"file:///android_asset/fonts/" + BuildApp.fontName + ".ttf\")}body {font-family: sans !important;font-size: light;text-align: justify;}*{max-width:100%}" +
                        "</style>" +
                        "</head>" +
                        "<body dir='rtl'>" +
                        getDataArguments().get("content").toString() +
                        "</body></html>",
                "text/html",
                "UTF-8",
                "");
    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);
        view = findViewById(R.id.fake);
        toolbar = findViewById(R.id.toolbar);
        tolbarColor = ((ColorDrawable) toolbar.getBackground()).getColor();
        nestedScrollView = findViewById(R.id.nestedScrollView);
        titleToolbar = findViewById(R.id.title_toolbar);
        ImageView back = findViewById(R.id.back);
        image = findViewById(R.id.image);
        author = findViewById(R.id.author);
        date = findViewById(R.id.date);
        web = findViewById(R.id.web);
        contextWeb = findViewById(R.id.contextWeb);
        showBottomBarPost(true, true, isMark = BuildApp.hasID(getDataArguments().get("id").toString()));
    }

    @Override
    public void onClickBottomBarPost(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        switch (view.getId()) {
            case R.id.share:
                intent.putExtra(Intent.EXTRA_TEXT, getDataArguments().get("title").toString() + ":\n" + getDataArguments().get("url").toString());
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "اشتراک ..."));
                break;
            case R.id.screen:
                PhotoEditActivity.loadBitmapFromView(findViewById(R.id.relative));
                startActivity(new Intent(getContext(), PhotoEditActivity.class));
                break;
            case R.id.comments:
                showBottomBarPost(false);
                Comments baseFragment = new Comments();
                Bundle bundle = new Bundle();
                bundle.putString("post", getDataArguments().get("id").toString());
                bundle.putString("author", getDataArguments().get("author").toString());
                baseFragment.setArguments(bundle);
                startFragment(baseFragment);
                break;
            case R.id.fav:
                if (isMark) {
                    isMark = false;
                    ((ImageView) view).setImageResource(R.drawable.ic_turned_not);
                    BuildApp.removeBookMark(getDataArguments().get("id").toString());
                    break;
                }
                isMark = true;
                SaveModel saveModel = new SaveModel();
                saveModel.hashMapList.add(getDataArguments());
                ((ImageView) view).setImageResource(R.drawable.ic_turned);
                BuildApp.addBookMark(saveModel);
                break;
            default:
                showWeb = !showWeb;
                if (showWeb) {
                    ((ImageView) view).setColorFilter(getContext().getTheme().getResources().getColor( R.color.colorAccent));
                    if (web.getUrl() == null) {
                        progressBar.setVisibility(View.VISIBLE);
                        web.loadUrl(getDataArguments().get("url").toString());
                    }
                    web.setVisibility(View.VISIBLE);
                    contextWeb.setVisibility(View.GONE);
                    if (!getDataArguments().get("url_image").toString().isEmpty()) {
                        image.setVisibility(View.GONE);
                        this.view.setVisibility(View.VISIBLE);
                        titleToolbar.setVisibility(View.VISIBLE);
                        toolbar.setBackgroundColor(getContext().getTheme().getResources().getColor( R.color.colorPrimary));
                        nestedScrollView.setScrollY(0);
                        requestTitle();
                    }
                } else {
                    ((ImageView) view).setColorFilter(getResources().getColor(R.color.inActiveTabColor));
                    web.setVisibility(View.GONE);
                    contextWeb.setVisibility(View.VISIBLE);
                    if (!getDataArguments().get("url_image").toString().isEmpty()) {
                        image.setVisibility(View.VISIBLE);
                        this.view.setVisibility(View.GONE);
                        titleToolbar.setVisibility(View.GONE);
                        toolbar.setBackgroundColor(0);
                        nestedScrollView.setScrollY(0);
                    }
                }
        }
    }

    @Override
    public void onResumeFragment() {
        showBottomBarPost(true, true, isMark = BuildApp.hasID(getDataArguments().get("id").toString()));
        setButtonBarPostStatus(showWeb, isMark);
    }
}
