/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Date;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.ui.fragment.BaseFragment;
import ghasemi.abbas.wordpress.ui.fragment.BookMark;
import ghasemi.abbas.wordpress.ui.fragment.Category;
import ghasemi.abbas.wordpress.ui.fragment.Home;
import ghasemi.abbas.wordpress.ui.fragment.Menu;
import ghasemi.abbas.wordpress.ui.fragment.Search;
import ghasemi.abbas.wordpress.ui.fragment.FinishListener;
import ghasemi.abbas.wordpress.ui.adapter.OnClickListener;

import static ghasemi.abbas.wordpress.BuildApp.doubleClickForExit;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static MainActivity activity;
    ViewPager2 viewPager;
    long preTime;
    BottomNavigationView bottomBar;
    LinearLayout bottomBarPost;
    ImageView share, screen, comments, fav, web;
    OnClickListener onClickListener;
    BaseFragment[] baseFragments = new BaseFragment[5];
    FinishListener finishListener;

    @Override
    protected void onCreate(Bundle superSave) {
        super.onCreate(superSave);
        activity = this;
        init();
    }

    private void init() {
        setContentView(R.layout.activity_luancher);
        viewPager = findViewById(R.id.contentContainer);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(new FragmentStateAdapter(this) {

            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        baseFragments[0] = new Home();
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
            public int getItemCount() {
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
        bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id;
                switch (item.getItemId()) {
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
                return true;
            }
        });

        bottomBar.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if (finishListener != null) {
                    finishListener.finishFragment();
                }
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
            web.setColorFilter(getTheme().getResources().getColor(R.color.colorAccent));
        }
        if (isMark) {
            fav.setImageResource(R.drawable.ic_turned);
        }
    }

    @Override
    public void onBackPressed() {
        if (finishListener != null && finishListener.finishFragment()) {
            return;
        }
        if (doubleClickForExit) {
            long currentTime = new Date().getTime();
            if ((currentTime - preTime) > 2000) {
                Toast.makeText(MainActivity.this, "جهت خروج مجددا کلیک کنید ...", Toast.LENGTH_SHORT).show();
                preTime = currentTime;
            } else {
                MainActivity.this.finish();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("خروج از برنامه")
                    .setMessage("آیا می خواهید از برنامه خارج شوید؟")
                    .setPositiveButton("خیر", null)
                    .setNeutralButton("بله", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
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