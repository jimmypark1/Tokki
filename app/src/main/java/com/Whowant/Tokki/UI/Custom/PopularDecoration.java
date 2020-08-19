package com.Whowant.Tokki.UI.Custom;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopularDecoration extends RecyclerView.ItemDecoration {
    private Activity mActivity;

    public PopularDecoration(Activity context) {
        mActivity = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float getDeviceDpi = displayMetrics.density;

        outRect.left = (int)(10 * getDeviceDpi);
        outRect.right = (int)(10 * getDeviceDpi);
    }
}
