package com.Whowant.Tokki.UI.Activity.Main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.SNS.KakaoTalk.KakaoSDKAdapter;
import com.Whowant.Tokki.UI.Activity.Admin.AproveWaitingEpisodeListActivity;
import com.Whowant.Tokki.UI.Activity.Admin.CommentManagementActivity;
import com.Whowant.Tokki.UI.Activity.Admin.ContestListActivity;
import com.Whowant.Tokki.UI.Activity.Admin.MemberManagementActivity;
import com.Whowant.Tokki.UI.Activity.Admin.WorkEditManageActivity;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.AlarmActivity;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.EventActivity;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.NoticeActivity;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Login.TermsActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketMainActivity;
import com.Whowant.Tokki.UI.Activity.Market.MarketPagerAdapter;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageAccountSettingActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageFragment;
import com.Whowant.Tokki.UI.Activity.VersionActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkRegActivity;
import com.Whowant.Tokki.UI.Adapter.MainViewpagerAdapter;
import com.Whowant.Tokki.UI.Custom.CustomViewPager;
import com.Whowant.Tokki.UI.Fragment.Friend.MessageDetailActivity;
import com.Whowant.Tokki.UI.Fragment.Main.MainFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MyFragment;
import com.Whowant.Tokki.UI.Fragment.Main.StorageBoxFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageFeedFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageSpaceFragment;
import com.Whowant.Tokki.UI.Popup.EpisodeAproveCancelPopup;
import com.Whowant.Tokki.UI.Popup.ProfileEmailPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.AlarmVO;
import com.Whowant.Tokki.VO.EventVO;
import com.Whowant.Tokki.VO.MessageThreadVO;
import com.Whowant.Tokki.VO.NoticeVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.nhn.android.naverlogin.OAuthLogin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;                            // ?????? ??????
    private ActionBarDrawerToggle drawerToggle;             // ?????? ??????/?????? ??? ???????????? ?????? ??????

    private SharedPreferences pref;
    private NavigationView navigationView;                  // ?????? ?????? ?????????
    private String strUserID;

    private CustomViewPager viewPager;                      // ?????? ????????? ?????? ????????? ?????? ????????? ????????????
    private MainViewpagerAdapter mainPagerAdapter;          // ?????? ??? 4?????? ????????? ???????????? ?????????

    private RelativeLayout managerLayout;                   // ?????? ?????????????????? ???????????? ??? ??? ?????? ????????? ?????? ?????? round_squre_stroke_gray_bg
    private TextView memberManagementView;                  // ????????? ?????? - ????????????        
    private TextView workManagementView, workEditView;                    // ????????? ?????? - ????????????, ????????????
    private TextView commentManagementView, contestManagementView, episodeReportView;          //  ????????????, ????????? ??????, ???????????? ??????
    private boolean bFinish = false;                        // ????????? ?????? ????????? ??? ??????????????? ?????? ?????????
    private ImageView centerLogoView;
    private ImageButton leftBtn;
    private ImageButton rightBtn;
    private ImageButton reportBtn;
    private ImageButton addBtn;
    private ImageButton alarmBtn, marketBtn;
    private ImageView alarmNewView;
    private ImageView profileIv;
    private ImageView faceView;
    private TextView titleView;
    private TextView inviteView;
    private ImageView alarmNewIconView, noticeNewIconView, eventNewIconView;                // ???????????? ?????? ??????, ????????????, ????????? ?????? ????????? ?????? ???. ???????????? ????????? SharedPreference ?????? ??????(????????? ?????? ????????? ??????????????? ????????? ?????????)
    private boolean bCreated = true;                        // ????????? ????????? ????????????????????? ???????????? ?????????
    private TabLayout tabLayout;

    MyPageFragment.MyPageAdapter myPageAdapter;

    RelativeLayout topBarLayout;

    int selectedPosition = 0;
    private MainNovelPagerAdapter pagerAdapter;
    ArrayList<MessageThreadVO> messageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_renewal_layout);

        int nType = getIntent().getIntExtra("TYPE", -1);                                // FCM ???????????? ????????? ??????????????? ????????? ???????????? ??????
        boolean bFirst = getIntent().getBooleanExtra("FIRST", false);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }
        tabLayout = findViewById(R.id.tabLayout);

        centerLogoView = findViewById(R.id.cenverLogoView);
        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        inviteView = findViewById(R.id.inviteView);
        reportBtn = findViewById(R.id.ib_top_bar_report);
        addBtn = findViewById(R.id.ib_top_bar_add);
        alarmBtn = findViewById(R.id.alarmBtn);
        alarmBtn.setVisibility(View.VISIBLE);
        alarmNewView = findViewById(R.id.alarmNewView);
        marketBtn = findViewById(R.id.marketBtn);
        marketBtn.setVisibility(View.VISIBLE);
        profileIv = findViewById(R.id.iv_top_bar_profile);
