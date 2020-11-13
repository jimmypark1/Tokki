package com.Whowant.Tokki.UI.ViewHolder;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.TypeOnClickListener;

public class SearchResultViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout notiLl;
    public TextView notiTv;
    public ImageView photoIv;
    public LinearLayout rankLl;
    public TextView rankTv;
    public TextView titleTv;
    public ImageView optionIv;
    public TextView writerTv;
    public TextView heartTv;
    public TextView starTv;
    public TextView tabTv;
    public TextView synopsisTv;

    public SearchResultViewHolder(@NonNull View itemView, TypeOnClickListener listener) {
        super(itemView);

        notiLl = itemView.findViewById(R.id.ll_row_search_category_noti);
        notiTv = itemView.findViewById(R.id.tv_row_search_category_noti);
        photoIv = itemView.findViewById(R.id.iv_row_search_category_photo);
        photoIv.setClipToOutline(true);
        rankLl = itemView.findViewById(R.id.ll_row_search_category_rank);
        rankLl.setVisibility(View.GONE);
        rankTv = itemView.findViewById(R.id.tv_row_search_category_rank);
        titleTv = itemView.findViewById(R.id.tv_row_search_category_title);
        optionIv = itemView.findViewById(R.id.iv_row_search_category_option);
        optionIv.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onClick(0, getAdapterPosition());
            }
        });
        writerTv = itemView.findViewById(R.id.tv_row_search_category_writer);
        heartTv = itemView.findViewById(R.id.tv_row_search_category_heart);
        starTv = itemView.findViewById(R.id.tv_row_search_category_star);
        tabTv = itemView.findViewById(R.id.tv_row_search_category_tab);
        synopsisTv = itemView.findViewById(R.id.tv_row_search_category_synopsis);
    }
}
