package com.Whowant.Tokki.UI.Activity.Mypage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Activity.Work.WebWorkViewerActivity;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageFeedFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageSpaceFragment;
import com.Whowant.Tokki.UI.Popup.TokkiSNSPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static android.app.Activity.RESULT_OK;

public class MyPageFragment extends Fragment {

    TabLayout tabLayout;
    AppBarLayout appbar;
    ViewPager viewPager;
    public  MyPageAdapter myPageAdapter;
    Fragment fragment;
    MyPageSpaceFragment.MyPageSpaceAdapter myPageSpaceAdapter;

    ImageView backIv;
    ImageView photoIv;
    TextView nameTv;
    TextView carrotTv;
    ImageView levelIv;
    TextView levelTv;

    TextView workCountTv;
    TextView readCountTv;
    TextView followCountTv;
    TextView followingCountTv;
    TextView introductionTv;

    TextView typeView;

    RelativeLayout btnTokkiSNS;
    LinearLayout btnFollower;
    LinearLayout btnCarrot;
    LinearLayout btnWork;
    LinearLayout btnRead;
    LinearLayout btnFollowing;

    LinearLayout followLayer;
    LinearLayout unfollowLayer;

    TextView followerCountTv;

    RelativeLayout comment_layer;

    RelativeLayout topInfo;
    RelativeLayout bottomInfo;

    ImageView backBt;
    ImageView settingsBt;

    private int nCurrentCarrot = 0;                                                         // 현재 당근 갯수
    private int nTotalUsedCarrot = 0;                                                       // 총 당근 갯수
    private int nDonationCarrot = 0;                                                        // 후원받은 당근 갯수
    private int nLevel = 1;                                                                 // 당근 갯수에 따라 레벨 결정
    private int  nTotalAcquireCarrot = 0;

    private boolean isPopup = false;
    boolean isFirst = true;

    private FragmentActivity myContext;
    SharedPreferences pref;


    public int type = 0;
    public String writerId = "";


    int[] levelRes = new int[]{
            R.drawable.ic_i_level_1, R.drawable.ic_i_level_2, R.drawable.ic_i_level_3, R.drawable.ic_i_level_4, R.drawable.ic_i_level_5,
            R.drawable.ic_i_level_6, R.drawable.ic_i_level_7, R.drawable.ic_i_level_8, R.drawable.ic_i_level_9, R.drawable.ic_i_level_10
    };

    String[] levelName = new String[]{
            "LV.1", "LV.2", "LV.3", "LV.4", "LV.5", "LV.6", "LV.7", "LV.8", "LV.9", "LV.10",
    };

