package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ContestEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_event);

//        int nEventID = getIntent().getIntExtra("EVENT_ID", 0);
        String strImg = getIntent().getStringExtra("IMG_URL");
        String strTitle = getIntent().getStringExtra("EVENT_TITLE");
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(strTitle);
        ImageView imgView = findViewById(R.id.eventImgView);
        if(!strImg.startsWith("http"))
            strImg = CommonUtils.strDefaultUrl + "images/" + strImg;

        Glide.with(this)
                .asBitmap() // some .jpeg files are actually gif
                .load(strImg)
                .into(imgView);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
    
    public void onClickOKBtn(View view) {
        Toast.makeText(this, "접수 기간이 아닙니다.", Toast.LENGTH_SHORT).show();

//        startActivity(new Intent(ContestEventActivity.this, ContestInputActivity.class));
    }
}