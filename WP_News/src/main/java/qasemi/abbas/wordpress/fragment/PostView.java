/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import qasemi.abbas.wordpress.PhotoEdit;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.CheckNetworkStatus;
import qasemi.abbas.wordpress.builder.SaveModel;

public class PostView extends BaseFragment {
    ImageView back, image;
    TextView title_toolbar, author, date;
    WebView contextWeb, web;
    NestedScrollView nestedScrollView;
    RelativeLayout toolbar;
    boolean isMark, showWeb;
    View view;
    ProgressBar progressBar;


    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment,Builder.getItem(R.layout.show_post,R.layout.d_show_post));

        init();

        if (getPost().get("url_image").toString().isEmpty()) {
            image.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        } else {
            Glide.with(getContext()).load(getPost().get("url_image").toString()).into(image);
            title_toolbar.setVisibility(View.GONE);
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
                int x = (int) (i1 * 255 / y);
                int c = Builder.getItem(getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.d_colorPrimary));
                if (i1 <= y) {
                    if (title_toolbar.getVisibility() == View.VISIBLE) {
                        title_toolbar.setVisibility(View.GONE);
                    }
                    toolbar.setBackgroundColor(Color.argb(x, Color.red(c), Color.green(c), Color.blue(c)));
                } else if (title_toolbar.getVisibility() == View.GONE) {
                    toolbar.setBackgroundColor(c);
                    title_toolbar.setVisibility(View.VISIBLE);
                    requestTitle();
                }

            }
        });
    }

    private void requestTitle() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                title_toolbar.requestFocus();
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
        title_toolbar.setText(getPost().get("title").toString());
        author.setText(String.format("نویسنده: %s", getPost().get("author").toString()));
        date.setText(String.format("منتشر شده در: %s", getPost().get("date").toString()));
        contextWeb.getSettings().setJavaScriptEnabled(true);
        contextWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Build.VERSION.SDK_INT >= 16) {
                    Builder.setViewSite(getContext(), url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (CheckNetworkStatus.isOnline()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    new Handler().postDelayed(new Runnable() {
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
                if (Build.VERSION.SDK_INT >= 16) {
                    Builder.setViewSite(getContext(), url);
                    return true;
                }
                return false;
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
                                    "font-family: sans;src: url(\"file:///android_asset/fonts/" + Builder.nameFont + ".ttf\")}body {font-family: sans !important;font-size: light;text-align: justify;}*{max-width:100%}" +
                                    "</style>" +
                                    "</head>" +
                                    "<body dir='rtl'>" +
                                    getPost().get("content").toString() +
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
                        "font-family: sans;src: url(\"file:///android_asset/fonts/" + Builder.nameFont + ".ttf\")}body {font-family: sans !important;font-size: light;text-align: justify;}*{max-width:100%}" +
                        "</style>" +
                        "</head>" +
                        "<body dir='rtl'>" +
                        getPost().get("content").toString() +
                        "</body></html>",
                "text/html",
                "UTF-8",
                "");
    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);
        view = findViewById(R.id.fake);
        toolbar = findViewById(R.id.toolbar);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        title_toolbar = findViewById(R.id.title_toolbar);
        back = findViewById(R.id.back);
        image = findViewById(R.id.image);
        author = findViewById(R.id.author);
        date = findViewById(R.id.date);
        web = findViewById(R.id.web);
        contextWeb = findViewById(R.id.contextWeb);
        showBottomBarPost(true, !getPost().get("comments").toString().equals("[]"), isMark = Builder.hasID(getPost().get("id").toString()));
    }

    @Override
    public void onClickBottomBarPost(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        switch (view.getId()) {
            case R.id.share:
                intent.putExtra(Intent.EXTRA_TEXT, getPost().get("title").toString() + ":\n" + getPost().get("url").toString());
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "اشتراک ..."));
                break;
            case R.id.screen:
                PhotoEdit.loadBitmapFromView(findViewById(R.id.relative));
                startActivity(new Intent(getContext(), PhotoEdit.class));
                break;
            case R.id.comments:
                showBottomBarPost(false);
                BaseFragment baseFragment = new Comments();
                baseFragment.setData(getPost().get("comments").toString());
                startFragment(baseFragment);
                break;
            case R.id.fav:
                if (isMark) {
                    isMark = false;
                    ((ImageView) view).setImageResource(R.drawable.ic_turned_not);
                    Builder.removeBookMark(getPost().get("id").toString());
                    break;
                }
                isMark = true;
                SaveModel saveModel = new SaveModel();
                saveModel.hashMapList.add(getPost());
                ((ImageView) view).setImageResource(R.drawable.ic_turned);
                Builder.addBookMark(saveModel);
                break;
            default:
                showWeb = !showWeb;
                if (showWeb) {
                    ((ImageView) view).setColorFilter(Builder.getItem(getResources().getColor(R.color.activeTabColor),getResources().getColor(R.color.d_activeTabColor)));
                    if (web.getUrl() == null) {
                        progressBar.setVisibility(View.VISIBLE);
                        web.loadUrl(getPost().get("url").toString());
                    }
                    web.setVisibility(View.VISIBLE);
                    contextWeb.setVisibility(View.GONE);
                    if (!getPost().get("url_image").toString().isEmpty()) {
                        image.setVisibility(View.GONE);
                        this.view.setVisibility(View.VISIBLE);
                        title_toolbar.setVisibility(View.VISIBLE);
                        toolbar.setBackgroundColor(Builder.getItem(getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.d_colorPrimary)));
                        nestedScrollView.setScrollY(0);
                        requestTitle();
                    }
                } else {
                    ((ImageView) view).setColorFilter(getResources().getColor(R.color.inActiveTabColor));
                    web.setVisibility(View.GONE);
                    contextWeb.setVisibility(View.VISIBLE);
                    if (!getPost().get("url_image").toString().isEmpty()) {
                        image.setVisibility(View.VISIBLE);
                        this.view.setVisibility(View.GONE);
                        title_toolbar.setVisibility(View.GONE);
                        toolbar.setBackgroundColor(0);
                        nestedScrollView.setScrollY(0);
                    }
                }
        }
    }

    @Override
    public void onResumeFragment() {
        showBottomBarPost(true, !getPost().get("comments").toString().equals("[]"), isMark = Builder.hasID(getPost().get("id").toString()));
        setButtonBarPostStatus(showWeb, isMark);
    }
}
