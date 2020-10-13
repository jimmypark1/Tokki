package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;

public class WorkPostPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_post_popup);
    }

    public void okClickCancelBtn(View view) {
        finish();
    }

    public void okClickOKBtn(View view) {
        setResult(RESULT_OK);
        finish();
    }
}