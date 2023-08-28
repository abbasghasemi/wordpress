/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.ui.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.api.ApiSeivice;
import ghasemi.abbas.wordpress.api.ConnectionManager;
import ghasemi.abbas.wordpress.BuildApp;

public class Comments extends BaseFragment {
    private List<HashMap<String, Object>> hash;
    private boolean isLoading;
    private int page = 1;
    private boolean hasNext;
    private AdapterComments adapterComments;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment, R.layout.fragment_comment);
        RecyclerView recView = findViewById(R.id.recycler_comment);

        findViewById(R.id.back_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });

        hash = new ArrayList<>();
        adapterComments = new AdapterComments();
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.setAdapter(adapterComments);
        getComments();
    }

    private void getComments() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        if (isLoading) {
            return;
        }
        isLoading = true;
        new ConnectionManager.Builder()
                .setURL(ApiSeivice.getComments(getArguments().getString("post"), String.valueOf(page)))
                .request(new ConnectionManager.ResultConnection() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) throws JSONException {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HashMap<String, Object> add = new HashMap<>();
                            add.put("name", object.getString("author_name"));
                            add.put("date", object.getString("date").replace("T", " "));
                            add.put("content", Html.fromHtml(object.getJSONObject("content").getString("rendered")));
                            boolean author = object.getString("author").equals(getArguments().getString("author"));
                            add.put("type", author ? 1 : 0);
                            hash.add(add);
                        }
                        hasNext = jsonArray.length() == BuildApp.limitPage;
                        isLoading = false;
                        adapterComments.notifyDataSetChanged();
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if (jsonArray.length() == 0) {
                            findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        isLoading = false;
                        finishFragment();
                        Toast.makeText(getContext(), "شبکه در دسترس نیست!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class AdapterComments extends RecyclerView.Adapter<AdapterComments.contentViewHolder> {

        @NonNull
        @Override
        public contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ?
                    R.layout.row_comment_user :
                    R.layout.row_comment_admin, parent, false);
            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(contentViewHolder holder, int position) {
            final HashMap<String, Object> hash_get = hash.get(position);
            String c = hash_get.get("name").toString() + " میگه: \n" + hash_get.get("content").toString().trim();
            holder.msg.setText(c);
            holder.date.setText(hash_get.get("date").toString());
        }

        @Override
        public int getItemCount() {
            return hash.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (int) hash.get(position).get("type");
        }

        class contentViewHolder extends RecyclerView.ViewHolder {
            TextView msg, date;

            contentViewHolder(View itemView) {
                super(itemView);
                msg = itemView.findViewById(R.id.msg);
                date = itemView.findViewById(R.id.date);
            }
        }
    }
}