/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.builder;

import android.app.Activity;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import qasemi.abbas.wordpress.fragment.Search;
import qasemi.abbas.wordpress.listener.ResultListener;

public class GetSearch {

    private static GetSearch controller;
    private String status = "";
    private int pages = 0;

    public static GetSearch getInstance() {
        if (controller == null) {
            controller = new GetSearch();
        }
        return controller;
    }

    public void request(final Activity c, final String t, final String page, final ResultListener r, final int id) {
        AsyncHttpPost post = new AsyncHttpPost(Api.getSearchResults(t, page));
        post.setTimeout(Builder.Timeout * 1000);
        AsyncHttpClient.getDefaultInstance().executeString(
                post,
                new AsyncHttpClient.StringCallback() {
                    @Override
                    public void onCompleted(Exception ex, AsyncHttpResponse source, final String result) {
                        if (!CheckNetworkStatus.isOnline()) {
                            c.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    r.onResult(false, "Error Network", null, 0, id);
                                }
                            });
                        } else if (ex != null) {
                            c.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (id == Search.id)
                                        request(c, t, page, r, id);
                                }
                            });
                        } else {
                            c.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONArray post = new JSONArray();
                                    JSONObject jSONObject;
                                    try {
                                        jSONObject = new JSONObject(result);
                                        post = jSONObject.getJSONArray("posts");
                                        status = jSONObject.getString("status");
                                        pages = jSONObject.getInt("pages");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (status.equals("ok"))
                                        r.onResult(true, "ok", post, pages, id);
                                    else
                                        r.onResult(false, "not found", null, 0, id);
                                }
                            });
                        }
                    }
                }
        );
    }

}