//        profileIv.setVisibility(View.VISIBLE);
        titleView = findViewById(R.id.titleView);
    //    rightBtn.setImageResource(R.drawable.serch_icon_balck);
    //    rightBtn.setVisibility(View.GONE);
        eventNewIconView = findViewById(R.id.eventNewIconView);
        noticeNewIconView = findViewById(R.id.noticeNewIconView);
//        alarmNewIconView = findViewById(R.id.alarmNewIconView);


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("????????????"));
        tabLayout.addTab(tabLayout.newTab().setText("?????????"));
       // tabLayout.addTab(tabLayout.newTab().setText("e??????"));
        tabLayout.addTab(tabLayout.newTab().setText("?????????"));

        topBarLayout= findViewById(R.id.topBarLayout);


        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        drawer = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        mainPagerAdapter = new MainViewpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        //viewPager.getCurrentItem();



       // pagerAdapter = new MainNovelPagerAdapter(getSupportFragmentManager());

     //   viewPager.setAdapter(pagerAdapter);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                selectedPosition = position;
                // ViewPager ?????? ????????? ????????? ???????????? ?????? ????????? ?????? ??? ???????????? ????????????.
                rightBtn.setVisibility(View.GONE);
                profileIv.setVisibility(View.GONE);
                reportBtn.setVisibility(View.GONE);
                addBtn.setVisibility(View.GONE);
                inviteView.setVisibility(View.GONE);
                alarmBtn.setVisibility(View.GONE);
                marketBtn.setVisibility(View.GONE);

                if (position == 0) {

                    topBarLayout.setVisibility(View.VISIBLE);

                    centerLogoView.setVisibility(View.VISIBLE);
                    titleView.setText("");
//                    rightBtn.setVisibility(View.VISIBLE);
//                    rightBtn.setImageResource(R.drawable.serch_icon_balck);
//                    profileIv.setVisibility(View.VISIBLE);

                    tabLayout.setVisibility(View.VISIBLE);
                    alarmBtn.setVisibility(View.VISIBLE);
                    marketBtn.setVisibility(View.VISIBLE);

                 //   alarmNewView.setVisibility(View.INVISIBLE);
                        getAlarmList();



                } else if (position == 1) {
                    topBarLayout.setVisibility(View.VISIBLE);
                    centerLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("??????");
                    tabLayout.setVisibility(View.GONE);

                    alarmNewView.setVisibility(View.INVISIBLE);

//                    rightBtn.setVisibility(View.VISIBLE);
//                    rightBtn.setImageResource(R.drawable.dot_menu);
                } else if (position == 2) {
                    centerLogoView.setVisibility(View.INVISIBLE);
                    /*
                    titleView.setText("?????? ?????????");
                    rightBtn.setVisibility(View.VISIBLE);
                    rightBtn.setImageResource(R.drawable.i_setting);
                    reportBtn.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    alarmNewView.setVisibility(View.INVISIBLE);

                     */
                    topBarLayout.setVisibility(View.GONE);

                    tabLayout.setVisibility(View.GONE);
                    alarmNewView.setVisibility(View.INVISIBLE);


                } else if (position == 3) {
                    topBarLayout.setVisibility(View.VISIBLE);
                    centerLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("????????????");
//                    rightBtn.setVisibility(View.VISIBLE);
                  //  rightBtn.setImageResource(R.drawable.i_dots_black);
                    addBtn.setVisibility(View.VISIBLE);
                    addBtn.setImageResource(R.drawable.i_plus);
//                    centerLogoView.setVisibility(View.INVISIBLE);
//                    titleView.setText("?????? ?????????");
//                    rightBtn.setVisibility(View.VISIBLE);
//                    rightBtn.setImageResource(R.drawable.gear_btn);
                    tabLayout.setVisibility(View.GONE);
                    alarmNewView.setVisibility(View.INVISIBLE);

                } else if (position == 4) {
                    topBarLayout.setVisibility(View.VISIBLE);
                    centerLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("??????");
                    inviteView.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    alarmNewView.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(drawerToggle);

        if (nType > 0) {                                                                                         // FCM ???????????? ??? ????????? ????????? ??????\
            int nObjectID = getIntent().getIntExtra("OBJECT_ID", -1);

            if(nType != 33)
            {
                Intent intent = new Intent(MainActivity.this, WorkMainActivity.class);

                intent.putExtra("WORK_ID", nObjectID);
                startActivity(intent);
            }
            else
            {
                SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

                // CommonUtils.showProgressDialog(getActivity(), "????????? ??????????????????.");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String userId =  pref.getString("USER_ID", "Guest");
                        messageList = HttpClient.getMessageThreadList(new OkHttpClient(),userId);

                        Intent intent = new Intent(MainActivity.this, MessageDetailActivity.class);
                        int nThreadID = nObjectID;
                        intent.putExtra("THREAD_ID", nThreadID);

                        for(int i=0;i<messageList.size();i++)
                        {
                            MessageThreadVO msg = messageList.get(i);
                            if( msg.getThreadID() == nObjectID)
                            {
                                if(userId.equals(msg.getUserID()))
                                    intent.putExtra("RECEIVER_ID", msg.getPartnerID());
                                else
                                    intent.putExtra("RECEIVER_ID", msg.getUserID());

                                startActivity(intent);

                                break;
                            }
                        }

                    }
                }).start();


            }

        }

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {                                               // ?????? ???????????? ??? ????????? ????????? ??????
            Uri uri = getIntent().getData();

            if (uri != null) {
                String strWorkID = uri.getQueryParameter("WORK_ID");

                if (strWorkID != null) {
                    Intent intent = new Intent(MainActivity.this, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", Integer.valueOf(strWorkID));
                    startActivity(intent);
                }
            }
        }

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())                                              // firebase ???????????? ?????? ???????????? ??? ????????? ????????? ??????
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            String strUrl = deepLink.toString();
                            strUrl = strUrl.substring(strUrl.lastIndexOf("?") + 1);         // d=t_e_c_ccode
                            Log.d("DEEP_LINK", strUrl);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        initMenuViews();


        requestAttendance();                                                                                                            // ????????????. ????????? ????????????, 1??? ???????????? ??????/?????? ????????? ???????????? ??????

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //MainNovelPagerAdapter
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                MainFragment main = (MainFragment)mainPagerAdapter.getItem(0);
              //  main.interruptRecommend();

                if(pos == 2)
                {
                    pos = 3;
                }
                mainPagerAdapter.nType = pos;
                mainPagerAdapter.updateMain();
                mainPagerAdapter.notifyDataSetChanged();

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });


    }


    public void requestAttendance() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestAttendance(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bResult) {
                            Toast.makeText(MainActivity.this, "??????????????? 5 ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void getAlarmList() {                                                                                                       // ?????? 1 - ?????? ?????? ????????????

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AlarmVO> list = HttpClient.getAlarmList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null) {
                            Toast.makeText(MainActivity.this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (AlarmVO vo : list) {
                            if (!vo.isbRead()) {
//                                alarmNewIconView.setVisibility(View.VISIBLE);
                                alarmNewView.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                        getNoticeData();
                    }
                });
            }
        }).start();
    }

    private void getNoticeData() {                                                                                                          // ?????? 2 - ???????????? ????????????


        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NoticeVO> noticeList = HttpClient.getNoticeList(new OkHttpClient(), MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (noticeList == null) {
                            Toast.makeText(MainActivity.this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (NoticeVO vo : noticeList) {
                            if (!vo.getbRead()) {
                                noticeNewIconView.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                        getEventData();
                    }
                });
            }
        }).start();
    }

    private void getEventData() {                                                                                                               // ?????? 3 - ????????? ????????? ????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<EventVO> eventList = HttpClient.getEventList(new OkHttpClient(), MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (eventList == null) {
                            return;
                        }

                        EventVO snsEvent = new EventVO();
                        snsEvent.setnEventID(-10);
                        snsEvent.setnEventType(100);
                        snsEvent.setbRead(true);
                        eventList.add(snsEvent);

                        for (EventVO vo : eventList) {
                            if (!vo.isbRead()) {
                                eventNewIconView.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                        if (bCreated) {
                            for (EventVO vo : eventList) {                                                                           // ???????????? ????????? ??????????????? ?????? ???????????? ????????? ?????? ??????
                                int nEventID = vo.getnEventID();

                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                String strToday = sdf.format(date);

                                String strDate = pref.getString("" + nEventID, "");
/*
                                if (nEventID == -10 && strDate.length() == 0) {
                                    EventPopupActivity.eventList = eventList;
                                    startActivity(new Intent(MainActivity.this, EventPopupActivity.class));
                                    break;
                                } else if (strDate.length() == 0 || !strDate.equals(strToday)) {
                                    EventPopupActivity.eventList = eventList;
                                    startActivity(new Intent(MainActivity.this, EventPopupActivity.class));
                                    break;
                                }

 */
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public String getToday() {
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);
        return (cal.get(Calendar.YEAR) + "" + cal.get(Calendar.MONTH) + "" + cal.get(Calendar.DAY_OF_MONTH) + "");
    }

    @Override
    public void onResume() {
        super.onResume();
        //noticeNewIconView.setVisibility(View.INVISIBLE);
//        alarmNewIconView.setVisibility(View.INVISIBLE);
        eventNewIconView.setVisibility(View.INVISIBLE);
        alarmNewView.setVisibility(View.INVISIBLE);

        int nType = getIntent().getIntExtra("TYPE", -1);                                // FCM ???????????? ????????? ??????????????? ????????? ???????????? ??????


        //if(selectedPosition == 0)

         getAlarmList();

        strUserID = pref.getString("USER_ID", "Guest");

        TextView nameView = navigationView.findViewById(R.id.nameView);
        nameView.setText(pref.getString("USER_NAME", "Guest"));

        String strPhoto = pref.getString("USER_PHOTO", "");

        if (strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if (!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(MainActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.user_icon)
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);

            Glide.with(MainActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.user_icon)
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(profileIv);
        } else {
            faceView.setImageResource(R.drawable.user_icon);
            profileIv.setImageResource(R.drawable.user_icon);
        }
        String end = getIntent().getStringExtra("END");
        if(end != null )
        {
            if(end.equals("YES"))
            {


            //    viewPager.setCurrentItem(2);
            }

        }
      //  mainPagerAdapter.notifyDataSetChanged();



    }

    @Override
    protected void onPause() {
        super.onPause();
        bCreated = false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!bFinish) {
                Toast.makeText(this, "??? ??? ??? ?????? ?????? ????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                bFinish = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bFinish = false;
                    }
                }, 2000);

                return;
            }

            super.onBackPressed();
        }
    }

    public void onClickTopLeftBtn(View view) {
        drawer.openDrawer(Gravity.LEFT);
    }
    public void onClickSettingsBtn(View view) {                                                                                     // ??????????????? ?????? ?????? ????????? ??????
        startActivityForResult(new Intent(MainActivity.this, MyPageAccountSettingActivity.class), 3333);

    }
    public void onClickTopRightBtn(View view) {                                                                                     // ??????????????? ?????? ?????? ????????? ??????
        int nPosition = viewPager.getCurrentItem();

        if (nPosition == 0) {
//            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        } else if (nPosition == 2) {
//            KeepFragment fragment = (KeepFragment) mainPagerAdapter.getItem(2);
//            fragment.showMenus();
//            StorageBoxFragment fragment = (StorageBoxFragment) mainPagerAdapter.getItem(2);
//            fragment.showMenus(view);
            startActivity(new Intent(this, MyPageAccountSettingActivity.class));
        } else if (nPosition == 3) {
//            mainPagerAdapter.getItem(2);
//            startActivity(new Intent(MainActivity.this, CreateWorkActivity.class));
//            startActivity(new Intent(MainActivity.this, WorkRegActivity.class));
        } /*else if(nPosition == 4) {
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        }*/
    }

    public void onClickInviteBtn(View view) {
        String strURL = "https://tokki.page.link/1ux2?CMD=workmain&WORK_ID=0\n\n????????? ?????? : " + pref.getString("RECOMMEND_CODE", "");
        //String strURL = "panbook://workmain?WORK_ID=" + workVO.getnWorkID();

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tokki ?????? ?????? ????????? ??????????????????!"); // ??????
        shareIntent.putExtra(Intent.EXTRA_TEXT, strURL); // ??????
        startActivity(Intent.createChooser(shareIntent, "?????? ??????")); // ????????? ??????
    }

    public void onClickTopProfileBtn(View view) {
     //   startActivityForResult(new Intent(this, MyPageActivity.class), 878);
//        startActivity(new Intent(this, TagRegActivity.class));
//        startActivity(new Intent(this, WriterPageActivity.class));
//        viewPager.setCurrentItem(5);

    }

    public void onClickTopReportBtn(View v) {
        StorageBoxFragment fragment = (StorageBoxFragment) mainPagerAdapter.getItem(2);
        fragment.showBugReport();
    }

    public void onClickTopAdd(View v) {
        WorkRegActivity.workVO = null;

       // startActivity(new Intent(MainActivity.this, WorkRegActivity.class));


        PopupMenu popup = new PopupMenu(MainActivity.this, addBtn);

        popup.getMenuInflater().inflate(R.menu.novel_category_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
               // AlertDialog.Builder builder = new AlertDialog.Builder(AproveWaitingEpisodeListActivity.this);
               // AlertDialog alertDialog = null;

                switch(item.getItemId()) {
                    case R.id.chatting_novel:

                        intent = new Intent(MainActivity.this, WorkRegActivity.class);
                        intent.putExtra("NOVEL_TYPE", 0);

                        startActivity(intent);

                        break;
                    case R.id.web_novel:

                        intent = new Intent(MainActivity.this, WorkRegActivity.class);
                        intent.putExtra("NOVEL_TYPE", 1);

                        startActivity(intent);

                        break;
                        /*
                    case R.id.e_novel:

                        intent = new Intent(MainActivity.this, WorkRegActivity.class);
                        intent.putExtra("NOVEL_TYPE", 2);

                        startActivity(intent);

                        break;

                         */
                    case R.id.story_novel:

                        intent = new Intent(MainActivity.this, WorkRegActivity.class);
                        intent.putExtra("NOVEL_TYPE", 3);

                        startActivity(intent);

                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    //
    public void onClickMarketBtn(View v) {
        ///   startActivityForResult(intent, 777);
      //  startActivityForResult(new Intent(MainActivity.this, MarketMainActivity.class),777);
        startActivity(new Intent(MainActivity.this, MarketMainActivity.class));
    }

    public void onClickAlarmBtn(View v) {
        startActivity(new Intent(MainActivity.this, AlarmActivity.class));
    }

    private void initMenuViews() {                                                                                  // ?????? ???????????? ??????
        LinearLayout topLayout = navigationView.findViewById(R.id.topLayout);
        topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
                viewPager.setCurrentItem(2);
                resetBottomBar(2);
              //  startActivity(new Intent(MainActivity.this, MyPageActivity.class));
            }
        });

        faceView = navigationView.findViewById(R.id.faceView);                                        // ?????? ??????
        String strPhoto = pref.getString("USER_PHOTO", "");

        if (strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if (!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(MainActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.user_icon)
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);

            Glide.with(MainActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.user_icon)
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(profileIv);
        } else {
            faceView.setImageResource(R.drawable.user_icon);
            profileIv.setImageResource(R.drawable.user_icon);
        }

        strUserID = pref.getString("USER_ID", "Guest");

        TextView nameView = navigationView.findViewById(R.id.nameView);
        nameView.setText(pref.getString("USER_NAME", "Guest"));

        TextView typeView = navigationView.findViewById(R.id.typeView);
        String strAdmin = pref.getString("ADMIN", "N");

        int nUserType = pref.getInt("USER_TYPE", 0);


        if (strAdmin.equals("Y")) {          // ????????? ?????? ????????? ?????? ?????? ??????
            typeView.setText("?????????");
            managerLayout = navigationView.findViewById(R.id.managerLayout);
            managerLayout.setVisibility(View.VISIBLE);
            memberManagementView = navigationView.findViewById(R.id.memberManagementView);
            workManagementView = navigationView.findViewById(R.id.workManagementView);
            workEditView = navigationView.findViewById(R.id.workEditView);
            commentManagementView = navigationView.findViewById(R.id.commentManagementView);
            contestManagementView = navigationView.findViewById(R.id.contestManagementView);
            episodeReportView = navigationView.findViewById(R.id.episodeReportView);

            memberManagementView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, MemberManagementActivity.class));
                }
            });

            workManagementView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, AproveWaitingEpisodeListActivity.class));
                }
            });

            workEditView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, WorkEditManageActivity.class));
                }
            });

            commentManagementView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CommentManagementActivity.class));
                }
            });

            episodeReportView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            contestManagementView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, ContestListActivity.class));
                }
            });
        } else {
            managerLayout = navigationView.findViewById(R.id.managerLayout);
            managerLayout.setVisibility(View.INVISIBLE);
            if(nUserType == 0)
            {
                typeView.setText("??????");

            }
            else if(nUserType == 1)
            {
                typeView.setText("??????");

            }
            else if(nUserType == 2)
            {
                typeView.setText("?????????");

            }
        }


        TextView logoutBtn = navigationView.findViewById(R.id.logoutBtn);
        if (strUserID.equals("Guest")) {
            logoutBtn.setText("?????????");
        } else {
            logoutBtn.setText("????????????");
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strUserID.equals("Guest")) {
                    startActivity(new Intent(MainActivity.this, PanbookLoginActivity.class));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("????????????");
                    builder.setMessage("???????????? ???????????????????");
                    builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            logout();
                        }
                    });

                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        TextView noticeView = navigationView.findViewById(R.id.noticeView);
        noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));
            }
        });

        TextView alarmView = navigationView.findViewById(R.id.alarmView);
        alarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AlarmActivity.class));
            }
        });

        TextView eventView = navigationView.findViewById(R.id.eventView);
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EventActivity.class));
            }
        });

        TextView personalTermsView = navigationView.findViewById(R.id.personalTermsView);
        personalTermsView.setOnClickListener(new View.OnClickListener() {                                   // ???????????? ????????????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                intent.putExtra("TERMS_TYPE", 1);
                intent.putExtra("MAIN", true);
                startActivity(intent);

//                Intent intent = new Intent(MainActivity.this, AgreementPopup.class);
//                intent.putExtra("TITLE", "???????????? ?????? ??????");
//                intent.putExtra("CONTENTS", getResources().getString(R.string.service_agreement));
//                intent.putExtra("TWOBTN", false);
//                startActivity(intent);
            }
        });

        TextView termsView = navigationView.findViewById(R.id.termsView);
        termsView.setOnClickListener(new View.OnClickListener() {                                           // ????????????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                intent.putExtra("TERMS_TYPE", 0);
                intent.putExtra("MAIN", true);
                startActivity(intent);
            }
        });

