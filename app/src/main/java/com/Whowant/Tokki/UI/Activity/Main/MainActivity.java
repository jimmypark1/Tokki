package com.Whowant.Tokki.UI.Activity.Main;

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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageAccountSettingActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageFragment;
import com.Whowant.Tokki.UI.Activity.VersionActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkRegActivity;
import com.Whowant.Tokki.UI.Adapter.MainViewpagerAdapter;
import com.Whowant.Tokki.UI.Custom.CustomViewPager;
import com.Whowant.Tokki.UI.Fragment.Main.MyFragment;
import com.Whowant.Tokki.UI.Fragment.Main.StorageBoxFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageSpaceFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.AlarmVO;
import com.Whowant.Tokki.VO.EventVO;
import com.Whowant.Tokki.VO.NoticeVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.nhn.android.naverlogin.OAuthLogin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;                            // 서랍 메뉴
    private ActionBarDrawerToggle drawerToggle;             // 메뉴 열기/닫기 를 관리하는 토글 객체

    private SharedPreferences pref;
    private NavigationView navigationView;                  // 서랍 메뉴 관련된
    private String strUserID;

    private CustomViewPager viewPager;                      // 좌우 플리킹 되지 않도록 만든 커스텀 뷰페이저
    private MainViewpagerAdapter mainPagerAdapter;          // 메인 탭 4개를 오가는 뷰페이저 어댑터

    private RelativeLayout managerLayout;                   // 좌측 서랍메뉴에서 관리자만 볼 수 있는 관리자 전용 메뉴 round_squre_stroke_gray_bg
    private TextView memberManagementView;                  // 관리자 메뉴 - 회원관리        
    private TextView workManagementView, workEditView;                    // 관리자 메뉴 - 작품관리, 작품수정
    private TextView commentManagementView, contestManagementView, episodeReportView;          //  댓글관리, 공모전 관리, 신고회차 관리
    private boolean bFinish = false;                        // 백버튼 두번 클릭시 앱 종료시키기 위한 플래그
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
    private ImageView alarmNewIconView, noticeNewIconView, eventNewIconView;                // 서랍메뉴 에서 알림, 공지사항, 이벤트 등에 찍히는 빨간 점. 읽었는지 여부는 SharedPreference 에서 판단(애초에 없던 기능을 추가한거라 그렇게 구현함)
    private boolean bCreated = true;                        // 이벤트 팝업이 생성되었는지를 가늠하는 플래그

    MyPageFragment.MyPageAdapter myPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_renewal_layout);

        int nType = getIntent().getIntExtra("TYPE", -1);                                // FCM 메시지를 통해서 들어왔는지 여부를 판단하는 변수
        boolean bFirst = getIntent().getBooleanExtra("FIRST", false);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

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
        rightBtn.setImageResource(R.drawable.serch_icon_balck);
        rightBtn.setVisibility(View.GONE);
        eventNewIconView = findViewById(R.id.eventNewIconView);
        noticeNewIconView = findViewById(R.id.noticeNewIconView);
