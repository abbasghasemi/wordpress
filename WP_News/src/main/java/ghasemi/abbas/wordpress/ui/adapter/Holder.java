/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
  */
package ghasemi.abbas.wordpress.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ghasemi.abbas.wordpress.R;
import ghasemi.abbas.wordpress.BuildApp;

class Holder extends RecyclerView.ViewHolder {
    ImageView imgPost;
    View card;
    TextView titlePost, contextPost, authorPost, datePost;


    Holder(View itemView) {
        super(itemView);
        imgPost = itemView.findViewById(R.id.imgPost);
        titlePost = itemView.findViewById(R.id.titlePost);
        authorPost = itemView.findViewById(R.id.writerPost);
        if (BuildApp.showType == 1) {
            contextPost = itemView.findViewById(R.id.contextPost);
        }
        if (BuildApp.showType != 2) {
            datePost = itemView.findViewById(R.id.timePost);
            card = itemView.findViewById(R.id.card_view);
        } else {
            card = itemView;
        }
    }
}
