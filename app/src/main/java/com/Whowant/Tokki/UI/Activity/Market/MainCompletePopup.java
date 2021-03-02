package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.Whowant.Tokki.R;

public class MainCompletePopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_complete_popup);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

}