package com.Whowant.Tokki.UI.Activity.Decoration;


import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StorageBoxItemDecoration extends RecyclerView.ItemDecoration {

    private int dip10;
    private int dip20;
    private int dip25;

    public StorageBoxItemDecoration(Context context) {

        dip10 = dpToPx(context, 10);
        dip20 = dpToPx(context, 20);
        dip25 = dpToPx(context, 25);
    }

    private int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        if (position == 0 || position == 1) {
            outRect.top = dip25;
        }

        outRect.bottom = dip20;

        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = lp.getSpanIndex();

        if (spanIndex == 0) {
            outRect.left = dip20;
            outRect.right = dip10;
        } else if (spanIndex == 1) {
            outRect.left = dip10;
            outRect.right = dip20;
        }
    }
}
