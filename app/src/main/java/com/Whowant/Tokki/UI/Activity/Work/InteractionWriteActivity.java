package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.InteractionAdapter;
import com.Whowant.Tokki.UI.Fragment.Work.InteractionMainFragment;
import com.Whowant.Tokki.UI.Fragment.Work.InteractionSubFragment;
import com.Whowant.Tokki.UI.Popup.ChangeTitlePopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.WorkVO;
import com.google.android.material.tabs.TabLayout;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class InteractionWriteActivity extends AppCompatActivity implements ColorPickerDialogListener {                          // 작품에 인터렉션(분기)가 설정되어 있는경우 해당 화면에서 fragment 두개로 분기된 작품 내용을 각각 보여준다
    public static WorkVO workVO;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private InteractionAdapter pagerAdapter;
    private TextView titleView, episodeNumView;

    public String strTitle;
    public int nEpisodeID;
    public int nEpisodeIndex;

    public static int BG_SELECT_POPUP = 2000;
    public static int MEDIA_SELECT_POPUP = 2100;
    public static int MEDIA_SELECT_POPUP_VIDEO = 2200;
    public static int PHOTOPICKER_BG_IMAGE = 2300;
    public static int PHOTOPICKER_CONTENTS_IMAGE = 2400;
    public static int PHOTOPICKER_CONTENTS_VIDEO = 2500;
    public static int EXCEL = 2600;
    private boolean bSubmit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_write);

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        titleView = findViewById(R.id.episodeTitleView);
        episodeNumView = findViewById(R.id.episodeNumView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        strTitle = getIntent().getStringExtra("TITLE");
        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", -1);
        nEpisodeIndex = getIntent().getIntExtra("EPISODE_INDEX", -1);
        bSubmit = getIntent().getBooleanExtra("SUBMIT", false);

        titleView.setText(strTitle);
        episodeNumView.setText((nEpisodeIndex+1) + "화");
        pagerAdapter = new InteractionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1100) {                    // 제목 변경
                strTitle = data.getStringExtra("TITLE");
                titleView.setText(strTitle);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {                       // 블루투스 키보드가 연결되어 있는 경우 키보드 보여주기/감추기 이벤트를 해제한다. 해당 이벤트 때문에 에러 발생했었음
        super.onConfigurationChanged(newConfig);

        if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {           // BT 키보드 접속됨
            InteractionMainFragment fragment = (InteractionMainFragment)pagerAdapter.getItem(0);
            fragment.removeKeyboardEvent();
            InteractionSubFragment fragment2 = (InteractionSubFragment)pagerAdapter.getItem(1);
            fragment2.removeKeyboardEvent();
        } else if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {     // BT 키보드 해제됨
            InteractionMainFragment fragment = (InteractionMainFragment)pagerAdapter.getItem(0);
            fragment.setKeyboardEvent();
            InteractionSubFragment fragment2 = (InteractionSubFragment)pagerAdapter.getItem(1);
            fragment2.setKeyboardEvent();
        }
    }

    @Override
    public void onDestroy() {
        workVO = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_EXCEL_DONE");                          // 엑셀로 작품 업로드 시에 활용하는 필터
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {                  // 컬러 팔레트 Callback
        if(dialogId == 1000) {              // 왼쪽 fragment
            InteractionMainFragment mainFragment = (InteractionMainFragment)pagerAdapter.getItem(0);
            mainFragment.onColorSelected(dialogId, color);
        } else {                            // 오른쪽 fragment
            InteractionSubFragment subFragment = (InteractionSubFragment)pagerAdapter.getItem(1);
            subFragment.onColorSelected(dialogId, color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public void onBackPressed() {
        int nCurrent = viewPager.getCurrentItem();

        if(nCurrent == 0) {
            InteractionMainFragment mainFragment = (InteractionMainFragment)pagerAdapter.getItem(0);
            mainFragment.onBackpress();
        } else {
            InteractionSubFragment subFragment = (InteractionSubFragment)pagerAdapter.getItem(1);
            subFragment.onBackpress();
        }
    }

    public void setTitle(String title) {
        strTitle = title;
        titleView.setText(strTitle);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int nCurrent = viewPager.getCurrentItem();
                    if(nCurrent == 0) {
                        InteractionMainFragment mainFragment = (InteractionMainFragment)pagerAdapter.getItem(0);
                        mainFragment.excelDoen();
                    } else {
                        InteractionSubFragment subFragment = (InteractionSubFragment)pagerAdapter.getItem(1);
                        subFragment.excelDoen();
                    }
                }
            });
        }
    };

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickSubmitBtn(View view) {
        requestEpisodeSubmit();
    }

    private void requestEpisodeSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InteractionWriteActivity.this);
        builder.setTitle("회차 제출");
        builder.setMessage("회차를 제출하면 승인 대기 상태가 됩니다. 관리자가 회차를 승인한 이후부터 회차가 독자들에게 게시됩니다.\n회차를 제출하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                CommonUtils.showProgressDialog(InteractionWriteActivity.this, "작품을 제출하고 있습니다.");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject resultObject = HttpClient.requestEpisodeSubmit(new OkHttpClient(), nEpisodeID);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    CommonUtils.hideProgressDialog();

                                    if(resultObject == null) {
                                        Toast.makeText(InteractionWriteActivity.this, "제출에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                        Toast.makeText(InteractionWriteActivity.this, "제출 되었습니다.", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(InteractionWriteActivity.this, "제출에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onClickTopRightBtn(View view) {
        PopupMenu popup = new PopupMenu(InteractionWriteActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.work_write_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                int nCurrent = viewPager.getCurrentItem();
                if(nCurrent == 0) {
                    InteractionMainFragment mainFragment = (InteractionMainFragment)pagerAdapter.getItem(0);

                    switch(item.getItemId()) {
                        case R.id.action_btn2:
                            mainFragment.selectMenu(0);
                            break;
                        case R.id.action_btn3:
                            mainFragment.selectMenu(1);
                            break;
                        case R.id.action_btn4:
                            mainFragment.selectMenu(2);
                            break;
                    }
                } else {
                    InteractionSubFragment subFragment = (InteractionSubFragment)pagerAdapter.getItem(1);

                    switch(item.getItemId()) {
                        case R.id.action_btn2:
                            subFragment.selectMenu(0);
                            break;
                        case R.id.action_btn3:
                            subFragment.selectMenu(1);
                            break;
                        case R.id.action_btn4:
                            subFragment.selectMenu(2);
                            break;
                    }
                }

                return true;
            }
        });

        popup.show();//showing popup menu
    }

    public void clickEditTitleBtn(View view) {
        Intent intent = new Intent(InteractionWriteActivity.this, ChangeTitlePopup.class);
        intent.putExtra("TITLE", strTitle);
        intent.putExtra("EPISODE_ID", nEpisodeID);
        startActivityForResult(intent, 1100);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        if(viewPager.getCurrentItem() == 0) {
//            InteractionMainFragment mainFragment = (InteractionMainFragment)pagerAdapter.getItem(0);
//            mainFragment.onNewIntent(intent);
//        } else {
//            InteractionSubFragment mainFragment = (InteractionSubFragment)pagerAdapter.getItem(1);
//            mainFragment.onNewIntent(intent);
//        }
//    }
}
