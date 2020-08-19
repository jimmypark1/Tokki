package com.Whowant.Tokki.UI.Activity.Writer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Activity.Main.ChatActivity;
import com.Whowant.Tokki.UI.Activity.Main.FollowerActivity;
import com.Whowant.Tokki.UI.Adapter.MyViewPagerAdapter;
import com.Whowant.Tokki.UI.Adapter.MyWorkRecyclerAdapter;
import com.Whowant.Tokki.UI.Popup.MemberWithdrawActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class WriterMainActivity extends AppCompatActivity {                     // 작가 페이지
    private TextView writerNameView;
    private TextView followerView;
    private TextView followingView;
    private TextView followBtn;
    private TextView descView;
    private ImageView faceView;
    private TextView nameView;

    private RecyclerView recyclerView;
    private ArrayList<WorkVO> workList;
    private MyWorkRecyclerAdapter adapter;

//    private CustomViewPager myViewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private String strPhoto;
    private String strName = "";
    private String strUserComment;
    private int nFollowCount = 0;
    private boolean bFollow = false;
    private String strUserID;
    private LinearLayout followingLayout;
    private LinearLayout followerLayout;
    private TextView chattingBtn;
    private int nFollowerCount;
    private int nFollowingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_main_renewal);

        faceView = findViewById(R.id.profileImgView);
        nameView = findViewById(R.id.nameView);
        descView = findViewById(R.id.descView);
        followerView = findViewById(R.id.followerView);
        followingView = findViewById(R.id.followingView);
        followBtn = findViewById(R.id.followBtn);
        chattingBtn = findViewById(R.id.tokkiTokBtn);
        recyclerView = findViewById(R.id.myWorkRecyclerView);

        followingLayout = findViewById(R.id.followingLayout);
        followerLayout = findViewById(R.id.followerLayout);

        strUserID = getIntent().getStringExtra("USER_ID");
        boolean bWriter = getIntent().getBooleanExtra("WRITER", false);
        if(!bWriter) {
            TextView titleView = findViewById(R.id.titleView);
            titleView.setText("");
        }

        chattingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WriterMainActivity.this, ChatActivity.class);
                intent.putExtra("WRITER_ID", strUserID);
                startActivity(intent);
            }
        });

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WriterMainActivity.this, FollowerActivity.class);
                intent.putExtra("FOLLOWER_COUNT", nFollowerCount);
                intent.putExtra("FOLLOWING_COUNT", nFollowingCount);
                intent.putExtra("WRITER_ID", strUserID);
                intent.putExtra("WRITER_NAME", strName);
                intent.putExtra("SELECTED_INDEX", 1);
                startActivity(intent);
            }
        });

        followerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WriterMainActivity.this, FollowerActivity.class);
                intent.putExtra("FOLLOWER_COUNT", nFollowerCount);
                intent.putExtra("FOLLOWING_COUNT", nFollowingCount);
                intent.putExtra("WRITER_ID", strUserID);
                intent.putExtra("WRITER_NAME", strName);
                intent.putExtra("SELECTED_INDEX", 2);
                startActivity(intent);
            }
        });

        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String strMyID = pref.getString("USER_ID", "Guest");

        if (strMyID.equals(strUserID)) {
            followBtn.setVisibility(View.INVISIBLE);
        }

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CommonUtils.bLocinCheck(pref)) {
                    Toast.makeText(WriterMainActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WriterMainActivity.this, LoginSelectActivity.class));
                    return;
                }

                CommonUtils.showProgressDialog(WriterMainActivity.this, "서버와 통신중입니다.");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        String strMyID = pref.getString("USER_ID", "Guest");

                        boolean bResult = HttpClient.requestFollow(new OkHttpClient(), strMyID, strUserID, bFollow);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.hideProgressDialog();

                                if(bResult) {
                                    getWriterData();
                                } else {
                                    Toast.makeText(WriterMainActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_LONG).show();
                                    getWriterData();
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        if(pref.getString("ADMIN", "N").equals("N")) {
            return false;
        }

        getMenuInflater().inflate(R.menu.writer_admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_btn3:
                Intent intent = new Intent(WriterMainActivity.this, MemberWithdrawActivity.class);
                intent.putExtra("USER_ID", strUserID);
                startActivityForResult(intent, 1000);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                finish();
            }
        }
    }

    private void GetAllWorkList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                workList = HttpClient.GetAllWorkListWithWriterID(new OkHttpClient(), strUserID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyWorkRecyclerAdapter(WriterMainActivity.this, workList);
                        recyclerView.setAdapter(adapter);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(WriterMainActivity.this, 2);
                        recyclerView.setLayoutManager(mLayoutManager);
                    }
                });
            }
        }).start();
    }

//    private void clickTab(int nPosition) {
//        workTabTextView.setTextColor(getResources().getColor(R.color.colorTextGray));
//        commentTabTextView.setTextColor(getResources().getColor(R.color.colorTextGray));
//        workSelectColor.setBackgroundColor(0);
//        commentSelectColor.setBackgroundColor(0);
//
//        switch (nPosition) {
//            case 0:
//                workTabTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                workSelectColor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                break;
//
//            case 2:
//                commentTabTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                commentSelectColor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                break;
//        }
//
//        myViewPager.setCurrentItem(nPosition);
//    }

    @Override
    public void onResume() {
        super.onResume();

        getWriterData();
    }

    private void getWriterData() {
        CommonUtils.showProgressDialog(WriterMainActivity.this, "작가 정보를 가져오고 있습니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                String strMyID = pref.getString("USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getWriterInfo(new OkHttpClient(), strUserID, strMyID);
                CommonUtils.hideProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            strPhoto = resultObject.getString("WRITER_PHOTO");
                            strName = resultObject.getString("WRITER_NAME");
                            strUserComment = resultObject.getString("WRITER_COMMENT");
                            nFollowCount = resultObject.getInt("FOLLOW_COUNT");
                            bFollow = resultObject.getBoolean("FOLLOW");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
                            if(!strPhoto.startsWith("http"))
                                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                            Glide.with(WriterMainActivity.this)
                                    .asBitmap() // some .jpeg files are actually gif
                                    .load(strPhoto)
                                    .placeholder(R.drawable.user_icon)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(faceView);
                        }

                        nameView.setText(strName);

                        if(strUserComment != null && !strUserComment.equals("null") && strUserComment.length() > 0)
                            descView.setText(strUserComment);
                        else
                            descView.setText("");

                        try {
                            nFollowerCount = resultObject.getInt("FOLLOW_COUNT");
                            nFollowingCount = resultObject.getInt("FOLLOWING_COUNT");
                            followingView.setText(CommonUtils.getPointCount(resultObject.getInt("FOLLOWING_COUNT")));
                            followerView.setText(CommonUtils.getPointCount(resultObject.getInt("FOLLOW_COUNT")));

//                            followerView.setText("" + resultObject.getInt("FOLLOW_COUNT"));
//                            followingView.setText("" + resultObject.getInt("FOLLOWING_COUNT"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(bFollow) {
                            followBtn.setText("언팔로우");
                            followBtn.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                            followBtn.setTextColor(ContextCompat.getColor(WriterMainActivity.this, R.color.colorPrimary));
                        } else {
                            followBtn.setText("팔로우");
                            followBtn.setBackgroundResource(R.drawable.round_blue_btn_bg);
                            followBtn.setTextColor(ContextCompat.getColor(WriterMainActivity.this, R.color.colorWhite));
                        }

                        GetAllWorkList();
                    }
                });
            }
        }).start();
    }

}