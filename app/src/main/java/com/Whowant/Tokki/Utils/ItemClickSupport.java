package com.Whowant.Tokki.Utils;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;

public class ItemClickSupport {
    private static final String TAG = ItemClickSupport.class.getSimpleName();
    private RecyclerView recyclerView;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                final int position = recyclerView.getChildAdapterPosition(v);
                final long id = recyclerView.getChildItemId(v);
                itemClickListener.onItemClick(recyclerView, v, position, id);
            }
        }
    };

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null) {
                final int position = recyclerView.getChildAdapterPosition(v);
                final long id = recyclerView.getChildItemId(v);
                itemLongClickListener.onItemLongClick(recyclerView, v, position, id);
            }
            return false;
        }
    };

    private ItemClickSupport(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    private RecyclerView.OnChildAttachStateChangeListener attachListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(@NonNull View view) {
            if (itemClickListener != null) {
                view.setOnClickListener(clickListener);
            }

            if (itemLongClickListener != null) {
                view.setOnLongClickListener(longClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(@NonNull View view) {
            if (itemClickListener != null) {
                view.setOnClickListener(null);
            }

            if (itemLongClickListener != null) {
                view.setOnLongClickListener(null);
            }
        }
    };

    public static ItemClickSupport addTo(RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport = (ItemClickSupport) recyclerView.getTag(R.id.item_click_support);
        if (itemClickSupport == null) {
            itemClickSupport = new ItemClickSupport(recyclerView);
            itemClickSupport.attach(recyclerView);
        } else {
            Log.d(TAG, "RecyclerView already has ItemClickSupport");
        }

        return itemClickSupport;
    }

    private void attach(RecyclerView recyclerView) {
        recyclerView.setTag(R.id.item_click_support, false);
        recyclerView.addOnChildAttachStateChangeListener(attachListener);
    }

    public static ItemClickSupport removeFrom(RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport = (ItemClickSupport) recyclerView.getTag(R.id.item_click_support);
        if (itemClickSupport != null) {
            itemClickSupport.detach(recyclerView);
        } else {
            Log.d(TAG, "RecyclerView does not have ItemClickSupport");
        }
        return itemClickSupport;
    }

    private void detach(RecyclerView recyclerView) {
        recyclerView.removeOnChildAttachStateChangeListener(attachListener);
        recyclerView.setTag(R.id.item_click_support, null);
        itemClickListener = null;
        itemLongClickListener = null;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }

    interface OnItemLongClickListener {
        void onItemLongClick(RecyclerView parent, View view, int position, long id);
    }
}
