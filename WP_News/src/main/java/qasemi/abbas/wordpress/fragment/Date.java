/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import qasemi.abbas.wordpress.Application;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Api;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.CheckNetworkStatus;
import qasemi.abbas.wordpress.builder.PullRefreshLayout;
import qasemi.abbas.wordpress.builder.SaveModel;
import qasemi.abbas.wordpress.builder.TinyData;

import static qasemi.abbas.wordpress.builder.Builder.Divider;

/**
 * Created by abbas qasemi on 08/05/2018.
 */

public class Date extends BaseFragment {
    RecyclerView recView;
    Adapter_Arshiv adaptor_arshiv;
    ProgressBar progress;
    String status = "";
    List<HashMap<String, Object>> hash;
    TextView check;
    PullRefreshLayout pullRefreshLayout;
    LinearLayout net;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment,Builder.getItem(R.layout.base_list,R.layout.d_base_list));
        setTitle(getResources().getString(R.string.date_name));
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
        pullRefreshLayout = findViewById(R.id.pull);
        recView = findViewById(R.id.recycler_view);
        check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        progress = findViewById(R.id.progressBar);
        hash = new ArrayList<>();
        adaptor_arshiv = new Adapter_Arshiv();
        recView.hasFixedSize();
        recView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recView.setAdapter(adaptor_arshiv);
        if (Divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }
        SaveModel saveModel = Application.easySave.retrieveModel("Date", SaveModel.class);
        if (saveModel != null) {
            hash.addAll(saveModel.hashMapList);
            adaptor_arshiv.notifyDataSetChanged();
            progress.setVisibility(View.GONE);
            pullRefreshLayout.setLoading(false);
        } else {
            get();
        }

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.setLoading(true);
                distory();
                Application.easySave.saveModel("Date", null);
                progress.setVisibility(View.VISIBLE);
                hash.clear();
                adaptor_arshiv.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                    }
                }, 500);
                get();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pullRefreshLayout.setLoading(true);
                net.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                get();
            }
        });

    }

    private void distory() {
        for (HashMap<String, Object> s : hash) {
            Application.easySave.saveModel("ListPosts_" + s.get("date").toString(), null);
        }
    }

    private void get() {

        AsyncHttpPost post = new AsyncHttpPost(Api.getDateIndex());
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
                                    else{
                                        net.setVisibility(View.VISIBLE);
                                        progress.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            getContext().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject post = new JSONObject();
                                    JSONObject post2 = new JSONObject();
                                    JSONObject jSONObject;
                                    try {
                                        jSONObject = new JSONObject(result);
                                        post = jSONObject.getJSONObject("tree");
                                        status = jSONObject.getString("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (status.equals("ok")) {
                                        if (!post.toString().equals("[]")) {
                                            Iterator<?> years = post.keys();
                                            while (years.hasNext()) {
                                                String year = (String) years.next();
                                                try {
                                                    post2 = post.getJSONObject(year);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Iterator<?> months = post2.keys();
                                                while (months.hasNext()) {
                                                    String month = (String) months.next();
                                                    try {
                                                        HashMap<String, Object> add = new HashMap<>();
                                                        add.put("date", year + "-" + month);
                                                        add.put("count", post2.getString(month));
                                                        hash.add(add);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            SaveModel saveModel = new SaveModel();
                                            saveModel.hashMapList.addAll(hash);
                                            adaptor_arshiv.notifyDataSetChanged();
                                            Application.easySave.saveModel("Date", saveModel);
                                            TinyData.getInstanse().putStringCashe("Date");
                                        } else {
                                            Toast.makeText(getContext(), "موردی موجود نیست!", Toast.LENGTH_LONG).show();
                                            finishFragment();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "خطائی در دریافت اطلاعات پیش آمده است!", Toast.LENGTH_LONG).show();
                                        finishFragment();
                                    }
                                    progress.setVisibility(View.GONE);
                                    pullRefreshLayout.setLoading(false);
                                }
                            });
                        }
                    }
                }
        );
    }


    class Adapter_Arshiv extends RecyclerView.Adapter<Adapter_Arshiv.contentViewHolder> {

        @NonNull
        @Override
        public Adapter_Arshiv.contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(Builder.getItem(R.layout.row_arshiv,R.layout.d_row_arshiv), parent, false);
            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(contentViewHolder holder, int position) {
            final HashMap<String, Object> hash_get = hash.get(position);
            holder.timePost.setText(String.format("در تاریخ: %s", hash_get.get("date").toString()));
            holder.countPost.setText(String.format("تعداد مطالب: %s", hash_get.get("count").toString()));

        }


        @Override
        public int getItemCount() {
            return hash.size();
        }

        class contentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView timePost, countPost;
            CardView card;

            contentViewHolder(View itemView) {
                super(itemView);
                timePost = itemView.findViewById(R.id.timePost);
                countPost = itemView.findViewById(R.id.countPost);
                card = itemView.findViewById(R.id.card_view);
                card.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                BaseFragment baseFragment = new ListPosts();
                baseFragment.setData(hash.get(getAdapterPosition()).get("date").toString());
                startFragment(baseFragment);
            }
        }

    }
}