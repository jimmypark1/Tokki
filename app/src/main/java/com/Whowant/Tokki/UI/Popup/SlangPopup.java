package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Whowant.Tokki.R;

public class SlangPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slang_popup);

        String strSlang = getIntent().getStringExtra("SLANG");
        TextView slangView = findViewById(R.id.slangView);
        slangView.setText(strSlang);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }
}