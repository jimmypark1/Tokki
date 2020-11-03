package com.Whowant.Tokki.UI.Activity.Photopicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.TokkiGalleryAdapter;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class TokkiGalleryActivity extends AppCompatActivity {
    private ArrayList<String> folderNameList;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TokkiGalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokki_gallery);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        getFolderNameList();

    }

    private void getFolderNameList() {
        folderNameList = new ArrayList<>();
        CommonUtils.showProgressDialog(TokkiGalleryActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                folderNameList = HttpClient.getFolderName(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(folderNameList == null) {
                            Toast.makeText(TokkiGalleryActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter = new TokkiGalleryAdapter(TokkiGalleryActivity.this, getSupportFragmentManager(), folderNameList);
                        viewPager.setAdapter(adapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
}