/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.Date;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.ui.ViewPager;
import qasemi.abbas.wordpress.fragment.BaseFragment;
import qasemi.abbas.wordpress.fragment.BookMark;
import qasemi.abbas.wordpress.fragment.Category;
import qasemi.abbas.wordpress.fragment.Home;
import qasemi.abbas.wordpress.fragment.Menu;
import qasemi.abbas.wordpress.fragment.Search;
import qasemi.abbas.wordpress.listener.FinishListener;
import qasemi.abbas.wordpress.listener.OnClickListener;

import static qasemi.abbas.wordpress.builder.Builder.DoubleClickToExit;


public class Luancher extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Luancher activity;
    ViewPager viewPager;
    long preTime;
    BottomBar bottomBar;
    LinearLayout bottomBarPost;
    ImageView share, screen, comments, fav, web;
    OnClickListener onClickListener;
    BaseFragment[] baseFragments;
    FinishListener finishListener;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle superSave) {
        Builder.setTheme(this);
        super.onCreate(superSave);
        activity = this;
        init();
    }

    private void init() {
        setContentView(Builder.getItem(R.layout.main_activity, R.layout.d_main_activity));
        baseFragments = new BaseFragment[5];
        viewPager = findViewById(R.id.contentContainer);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0:
                        baseFragments[0] = new Home();
                        finishListener = baseFragments[0].getFinishFragment();
                        return baseFragments[0];
                    case 1:
                        baseFragments[1] = new Category();
                        return baseFragments[1];
                    case 2:
                        baseFragments[2] = new Search();
                        return baseFragments[2];
                    case 3:
                        baseFragments[3] = new BookMark();
                        return baseFragments[3];
                    default:
                        baseFragments[4] = new Menu();
                        return baseFragments[4];
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
        bottomBarPost = findViewById(R.id.bottomBarPost);
        share = findViewById(R.id.share);
        share.setOnClickListener(new BottomBarPost());
        screen = findViewById(R.id.screen);
        screen.setOnClickListener(new BottomBarPost());
        comments = findViewById(R.id.comments);
        comments.setOnClickListener(new BottomBarPost());
        fav = findViewById(R.id.fav);
        fav.setOnClickListener(new BottomBarPost());
        web = findViewById(R.id.web);
        web.setOnClickListener(new BottomBarPost());
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_home);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                int id;
                switch (tabId) {
                    case R.id.tab_home:
                        id = 0;
                        break;
                    case R.id.tab_categories:
                        id = 1;
                        break;
                    case R.id.tab_search:
                        id = 2;
                        break;
                    case R.id.tab_favorites:
                        id = 3;
                        break;
                    case R.id.tab_menu:
                        id = 4;
                        break;
                    default:
                        throw new RuntimeException("bottomBar:: tabId not found!.");
                }
                viewPager.setCurrentItem(id);
                if (baseFragments[id] != null)
                    finishListener = baseFragments[id].getFinishFragment();
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {
                finishListener.finishFragment();
            }
        });

    }

    public void showBottomBarPost(boolean show, boolean showComment, boolean isMark, OnClickListener onClickListener) {
        if (showComment) {
            comments.setVisibility(View.VISIBLE);
        } else {
            comments.setVisibility(View.GONE);
        }
        if (isMark) {
            fav.setImageResource(R.drawable.ic_turned);
        } else {
            fav.setImageResource(R.drawable.ic_turned_not);
        }
        showBottomBarPost(show);
        this.onClickListener = onClickListener;
    }

    public void showBottomBarPost(boolean show) {
        if (show) {
            bottomBar.setVisibility(View.INVISIBLE);
            this.bottomBarPost.setVisibility(View.VISIBLE);
            web.setColorFilter(getResources().getColor(R.color.inActiveTabColor));
        } else {
            bottomBar.setVisibility(View.VISIBLE);
            this.bottomBarPost.setVisibility(View.GONE);
        }
    }

    public void setStatus(boolean showWeb, boolean isMark) {
        if (showWeb) {
            web.setColorFilter(Builder.getItem(getResources().getColor(R.color.activeTabColor), getResources().getColor(R.color.d_activeTabColor)));
        }
        if (isMark) {
            fav.setImageResource(R.drawable.ic_turned);
        }
    }

    @Override
    public void onBackPressed() {
        if (finishListener.finishFragment()) {
            return;
        }
        if (DoubleClickToExit) {
            long currentTime = new Date().getTime();
            if ((currentTime - preTime) > 2000) {
                Toast.makeText(Luancher.this, "جهت خروج مجددا کلیک کنید ...", Toast.LENGTH_SHORT).show();
                preTime = currentTime;
            } else {
                Luancher.this.finish();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("خروج از برنامه")
                    .setMessage("آیا می خواهید از برنامه خارج شوید؟")
                    .setPositiveButton("خیر", null)
                    .setNeutralButton("بله", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Luancher.this.finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class BottomBarPost implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (onClickListener == null) {
                return;
            }
            onClickListener.onClick(v, 0);
        }
    }

}