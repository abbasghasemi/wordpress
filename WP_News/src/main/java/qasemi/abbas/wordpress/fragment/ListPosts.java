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
import qasemi.abbas.wordpress.builder.SaveModel;
import qasemi.abbas.wordpress.builder.TinyData;
import qasemi.abbas.wordpress.listener.OnClickListener;

import static qasemi.abbas.wordpress.builder.Builder.Divider;
import static qasemi.abbas.wordpress.builder.Builder.nameAuthor;


public class ListPosts extends BaseFragment {
    private PostsAdapter postsAdapter;
    private ProgressBar progress;
    private String status = "";
    private boolean isLoading;
    private LinearLayout net;
    private int pages, page = 1;
    private StaggeredGridLayoutManager gridLayoutManager;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment, Builder.getItem(R.layout.base_list,R.layout.d_base_list));
        setTitle(getArguments().getString("date"));
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
        RecyclerView recView = findViewById(R.id.recycler_view);
        //        title.setText(getResources().getString(R.string.app_name));
        progress = findViewById(R.id.progressBar);
        TextView check = findViewById(R.id.check);
        net = findViewById(R.id.error_net);

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
        SaveModel saveModel = Application.easySave.retrieveModel("ListPosts_" + getArguments().getString("date"), SaveModel.class);
        if (saveModel != null) {
            postsAdapter.getDate().addAll(saveModel.hashMapList);
            page = saveModel.page;
            pages = saveModel.pages;
            progress.setVisibility(View.GONE);
        } else {
            get();
        }
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount_posts = gridLayoutManager.getChildCount();
                    int totalItemCount_posts = gridLayoutManager.getItemCount();
                    int[] pastVisiblesItems_posts = gridLayoutManager.findFirstVisibleItemPositions(new int[gridLayoutManager.getSpanCount()]);
                    if (!isLoading) {
                        if ((visibleItemCount_posts + pastVisiblesItems_posts[0]) >= totalItemCount_posts) {
                            if (pages > page) {
                                page++;
                                progress.setVisibility(View.VISIBLE);
                                isLoading = true;
                                get();
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
                isLoading = true;
                progress.setVisibility(View.VISIBLE);
                get();
            }
        });
    }

    private void get() {
        AsyncHttpPost post = new AsyncHttpPost(Api.getDatePosts(getArguments().getString("date"), String.valueOf(page)));
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
                                    else if (postsAdapter.getDate().isEmpty()) {
                                        net.setVisibility(View.VISIBLE);
                                        progress.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getContext(), "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
                                        isLoading = false;
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
                                            String ss = "ListPosts_" + getArguments().getString("date");
                                            Application.easySave.saveModel(ss, saveModel);
                                            postsAdapter.notifyDataSetChanged();
                                            TinyData.getInstanse().putStringCashe(ss);
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
                                    isLoading = false;
                                    progress.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
        );
    }

}
