package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.AlarmActivity;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.EventActivity;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.NoticeActivity;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;

public class MainBaseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    private SharedPreferences pref;
    private NavigationView navigationView;
    private String strUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawawer);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(drawerToggle);
    }

    private void initMenuViews() {
        ImageView faceView = navigationView.findViewById(R.id.faceView);
        String strPhoto = pref.getString("USER_PHOTO", "");

        if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if(!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(MainBaseActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }

        strUserID = pref.getString("USER_ID", "Guest");

        TextView nameView = navigationView.findViewById(R.id.nameView);
        nameView.setText(pref.getString("USER_NAME", "Guest"));

        TextView typeView = navigationView.findViewById(R.id.typeView);
        String strAdmin = pref.getString("ADMIN", "N");
        typeView.setText(strAdmin.equals("Y") ? "관리자" : "일반회원");

        TextView logoutBtn = navigationView.findViewById(R.id.logoutBtn);
        if(strUserID.equals("Guest")) {
            logoutBtn.setText("로그인");
        } else {
            logoutBtn.setText("로그아웃");
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(strUserID.equals("Guest")) {
                    startActivity(new Intent(MainBaseActivity.this, LoginSelectActivity.class));
                } else {
//                    logout();
                }
            }
        });

        TextView noticeView = navigationView.findViewById(R.id.noticeView);
        noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainBaseActivity.this, NoticeActivity.class));
            }
        });

        TextView alarmView = navigationView.findViewById(R.id.alarmView);
        alarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainBaseActivity.this, AlarmActivity.class));
            }
        });

        TextView eventView = navigationView.findViewById(R.id.eventView);
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainBaseActivity.this, EventActivity.class));
            }
        });

        TextView personalTermsView = navigationView.findViewById(R.id.personalTermsView);
        personalTermsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainBaseActivity.this, CommonPopup.class);
                intent.putExtra("TITLE", "개인정보 수집 정책");
                intent.putExtra("CONTENTS", "약관이 들어갈 곳입니다.\n약관 약관 약관 \n 약관 약관 약관 약관 약관 약관 약관 약관 약관 약관 약관약관");
                intent.putExtra("TWOBTN", false);
                startActivity(intent);
            }
        });

        TextView termsView = navigationView.findViewById(R.id.termsView);
        termsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainBaseActivity.this, CommonPopup.class);
                intent.putExtra("TITLE", "이용약관 보기");
                intent.putExtra("CONTENTS", "약관이 들어갈 곳입니다.\n약관 약관 약관 \n 약관 약관 약관 약관 약관 약관 약관 약관 약관 약관 약관약관");
                intent.putExtra("TWOBTN", false);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(MainBaseActivity.this, SearchActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
