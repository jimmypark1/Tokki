package com.Whowant.Tokki.UI.Activity.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.Whowant.Tokki.UI.Activity.DrawerMenu.AlarmActivity;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.EventActivity;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.NoticeActivity;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Activity.Login.TermsActivity;
import com.Whowant.Tokki.UI.Activity.Work.CreateWorkActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Adapter.MainViewpagerAdapter;
import com.Whowant.Tokki.UI.Custom.CustomViewPager;
import com.Whowant.Tokki.UI.Fragment.Main.KeepFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MyFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.AlarmVO;
import com.Whowant.Tokki.VO.EventVO;
import com.Whowant.Tokki.VO.NoticeVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.nhn.android.naverlogin.OAuthLogin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity {
    //87:3d:1b:b4:4a:86:bc:89:86:2a:28:dc:c0:b9:f1:15:07:9c:ba:ee
    //07:85:b9:f3:08:14:5c:57:5a:b3:04:d8:c6:20:0b:7c:71:87:bc:e5
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    private SharedPreferences pref;
    private NavigationView navigationView;
    private String strUserID;

    private CustomViewPager viewPager;
    private MainViewpagerAdapter mainPagerAdapter;

    private RelativeLayout managerLayout;
    private TextView memberManagementView;
    private TextView workManagementView;
    private TextView commentManagementView, contestManagementView;
    private boolean bFinish = false;
    private ImageView cenverLogoView;
    private ImageButton leftBtn;
    private ImageButton rightBtn;
    private TextView titleView;
    private ImageView alarmNewIconView, noticeNewIconView, eventNewIconView;
    private boolean bCreated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_renewal_layout);

        String strKey  = getKeyHash(this);

        int nType = getIntent().getIntExtra("TYPE", -1);
        boolean bFirst = getIntent().getBooleanExtra("FIRST", false);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        cenverLogoView = findViewById(R.id.cenverLogoView);
        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        titleView = findViewById(R.id.titleView);
        rightBtn.setImageResource(R.drawable.serch_icon_balck);
        eventNewIconView = findViewById(R.id.eventNewIconView);
        noticeNewIconView = findViewById(R.id.noticeNewIconView);
        alarmNewIconView = findViewById(R.id.alarmNewIconView);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        drawer = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        mainPagerAdapter = new MainViewpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    cenverLogoView.setVisibility(View.VISIBLE);
                    titleView.setText("");
                    rightBtn.setVisibility(View.VISIBLE);
                    rightBtn.setImageResource(R.drawable.serch_icon_balck);
                } else if(position == 1) {
                    cenverLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("보관함");
                    rightBtn.setVisibility(View.VISIBLE);
                    rightBtn.setImageResource(R.drawable.dot_menu);
                } else if(position == 2) {
                    cenverLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("작품쓰기");
                    rightBtn.setVisibility(View.VISIBLE);
                    rightBtn.setImageResource(R.drawable.icon_button_writing);
                } else if(position == 3) {
                    cenverLogoView.setVisibility(View.INVISIBLE);
                    titleView.setText("마이 페이지");
                    rightBtn.setVisibility(View.VISIBLE);
                    rightBtn.setImageResource(R.drawable.gear_btn);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(drawerToggle);

        if(nType > 0) {
            int nObjectID = getIntent().getIntExtra("OBJECT_ID", -1);
            Intent intent;

            switch(nType) {
                case 1:                                 // 작품 상세화면으로 이동
                    intent = new Intent(MainActivity.this, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", nObjectID);
                    startActivity(intent);
                    break;

                case 2:                                 // 작품 상세화면으로 이동
                    intent = new Intent(MainActivity.this, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", nObjectID);
                    startActivity(intent);
                    break;
            }
        }

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();

            if(uri != null) {
                String strWorkID = uri.getQueryParameter("WORK_ID");

                if(strWorkID != null) {
                    Intent intent = new Intent(MainActivity.this, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", Integer.valueOf(strWorkID));
                    startActivity(intent);
                }
            }
        }

        initMenuViews();

        if(bFirst) {
            startActivity(new Intent(MainActivity.this, MainTutorialActivity.class));
        }

//        SharedPreferences.Editor editor = pref.edit();
//        editor.remove("4");
//        editor.remove("5");
//        editor.commit();

        requestAttendance();
    }

    public void requestAttendance() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestAttendance(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bResult) {
                            Toast.makeText(MainActivity.this, "출석체크로 5 당근을 적립하였습니다.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        }).start();
    }

    private void getAlarmList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AlarmVO> list = HttpClient.getAlarmList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(AlarmVO vo : list) {
                            if(!vo.isbRead()) {
                                alarmNewIconView.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                        getNoticeData();
                    }
                });
            }
        }).start();
    }

    private void getNoticeData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NoticeVO> noticeList = HttpClient.getNoticeList(new OkHttpClient(), MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(NoticeVO vo : noticeList) {
                            if(!vo.getbRead()) {
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

    private void getEventData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<EventVO> eventList = HttpClient.getEventList(new OkHttpClient(), MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(EventVO vo : eventList) {
                            if(!vo.isbRead()) {
                                eventNewIconView.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                        if(bCreated) {
                            for(EventVO vo : eventList) {
                                int nEventID = vo.getnEventID();

                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                String strToday = sdf.format(date);

                                String strDate = pref.getString("" + nEventID, "");
                                if(strDate.length() == 0 || !strDate.equals(strToday)) {
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


    public boolean isAttendanceToday() {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        return pref.getBoolean( getToday() , false );
    }

    public String getToday() {
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);
        return ( cal.get(Calendar.YEAR)+"" + cal.get(Calendar.MONTH)+"" + cal.get(Calendar.DAY_OF_MONTH)+"" ) ;
    }

    @Override
    public void onResume() {
        super.onResume();
        noticeNewIconView.setVisibility(View.INVISIBLE);
        alarmNewIconView.setVisibility(View.INVISIBLE);
        eventNewIconView.setVisibility(View.INVISIBLE);
        getAlarmList();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        bCreated = false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!bFinish) {
                Toast.makeText(this, "한 번 더 뒤로가기 버튼을 누르시면 종료합니다.", Toast.LENGTH_SHORT).show();
                bFinish = true;

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
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

    public void onClickTopRightBtn(View view) {
        int nPosition = viewPager.getCurrentItem();

        if(nPosition == 0) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        } else if(nPosition == 1){
            KeepFragment fragment = (KeepFragment)mainPagerAdapter.getItem(1);
            fragment.showMenus();
        } else if(nPosition == 2) {
            mainPagerAdapter.getItem(2);
            startActivity(new Intent(MainActivity.this, CreateWorkActivity.class));
        } else if(nPosition == 3) {
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        }
    }

    private void initMenuViews() {
        LinearLayout topLayout = navigationView.findViewById(R.id.topLayout);
        topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bLogin = CommonUtils.bLocinCheck(pref);

                if(bLogin) {
                    drawer.closeDrawer(GravityCompat.START);
                    viewPager.setCurrentItem(3);
                    resetBottomBar(3);
                } else {
                    Toast.makeText(MainActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginSelectActivity.class));
                }

            }
        });

        ImageView faceView = navigationView.findViewById(R.id.faceView);
        String strPhoto = pref.getString("USER_PHOTO", "");

        if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if(!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(MainActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.user_icon)
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        } else {
            faceView.setImageResource(R.drawable.user_icon);
        }

        strUserID = pref.getString("USER_ID", "Guest");

        TextView nameView = navigationView.findViewById(R.id.nameView);
        nameView.setText(pref.getString("USER_NAME", "Guest"));

        TextView typeView = navigationView.findViewById(R.id.typeView);
        String strAdmin = pref.getString("ADMIN", "N");

        if(strAdmin.equals("Y")) {          // 관리자 라면
            typeView.setText("관리자");
            managerLayout = navigationView.findViewById(R.id.managerLayout);
            managerLayout.setVisibility(View.VISIBLE);
            memberManagementView = navigationView.findViewById(R.id.memberManagementView);
            workManagementView = navigationView.findViewById(R.id.workManagementView);
            commentManagementView = navigationView.findViewById(R.id.commentManagementView);
            contestManagementView = navigationView.findViewById(R.id.contestManagementView);

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

            commentManagementView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CommentManagementActivity.class));
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
        if(strUserID.equals("Guest")) {
            logoutBtn.setText("로그인");
        } else {
            logoutBtn.setText("로그아웃");
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(strUserID.equals("Guest")) {
                    startActivity(new Intent(MainActivity.this, LoginSelectActivity.class));
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
//                    startActivity(new Intent(MainActivity.this, LoginSelectActivity.class));
//                }
//
//            }
//        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String strUri = intent.getStringExtra("URI");
        MyFragment fragment = (MyFragment)mainPagerAdapter.getItem(3);
        fragment.setImage(strUri);
    }

    public void setMenuPhoto() {
        ImageView faceView = navigationView.findViewById(R.id.faceView);
        String strPhoto = pref.getString("USER_PHOTO", "");

        if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if(!strPhoto.startsWith("http"))
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
        if(strUserID.equals("Guest")) {
            startActivity(new Intent(MainActivity.this, LoginSelectActivity.class));
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

//        switch (item.getItemId()) {
//            case R.id.action_search:
//                startActivity(new Intent(MainActivity.this, SearchActivity.class));
//                break;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);

        CommonUtils.resetUserInfo(pref);

        if(nRegisterSNS == 1) {              // KakaoTalk Logout
            KakaoSDKAdapter.unregisterKakaoTalk(MainActivity.this);
        } else if(nRegisterSNS == 2) {              // Naver Logout
            OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();
            mOAuthLoginModule.logout(MainActivity.this);
        } else if(nRegisterSNS == 4) {              // Facebook Logout
            LoginManager.getInstance().logOut();
        }

//        initMenuViews();
        drawer.closeDrawer(Gravity.LEFT, true);
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

        viewPager.setCurrentItem(0);
//        resetBottomBar(0);

        Intent intent = new Intent(MainActivity.this, LoginSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    private void resetBottomBar(int nIndex) {
        ImageView homeImgView = findViewById(R.id.homeImgView);
        ImageView storageImgView = findViewById(R.id.storageImgView);
        ImageView writeImgView = findViewById(R.id.writeImgView);
        ImageView myImgView = findViewById(R.id.myImgView);

        homeImgView.setBackgroundResource(R.drawable.ic_home_off);
        storageImgView.setBackgroundResource(R.drawable.ic_storage_off);
        writeImgView.setBackgroundResource(R.drawable.ic_write_off);
        myImgView.setBackgroundResource(R.drawable.ic_mypage_off);

        switch(nIndex) {
            case 0:
                homeImgView.setBackgroundResource(R.drawable.ic_home_on);
                break;
            case 1:
                storageImgView.setBackgroundResource(R.drawable.ic_storage_on);
                break;
            case 2:
                writeImgView.setBackgroundResource(R.drawable.ic_write_on);
                break;
            case 3:
                myImgView.setBackgroundResource(R.drawable.ic_mypage_on);
                break;
        }
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("Login", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }
}