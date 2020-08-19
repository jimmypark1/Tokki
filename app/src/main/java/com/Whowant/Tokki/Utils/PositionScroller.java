package com.Whowant.Tokki.Utils;

import com.Whowant.Tokki.UI.Activity.Work.EpisodePreviewActivity;

public class PositionScroller implements Runnable {
    private static final int SMOOTH_SCROLL_DURATION = 25;
    private int mSelectedPosition;

    private final EpisodePreviewActivity mParent;

    public PositionScroller(EpisodePreviewActivity mParent, int nPosition) {
        this.mParent = mParent;
        mSelectedPosition = nPosition;
    }

    @Override
    public void run() {
//        final ListView list = mParent.chattingListView;
//        if (mSelectedPosition <= 5) {
//            if (list.postDelayed(this, SMOOTH_SCROLL_DURATION)) {
//                list.setSelection(mSelectedPosition);
//            }
//        }
    }
}
