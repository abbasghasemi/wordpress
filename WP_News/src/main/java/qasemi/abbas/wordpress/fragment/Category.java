/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qasemi.abbas.wordpress.Application;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Api;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.CheckNetworkStatus;
import qasemi.abbas.wordpress.builder.SaveModel;
import qasemi.abbas.wordpress.builder.TinyData;

import static qasemi.abbas.wordpress.builder.Builder.FilterCategory;


public class Category extends BaseFragment {
    String status = "";
    List<HashMap<String, Object>> hash;
    ProgressBar progressBar;
    TextView check;
    LinearLayout net;
    RecyclerView recyclerView;
    CategoryAdaper categoryAdaper;

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(Builder.getItem(R.layout.list_main,R.layout.d_list_main), null));
        setTitle(getResources().getString(R.string.boxs));
        progressBar = findViewById(R.id.progressBar);
        hash = new ArrayList<>();
        check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        categoryAdaper = new CategoryAdaper();
        recyclerView.setAdapter(categoryAdaper);
        final SaveModel saveModel = Application.easySave.retrieveModel("Category", SaveModel.class);
        if (saveModel != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hash.addAll(saveModel.hashMapList);
                    progressBar.setVisibility(View.GONE);
                    categoryAdaper.notifyDataSetChanged();
                }
            }, 500);
        } else {
            get();
        }
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                get();
            }
        });
    }

    private void get() {
        AsyncHttpPost post = new AsyncHttpPost(Api.getCategoryIndex());
        post.setTimeout(Builder.Timeout * 1000);

        AsyncHttpClient.getDefaultInstance().executeString(
                post,
                new AsyncHttpClient.StringCallback() {
                    @Override
                    public void onCompleted(Exception ex, AsyncHttpResponse source, final String result) {

                        if (ex != null) {
                            getContext().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (CheckNetworkStatus.isOnline())
                                        get();
                                    else {
                                        progressBar.setVisibility(View.GONE);
                                        net.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } else {
                            getContext().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONArray post = new JSONArray();
                                    JSONObject jSONObject;
                                    int count = 0;
                                    try {
                                        jSONObject = new JSONObject(result);
                                        post = jSONObject.getJSONArray("categories");
                                        status = jSONObject.getString("status");
                                        count = jSONObject.getInt("count");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (status.equals("ok")) {
                                        if (count == 0) {
                                            ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                                            net.setVisibility(View.VISIBLE);
                                            check.setText(getResources().getString(R.string.empty));
                                            check.setOnClickListener(null);
                                            check.setClickable(false);
                                        } else {
                                            for (int i = 0; i < post.length(); i++) {
                                                boolean isFilter = false;
                                                try {
                                                    JSONObject object = post.getJSONObject(i);
                                                    String id = object.getString("id");
                                                    for (String p : FilterCategory.split("__")) {
                                                        if (p.equals(id))
                                                            isFilter = !isFilter;
                                                    }
                                                    if (!isFilter) {
                                                        HashMap<String, Object> add = new HashMap<>();
                                                        add.put("id", id);
                                                        add.put("slug", object.getString("slug"));
                                                        add.put("title", object.getString("title"));
                                                        hash.add(add);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            if (hash.isEmpty()) {
                                                ((ImageView)findViewById(R.id.image)).setImageResource(R.drawable.sad);
                                                net.setVisibility(View.VISIBLE);
                                                check.setText(getResources().getString(R.string.empty));
                                                check.setOnClickListener(null);
                                                check.setClickable(false);
                                            } else {
                                                SaveModel saveModel = new SaveModel();
                                                saveModel.hashMapList.addAll(hash);
                                                Application.easySave.saveModel("Category", saveModel);
                                                TinyData.getInstanse().putStringCashe("Category");
                                                categoryAdaper.notifyDataSetChanged();
                                            }
                                        }
                                    } else {
                                        net.setVisibility(View.VISIBLE);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
        );
    }

    public class CategoryAdaper extends RecyclerView.Adapter<CategoryAdaper.Holder> {

        @NonNull
        @Override
        public CategoryAdaper.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new CategoryAdaper.Holder(LayoutInflater.from(getContext()).inflate(Builder.getItem(R.layout.row_category,R.layout.d_row_category), null));
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdaper.Holder holder, final int i) {
            holder.title.setText(hash.get(i).get("title").toString());
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseFragment baseFragment = new ShowCategory();
                    baseFragment.setPost(hash.get(i));
                    baseFragment.setData(hash.get(i).get("id").toString());
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

