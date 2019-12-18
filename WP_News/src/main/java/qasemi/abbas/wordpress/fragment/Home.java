/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import qasemi.abbas.wordpress.Application;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.adapter.PostsAdapter;
import qasemi.abbas.wordpress.builder.Api;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.builder.CheckNetworkStatus;
import qasemi.abbas.wordpress.builder.ui.PullRefreshLayout;
import qasemi.abbas.wordpress.builder.SaveModel;
import qasemi.abbas.wordpress.builder.TinyData;
import qasemi.abbas.wordpress.listener.OnClickListener;

import static qasemi.abbas.wordpress.builder.Builder.Divider;
import static qasemi.abbas.wordpress.builder.Builder.nameAuthor;

public class Home extends BaseFragment {
    private PostsAdapter postsAdapter;
    private ProgressBar progress;
    private String status = "";
    private LinearLayout net;
    private PullRefreshLayout pullRefreshLayout;
    private int pages, page = 1;
    private StaggeredGridLayoutManager gridLayoutManager;

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater) {
        frameLayout.addView(inflater.inflate(Builder.getItem(R.layout.list_main,R.layout.d_list_main), null));
        pullRefreshLayout = findViewById(R.id.pull);
        RecyclerView recView = findViewById(R.id.recycler_view);
        TextView check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        progress = findViewById(R.id.progressBar);
        postsAdapter = new PostsAdapter();
        postsAdapter.setDate(new ArrayList<HashMap<String, Object>>());
        postsAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                PostView baseFragment = new PostView();
                baseFragment.addDataArguments(postsAdapter.getDate().get(position));
                startFragment(baseFragment);
            }
        });
        gridLayoutManager = new StaggeredGridLayoutManager(Builder.typeShow == 3 ? Builder.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL);
        recView.setLayoutManager(gridLayoutManager);
        recView.setAdapter(postsAdapter);
        if (Divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }
        SaveModel saveModel = Application.easySave.retrieveModel("Luancher", SaveModel.class);
        if (saveModel != null) {
            page = saveModel.page;
            pages = saveModel.pages;
            postsAdapter.setDate(saveModel.hashMapList);
            progress.setVisibility(View.GONE);
            pullRefreshLayout.setLoading(false);
        } else {
            get();
        }

        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount_posts = gridLayoutManager.getChildCount();
                    int totalItemCount_posts = gridLayoutManager.getItemCount();
                    int[] pastVisiblesItems_posts = gridLayoutManager.findFirstVisibleItemPositions(new int[]{0, 0});
                    if ((visibleItemCount_posts + pastVisiblesItems_posts[0]) >= totalItemCount_posts) {
                        if (pages > page) {
                            if (!pullRefreshLayout.isLoading()) {
                                pullRefreshLayout.setLoading(true);
                                progress.setVisibility(View.VISIBLE);
                                page++;
                                get();
                            }
                        }
                    }
                }
            }
        });
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.setLoading(true);
                page = 1;
                Application.easySave.saveModel("Luancher", null);
                postsAdapter.getDate().clear();
                postsAdapter.notifyDataSetChanged();
                progress.setVisibility(View.VISIBLE);
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

    private void get() {
        AsyncHttpGet post = new AsyncHttpGet(Api.getPosts(String.valueOf(page)));
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
                                    if (CheckNetworkStatus.isOnline()) {
                                        get();
                                    } else if (postsAdapter.getDate().isEmpty()) {
                                        net.setVisibility(View.VISIBLE);
                                        progress.setVisibility(View.GONE);
                                    } else {
                                        page--;
                                        Toast.makeText(getContext(), "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
                                        pullRefreshLayout.setLoading(false);
                                        progress.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            getContext().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONArray post = new JSONArray();
                                    JSONObject jSONObject;
                                    try {
                                        jSONObject = new JSONObject(result);
                                        post = jSONObject.getJSONArray("posts");
                                        pages = jSONObject.getInt("pages");
                                        status = jSONObject.getString("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (status.equals("ok")) {
                                        if (!post.toString().equals("[]")) {
                                            for (int i = 0; i < post.length(); i++) {
                                                try {
                                                    JSONObject object = post.getJSONObject(i);
                                                    HashMap<String, Object> add = new HashMap<>();
                                                    add.put("id", object.getString("id"));
                                                    JSONArray jsonArray = object.getJSONArray("categories");
                                                    int j = jsonArray.length();
                                                    if (j > 0) {
                                                        StringBuilder ca = new StringBuilder();
                                                        ca.append(jsonArray.getJSONObject(0).getString("title"));
                                                        for (int k = 1; k < j; k++) {
                                                            ca.append(",").append(jsonArray.getJSONObject(k).getString("title"));
                                                        }
                                                        add.put("category", ca.toString());
                                                    } else {
                                                        add.put("category", "-- - --");
                                                    }
                                                    add.put("url", object.getString("url"));
                                                    add.put("title", object.getString("title"));
                                                    add.put("content", object.getString("content"));
                                                    String excerpt = String.valueOf(Html.fromHtml(object.getString("excerpt")));
                                                    add.put("excerpt", excerpt);
                                                    add.put("date", object.getString("date"));
                                                    add.put("comments", object.getString("comments"));
                                                    JSONObject author = object.getJSONObject("author");
                                                    String name = nameAuthor.isEmpty() ? author.getString("name") : nameAuthor;
                                                    add.put("author", name);
                                                    try {
                                                        JSONObject thumbnail_images = object.getJSONObject("thumbnail_images");
                                                        JSONObject full = thumbnail_images.getJSONObject("full");
                                                        add.put("url_image", full.getString("url"));
                                                    } catch (JSONException e) {
                                                        add.put("url_image", "");
                                                    }
                                                    postsAdapter.getDate().add(add);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            SaveModel saveModel = new SaveModel();
                                            saveModel.hashMapList.addAll(postsAdapter.getDate());
                                            saveModel.page = page;
                                            saveModel.pages = pages;
                                            Application.easySave.saveModel("Luancher", saveModel);
                                            postsAdapter.notifyDataSetChanged();
                                            TinyData.getInstanse().putStringCashe("Luancher");
                                        } else {
                                            Toast.makeText(getContext(), "موردی موجود نیست!", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        if (postsAdapter.getDate().isEmpty()) {
                                            net.setVisibility(View.VISIBLE);
                                        } else {
                                            page--;
                                        }
                                        Toast.makeText(getContext(), "خطائی در دریافت اطلاعات پیش آمده است!", Toast.LENGTH_LONG).show();
                                    }
                                    pullRefreshLayout.setLoading(false);
                                    progress.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
        );
    }
}