//        TextView versionView = navigationView.findViewById(R.id.versionView);   // ?????? ????????????
//        versionView.setOnClickListener((v) -> {
//            startActivity(new Intent(MainActivity.this, VersionActivity.class));
//        });

        TextView versionNumberView = navigationView.findViewById(R.id.versionNumber);
        try {
            versionNumberView.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // ?????? ?????? ????????? ?????? ???????????? ?????? ???????????? ??????
//        TextView coinLogView = navigationView.findViewById(R.id.coinLogView);
//        coinLogView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean bLogin = CommonUtils.bLocinCheck(pref);
//
//                if(bLogin) {
//                    startActivity(new Intent(MainActivity.this, CoinLogActivity.class));
//                } else {
//                    Toast.makeText(MainActivity.this, "???????????? ????????? ???????????????. ????????? ????????????.", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainActivity.this, PanbookLoginActivity.class));
//                }
//
//            }
//        });

    }

    @Override
    protected void onNewIntent(Intent intent) {                                                         // ?????? ???????????? ????????? onNewIntent ??? ?????? ??????. ???????????? ?????? ??????????????? ????????? ?????? ???????????? ?????? ??????
        super.onNewIntent(intent);

        String strUri = intent.getStringExtra("URI");

        MyPageSpaceFragment.strUri = strUri;

      //  viewPager.setCurrentItem(2);
      //  resetBottomBar(2);
        MyPageFragment fragment = (MyPageFragment) mainPagerAdapter.getItem(2);
   //     fragment.setImage(strUri);

        MyPageSpaceFragment myPageSpaceFragment = (MyPageSpaceFragment) fragment.myPageAdapter.getItem(1);



        Boolean isMove = intent.getBooleanExtra("MOVE",false);
        if(isMove == true)
        {
            moveViewpager(3);
        }

        //fragment.myPageAdapter.notifyDataSetChanged();
        //myPageSpaceFragment.imageSetting(intent);


        /*
        MyFragment fragment = (MyFragment) mainPagerAdapter.getItem(3);
        fragment.setImage(strUri);

        MyPageSpaceFragment myPageSpaceFragment = (MyPageSpaceFragment) myPageAdapter.getItem(1);
        myPageSpaceFragment.imageSetting(intent);

         */
    }

    public void setMenuPhoto() {                                                                            // ????????????????????? ?????? ???????????? ?????? ????????? ????????? ?????? ???????????? ?????? ??????
        ImageView faceView = navigationView.findViewById(R.id.faceView);
        String strPhoto = pref.getString("USER_PHOTO", "");

        if (strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if (!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(MainActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.caracter_a_icon)
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        } else {
            faceView.setImageResource(R.drawable.user_icon);
        }
    }

    public void onClickLogoutBtn(View view) {
        if (strUserID.equals("Guest")) {
            startActivity(new Intent(MainActivity.this, PanbookLoginActivity.class));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("????????????");
            builder.setMessage("???????????? ???????????????????");
            builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    logout();
                }
            });

            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void logout() {
        int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);

        CommonUtils.resetUserInfo(pref);

        if (nRegisterSNS == 1) {              // KakaoTalk Logout
            KakaoSDKAdapter.unregisterKakaoTalk(MainActivity.this);
        } else if (nRegisterSNS == 2) {              // Naver Logout
            OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();
            mOAuthLoginModule.logout(MainActivity.this);
        } else if (nRegisterSNS == 4) {              // Facebook Logout
            LoginManager.getInstance().logOut();
        }

        drawer.closeDrawer(Gravity.LEFT, true);
        Toast.makeText(this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();

//        viewPager.setCurrentItem(0);

        Intent intent = new Intent(MainActivity.this, PanbookLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onClickNav1Btn(View view) {
        viewPager.setCurrentItem(0);
        resetBottomBar(0);
    }

    public void onClickNav2Btn(View view) {
        viewPager.setCurrentItem(1);
        resetBottomBar(1);
    }

    public void onClickNav3Btn(View view) {
        viewPager.setCurrentItem(2);
        resetBottomBar(2);
    }

    public void onClickNav4Btn(View view) {
        viewPager.setCurrentItem(3);
        resetBottomBar(3);
    }

    public void onClickNav5Btn(View view) {
        viewPager.setCurrentItem(4);
        resetBottomBar(4);
    }

    private void resetBottomBar(int nIndex) {
        ImageView homeImgView = findViewById(R.id.homeImgView);
        ImageView searchView = findViewById(R.id.iv_bottom_bar_search);
        ImageView storageImgView = findViewById(R.id.storageImgView);
        ImageView writeImgView = findViewById(R.id.writeImgView);
        ImageView myImgView = findViewById(R.id.myImgView);

        homeImgView.setBackgroundResource(R.drawable.i_tapbar_1_1);
        searchView.setImageResource(R.drawable.i_tapbar_2_1);
        storageImgView.setBackgroundResource(R.drawable.i_tapbar_3_1);
        writeImgView.setBackgroundResource(R.drawable.i_tapbar_4_1);
        myImgView.setBackgroundResource(R.drawable.i_tapbar_5_1);

        switch (nIndex) {
            case 0:
                homeImgView.setBackgroundResource(R.drawable.i_tapbar_1_2);
                break;
            case 1:
                searchView.setImageResource(R.drawable.ic_i_tapbar_2_2);
                break;
            case 2:
                storageImgView.setBackgroundResource(R.drawable.i_tapbar_3_2);
                break;
            case 3:
                writeImgView.setBackgroundResource(R.drawable.i_tapbar_4_2);
                break;
            case 4:
                myImgView.setBackgroundResource(R.drawable.i_tapbar_5_2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 878 && resultCode == RESULT_OK) {
            if (data != null) {
                int type = data.getIntExtra("type", 0);

                viewPager.setCurrentItem(type);
                resetBottomBar(type);
            }
        }
        else if(requestCode == 3333)
        {
            resetBottomBar(2);
            viewPager.setCurrentItem(2);

            MyPageFragment my =  (MyPageFragment)mainPagerAdapter.getItem(2);

            my.refresh();



        }
        else if(requestCode == 777)
        {
            if(data != null)
            {
                int result = data.getIntExtra("MAIN_RESULT_TYPE",0);

                if(result == 1)
                {
                    resetBottomBar(3);
                    viewPager.setCurrentItem(3);
                }

            }



        }
    }

    public void moveViewpager(int type) {
        viewPager.setCurrentItem(type);
        resetBottomBar(type);
    }
}