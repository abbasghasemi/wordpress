/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.fragment;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ghasemi.abbas.wordpress.builder.Application;
import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.api.ApiSeivice;
import ghasemi.abbas.wordpress.api.CheckNetworkStatus;
import ghasemi.abbas.wordpress.api.ConnectionManager;
import ghasemi.abbas.wordpress.builder.SaveModel;
import ghasemi.abbas.wordpress.builder.TinyData;

public class Category extends BaseFragment {
    private String status = "";
    private List<HashMap<String, Object>> hash;
    private ProgressBar progressBar;
    private TextView check;
    private LinearLayout net;
    private CategoryAdaper categoryAdaper;
    private boolean isLoading;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(R.layout.fragment_list_main, null));
        setTitle(getResources().getString(R.string.boxs));
        progressBar = findViewById(R.id.progressBar);
        hash = new ArrayList<>();
        check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdaper = new CategoryAdaper();
        recyclerView.setAdapter(categoryAdaper);
        final SaveModel saveModel = Application.easySave.retrieveModel("Category", SaveModel.class);
        if (saveModel != null) {
            isLoading = true;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    hash.addAll(saveModel.hashMapList);
                    progressBar.setVisibility(View.GONE);
                    categoryAdaper.notifyDataSetChanged();
                    isLoading = false;
                }
            }, 500);
        } else {
            getCategory();
        }
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getCategory();
            }
        });

        refreshLayout = findViewById(R.id.pull);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                getCategory();
            }
        });
    }

    private void getCategory() {
        if (isLoading) {
            return;
        }
        hash.clear();
        categoryAdaper.notifyDataSetChanged();
        isLoading = true;
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getCategoryIndex())
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        if (jsonArray.length() == 0) {
                            ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                            net.setVisibility(View.VISIBLE);
                            check.setText(getResources().getString(R.string.empty));
                            check.setOnClickListener(null);
                            check.setClickable(false);
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                HashMap<String, Object> add = new HashMap<>();
                                add.put("id", object.getString("id"));
                                add.put("slug", object.getString("slug"));
                                add.put("title", object.getString("name"));
                                add.put("count", object.getString("count"));
                                hash.add(add);
                            }
                            if (hash.isEmpty()) {
                                ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                                net.setVisibility(View.VISIBLE);
                                check.setText(getResources().getString(R.string.empty));
                                check.setOnClickListener(null);
                                check.setClickable(false);
                            } else {
                                SaveModel saveModel = new SaveModel();
                                saveModel.hashMapList.addAll(hash);
                                Application.easySave.saveModel("Category", saveModel);
                                TinyData.getInstance().putStringCashe("Category");
                                categoryAdaper.notifyDataSetChanged();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onFail(String error) {
                        if (CheckNetworkStatus.isOnline())
                            getCategory();
                        else {
                            progressBar.setVisibility(View.GONE);
                            net.setVisibility(View.VISIBLE);
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }
                        isLoading = false;
                    }
                });
    }

    public class CategoryAdaper extends RecyclerView.Adapter<CategoryAdaper.Holder> {

        @NonNull
        @Override
        public CategoryAdaper.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new CategoryAdaper.Holder(LayoutInflater.from(getContext()).inflate(R.layout.row_category, null));
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdaper.Holder holder, final int i) {
            holder.title.setText(hash.get(i).get("title").toString());
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowCategory baseFragment = new ShowCategory();
                    baseFragment.addDataArguments(hash.get(i));
                    Bundle bundle = new Bundle();
                    bundle.putString("id", hash.get(i).get("id").toString());
                    baseFragment.setArguments(bundle);
                    startFragment(baseFragment);
                }
            });
        }

        @Override
        public int getItemCount() {
            return hash.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView title;

            Holder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
            }
        }
    }

}

