package com.Whowant.Tokki.UI.Activity.Mypage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    MyPageAdapter myPageAdapter;
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

    RelativeLayout btnTokkiSNS;
    LinearLayout btnFollower;
    LinearLayout btnCarrot;
    LinearLayout btnWork;
    LinearLayout btnRead;

    private int nCurrentCarrot = 0;                                                         // 현재 당근 갯수
    private int nTotalUsedCarrot = 0;                                                       // 총 당근 갯수
    private int nDonationCarrot = 0;                                                        // 후원받은 당근 갯수
    private int nLevel = 1;                                                                 // 당근 갯수에 따라 레벨 결정
    private int  nTotalAcquireCarrot = 0;

    private boolean isPopup = false;
    boolean isFirst = true;

    private FragmentActivity myContext;

    int[] levelRes = new int[]{
            R.drawable.ic_i_level_1, R.drawable.ic_i_level_2, R.drawable.ic_i_level_3, R.drawable.ic_i_level_4, R.drawable.ic_i_level_5,
            R.drawable.ic_i_level_6, R.drawable.ic_i_level_7, R.drawable.ic_i_level_8, R.drawable.ic_i_level_9, R.drawable.ic_i_level_10
    };

    String[] levelName = new String[]{
            "LV.1", "LV.2", "LV.3", "LV.4", "LV.5", "LV.6", "LV.7", "LV.8", "LV.9", "LV.10",
    };

    Activity mActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_page, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = getActivity();
        initView();
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

    @Override
    public void onResume() {
        super.onResume();

        if (isPopup) {
//            isPopup = false;
            return;
        }

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

        initData();
    }

    private void initView() {
        tabLayout = getView().findViewById(R.id.tabLayout);
        appbar = getView().findViewById(R.id.appBarLayout);
        viewPager = getView().findViewById(R.id.viewPager);

//        FragmentManager fragmentManager = myContext.getSupportFragmentManager();
        myPageAdapter = new MyPageAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(myPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        backIv = getView().findViewById(R.id.background);
        photoIv = getView().findViewById(R.id.iv_my_page_photo);
        nameTv = getView().findViewById(R.id.tv_my_page_name);
        carrotTv = getView().findViewById(R.id.tv_my_page_carrot);
        levelIv = getView().findViewById(R.id.iv_my_page_level);
        levelTv = getView().findViewById(R.id.tv_my_page_level);

        btnTokkiSNS = getView().findViewById(R.id.btnTokkiSNS);
        btnTokkiSNS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPopup = true;
                startActivity(new Intent(getActivity(), TokkiSNSPopup.class));
            }
        });

        btnFollower = getView().findViewById(R.id.btnFollower);
        btnFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyPageFollowerActivity.class);
                intent.putExtra("writerId", SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest"));
                startActivity(intent);
            }
        });

        btnCarrot = getView().findViewById(R.id.btnCarrot);
        btnCarrot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyPageCarrotInfoActivity.class));
            }
        });

        btnWork = getView().findViewById(R.id.btnWork);
        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).moveViewpager(3);

            }
        });

        btnRead = getView().findViewById(R.id.ll_my_page_read);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 보관함 삭제
            }
        });

        workCountTv = getView().findViewById(R.id.tv_my_page_work_count);
        readCountTv = getView().findViewById(R.id.tv_my_page_read);
        followCountTv = getView().findViewById(R.id.tv_my_page_follower);
    }

    private void initData() {
        // 마이 페이지 화면 갱신
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

            fragments.add(new MyPageFeedFragment());
            fragments.add(new MyPageSpaceFragment());

            titles.add("피드");
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
            return fragments.size();
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
                            nLevel = CommonUtils.getLevel(nCurrentCarrot   );

                            levelIv.setImageResource(levelRes[nLevel - 1]);
                            levelTv.setText(levelName[nLevel - 1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}