//        alarmNewIconView = findViewById(R.id.alarmNewIconView);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        drawer = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        mainPagerAdapter = new MainViewpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {                                              // ViewPager 구조 이므로 화면이 전환될때 상단 메뉴의 상태 및 타이틀을 변경한다.
                rightBtn.setVisibility(View.GONE);
                profileIv.setVisibility(View.GONE);
                reportBtn.setVisibility(View.GONE);
                addBtn.setVisibility(View.GONE);
                inviteView.setVisibility(View.GONE);
                alarmBtn.setVisibility(View.GONE);
                marketBtn.setVisibility(View.GONE);

                if (position == 0) {
                    centerLogoView.setVisibility(View.VISIBLE);
                    titleView.setText("");
//                    rightBtn.setVisibility(View.VISIBLE);
//                    rightBtn.setImageResource(R.drawable.serch_icon_balck);
//                    profileIv.setVisibility(View.VISIBLE);
                    alarmBtn.setVisibility(View.VISIBLE);
                    marketBtn.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    centerLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("검색");
//                    rightBtn.setVisibility(View.VISIBLE);
//                    rightBtn.setImageResource(R.drawable.dot_menu);
                } else if (position == 2) {
                    centerLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("마이 페이지");
                    rightBtn.setVisibility(View.VISIBLE);
                    rightBtn.setImageResource(R.drawable.ic_i_setting_white);
                    reportBtn.setVisibility(View.GONE);
                } else if (position == 3) {
                    centerLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("작품쓰기");
//                    rightBtn.setVisibility(View.VISIBLE);
//                    rightBtn.setImageResource(R.drawable.dot_menu);
                    addBtn.setVisibility(View.VISIBLE);
//                    centerLogoView.setVisibility(View.INVISIBLE);
//                    titleView.setText("마이 페이지");
//                    rightBtn.setVisibility(View.VISIBLE);
//                    rightBtn.setImageResource(R.drawable.gear_btn);
                } else if (position == 4) {
                    centerLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("친구");
                    inviteView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(drawerToggle);

        if (nType > 0) {                                                                                         // FCM 클릭해서 앱 진입시 페이지 이동
            int nObjectID = getIntent().getIntExtra("OBJECT_ID", -1);
            Intent intent = new Intent(MainActivity.this, WorkMainActivity.class);
            intent.putExtra("WORK_ID", nObjectID);
            startActivity(intent);
        }

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {                                               // 링크 클릭해서 앱 진입시 페이지 이동
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

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())                                              // firebase 다이나믹 링크 클릭해서 앱 진입시 페이지 이동
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

//        if (bFirst) {                                                                                                        // 앱 최초 실행으로 판단되면 튜토리얼 페이지 노출
//            startActivity(new Intent(MainActivity.this, MainTutorialActivity.class));                                        // UI 변경으로 인해 화면이 맞지 않아서 우선 숨김
//        }

//        SharedPreferences.Editor editor = pref.edit();
//        editor.remove("4");
//        editor.remove("5");
//        editor.commit();

        requestAttendance();                                                                                                            // 출석체크. 무조건 전송하고, 1일 지났는지 성공/실패 여부는 서버에서 판단
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
                            Toast.makeText(MainActivity.this, "출석체크로 5 당근을 적립하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void getAlarmList() {                                                                                                       // 통신 1 - 알림 목록 가져오기
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AlarmVO> list = HttpClient.getAlarmList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null) {
                            Toast.makeText(MainActivity.this, "서버와의 통신이 실패했습니다.", Toast.LENGTH_SHORT).show();
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

    private void getNoticeData() {                                                                                                          // 통신 2 - 공지사항 가져오기
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NoticeVO> noticeList = HttpClient.getNoticeList(new OkHttpClient(), MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (noticeList == null) {
                            Toast.makeText(MainActivity.this, "서버와의 통신이 실패했습니다.", Toast.LENGTH_SHORT).show();
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

    private void getEventData() {                                                                                                               // 통신 3 - 이벤트 데이터 가져오기
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
                            for (EventVO vo : eventList) {                                                                           // 하루동안 안보기 클릭했는지 여부 판단해서 이벤트 팝업 노출
                                int nEventID = vo.getnEventID();

                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                String strToday = sdf.format(date);

                                String strDate = pref.getString("" + nEventID, "");

                                if (nEventID == -10 && strDate.length() == 0) {
                                    EventPopupActivity.eventList = eventList;
                                    startActivity(new Intent(MainActivity.this, EventPopupActivity.class));
                                    break;
                                } else if (strDate.length() == 0 || !strDate.equals(strToday)) {
                                    EventPopupActivity.eventList = eventList;
                                    startActivity(new Intent(MainActivity.this, EventPopupActivity.class));
                                    break;
                                }
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
        noticeNewIconView.setVisibility(View.INVISIBLE);
//        alarmNewIconView.setVisibility(View.INVISIBLE);
        eventNewIconView.setVisibility(View.INVISIBLE);
        alarmNewView.setVisibility(View.INVISIBLE);
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
                Toast.makeText(this, "한 번 더 뒤로 가기 버튼을 누르시면 종료합니다.", Toast.LENGTH_SHORT).show();
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

    public void onClickTopRightBtn(View view) {                                                                                     // 페이지별로 우측 버튼 다르게 동작
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
        String strURL = "https://tokki.page.link/1ux2?CMD=workmain&WORK_ID=0\n\n추천인 코드 : " + pref.getString("RECOMMEND_CODE", "");
        //String strURL = "panbook://workmain?WORK_ID=" + workVO.getnWorkID();

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tokki 에서 친구 초대가 도착했습니다!"); // 제목
        shareIntent.putExtra(Intent.EXTRA_TEXT, strURL); // 내용
        startActivity(Intent.createChooser(shareIntent, "친구 초대")); // 공유창 제목
    }

    public void onClickTopProfileBtn(View view) {
        startActivityForResult(new Intent(this, MyPageActivity.class), 878);
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
        startActivity(new Intent(MainActivity.this, WorkRegActivity.class));
//        startActivity(new Intent(MainActivity.this, CreateWorkActivity.class));
    }

    public void onClickAlarmBtn(View v) {
        startActivity(new Intent(MainActivity.this, AlarmActivity.class));
    }

    private void initMenuViews() {                                                                                  // 좌측 서랍메뉴 설정
        LinearLayout topLayout = navigationView.findViewById(R.id.topLayout);
        topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
//                viewPager.setCurrentItem(3);
//                resetBottomBar(3);
                startActivity(new Intent(MainActivity.this, MyPageActivity.class));
            }
        });

        faceView = navigationView.findViewById(R.id.faceView);                                        // 얼굴 사진
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

        if (strAdmin.equals("Y")) {          // 관리자 라면 관리자 전용 메뉴 노출
            typeView.setText("관리자");
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
            typeView.setText("일반회원");
        }


        TextView logoutBtn = navigationView.findViewById(R.id.logoutBtn);
        if (strUserID.equals("Guest")) {
            logoutBtn.setText("로그인");
        } else {
            logoutBtn.setText("로그아웃");
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strUserID.equals("Guest")) {
                    startActivity(new Intent(MainActivity.this, PanbookLoginActivity.class));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("로그아웃");
                    builder.setMessage("로그아웃 하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            logout();
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
        personalTermsView.setOnClickListener(new View.OnClickListener() {                                   // 개인정보 취급방침
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                intent.putExtra("TERMS_TYPE", 1);
                intent.putExtra("MAIN", true);
                startActivity(intent);

//                Intent intent = new Intent(MainActivity.this, AgreementPopup.class);
//                intent.putExtra("TITLE", "개인정보 수집 정책");
//                intent.putExtra("CONTENTS", getResources().getString(R.string.service_agreement));
//                intent.putExtra("TWOBTN", false);
//                startActivity(intent);
            }
        });

        TextView termsView = navigationView.findViewById(R.id.termsView);
        termsView.setOnClickListener(new View.OnClickListener() {                                           // 이용약관
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                intent.putExtra("TERMS_TYPE", 0);
                intent.putExtra("MAIN", true);
                startActivity(intent);
            }
        });

//        TextView versionView = navigationView.findViewById(R.id.versionView);   // 어플 버전정보
//        versionView.setOnClickListener((v) -> {
//            startActivity(new Intent(MainActivity.this, VersionActivity.class));
//        });

        TextView versionNumberView = navigationView.findViewById(R.id.versionNumber);
        try {
            versionNumberView.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // 코인 관련 부분은 모두 삭제하여 현재 사용하지 않음
//        TextView coinLogView = navigationView.findViewById(R.id.coinLogView);
//        coinLogView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean bLogin = CommonUtils.bLocinCheck(pref);
//
//                if(bLogin) {
//                    startActivity(new Intent(MainActivity.this, CoinLogActivity.class));
//                } else {
//                    Toast.makeText(MainActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainActivity.this, PanbookLoginActivity.class));
//                }
//
//            }
//        });

    }

    @Override
    protected void onNewIntent(Intent intent) {                                                         // 사진 설정등의 동작에 onNewIntent 를 많이 활용. 사진설정 시에 마이페이지 사진도 함께 변경하기 위한 부분
        super.onNewIntent(intent);

        String strUri = intent.getStringExtra("URI");
        MyFragment fragment = (MyFragment) mainPagerAdapter.getItem(3);
        fragment.setImage(strUri);

        MyPageSpaceFragment myPageSpaceFragment = (MyPageSpaceFragment) myPageAdapter.getItem(1);
        myPageSpaceFragment.imageSetting(intent);
    }

    public void setMenuPhoto() {                                                                            // 마이페이지에서 사진 설정시에 좌측 서랍의 사진도 함께 변경하기 위한 부분
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
            builder.setTitle("로그아웃");
            builder.setMessage("로그아웃 하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    logout();
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

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
        searchView.setImageResource(R.drawable.ic_i_tapbar_2_1);
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
    }

    public void moveViewpager(int type) {
        viewPager.setCurrentItem(type);
        resetBottomBar(type);
    }
}