    Activity mActivity;

/*
         case R.id.ll_writer_page_follow:
                requestFollow(writerId, false);
                break;
            case R.id.ll_writer_page_unfollow:
                requestFollow(writerId, true);
                break;
 */
    private void requestFollow(String strUserID, boolean bFollow) {
        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strMyID = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                boolean bResult = HttpClient.requestFollow(new OkHttpClient(), strMyID, strUserID, bFollow);

                getActivity().runOnUiThread(new Runnable() {
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page, container, false);

        pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        tabLayout = v.findViewById(R.id.tabLayout);
        appbar = v.findViewById(R.id.appBarLayout);
        viewPager = v.findViewById(R.id.viewPager);

//        FragmentManager fragmentManager = myContext.getSupportFragmentManager();
        myPageAdapter = new MyPageAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(myPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        backIv = v.findViewById(R.id.background);
        photoIv = v.findViewById(R.id.iv_my_page_photo);
        nameTv = v.findViewById(R.id.tv_my_page_name);
        carrotTv = v.findViewById(R.id.tv_my_page_carrot);
       // levelIv = v.findViewById(R.id.iv_my_page_level);
       // levelTv = v.findViewById(R.id.tv_my_page_level);
        typeView    = v.findViewById(R.id.tv_my_page_typel);

        followLayer  = v.findViewById(R.id.ll_writer_page_follow);
        unfollowLayer  = v.findViewById(R.id.ll_writer_page_unfollow);
        btnCarrot = v.findViewById(R.id.btnCarrot);
        btnTokkiSNS = v.findViewById(R.id.btnTokkiSNS);
        comment_layer = v.findViewById(R.id.comment_layer);

        bottomInfo= v.findViewById(R.id.bottomInfo);

        backBt = v.findViewById(R.id.back);

        topInfo = v.findViewById(R.id.topInfo);
        settingsBt = v.findViewById(R.id.settings);
    //    followerCountTv = v.findViewById(R.id.tv_writer_page_follower_count);

        //
        //
        if(type == 0)
        {
            followLayer.setVisibility(View.GONE);
            unfollowLayer.setVisibility(View.GONE);
            btnCarrot.setVisibility(View.VISIBLE);
            btnTokkiSNS.setVisibility(View.VISIBLE);
            settingsBt.setVisibility(View.VISIBLE);
            backBt.setVisibility(View.GONE);

        }
        else
        {
            followLayer.setVisibility(View.VISIBLE);
            unfollowLayer.setVisibility(View.GONE);
            btnCarrot.setVisibility(View.GONE);
            btnTokkiSNS.setVisibility(View.GONE);
            backBt.setVisibility(View.GONE);
            settingsBt.setVisibility(View.GONE);

        }


        if(type == 1)
        {
            unfollowLayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestFollow(writerId, true);

                }
            });

            followLayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestFollow(writerId, false);

                }
            });
        }

        introductionTv = v.findViewById(R.id.comment);

        followingCountTv =v.findViewById(R.id.tv_my_page_followeing);
        //    TextView ;

        btnTokkiSNS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TokkiSNSPopup.class));
            }
        });

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        btnFollower = v.findViewById(R.id.btnFollower);
        btnFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyPageFollowerActivity.class);
                if(type == 0)
                    intent.putExtra("writerId", SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest"));
                else
                    intent.putExtra("writerId", writerId);
                intent.putExtra("WRITER_TYPE", type);

                startActivity(intent);
            }
        });
        btnFollowing = v.findViewById(R.id.btnFollowing);
        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyPageFollowerActivity.class);
             //   intent.putExtra("writerId", SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest"));
                if(type == 0)
                    intent.putExtra("writerId", SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest"));
                else
                    intent.putExtra("writerId", writerId);

                intent.putExtra("TYPE", 1);
                intent.putExtra("WRITER_TYPE", type);
//WRITER_TYPE
                startActivity(intent);
            }
        });

        btnCarrot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyPageCarrotInfoActivity.class));
            }
        });

        btnWork = v.findViewById(R.id.btnWork);
        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(type == 0)
               // ((MainActivity)getActivity()).moveViewpager(3);
            }
        });

        btnRead = v.findViewById(R.id.ll_my_page_read);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 보관함 삭제
            }
        });

        workCountTv = v.findViewById(R.id.tv_my_page_work_count);
        readCountTv = v.findViewById(R.id.tv_my_page_read);
        followCountTv = v.findViewById(R.id.tv_my_page_follower);

        initLayout();


        return v;
    }
    private void getWriterInfo() {
      //  CommonUtils.showProgressDialog(mActivity, "작가 정보를 가져오고 있습니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strMyID = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getWriterInfo(new OkHttpClient(), writerId, strMyID);
                //CommonUtils.hideProgressDialog();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultObject == null) {
                            Toast.makeText(mActivity, "서버와의 연결이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                           boolean bFollow = resultObject.getBoolean("FOLLOW");
                            int nDonationCarrotCount = resultObject.getInt("DONATION_CARROT");


//                            nLevel = CommonUtils.getLevel(nDonationCarrotCount);
//                            levelIv.setImageResource(levelRes[nLevel - 1]);
//                            levelTv.setText(levelName[nLevel - 1]);

//                            nameTv.setText(strName);
                     //       followerCountTv.setText(CommonUtils.getPointCount(nFollowCount));

                  //          workCountTv.setText(CommonUtils.getPointCount(resultObject.getInt("WORK_COUNT")));
                  //          readCountTv.setText(CommonUtils.getPointCount(resultObject.getInt("READ_COUNT")));

                            if (bFollow) {
                                unfollowLayer.setVisibility(View.VISIBLE);
                                followLayer.setVisibility(View.GONE);
                            } else {
                                unfollowLayer.setVisibility(View.GONE);
                                followLayer.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = getActivity();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    //    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        MyPageSpaceFragment myPageSpaceFragment = (MyPageSpaceFragment) myPageAdapter.getItem(1);
//        myPageSpaceFragment.imageSetting(intent);
//    }
    public void onClickSettingsBtn(View view) {                                                                                     // 페이지별로 우측 버튼 다르게 동작
     //   startActivity(new Intent(getActivity(), MyPageAccountSettingActivity.class));

        MainActivity parent =  (MainActivity)getActivity();
        parent.onClickSettingsBtn(view);

    }

    private int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    void initLayout()
    {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;


        ViewGroup.LayoutParams params = topInfo.getLayoutParams();
        params.width = width;
        params.height = width;

        int height = (width- (dpToPx(getActivity(),50) + dpToPx(getActivity(),20) ));

        int compHeight = dpToPx(getActivity(),88) +  dpToPx(getActivity(),20) +  dpToPx(getActivity(),26) +  dpToPx(getActivity(),36);

        int topMargin = (height - compHeight ) / 3 ;

        ViewGroup.MarginLayoutParams p0 = (ViewGroup.MarginLayoutParams) photoIv.getLayoutParams();
        ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) nameTv.getLayoutParams();
        ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) introductionTv.getLayoutParams();
        ViewGroup.MarginLayoutParams p3 = (ViewGroup.MarginLayoutParams) bottomInfo.getLayoutParams();

        p0.topMargin = topMargin;
      //  p1.topMargin = topMargin;
     //   p2.topMargin = topMargin;
        p3.topMargin = topMargin;

        ViewGroup.LayoutParams params0 = btnWork.getLayoutParams();
        ViewGroup.LayoutParams params1 = btnRead.getLayoutParams();
        ViewGroup.LayoutParams params2 = btnFollower.getLayoutParams();
        ViewGroup.LayoutParams params3 = btnFollowing.getLayoutParams();

        int compWidth = (width - dpToPx(getActivity(),40) )/ 4;

        params0.width = compWidth;
        params1.width = compWidth;
        params2.width = compWidth;
        params3.width = compWidth;

        /*
        LinearLayout btnFollower;
    LinearLayout btnCarrot;
    LinearLayout btnWork;
    LinearLayout btnRead;
    LinearLayout btnFollowing;
         */


    }
    @Override
    public void onResume() {
        super.onResume();

        if (isPopup) {
//            isPopup = false;
    //        return;
        }
/*
        if(writerId.length() == 0)
        {
            String strPhoto = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_PHOTO", "");

            if (strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
                if (!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(mActivity)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.user_icon)
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(photoIv);
            } else {
                photoIv.setImageResource(R.drawable.user_icon);
            }

            String strBack = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_BACKGROUND", "");

            if (strBack != null && strBack.length() > 0 && !strBack.equals("null")) {
                if (!strBack.startsWith("http"))
                    strBack = CommonUtils.strDefaultUrl + "images/" + strBack;

                Glide.with(mActivity)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strBack)
                        .apply(new RequestOptions().circleCrop())
                        .into(backIv);
            }

       }

 */
        initData();

    }

    private void initData() {
        // 마이 페이지 화면 갱신
        /*
        String photoUrl = SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_PHOTO", "");

        if (!TextUtils.isEmpty(photoUrl)) {
            if (!photoUrl.startsWith("http"))
                photoUrl = CommonUtils.strDefaultUrl + "images/" + photoUrl;

            Glide.with(mActivity)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(photoUrl)
                    .placeholder(R.drawable.user_icon)
                    .apply(new RequestOptions().circleCrop())
                    .into(photoIv);
        }

        String strBack = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_BACKGROUND", "");

        if (!TextUtils.isEmpty(strBack)) {
            if (!strBack.startsWith("http"))
                strBack = CommonUtils.strDefaultUrl + "images/" + strBack;

            Glide.with(mActivity)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strBack)
                    .apply(new RequestOptions().centerCrop())
                    .into(backIv);
        }

        nameTv.setText(SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_NAME", ""));

         */
        if(type ==1)
            getWriterInfo();

        getMyFollowInfo();
    }

    public boolean isPopup() {
        return isPopup;
    }

    public void clearPopup() {
        isPopup = false;
    }

    public class MyPageAdapter extends FragmentStatePagerAdapter {
        private Context mContext;
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> titles = new ArrayList<>();

        public MyPageAdapter(Context context, @NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            this.mContext = context;

            MyPageFeedFragment feed = new MyPageFeedFragment();
            feed.type = type;
            feed.writerId = writerId;
            fragments.add(feed);
            fragments.add(new MyPageSpaceFragment());

            titles.add("작품");
            if(type == 0)
                titles.add("스페이스");

            appbar.setExpanded(true, true);


            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (!isFirst) {
                        appbar.setExpanded(false, true);
                    }

                    isFirst = false;
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    appbar.setExpanded(false, true);
                }
            });

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void getMyFollowInfo() {
//        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");
                if(type == 1)
                {
                    userId = writerId;
                }

                JSONObject resultObject = HttpClient.getMyFollowInfo(new OkHttpClient(), userId);
                CommonUtils.hideProgressDialog();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.hideProgressDialog();
                        try {
                            if (resultObject == null)
                                return;

                            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                int nFollowerCount = resultObject.getInt("FOLLOW_COUNT");
                                int nFollowingCount = resultObject.getInt("FOLLOWING_COUNT");

                                followingCountTv.setText(CommonUtils.getPointCount(nFollowingCount));
//                                nFollowingCount = resultObject.getInt("FOLLOWING_COUNT");
//                                followingView.setText(CommonUtils.getPointCount(resultObject.getInt("FOLLOWING_COUNT")));
//                                followerView.setText(CommonUtils.getPointCount(resultObject.getInt("FOLLOW_COUNT")));
                                workCountTv.setText(CommonUtils.getPointCount(resultObject.getInt("WORK_COUNT")));
                                readCountTv.setText(CommonUtils.getPointCount(resultObject.getInt("READ_COUNT")));
                                followCountTv.setText(CommonUtils.getPointCount(nFollowerCount));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getMyCarrotInfo();
                        getUserInfo();
                    }
                });
            }
        }).start();
    }

    private void getMyCarrotInfo() {
//        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                if(type == 1)
                {
                    userId = writerId;
                }
                JSONObject resultObject = HttpClient.getMyCarrotInfo(new OkHttpClient(), userId);
               // JSONObject resultObject = HttpClient.getMyInfo(new OkHttpClient(), userId);
                CommonUtils.hideProgressDialog();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.hideProgressDialog();

                        try {
                            if (resultObject == null) {
                                return;
                            }

                            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                nCurrentCarrot = resultObject.getInt("CURRENT_CARROT");
//                                carrotTv.setText(resultObject.getString("CURRENT_CARROT") + " 개");
//                                nTotalUsedCarrot = resultObject.getInt("USED_CARROT");
                                nDonationCarrot = resultObject.getInt("DONATION_CARROT");
                                nTotalAcquireCarrot = resultObject.getInt("ACQUIRE_CARROT");
//                                strRecommendCode = resultObject.getString("RECOMMEND_CODE");
//                                currentCarrotCountView.setText(CommonUtils.comma(nCurrentCarrot));
//                                cumulativeUsageCarrotCountView.setText(CommonUtils.comma(nTotalUsedCarrot));
//                                totalCarrotCountView.setText(CommonUtils.comma(nTotalAcquireCarrot));
                            }

                            carrotTv.setText(new DecimalFormat("###,###").format(nCurrentCarrot) + " 개");

                        //    nLevel = CommonUtils.getLevel(nDonationCarrot);
//                            nLevel = CommonUtils.getLevel(nCurrentCarrot   );

                          /*
                            levelIv.setImageResource(levelRes[nLevel - 1]);
                            levelTv.setText(levelName[nLevel - 1]);
                            */

//                            levelTv.bringToFront();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
    private void getUserInfo() {
//        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");
                if(type == 1)
                {
                    userId = writerId;
                }
                JSONObject resultObject = HttpClient.getUserInfo(new OkHttpClient(), userId);
                // JSONObject resultObject = HttpClient.getMyInfo(new OkHttpClient(), userId);
                CommonUtils.hideProgressDialog();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.hideProgressDialog();

                        try {
                            if (resultObject == null) {
                                return;
                            }

                            String ret = resultObject.getString("COMMENT");
                            int nType = resultObject.getInt("TYPE");
                            if(nType == 0 )
                            {
                                typeView.setText("독자");
                                //
                                typeView.setBackgroundResource(R.drawable.round_my_blue);

                            }
                            else if(nType == 1 )
                            {
                                typeView.setText("작가");
                                typeView.setBackgroundResource(R.drawable.round_my_purple);

                            }
                            else if(nType == 2 )
                            {
                                typeView.setText("제작자");
                                typeView.setBackgroundResource(R.drawable.round_my_red);

                            }
                            if(ret != "null")
                            {
                                comment_layer.setVisibility(View.VISIBLE);
                                introductionTv.setText(ret);

                            }
                            else
                            {
                                ViewGroup.LayoutParams params = comment_layer.getLayoutParams();
                                params.height = 0;

                                comment_layer.setVisibility(View.GONE);
                                introductionTv.setText("");

                            }
                            String name = resultObject.getString("NAME");

                            nameTv.setText(name);

                            String back = resultObject.getString("BACKGROUND");

                            //
                            String birth = resultObject.getString("BIRTHDAY");
                            String photo = resultObject.getString("PHOTO");


                            if (!TextUtils.isEmpty(back)) {
                                if (!back.startsWith("http"))
                                    back = CommonUtils.strDefaultUrl + "images/" + back;

                                Glide.with(mActivity)
                                        .asBitmap() // some .jpeg files are actually gif
                                        .load(back)
                                        .apply(new RequestOptions().centerCrop())
                                        .into(backIv);


                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("USER_BACKGROUND", back);
                                editor.putString("USER_COMMENT", ret);
                                editor.putString("USER_BIRTHDAY", birth);

                                editor.putInt("USER_TYPE", nType);
                                editor.putInt("IS_SNS", resultObject.getInt("IS_SNS"));

                                editor.commit();

                            }

                            if (photo != null && photo.length() > 0 && !photo.equals("null")) {
                                if (!photo.startsWith("http"))
                                    photo = CommonUtils.strDefaultUrl + "images/" + photo;

                                Glide.with(mActivity)
                                        .asBitmap() // some .jpeg files are actually gif
                                        .placeholder(R.drawable.user_icon)
                                        .load(photo)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(photoIv);
                            } else {
                                photoIv.setImageResource(R.drawable.user_icon);
                            }


                            if (back != null && back.length() > 0 && !back.equals("null")) {
                                if (!back.startsWith("http"))
                                    back = CommonUtils.strDefaultUrl + "images/" + back;

                                Glide.with(mActivity)
                                        .asBitmap() // some .jpeg files are actually gif
                                        .load(back)
                                        .into(backIv);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}