/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import qasemi.abbas.wordpress.Application;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.adapter.PostsAdapter;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.PullRefreshLayout;
import qasemi.abbas.wordpress.builder.SaveModel;
import qasemi.abbas.wordpress.listener.OnClickListener;

import static qasemi.abbas.wordpress.builder.Builder.Divider;

public class BookMark extends BaseFragment {

    PostsAdapter postsAdapter;
    LinearLayout error_net;

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(Builder.getItem(R.layout.list_main,R.layout.d_list_main), null));
        setTitle(getResources().getString(R.string.mark_name));
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        PullRefreshLayout pullRefreshLayout = findViewById(R.id.pull);
        error_net = findViewById(R.id.error_net);
        ((ImageView)findViewById(R.id.image)).setImageResource(R.drawable.sad);
        ((TextView)findViewById(R.id.check)).setText(getResources().getString(R.string.empty));
        pullRefreshLayout.setLoading(true);
        postsAdapter = new PostsAdapter();
        RecyclerView recView = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(Builder.typeShow == 3 ? Builder.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL);
        recView.setLayoutManager(gridLayoutManager);
        postsAdapter.setDate(new ArrayList<HashMap<String, Object>>());
        postsAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                BaseFragment baseFragment = new PostView();
                baseFragment.setPost(postsAdapter.getDate().get(position));
                startFragment(baseFragment);
            }
        });
        recView.setAdapter(postsAdapter);
        if (Divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }
    }

    @Override
    public void onResumeFragment() {
        postsAdapter.getDate().clear();
        SaveModel saveModel = Application.easySave.retrieveModel("book_mark_info", SaveModel.class);
        if (saveModel != null) {
            error_net.setVisibility(View.GONE);
            postsAdapter.getDate().addAll(saveModel.hashMapList);
        }else {
            error_net.setVisibility(View.VISIBLE);
        }
        postsAdapter.notifyDataSetChanged();
    }
}
