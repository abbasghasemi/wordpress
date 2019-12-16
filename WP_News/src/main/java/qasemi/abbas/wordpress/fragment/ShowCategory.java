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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
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
import qasemi.abbas.wordpress.builder.PullRefreshLayout;
import qasemi.abbas.wordpress.builder.SaveModel;
import qasemi.abbas.wordpress.builder.TinyData;
import qasemi.abbas.wordpress.listener.OnClickListener;

import static qasemi.abbas.wordpress.builder.Builder.Divider;
import static qasemi.abbas.wordpress.builder.Builder.nameAuthor;

public class ShowCategory extends BaseFragment {

    private PostsAdapter postsAdapter;
    private String status = "";
    private int pages, page = 1;
    private StaggeredGridLayoutManager linearLayoutManager;
    private qasemi.abbas.wordpress.builder.PullRefreshLayout pullRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout net;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment, Builder.getItem(R.layout.base_list,R.layout.d_base_list));
        setTitle(getPost().get("title").toString());
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        pullRefreshLayout = findViewById(R.id.pull);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        linearLayoutManager = new StaggeredGridLayoutManager(Builder.typeShow == 3 ? Builder.getCountPx() : 1, StaggeredGridLayoutManager.VERTICAL);
        TextView check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);
        postsAdapter = new PostsAdapter();
        postsAdapter.setDate(new ArrayList<HashMap<String, Object>>());
        postsAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                BaseFragment baseFragment = new PostView();
                baseFragment.setPost(postsAdapter.getDate().get(position));
                startFragment(baseFragment);
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(postsAdapter);

        if (Divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recyclerView.addItemDecoration(itemDecor);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount_posts = recyclerView.getChildCount();
                    int totalItemCount_posts = linearLayoutManager.getItemCount();
                    int[] pastVisiblesItems_posts = linearLayoutManager.findFirstVisibleItemPositions(new int[]{0, 0});
                    if (!pullRefreshLayout.isLoading()) {
                        if ((visibleItemCount_posts + pastVisiblesItems_posts[0]) >= totalItemCount_posts) {
                            if (pages > page) {
                                page++;
                                progressBar.setVisibility(View.VISIBLE);
                                pullRefreshLayout.setLoading(true);
                                Get();
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
                pullRefreshLayout.setLoading(true);
                progressBar.setVisibility(View.VISIBLE);
                Get();
            }
        });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.setLoading(true);
                page = 1;
                Application.easySave.saveModel("ListCategory_" + getData(), null);
                postsAdapter.getDate().clear();
                postsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                    }
                }, 500);
                Get();
            }
        });

        final SaveModel saveModel = Application.easySave.retrieveModel("ListCategory_" + getData(), SaveModel.class);
        if (saveModel != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    postsAdapter.getDate().addAll(saveModel.hashMapList);
                    progressBar.setVisibility(View.GONE);
                    postsAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setLoading(false);
                }
            }, 500);
        } else {
            Get();
        }

    }

    private void Get() {
        AsyncHttpPost post = new AsyncHttpPost(Api.getCategoryPosts(getData(), String.valueOf(page)));
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
                                        Get();
                                    else if (postsAdapter.getDate().isEmpty()) {
                                        net.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getContext(), "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
                                        page--;
                                        pullRefreshLayout.setLoading(false);
                                        progressBar.setVisibility(View.GONE);
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
                                        saveModel.pages = pages;
                                        saveModel.page = page;
                                        Application.easySave.saveModel("ListCategory_" + getData(), saveModel);
                                        postsAdapter.notifyDataSetChanged();
                                        TinyData.getInstanse().putStringCashe("ListCategory_" + getData());
                                    } else {
                                        if (postsAdapter.getDate().isEmpty()) {
                                            net.setVisibility(View.VISIBLE);
                                        } else {
                                            page--;
                                        }
                                        Toast.makeText(getContext(), "خطای نامشخص!", Toast.LENGTH_SHORT).show();
                                    }
                                    pullRefreshLayout.setLoading(false);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
        );
    }

}
