package com.Whowant.Tokki.UI.Popup;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;

public class CommonPopup extends AppCompatActivity {
    private Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common2_btn_popup);

        TextView titleView = findViewById(R.id.titleView);
        TextView contentsView = findViewById(R.id.contentsView);

        oldIntent = getIntent();
        String strTitle = oldIntent.getStringExtra("TITLE");
        String strContents = oldIntent.getStringExtra("CONTENTS");
        boolean bTwoBtn = oldIntent.getBooleanExtra("TWOBTN", false);
        boolean bCenter = oldIntent.getBooleanExtra("CENTER", false);

        if(bTwoBtn) {
            TextView cancelBtn = findViewById(R.id.cancelBtn);
            cancelBtn.setVisibility(View.VISIBLE);
        }

        titleView.setText(strTitle);
        contentsView.setText(strContents);

        if(bCenter)
            contentsView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}
