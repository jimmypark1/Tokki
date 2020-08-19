package com.Whowant.Tokki.UI.Custom;

import com.ycuwq.datepicker.date.DatePickerDialogFragment;

public class MyDatePickerDialogFragment extends DatePickerDialogFragment {

    @Override
    protected void initChild() {
        super.initChild();
        mCancelButton.setText("취소");
        mDecideButton.setText("확인");
    }
}
