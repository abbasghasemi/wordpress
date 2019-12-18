/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Builder;

public class Comments extends BaseFragment {
    private List<HashMap<String, Object>> hash;

    @Override
    public void onCreateView(@NonNull BaseFragment baseFragment, int id) {
        super.onCreateView(baseFragment, Builder.getItem(R.layout.comment_activity, R.layout.d_comment_activity));
        RecyclerView recView = findViewById(R.id.recycler_comment);

        findViewById(R.id.back_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });

        hash = new ArrayList<>();
        Adapter_Posts adaptor_post = new Adapter_Posts();
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.setAdapter(adaptor_post);
        try {
            JSONArray jsonArray = new JSONArray(getArguments().getString("comments"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                HashMap<String, Object> add = new HashMap<>();
                add.put("name", object.getString("name"));
                add.put("date", object.getString("date"));
                add.put("content", Html.fromHtml(object.getString("content")));
                try {
                    String author = object.getString("author");
                    add.put("type", 1);
                } catch (JSONException e) {
                    add.put("type", 0);
                }
                hash.add(add);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adaptor_post.notifyDataSetChanged();
    }

    class Adapter_Posts extends RecyclerView.Adapter<Adapter_Posts.contentViewHolder> {

        @NonNull
        @Override
        public contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ?
                    Builder.getItem(R.layout.row_comment_user, R.layout.d_row_comment_user) :
                    Builder.getItem(R.layout.row_comment_admin, R.layout.d_row_comment_admin), parent, false);
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