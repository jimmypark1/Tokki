package com.Whowant.Tokki.UI.Activity.Writer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.ChatActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageFollowerActivity;
import com.Whowant.Tokki.UI.Activity.Report.ReportActivity;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageFeedFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageTalkFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class WriterPageActivity extends AppCompatActivity implements View.OnClickListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    WriterPageAdapter myPageAdapter;

    ImageView photoIv;
    TextView nameTv;
    ImageView levelIv;
    TextView levelTv;
    TextView workCountTv;
    TextView readCountTv;
    TextView followerCountTv;
    LinearLayout followLl;
    LinearLayout unfollowLl;

    String writerId;

    int[] levelRes = new int[]{
            R.drawable.ic_i_level_1, R.drawable.ic_i_level_2, R.drawable.ic_i_level_3, R.drawable.ic_i_level_4, R.drawable.ic_i_level_5,
            R.drawable.ic_i_level_6, R.drawable.ic_i_level_7, R.drawable.ic_i_level_8, R.drawable.ic_i_level_9, R.drawable.ic_i_level_10
    };

    String[] levelName = new String[]{
            "LV.1", "LV.2", "LV.3", "LV.4", "LV.5", "LV.6", "LV.7", "LV.8", "LV.9", "LV.10",
    };

    private int nLevel = 1;                                                                 // 당근 갯수에 따라 레벨 결정

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DeviceUtils.setStatusColor(this, "#b3000000", true);

        setContentView(R.layout.activity_writer_page);

        mActivity = this;

        getData();
        initView();

        getWriterInfo();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            writerId = getIntent().getStringExtra("writerId");
        }
    }

    private void initView() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        myPageAdapter = new WriterPageAdapter(this, getSupportFragmentManager(), writerId);
        viewPager.setAdapter(myPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        photoIv = findViewById(R.id.iv_writer_page_photo);
        nameTv = findViewById(R.id.tv_writer_page_name);
        levelIv = findViewById(R.id.iv_writer_page_level);
        levelTv = findViewById(R.id.tv_writer_page_level);

        followLl = findViewById(R.id.ll_writer_page_follow);
        followLl.setOnClickListener(this);
        unfollowLl = findViewById(R.id.ll_writer_page_unfollow);
        unfollowLl.setOnClickListener(this);

        workCountTv = findViewById(R.id.tv_writer_page_work_count);
        readCountTv = findViewById(R.id.tv_writer_page_read_count);
        followerCountTv = findViewById(R.id.tv_writer_page_follower_count);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_writer_page_follow:
                requestFollow(writerId, false);
                break;
            case R.id.ll_writer_page_unfollow:
                requestFollow(writerId, true);
                break;
        }
    }


    public class WriterPageAdapter extends FragmentPagerAdapter {
        private Context mContext;
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> titles = new ArrayList<>();
        private String writerId;

        public WriterPageAdapter(Context context, @NonNull FragmentManager fm, String writerId) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            this.mContext = context;
            this.writerId = writerId;

            MyPageFeedFragment myPageFeedFragment = new MyPageFeedFragment();
            MyPageTalkFragment myPageTalkFragment = new MyPageTalkFragment();

            Bundle bundle = new Bundle();
            bundle.putString("writerId", writerId);
            myPageFeedFragment.setArguments(bundle);
            myPageTalkFragment.setArguments(bundle);

            fragments.add(myPageFeedFragment);
            fragments.add(myPageTalkFragment);

            titles.add("피드");
            titles.add("대화");
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    // 뒤로가기 버튼
    public void btnBack(View v) {
        finish();
    }

    // 옵션 버튼
    public void btnDots(View v) {
        PopupMenu popup = new PopupMenu(mActivity, v);

        popup.getMenu().add(0, 0, 0, "메세지 보내기");
        popup.getMenu().add(0, 1, 1, nameTv.getText().toString() + " 차단");
        popup.getMenu().add(0, 2, 2, "보고서");

//        popup.getMenuInflater().inflate(R.menu.work_write_list_comment_menu, popup.getMenu());

        MenuItem item = popup.getMenu().getItem(0);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 0: {
                        Intent intent = new Intent(mActivity, ChatActivity.class);
                        intent.putExtra("WRITER_ID", writerId);
                        startActivity(intent);
                    }
                    break;
                    case 1: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle(item.getTitle());
                        builder.setMessage("This account will not be able to follow you, Send you messages, post on your profile or comment on your stories.");
                        builder.setPositiveButton("차단", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(mActivity, "차단", Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(mActivity, "취소", Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    break;
                    case 2: {

                        Intent intent = new Intent(mActivity, ReportActivity.class);
                        intent.putExtra("title", "댓글 신고");
                        startActivity(intent);
                    }
                    break;
                }
                return true;
            }
        });

        popup.show();
    }

    private void getWriterInfo() {
        CommonUtils.showProgressDialog(mActivity, "작가 정보를 가져오고 있습니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strMyID = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getWriterInfo(new OkHttpClient(), writerId, strMyID);
                CommonUtils.hideProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultObject == null) {
                            Toast.makeText(mActivity, "서버와의 연결이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            String strPhoto = resultObject.getString("WRITER_PHOTO");
                            String strName = resultObject.getString("WRITER_NAME");
                            String strUserComment = resultObject.getString("WRITER_COMMENT");
                            int nFollowCount = resultObject.getInt("FOLLOW_COUNT");
                            boolean bFollow = resultObject.getBoolean("FOLLOW");
                            int nDonationCarrotCount = resultObject.getInt("DONATION_CARROT");

                            int nFollowerCount = resultObject.getInt("FOLLOW_COUNT");
                            int nFollowingCount = resultObject.getInt("FOLLOWING_COUNT");

                            if (strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
                                if (!strPhoto.startsWith("http"))
                                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                                Glide.with(mActivity)
                                        .asBitmap() // some .jpeg files are actually gif
                                        .load(strPhoto)
                                        .placeholder(R.drawable.user_icon)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(photoIv);
                            }

                            nLevel = CommonUtils.getLevel(nDonationCarrotCount);
                            levelIv.setImageResource(levelRes[nLevel - 1]);
                            levelTv.setText(levelName[nLevel - 1]);

                            nameTv.setText(strName);
                            followerCountTv.setText(nFollowCount + "");

                            if (bFollow) {
                                unfollowLl.setVisibility(View.VISIBLE);
                                followLl.setVisibility(View.GONE);
                            } else {
                                unfollowLl.setVisibility(View.GONE);
                                followLl.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestFollow(String strUserID, boolean bFollow) {
        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strMyID = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                boolean bResult = HttpClient.requestFollow(new OkHttpClient(), strMyID, strUserID, bFollow);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult) {
                            getWriterInfo();
                        } else {
                            Toast.makeText(mActivity, "서버와의 통신에 실패했습니다.", Toast.LENGTH_LONG).show();
                            getWriterInfo();
                        }
                    }
                });
            }
        }).start();
    }

    // 팔로워 버튼
    public void btnFollower(View v) {
        Intent intent = new Intent(mActivity, MyPageFollowerActivity.class);
        intent.putExtra("writerId", writerId);
        startActivity(intent);
    }
}