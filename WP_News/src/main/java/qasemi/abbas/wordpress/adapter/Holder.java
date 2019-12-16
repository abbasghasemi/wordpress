/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import qasemi.abbas.wordpress.R;
import qasemi.abbas.wordpress.builder.Builder;

class Holder extends RecyclerView.ViewHolder {
    ImageView img_post;
    View card;
    TextView title_post, context_post, author_post, date_post, category;


    Holder(View itemView) {
        super(itemView);
        img_post = itemView.findViewById(R.id.imgPost);
        title_post = itemView.findViewById(R.id.titlePost);
        author_post = itemView.findViewById(R.id.writerPost);
        if (Builder.typeShow == 1) {
            context_post = itemView.findViewById(R.id.contextPost);
        }
        if (Builder.typeShow != 2) {
            date_post = itemView.findViewById(R.id.timePost);
            category = itemView.findViewById(R.id.category);
            card = itemView.findViewById(R.id.card_view);
        } else {
            card = itemView;
        }
    }
}
