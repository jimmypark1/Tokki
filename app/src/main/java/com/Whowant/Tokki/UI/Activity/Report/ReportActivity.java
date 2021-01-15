package com.Whowant.Tokki.UI.Activity.Report;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;

public class ReportActivity extends AppCompatActivity {

    final int TYPE_RADIO = 0;
    final int TYPE_EDIT = 1;
    int type = TYPE_RADIO;

    ScrollView scrollView;
    LinearLayout contentLl;
    String title = "";

    // Radio
    EditText reportRadioEt;
    // Edit
    EditText reportEditEt;

    ImageView[] radioButtons = new ImageView[6];
    int[] radioRes = new int[]{
            R.id.iv_report_radio_0, R.id.iv_report_radio_1, R.id.iv_report_radio_2, R.id.iv_report_radio_3, R.id.iv_report_radio_4, R.id.iv_report_radio_5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getData();
        initView();

        if ("저작권 신고".equals(title)) {
            addEditView();
        } else {
            addRadioView();
        }
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText(title);

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        scrollView = findViewById(R.id.sv_report);
        contentLl = findViewById(R.id.ll_report_content);
    }

    private void addRadioView() {
        scrollView.setBackgroundColor(Color.parseColor("#fafafa"));
        type = TYPE_RADIO;

        View v = LayoutInflater.from(this).inflate(R.layout.view_report_radio, null);

        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i] = v.findViewById(radioRes[i]);
        }

        reportRadioEt = v.findViewById(R.id.et_report_radio);

        contentLl.addView(v);
    }

    private void addEditView() {
        scrollView.setBackgroundColor(Color.parseColor("#ffffff"));
        type = TYPE_EDIT;

        View v = LayoutInflater.from(this).inflate(R.layout.view_report_edit, null);

        reportEditEt = v.findViewById(R.id.et_report_edit);

        contentLl.addView(v);
    }

    // 다음
    public void btnNext(View v) {
        if (type == TYPE_RADIO) {
            Toast.makeText(this, "Radio", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
        }
    }

    // RadioButton 선택
    public void btnRadio(View v) {
        for (ImageView iv : radioButtons) {
            iv.setSelected(false);
        }

        String tag = (String) v.getTag();
        int position = Integer.parseInt(tag);
        radioButtons[position].setSelected(true);
    }
}