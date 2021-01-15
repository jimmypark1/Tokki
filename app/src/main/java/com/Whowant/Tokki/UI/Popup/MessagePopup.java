package com.Whowant.Tokki.UI.Popup;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.DeviceUtils;

public class MessagePopup extends AppCompatActivity {

    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceUtils.setStatusColor(this, "#b2000000", true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_popup);

        getData();
        initView();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            message = getIntent().getStringExtra("message");
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_message_popup_message)).setText(message);
    }

    public void btnCancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void btnOk(View v) {
        setResult(RESULT_OK, getIntent());
        finish();
    }
}