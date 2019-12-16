/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Builder;
import qasemi.abbas.wordpress.listener.OnClickListener;

public class PostsAdapter extends RecyclerView.Adapter<Holder> {
    private List<HashMap<String, Object>> date;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public List<HashMap<String, Object>> getDate() {
        return date;
    }

    public void setDate(List<HashMap<String, Object>> date) {
        this.date = date;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (Builder.typeShow) {
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(Builder.getItem(R.layout.row_main_1,R.layout.d_row_main_1), viewGroup, false);
                break;
            case 2:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main_2, viewGroup, false);
                break;
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(Builder.getItem(R.layout.row_main_3,R.layout.d_row_main_3), viewGroup, false);
                break;
        }
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final HashMap<String, Object> hash_get = date.get(position);
        holder.title_post.setText(hash_get.get("title").toString());

        holder.author_post.setText(String.format("نویسنده: %s", hash_get.get("author").toString()));

        String url_image = hash_get.get("url_image").toString();

        if (!url_image.equals("")) {
            holder.img_post.setVisibility(View.VISIBLE);
            Glide.with(holder.card).load(url_image).into(holder.img_post);
        } else
            holder.img_post.setVisibility(View.GONE);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v, position);
            }
        });

        if (Builder.typeShow == 1) {
            String s = hash_get.get("excerpt").toString();
            holder.context_post.setText(s);
        }
        if (Builder.typeShow != 2) {
            holder.date_post.setText(hash_get.get("date").toString());
            holder.category.setText(hash_get.get("category").toString());
        }
    }

    @Override
    public int getItemCount() {
        return date.size();
    }
}
