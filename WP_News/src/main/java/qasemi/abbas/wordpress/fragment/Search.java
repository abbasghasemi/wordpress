/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import qasemi.abbas.wordpress.GetSearch;
import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.adapter.PostsAdapter;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.listener.OnClickListener;
import qasemi.abbas.wordpress.listener.ResultListener;

import static android.app.Activity.RESULT_OK;
import static qasemi.abbas.wordpress.builder.Builder.Divider;
import static qasemi.abbas.wordpress.builder.Builder.FilterSearch;
import static qasemi.abbas.wordpress.builder.Builder.nameAuthor;

public class Search extends BaseFragment implements ResultListener {

    public static int id;
    private ProgressBar progress;
    private EditText test;
    private PostsAdapter postsAdapter;
    private String searchText;
    private ImageView button;
    private boolean isLoading = true;
    private LinearLayout error_net;
    private int pages, page = 1;
    private StaggeredGridLayoutManager gridLayoutManager;

    @Override
    public void onCreateView(@NonNull LayoutInflater layoutInflater) {
        RelativeLayout view = (RelativeLayout) layoutInflater.inflate(Builder.getItem(R.layout.list_main,R.layout.d_list_main), null);
        frameLayout.addView(view);
        progress = view.findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        error_net = view.findViewById(R.id.error_net);
//        ImageView image = findViewById(R.id.image);
//        TextView txt_error = findViewById(R.id.check);
        view.removeView(findViewById(R.id.title));
        layoutInflater.inflate(Builder.getItem(R.layout.search,R.layout.d_search), view);
        test = view.findViewById(R.id.edit_text);
        test.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (test.getText().toString().trim().isEmpty()) {
                    test.setText("");
                    return false;
                }
                search();
                try {
                    InputMethodManager imm = (InputMethodManager) test.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(test.getWindowToken(), 0);
                } catch (Exception e) {
                    //
                }
                return true;
            }
        });
        test.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (test.getText().toString().trim().isEmpty()) {
                    if (s.length() > 0) {
                        test.setText("");
                    }
                    button.setImageResource(R.drawable.ic_mic);
                    button.setColorFilter(Builder.getItem(getResources().getColor(R.color.activeTabColor),getResources().getColor(R.color.d_activeTabColor)));
                } else {
                    button.setImageResource(R.drawable.ic_close);
                    button.setColorFilter(Builder.getItem(getResources().getColor(R.color.textColor),getResources().getColor(R.color.d_textColor)));
                    searchText = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        postsAdapter = new PostsAdapter();
        RecyclerView recView = findViewById(R.id.recycler_view);
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
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount_posts = gridLayoutManager.getChildCount();
                    int totalItemCount_posts = gridLayoutManager.getItemCount();
                    int[] pastVisiblesItems_posts = gridLayoutManager.findFirstVisibleItemPositions(new int[]{0, 0});
                    if ((visibleItemCount_posts + pastVisiblesItems_posts[0]) >= totalItemCount_posts) {
                        if (pages > page) {
                            if (!isLoading) {
                                isLoading = true;
                                page++;
                                progress.setVisibility(View.VISIBLE);
                                GetSearch.getInstance().request(getContext(), searchText, String.valueOf(page), Search.this, id);
                            }
                        }
                    }
                }
            }
        });

        if (Divider) {
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), 1);
            recView.addItemDecoration(itemDecor);
        }
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (test.getText().toString().isEmpty()) {
                    askSpeechInput();
                } else {
                    test.setText("");
                }
            }
        });
    }

    private void search() {
        id++;
        postsAdapter.getDate().clear();
        page = 1;
        isLoading = true;
        error_net.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        GetSearch.getInstance().request(getContext(), searchText, String.valueOf(page), Search.this, id);
    }

    @Override
    public void onResult(boolean isSuccess, String msg, JSONArray jsonArray, int Page, int id) {
        if (id == Search.id) {
            if (isSuccess) {
                pages = Page;
                for (int i = 0; i < jsonArray.length(); i++) {
                    boolean isFilter = false;
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        for (String p : FilterSearch.split("__")) {
                            if (p.equals(object.getString("type")))
                                isFilter = !isFilter;
                        }
                        if (!isFilter) {
                            HashMap<String, Object> add = new HashMap<>();
                            add.put("id", object.getString("id"));
                            JSONArray jsonArray1 = object.getJSONArray("categories");
                            int j = jsonArray1.length();
                            if (j > 0) {
                                StringBuilder ca = new StringBuilder();
                                ca.append(jsonArray1.getJSONObject(0).getString("title"));
                                for (int k = 1; k < j; k++) {
                                    ca.append(",").append(jsonArray1.getJSONObject(k).getString("title"));
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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                postsAdapter.notifyDataSetChanged();
                if (pages == 0) {
                    ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                    ((TextView) findViewById(R.id.check)).setText(getResources().getString(R.string.not_found));
                    error_net.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "چیزی یافت نشد.", Toast.LENGTH_SHORT).show();
                }
            } else {
                page--;
                switch (msg) {
                    case "Error Network":
                        ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.without_internet);
                        ((TextView) findViewById(R.id.check)).setText("شبکه در دسترس نیست");
                        error_net.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
                        break;
                    case "not found":
                        ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.sad);
                        ((TextView) findViewById(R.id.check)).setText("خطای نامشخص");
                        error_net.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "خطای نامشخص", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            isLoading = false;
            progress.setVisibility(View.GONE);
        }
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "دنبال چی میگردی؟");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(), "متاسفانه در دستگاه شما پشتیبانی نمی شود.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result!= null && result.size() > 0 && !result.get(0).equals("")) {
                    test.setText(result.get(0));
                    search();
                }
            }
        }
    }
}
