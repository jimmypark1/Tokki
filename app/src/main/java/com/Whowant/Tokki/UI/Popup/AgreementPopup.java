package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Whowant.Tokki.R;

public class AgreementPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_popup);

        TextView titleView = findViewById(R.id.titleView);
        TextView contentsView = findViewById(R.id.contentsView);

        Intent oldIntent = getIntent();
        String strTitle = oldIntent.getStringExtra("TITLE");
        String strContents = oldIntent.getStringExtra("CONTENTS");

        titleView.setText(strTitle);
        contentsView.setText(strContents);
    }

    public void onClickOKBtn(View view) {
        finish();
    }

    public void onClickCloseBtn(View view) {
        finish();
    }
}
