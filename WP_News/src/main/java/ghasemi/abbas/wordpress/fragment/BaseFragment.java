/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ghasemi.abbas.wordpress.LuancherActivity;
import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.adapter.OnClickListener;

public class BaseFragment extends Fragment {

    protected FrameLayout frameLayout;
    private ArrayList<BaseFragment> baseFragments;
    private View view;
    private HashMap<String, Object> hashMap;
    private boolean isShowBottomBarPost;


    protected final void setTitle(String title) {
        TextView t = findViewById(R.id.title);
        if (t == null) {
            return;
        }
        t.setText(title);
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        if (view == null) {
            if (frameLayout == null) {
                return null;
            }
            return frameLayout.findViewById(id);
        }
        return view.findViewById(id);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        frameLayout = new FrameLayout(getContext());
        baseFragments = new ArrayList<>();
        onCreateView(inflater);
        return frameLayout;
    }

    public void onCreateView(@NonNull LayoutInflater layoutInflater) {

    }

    @CallSuper
    public void onCreateView(@NonNull BaseFragment baseFragment, @LayoutRes int id) {
        view = getContext().getLayoutInflater().inflate(id, null);
        baseFragment.frameLayout.addView(view);
        baseFragment.baseFragments.add(this);
        frameLayout = baseFragment.frameLayout;
        baseFragments = baseFragment.baseFragments;
    }

    @NonNull
    @Override
    public AppCompatActivity getContext() {
        return LuancherActivity.activity;
    }

    @Nullable
    @Override
    public View getView() {
        return frameLayout;
    }

    public void startFragment(BaseFragment baseFragment) {
//        if (isShowBottomBarPost) {
//            showBottomBarPost(false);
//        }
        baseFragment.onCreateView(this, -1);
    }


    @CallSuper
    public boolean finishFragment() {
        if (frameLayout.getChildCount() > 1) {
            if (baseFragments.get(baseFragments.size() - 1).isShowBottomBarPost) {
                showBottomBarPost(false);
            }
            frameLayout.removeViewAt(frameLayout.getChildCount() - 1);
            baseFragments.remove(baseFragments.size() - 1);
            if (view == null && baseFragments.isEmpty()) {
                onResumeFragment(); // not good
            } else if (!baseFragments.isEmpty()) {
                baseFragments.get(baseFragments.size() - 1).onResumeFragment();
            }
            return true;
        }
        return false;
    }

    public HashMap<String, Object> getDataArguments() {
        return hashMap;
    }

    public void addDataArguments(HashMap<String, Object> data) {
        this.hashMap = new HashMap<>();
        this.hashMap.putAll(data);
    }

    public void startActivity(Intent intent) {
        getContext().startActivity(intent);
    }

    public void showBottomBarPost(boolean show, boolean showComment, boolean isMark) {
        ((LuancherActivity) getContext()).showBottomBarPost(show, showComment, isMark, show ? new OnClickListener() {
            @Override
            public void onClick(View view, int i) {
                baseFragments.get(baseFragments.size() - 1).onClickBottomBarPost(view);
            }
        } : null);
        baseFragments.get(baseFragments.size() - 1).isShowBottomBarPost = show;
    }

    public void showBottomBarPost(boolean show) {
        ((LuancherActivity) getContext()).showBottomBarPost(show);
    }

    public void onClickBottomBarPost(View view) {

    }

    public FinishListener getFinishFragment() {
        if (baseFragments != null && !baseFragments.isEmpty()) {
            baseFragments.get(baseFragments.size() - 1).onResumeFragment();
        } else {
            onResumeFragment();
        }
        return new FinishListener() {
            @Override
            public boolean finishFragment() {
                return BaseFragment.this.finishFragment();
            }
        };
    }

    public void setButtonBarPostStatus(boolean showWeb, boolean isMark) {
        ((LuancherActivity) getContext()).setStatus(showWeb, isMark);
    }

    public void onResumeFragment() {

    }

}
