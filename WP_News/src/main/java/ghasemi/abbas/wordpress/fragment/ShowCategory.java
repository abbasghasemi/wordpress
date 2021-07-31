/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.fragment;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ghasemi.abbas.wordpress.builder.Application;
import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.adapter.PostsAdapter;
import ghasemi.abbas.wordpress.api.ApiSeivice;
import ghasemi.abbas.wordpress.builder.BuildApp;
import ghasemi.abbas.wordpress.api.CheckNetworkStatus;
import ghasemi.abbas.wordpress.api.ConnectionManager;
import ghasemi.abbas.wordpress.builder.SaveModel;
import ghasemi.abbas.wordpress.builder.TinyData;
import ghasemi.abbas.wordpress.adapter.OnClickListener;

import static ghasemi.abbas.wordpress.builder.BuildApp.divider;
import static ghasemi.abbas.wordpress.builder.BuildApp.authorName;

public class ShowCategory extends BaseFragment {

    private PostsAdapter postsAdapter;
    private int page = 1;
    private StaggeredGridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout pullRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout net;
    private boolean isLoading, hasNext;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment, R.layout.fragment_base_list);
        setTitle(getDataArguments().get("title").toString());
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        pullRefreshLayout = findViewById(R.id.pull);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        gridLayoutManager = new StaggeredGridLayoutManager(BuildApp.showType == 3 ? BuildApp.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL);
        TextView check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        postsAdapter = new PostsAdapter();
        postsAdapter.setDate(new ArrayList<HashMap<String, Object>>());
        postsAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                BaseFragment baseFragment = new PostView();
                baseFragment.addDataArguments(postsAdapter.getDate().get(position));
                startFragment(baseFragment);
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(postsAdapter);

        if (divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recyclerView.addItemDecoration(itemDecor);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount_posts = recyclerView.getChildCount();
                    int totalItemCount_posts = gridLayoutManager.getItemCount();
                    int[] pastVisiblesItems_posts = gridLayoutManager.findFirstVisibleItemPositions(new int[gridLayoutManager.getSpanCount()]);
                    if (!isLoading) {
                        if ((visibleItemCount_posts + pastVisiblesItems_posts[0]) >= totalItemCount_posts) {
                            if (hasNext) {
                                page++;
                                progressBar.setVisibility(View.VISIBLE);
                                getCategory();
                            }
                        }
                    }
                }
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getCategory();
            }
        });

        pullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                page = 1;
                Application.easySave.saveModel("ListCategory_" + getArguments().getString("id"), null);
                postsAdapter.getDate().clear();
                postsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
                getCategory();
            }
        });

        final SaveModel saveModel = Application.easySave.retrieveModel("ListCategory_" + getArguments().getString("id"), SaveModel.class);
        if (saveModel != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    postsAdapter.getDate().addAll(saveModel.hashMapList);
                    progressBar.setVisibility(View.GONE);
                    postsAdapter.notifyDataSetChanged();
                }
            }, 500);
        } else {
            getCategory();
        }

    }

    private void getCategory() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getCategoryPosts(getArguments().getString("id"), String.valueOf(page)))
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HashMap<String, Object> add = new HashMap<>();
                            add.put("id", object.getString("id"));
                            add.put("categories", object.getJSONArray("categories").toString());
                            add.put("url", object.getString("link"));
                            add.put("title", object.getJSONObject("title").getString("rendered"));
                            add.put("content", object.getJSONObject("content").getString("rendered"));
                            String excerpt = String.valueOf(Html.fromHtml(object.getJSONObject("excerpt").getString("rendered")));
                            add.put("excerpt", excerpt);
                            add.put("date", object.getString("date").replace("T", " "));
                            add.put("author", object.getString("author"));
                            if (object.has("yoast_head_json") && object.getJSONObject("yoast_head_json").has("og_image")) {
                                JSONArray array = object.getJSONObject("yoast_head_json").getJSONArray("og_image");
                                add.put("url_image", array.getJSONObject(array.length() - 1).getString("url"));
                            } else if (object.has("yoast_head")) {
                                String head = object.getString("yoast_head");
                                Pattern pattern = Pattern.compile("property=\"og:image\" content=\"(.*)\"");
                                Matcher matcher = pattern.matcher(head);
                                if (matcher.find()) {
                                    head = matcher.group();
                                    add.put("url_image", head.substring(29, head.length() - 1));
                                } else {
                                    add.put("url_image", "");
                                }
                            } else {
                                add.put("url_image", "");
                            }
                            postsAdapter.getDate().add(add);
                        }
                        hasNext = jsonArray.length() == BuildApp.limitPage;
                        SaveModel saveModel = new SaveModel();
                        saveModel.hashMapList.addAll(postsAdapter.getDate());
                        saveModel.hasNext = hasNext;
                        saveModel.page = page;
                        Application.easySave.saveModel("ListCategory_" + getArguments().getString("id"), saveModel);
                        postsAdapter.notifyDataSetChanged();
                        TinyData.getInstance().putStringCashe("ListCategory_" + getArguments().getString("id"));
                        progressBar.setVisibility(View.GONE);
                        if (pullRefreshLayout.isRefreshing()) {
                            pullRefreshLayout.setRefreshing(false);
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onFail(String error) {
                        if (CheckNetworkStatus.isOnline())
                            getCategory();
                        else if (postsAdapter.getDate().isEmpty()) {
                            net.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(), "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
                            page--;
                            progressBar.setVisibility(View.GONE);
                        }
                        if (pullRefreshLayout.isRefreshing()) {
                            pullRefreshLayout.setRefreshing(false);
                        }
                        isLoading = false;
                    }
                });
    }
}