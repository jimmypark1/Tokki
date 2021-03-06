package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageActivity;
import com.Whowant.Tokki.UI.Activity.Rank.RankActivity;
import com.Whowant.Tokki.UI.Activity.Report.ReportActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterMainActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterPageActivity;
import com.Whowant.Tokki.UI.Custom.FlowLayout;
import com.Whowant.Tokki.UI.Popup.CarrotDoneActivity;
import com.Whowant.Tokki.UI.Popup.EpisodeAproveCancelPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.Utils.DialogMenu;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class WorkMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private WorkVO workVO;
    private ArrayList<String> showingList;
    private ArrayList<String> tagList = new ArrayList<>();
    private ArrayList<String> genreList = new ArrayList<>();
    private RecyclerView listView;
    private WorkAdapter aa;

    private int nWorkID;
    private boolean bKeep = false;
    private boolean bFisrt = true;
    private SharedPreferences pref;

    private Menu menu;
    private int nLastEpisodeID = -1;                // ??????????????? ?????? ???????????? ID
    private int nLastOrder = -1;
    private int nInteractionID = -1;
    private int nLastIndex = -1;                    // ??????????????? ?????? ??????????????? ????????????
    private String strBtnTitle = "????????? ????????????";
    //    private ActionBar actionBar;
    private boolean bModify = false;
    private boolean bDesc = true;
    private TextView titleView, carrotCountView;
    private Button showBtn;
    private Toast toast;

    int type = 0;

    // [S] winhmoon
    int[] levelRes = new int[]{
            R.drawable.ic_i_level_1, R.drawable.ic_i_level_2, R.drawable.ic_i_level_3, R.drawable.ic_i_level_4, R.drawable.ic_i_level_5,
            R.drawable.ic_i_level_6, R.drawable.ic_i_level_7, R.drawable.ic_i_level_8, R.drawable.ic_i_level_9, R.drawable.ic_i_level_10
    };

    String[] levelName = new String[]{
            "LV.1", "LV.2", "LV.3", "LV.4", "LV.5", "LV.6", "LV.7", "LV.8", "LV.9", "LV.10",
    };

    private int nLevel = 1;                                                                 // ?????? ????????? ?????? ?????? ??????
    // [E] winhmoon

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_main);

       type = getIntent().getIntExtra("WORK_TYPE",0);

        mActivity = this;

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        findViewById(R.id.ib_top_layout_dot).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_top_layout_dot).setOnClickListener((v) -> {
            DialogMenu dialogMenu = new DialogMenu();
            // ????????? ?????? : ??????, ?????????
            dialogMenu.showMenu(mActivity, "????????? ??????", R.array.REPORT_MENU, new DialogMenu.ItemClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    Intent intent = new Intent(mActivity, ReportActivity.class);

                    if (pos == 0) {
                        intent.putExtra("title", "?????? ??????");
                    } else {
                        intent.putExtra("title", "????????? ??????");
                    }

                    startActivity(intent);
                }
            });
        });

        ImageButton leftBtn = findViewById(R.id.leftBtn);
        ImageButton rightBtn = findViewById(R.id.rightBtn);
        leftBtn.setImageResource(R.drawable.back_button);
        rightBtn.setImageResource(R.drawable.dot_menu);
        ImageView cenverLogoView = findViewById(R.id.cenverLogoView);
        cenverLogoView.setVisibility(View.INVISIBLE);
//        titleView = findViewById(R.id.titleView);
        titleView = findViewById(R.id.tv_top_layout_title);
        showBtn = findViewById(R.id.showBtn);
        carrotCountView = findViewById(R.id.carrotCountView);
//        titleView.setText(workVO.getTitle());

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        nWorkID = getIntent().getIntExtra("WORK_ID", 0);

        showingList = new ArrayList<>();
        listView = findViewById(R.id.listView);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.work_main_menu, menu);
//        this.menu = menu;
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        boolean bLogin = false;
//
//        switch(item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//
//            case R.id.action_btn1:
//                bLogin = CommonUtils.bLocinCheck(pref);
//
//                if(!bLogin) {
//                    CommonUtils.makeText(this, "???????????? ????????? ???????????????. ????????? ????????????.", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(WorkMainActivity.this, LoginActivity.class));
//                    return false;
//                }
//
//                if(bKeep) {
//                    requestUnKeepWork();
//                } else {
//                    requestKeepWork();
//                }
//
//                break;
//
//            case R.id.action_btn2:
//                String strURL = "panbook://workmain?WORK_ID=" + workVO.getnWorkID();
//                TextTemplate params = TextTemplate.newBuilder(
//                        "PanBook ?????? " + workVO.getTitle() + " ????????? ???????????????!",
//                        LinkObject.newBuilder()
//                                .setWebUrl(strURL)
//                                .setMobileWebUrl(strURL)
//                                .setAndroidExecutionParams("WORK_ID=" + workVO.getnWorkID())
//                                .build()
//                )
//                        .setButtonTitle("Panbook ??? ????????????")
//                        .build();
//
//                KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
//                    @Override
//                    public void onFailure(ErrorResult errorResult) {
//                        Logger.e(errorResult.toString());
//                    }
//
//                    @Override
//                    public void onSuccess(KakaoLinkResponse result) {
//                    }
//                });
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private Map<String, String> serverCallbackArgs = getServerCallbackArgs();

    @Override
    public void onResume() {
        super.onResume();

        getKeepData();
    }

    private Map<String, String> getServerCallbackArgs() {
        Map<String, String> callbackParameters = new HashMap<>();
        callbackParameters.put("user_id", "1234");
        callbackParameters.put("title", "???????????? ????????? ?????? !@#$%");
        return callbackParameters;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus == true) {
//            aa.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void requestKeepWork() {
      //  CommonUtils.showProgressDialog(WorkMainActivity.this, "???????????? ?????? ????????????.");
        CommonUtils.showProgressDialog(WorkMainActivity.this, "??? ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.requestSetKeep(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "" + nWorkID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (resultObject == null) {
                            CommonUtils.makeText(WorkMainActivity.this, "???????????? ????????? ???????????? ????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        bKeep = true;
                        CommonUtils.makeText(WorkMainActivity.this, "??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        aa.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickTopRightBtn(View view) {
        showMenus();
    }

    public void showMenus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????");

        builder.setItems(getResources().getStringArray(R.array.REPORT_MENU), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestUnKeepWork() {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "??? ????????? ??? ?????????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.requestUnKeep(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "" + nWorkID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(resultObject == null) {
                            CommonUtils.makeText(WorkMainActivity.this, "???????????? ????????? ???????????? ????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        bKeep = false;
                        CommonUtils.makeText(WorkMainActivity.this, "??? ????????? ??? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        aa.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void onClickCarrotBtn(View view) {
        if(workVO.getnWriterID().equals(pref.getString("USER_ID", "Guest"))) {
            CommonUtils.makeText(this, "??? ???????????? ?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(WorkMainActivity.this, CarrotDoneActivity.class);
        intent.putExtra("WORK_ID", workVO.getnWorkID());
        startActivity(intent);
    }

    private void getKeepData() {            // ???????????? ???????????? ??????
        CommonUtils.showProgressDialog(WorkMainActivity.this, "?????? ????????? ???????????? ????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.isKeepWork(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "" + nWorkID);

                try {
                    if(resultObject == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.hideProgressDialog();
                                Toast.makeText(WorkMainActivity.this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                    if(resultObject.getString("RESULT").equals("SUCCESS"))
                        bKeep = true;

                    if(resultObject.has("INTERACTION_EPISODE"))
                        nInteractionID = resultObject.getInt("INTERACTION_EPISODE");
                    if(resultObject.has("LAST"))
                        nLastEpisodeID = resultObject.getInt("LAST");
                    if(resultObject.has("LAST_ORDER"))
                        nLastOrder = resultObject.getInt("LAST_ORDER");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTagData();
                    }
                });
            }
        }).start();
    }

    private void getTagData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getWorkTagWithID(new OkHttpClient(), "" + nWorkID);
                tagList.clear();
                genreList.clear();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(resultObject == null) {
                            CommonUtils.hideProgressDialog();
                            CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(tagList.size() == 0) {
                            try {
                                JSONArray tagArray = resultObject.getJSONArray("TAG_LIST");
                                JSONArray genreArray = resultObject.getJSONArray("GENRE_LIST");

                                for(int i = 0 ; i < tagArray.length() ; i++) {
                                    JSONObject object = tagArray.getJSONObject(i);
                                    tagList.add(object.getString("TAG_TITLE"));
                                }

                                for(int i = 0 ; i < genreArray.length() ; i++) {
                                    JSONObject object = genreArray.getJSONObject(i);
                                    genreList.add(object.getString("GENRE_NAME"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        getWorkData(bDesc);
                    }
                });
            }
        }).start();
    }

    private void getWorkData(boolean bDesc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                workVO = HttpClient.getWorkWithID(new OkHttpClient(), "" + nWorkID, pref.getString("USER_ID", "Guest"), bDesc);

                if(workVO == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(WorkMainActivity.this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }
                if (workVO.getnWriterID().equals(pref.getString("USER_ID", "Guest"))) {
                    bModify = true;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (workVO == null) {
                            CommonUtils.hideProgressDialog();
                            CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        showingList = new ArrayList<>();
                        int nEpisodeCount = 0;
                        boolean bComplete = false;

                        if(workVO.getEpisodeList() != null) {
                            nEpisodeCount = workVO.getEpisodeList().size();
                            bComplete = workVO.isbComplete();
                        }

                        showingList.add("IMAGE");
                        showingList.add("TITLE");
                        showingList.add("SYNOPSIS");
                        showingList.add("GENRE");

                        String strEpisodeCount = "??? " + nEpisodeCount + "??? / " + (bComplete == true ? "??????" : "?????????") + "";
                        showingList.add(strEpisodeCount);

                        for (int i = 0; i < nEpisodeCount; i++) {
                            if (nLastEpisodeID > -1) {
                                EpisodeVO vo = workVO.getSortedEpisodeList().get(i);

                                if(nLastEpisodeID == vo.getnEpisodeID() && CommonUtils.bLocinCheck(pref)) {
                                    nLastIndex = vo.getnOrder();
                                    strBtnTitle = nLastIndex + "??? ????????????";
                                }
                            }

                            showingList.add("EPISODE");
                        }

                        CommonUtils.hideProgressDialog();

                        if (bFisrt)
                            bFisrt = false;

                        titleView.setText(workVO.getTitle());
                        aa = new WorkAdapter(WorkMainActivity.this, showingList);
                        listView.setAdapter(aa);
                        listView.setLayoutManager(new LinearLayoutManager(WorkMainActivity.this, LinearLayoutManager.VERTICAL, false));

                        showBtn.setText(strBtnTitle);
                        showBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (workVO.getEpisodeList().size() == 0) {
                                    CommonUtils.makeText(WorkMainActivity.this, "????????? ????????????.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (nLastIndex == -1) {
                                    int nIndex = 0;

                                    EpisodeVO episodeVO = workVO.getSortedEpisodeList().get(nIndex);
                                    if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // ????????? ??????????????? ???????????? ?????? ??????????????????. ???, ?????? ????????? ??????????????????
                                        checkInteractionSelect(nIndex);
                                        return;
                                    }

                                    if(type == 0)
                                    {
                                        Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                        ViewerActivity.workVO = workVO;
                                        intent.putExtra("EPISODE_INDEX", nIndex);
                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        // ?????????,...
                                        Intent intent = new Intent(WorkMainActivity.this, WebWorkViewerActivity.class);
                                        ViewerActivity.workVO = workVO;
                                      //  intent.putExtra("WORK", (Serializable) workVO);
                                        intent.putExtra("EPISODE_INDEX", nIndex);
                                        startActivity(intent);

                                    }


                                } else {
                                    int nIndex = nLastIndex - 1;

                                    EpisodeVO episodeVO = workVO.getSortedEpisodeList().get(nIndex);
                                    if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // ????????? ??????????????? ???????????? ?????? ??????????????????. ???, ?????? ????????? ??????????????????
                                        checkInteractionSelect(nIndex);
                                        return;
                                    }

                                    if(type == 0)
                                    {
                                        Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                        ViewerActivity.workVO = workVO;
                                        intent.putExtra("EPISODE_INDEX", nIndex);
                                        intent.putExtra("LAST_ORDER", nLastOrder);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(WorkMainActivity.this, WebWorkViewerActivity.class);
                                        ViewerActivity.workVO = workVO;
                                      //  intent.putExtra("WORK", (Serializable) workVO);
                                        intent.putExtra("EPISODE_INDEX", nIndex);

                                        intent.putExtra("LAST_ORDER", nLastOrder);
                                        startActivity(intent);

                                    }



                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(position < 5) {
            if(position == 1) {
//                Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
//                ViewerActivity.workVO = workVO;
//                intent.putExtra("EPISODE_INDEX", nIndex);
//                startActivity(intent);
            }
            return;
        }

        int nIndex = position - 5;

        if(nIndex >= 2 && !CommonUtils.bLocinCheck(pref)) {
            CommonUtils.makeText(WorkMainActivity.this, "???????????? ????????? ???????????????. ????????? ????????????.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WorkMainActivity.this, PanbookLoginActivity.class));
            return;
        }

        EpisodeVO episodeVO = workVO.getSortedEpisodeList().get(nIndex);
        if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // ????????? ??????????????? ???????????? ?????? ???????????? ??????. ???, ?????? ????????? ???????????? ??????
            checkInteractionSelect(nIndex);
            return;
        }

        if(type == 0)
        {
            Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
            ViewerActivity.workVO = workVO;
            intent.putExtra("EPISODE_INDEX", nIndex);
            startActivity(intent);



        }
        else
        {
            Intent intent = new Intent(WorkMainActivity.this, WebWorkViewerActivity.class);
            ViewerActivity.workVO = workVO;
           // intent.putExtra("EPISODE_INDEX", nIndex);
          //  intent.putExtra("WORK", (Serializable) workVO);
            intent.putExtra("EPISODE_INDEX", nIndex);

            startActivity(intent);
        }

    }

    private void checkInteractionSelect(final int nIndex) {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "????????? ??????????????????. ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.checkInteractionSelect(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nWorkID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            CommonUtils.makeText(WorkMainActivity.this, "????????? ???????????? ??????????????????.  ????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                            return;
                        } else {

                            if(type == 0)
                            {
                                Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                                intent.putExtra("EPISODE_INDEX", nIndex);
                                intent.putExtra("INTERACTION", true);
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(WorkMainActivity.this, WebWorkViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                             //   intent.putExtra("EPISODE_INDEX", nIndex);
                            //    intent.putExtra("WORK", (Serializable) workVO);
                                intent.putExtra("EPISODE_INDEX", nIndex);

                                startActivity(intent);

                            }

                        }
                    }
                });
            }
        }).start();
    }

    private void requestWorkAprove(final int nEpisodeID) {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "?????? ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestWorkAprove(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            CommonUtils.makeText(WorkMainActivity.this, "?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            getKeepData();
                        } else {
                            CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ?????????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteEpisode(final int nEpisodeID) {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "?????? ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strResult = HttpClient.requestDeleteEpisode(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(strResult.equals("SUCCESS")) {
                            CommonUtils.makeText(WorkMainActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            getKeepData();
                        } else if(strResult.equals("INTERACTION")) {
                            CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ??????????????? ???????????? ????????????. ??????????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder>{
        private ArrayList<String> itemsList;
        private Activity mContext;

        public WorkAdapter(Activity context, ArrayList<String> itemsList) {
            this.mContext = context;
            this.itemsList = itemsList;
        }

        @Override
        public WorkViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = null;

            if(position == 0) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_image_row, viewGroup, false);
            } else if(position == 1) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_title_row, viewGroup, false);
            } else if(position == 2) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_synopsis_row, viewGroup, false);
            } else if(position == 3) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_tag_row, viewGroup, false);
            } else if(position == 4) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_order_row, viewGroup, false);
            } else if(position > 4) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_read_episode_row, viewGroup, false);
            }

            WorkViewHolder vh = new WorkViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(WorkViewHolder holder, int position) {
            if(position == 0) {
                ImageView coverImgView = holder.itemView.findViewById(R.id.coverImgView);
                ImageButton shareBtn = holder.itemView.findViewById(R.id.shareBtn);
                ImageView subscribeBtn = holder.itemView.findViewById(R.id.heartBtn);
                String strImgUrl = workVO.getCoverFile();
                coverImgView.setClipToOutline(true);

                if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                    Glide.with(WorkMainActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.no_poster)
                            .apply(new RequestOptions().centerCrop())
                            .into(coverImgView);
                } else {
                    if(!strImgUrl.startsWith("http")) {
                        strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                    }

                    Glide.with(WorkMainActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(strImgUrl)
                            .apply(new RequestOptions().centerCrop())
                            .into(coverImgView);
                }

                shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        String strURL = "http://175.123.253.231:8080/howmuch_web/StartApp.jsp?CMD=workmain&WORK_ID=" + workVO.getnWorkID();
                        String strURL = "https://tokki.page.link/1ux2?CMD=workmain&WORK_ID=" + workVO.getnWorkID();
                        //String strURL = "panbook://workmain?WORK_ID=" + workVO.getnWorkID();

                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tokki ?????? " + workVO.getTitle() + " ????????? ???????????????!"); // ??????
                        shareIntent.putExtra(Intent.EXTRA_TEXT, strURL); // ??????
                        startActivity(Intent.createChooser(shareIntent, "?????? ??????")); // ????????? ??????
                    }
                });

                if (bKeep) {
                    subscribeBtn.setImageResource(R.drawable.i_heart_red);
//                    subscribeBtn.setBackgroundResource(R.drawable.full_heart_button);
                } else {
                    subscribeBtn.setImageResource(R.drawable.i_heart_black);
//                    subscribeBtn.setBackgroundResource(R.drawable.empty_heart);
                }

                subscribeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean bLogin = CommonUtils.bLocinCheck(pref);

                        if(!bLogin) {
                            CommonUtils.makeText(WorkMainActivity.this, "???????????? ????????? ???????????????. ????????? ????????????.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(WorkMainActivity.this, PanbookLoginActivity.class));
                            return;
                        }

                        if(bKeep) {
                            requestUnKeepWork();
                        } else {
                            requestKeepWork();
                        }
                    }
                });
            } else if(position == 1) {
                holder.itemView.setEnabled(false);
                TextView titleView = holder.itemView.findViewById(R.id.titleView);
                TextView writerNameView = holder.itemView.findViewById(R.id.writerNameView);
                TextView startPointView = holder.itemView.findViewById(R.id.startPointView);
                TextView tapCountView = holder.itemView.findViewById(R.id.tapCountView);
                TextView commentCountView = holder.itemView.findViewById(R.id.commentCountView);
                ImageView faceView = holder.itemView.findViewById(R.id.faceView);

                RelativeLayout levelBGView = holder.itemView.findViewById(R.id.levelBGView);
                ImageView lv1IconView = holder.itemView.findViewById(R.id.lv1IconView);
                ImageView lv5IconView = holder.itemView.findViewById(R.id.lv5IconView);
                ImageView lv10IconView = holder.itemView.findViewById(R.id.lv10IconView);
                ImageView emptyIconView = holder.itemView.findViewById(R.id.emptyIconView);
                RelativeLayout smallLv10View = holder.itemView.findViewById(R.id.smallLv10View);
                TextView smallLvView = holder.itemView.findViewById(R.id.smallLvView);

                // [S] winhmoon
                TextView heartPointView = holder.itemView.findViewById(R.id.heartPointView);
                heartPointView.setText(CommonUtils.getPointCount(workVO.getnKeepcount()));

                LinearLayout carrotView = holder.itemView.findViewById(R.id.carrotView);
                carrotView.setOnClickListener((v) -> {
                    if (workVO.getnWriterID().equals(pref.getString("USER_ID", "Guest"))) {
                        CommonUtils.makeText(WorkMainActivity.this, "??? ???????????? ?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(WorkMainActivity.this, CarrotDoneActivity.class);
                    intent.putExtra("WORK_ID", workVO.getnWorkID());
                    startActivity(intent);
                });

                TextView carrotTv = holder.itemView.findViewById(R.id.tv_carrot);

              //  CommonUtils.getPointCount(workVO.getnDonationCarrot())

                //carrotTv.setText(new DecimalFormat("###,###").format(workVO.getnDonationCarrot()) + " ???");
                carrotTv.setText(CommonUtils.getPointCount(workVO.getnDonationCarrot()) + " ???");


                ImageView profilePhotoIv = holder.itemView.findViewById(R.id.iv_profile_photo);
                profilePhotoIv.setClipToOutline(true);
                String writerPhoto = workVO.getStrWriterPhoto();
                if (writerPhoto != null && !writerPhoto.equals("null") && !writerPhoto.equals("NULL") && writerPhoto.length() > 0) {
                    if (!writerPhoto.startsWith("http"))
                        writerPhoto = CommonUtils.strDefaultUrl + "images/" + writerPhoto;

                    Glide.with(WorkMainActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(writerPhoto)
                            .placeholder(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(profilePhotoIv);
                } else {
                    Glide.with(WorkMainActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(profilePhotoIv);
                }

                nLevel = CommonUtils.getLevel(workVO.getnDonationCarrot());

         //       ImageView levelIv = holder.itemView.findViewById(R.id.iv_level);
           //     levelIv.setImageResource(levelRes[nLevel - 1]);

//                TextView levelTv = holder.itemView.findViewById(R.id.tv_level);
          //      levelTv.setText(levelName[nLevel - 1]);

                holder.itemView.findViewById(R.id.rl_writer_name).setOnClickListener((v) -> {
                    Intent intent = new Intent(mActivity, WriterPageActivity.class);
                    intent.putExtra("writerId", workVO.getnWriterID());
                    startActivity(intent);
                });

                // [E] winhmoon

                LinearLayout pointLayout = holder.itemView.findViewById(R.id.pointLayout);
                pointLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(WorkMainActivity.this, EpisodeCommentActivity.class);
                        intent.putExtra("WORK_ID", workVO.getnWorkID());
                        startActivity(intent);
                    }
                });

                String strPhoto = workVO.getStrWriterPhoto();
                if (strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                    if (!strPhoto.startsWith("http"))
                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                    Glide.with(WorkMainActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strPhoto)
                            .placeholder(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    Glide.with(WorkMainActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                }

                titleView.setText(workVO.getTitle());
                RelativeLayout writerInfoLayout = holder.itemView.findViewById(R.id.writerInfoLayout);
                writerNameView.setText(workVO.getStrWriterName());
                startPointView.setText(String.format("%.1f", workVO.getfStarPoint()));
                tapCountView.setText(CommonUtils.getPointCount(workVO.getnHitsCount()));
                commentCountView.setText(CommonUtils.getPointCount(workVO.getnCommentCount()));

                writerInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(WorkMainActivity.this, WriterMainActivity.class);
                        intent.putExtra("WRITER", true);
                        intent.putExtra("USER_ID", workVO.getnWriterID());
                        startActivity(intent);
                    }
                });

                emptyIconView.setVisibility(View.GONE);
                lv1IconView.setVisibility(View.GONE);
                lv5IconView.setVisibility(View.GONE);
                lv10IconView.setVisibility(View.GONE);
                smallLv10View.setVisibility(View.GONE);
                int nLevel = CommonUtils.getLevel(workVO.getnDonationCarrot());

                switch(nLevel) {
                    case 1:
                        levelBGView.setBackgroundResource(R.drawable.lv1_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv1_bg);
                        smallLvView.setText("LV.1");
                        lv1IconView.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        levelBGView.setBackgroundResource(R.drawable.lv2_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv2_bg);
                        smallLvView.setText("LV.2");
                        emptyIconView.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        levelBGView.setBackgroundResource(R.drawable.lv3_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv3_bg);
                        smallLvView.setText("LV.3");
                        emptyIconView.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        levelBGView.setBackgroundResource(R.drawable.lv4_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv4_bg);
                        smallLvView.setText("LV.4");
                        emptyIconView.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        levelBGView.setBackgroundResource(R.drawable.lv5_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv5_bg);
                        smallLvView.setText("LV.5");
                        lv5IconView.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        levelBGView.setBackgroundResource(R.drawable.lv6_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv6_bg);
                        smallLvView.setText("LV.6");
                        emptyIconView.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        levelBGView.setBackgroundResource(R.drawable.lv7_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv7_bg);
                        smallLvView.setText("LV.7");
                        emptyIconView.setVisibility(View.VISIBLE);
                        break;
                    case 8:
                        levelBGView.setBackgroundResource(R.drawable.lv8_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv8_bg);
                        smallLvView.setText("LV.8");
                        emptyIconView.setVisibility(View.VISIBLE);
                        break;
                    case 9:
                        levelBGView.setBackgroundResource(R.drawable.lv9_bg);
                        smallLvView.setBackgroundResource(R.drawable.lv9_bg);
                        smallLvView.setText("LV.9");
                        emptyIconView.setVisibility(View.VISIBLE);
                        break;
                    case 10:
                        levelBGView.setBackgroundResource(R.drawable.lv10_bg);
                        smallLvView.setVisibility(View.GONE);
                        lv10IconView.setVisibility(View.VISIBLE);
                        smallLv10View.setVisibility(View.VISIBLE);
                        break;
                }
            } else if (position == 2) {
                holder.itemView.setEnabled(false);
                TextView synopsisView = holder.itemView.findViewById(R.id.synopsisView);
                synopsisView.setText(workVO.getSynopsis());

                TextView careerView = holder.itemView.findViewById(R.id.tv_career);

                if(workVO.getStrCareer().equals("null") == false)
                    careerView.setText(workVO.getStrCareer());
                else
                    careerView.setText("");





                //

                // [S] winhmoon
                holder.itemView.findViewById(R.id.ll_work_main_rank).setOnClickListener((v) -> startActivity(new Intent(WorkMainActivity.this, RankActivity.class)));
                TextView summary = holder.itemView.findViewById(R.id.tv_synopsis_row_summary);
                summary.setText(workVO.getSynopsis());

                TextView genre = holder.itemView.findViewById(R.id.tv_synopsis_row_genre);
                StringBuffer gerneBuffer = new StringBuffer();
                for (int i = 0; i < genreList.size(); i++) {
                    if (i != 0) {
                        gerneBuffer.append(" / ");
                    }
                    gerneBuffer.append(genreList.get(i));
                }
                genre.setText(gerneBuffer.toString());

                TextView tag = holder.itemView.findViewById(R.id.tv_synopsis_row_tag);
                StringBuffer tagBuffer = new StringBuffer();
                for (int i = 0; i < tagList.size(); i++) {
                    if (i != 0) {
                        tagBuffer.append(" ");
                    }
                    tagBuffer.append(tagList.get(i));
                }
                tag.setText(tagBuffer.toString());



                int nOwner =  workVO.getOwner();
                int nCopyright =  workVO.getCopyright();
                int nStatus =  workVO.getStatus();
                boolean bComplete = workVO.isbComplete();

                String strCareer =  workVO.getStrCareer();

                TextView owner = holder.itemView.findViewById(R.id.tv_owner);
                TextView copyright = holder.itemView.findViewById(R.id.tv_copyright);
                TextView status = holder.itemView.findViewById(R.id.tv_status);

                TextView career = holder.itemView.findViewById(R.id.tv_career);
                TextView episodeCnt = holder.itemView.findViewById(R.id.episodes_count);

                int nEpisode = workVO.getEpisodeList().size();
                episodeCnt.setText("???"+ String.valueOf(nEpisode) +"???");

              //  career.setText(strCareer);

                if(nOwner == 0)
                {
                    owner.setText("????????????");
                }
                else
                {
                    owner.setText("????????????");
                }

                if(nCopyright == 0)
                {
                    copyright.setText("????????????");
                }
                else
                {
                    copyright.setText("????????????");
                }

                if(nStatus == 0 && nEpisode == 0)
                {
                    status.setText("?????????");
                }
                else  if(nStatus == 1 && nEpisode > 0 && bComplete == false)
                {
                    status.setText("?????????");
                }
                else
                {
                    if(bComplete == true || nStatus == 2)
                    {
                        status.setText("??????");

                    }
                    else
                    {         status.setText("?????????");


                    }
                }

                TextView readShowTv = holder.itemView.findViewById(R.id.tv_read_show);
                readShowTv.setText(showBtn.getText().toString());

                LinearLayout readShowLl = holder.itemView.findViewById(R.id.ll_read_show);
                readShowLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (workVO.getEpisodeList().size() == 0) {
                            CommonUtils.makeText(WorkMainActivity.this, "????????? ????????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (nLastIndex == -1) {
                            int nIndex = 0;

                            EpisodeVO episodeVO = workVO.getSortedEpisodeList().get(nIndex);
                            if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // ????????? ??????????????? ???????????? ?????? ??????????????????. ???, ?????? ????????? ??????????????????
                                checkInteractionSelect(nIndex);
                                return;
                            }

                            if(type == 0)
                            {
                                Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                                intent.putExtra("EPISODE_INDEX", nIndex);
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(WorkMainActivity.this, WebWorkViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                               // intent.putExtra("EPISODE_INDEX", nIndex);
                               // intent.putExtra("WORK", (Serializable) workVO);
                                intent.putExtra("EPISODE_INDEX", nIndex);

                                startActivity(intent);

                            }


                        } else {
                            int nIndex = nLastIndex - 1;

                            EpisodeVO episodeVO = workVO.getSortedEpisodeList().get(nIndex);
                            if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // ????????? ??????????????? ???????????? ?????? ??????????????????. ???, ?????? ????????? ??????????????????
                                checkInteractionSelect(nIndex);
                                return;
                            }

                            if(type == 0)
                            {
                                Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                                intent.putExtra("EPISODE_INDEX", nIndex);
                                intent.putExtra("LAST_ORDER", nLastOrder);
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(WorkMainActivity.this, WebWorkViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                               // intent.putExtra("EPISODE_INDEX", nIndex);
                               // intent.putExtra("LAST_ORDER", nLastOrder);
                               intent.putExtra("EPISODE_INDEX", nIndex);

                                intent.putExtra("LAST_ORDER", nLastOrder);
                              //  intent.putExtra("WORK", (Serializable) workVO);

                                startActivity(intent);

                            }



                        }
                    }
                });
                // [E] winhmoon

                FlowLayout genreLayout = holder.itemView.findViewById(R.id.genreLayout);
                genreLayout.removeAllViews();
                int nIndex = 0;
                for (String strTag : genreList) {
                    if (nIndex > 0) {
                        TextView tv = new TextView(WorkMainActivity.this);
                        tv.setText(" / ");
                        tv.setTextColor(Color.parseColor("#d1d1d1"));
                        tv.setTextSize(12);
                        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
                        tv.setLayoutParams(params);
                        genreLayout.addView(tv);
                    }

                    TextView tv = new TextView(WorkMainActivity.this);
                    tv.setText(strTag);
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(14);

                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
                    tv.setLayoutParams(params);
                    genreLayout.addView(tv);
                }

                FlowLayout tagLayout = holder.itemView.findViewById(R.id.taglayout);
                tagLayout.removeAllViews();
                for (String strTag : tagList) {
                    strTag = strTag.replaceAll("\\n", "");
                    strTag = strTag.replaceAll("\\r", "");
                    TextView tv = new TextView(WorkMainActivity.this);
                    tv.setText(strTag);
                    tv.setTextColor(getResources().getColor(R.color.colorBlack));
                    tv.setTextSize(12);
                    tv.setIncludeFontPadding(false);

                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
                    tv.setLayoutParams(params);
                    tagLayout.addView(tv);
                }
            } else if(position == 3) {
                TextView carrotView = holder.itemView.findViewById(R.id.carrotCountView);
                carrotView.setText(CommonUtils.comma(workVO.getnDonationCarrot()));
            } else if(position == 4) {
                TextView episodeCountView = holder.itemView.findViewById(R.id.episodeCountView);
                int nEpisode = workVO.getEpisodeList().size();
                boolean bComplete = workVO.isbComplete();
                if(bComplete == true)
                {
                    episodeCountView.setText("??? " + String.valueOf(nEpisode) +"??? /" + " ??????");

                }
                else
                {
                    episodeCountView.setText("??? " + String.valueOf(nEpisode) +"??? /" + " ?????????");

                }

                //episodeCountView.setText(showingList.get(position));

                RelativeLayout orderBtn = holder.itemView.findViewById(R.id.orderBtn);
                orderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(WorkMainActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.work_new_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.asc_order:
                                        bDesc = true;
                                        getWorkData(bDesc);
                                        break;
                                    case R.id.desc_order:
                                        bDesc = false;
                                        getWorkData(bDesc);
                                        break;
                                }
                                return true;
                            }
                        });

                        popup.show();//showing popup menu
                    }
                });
            } else if (position > 4) {
                int nIndex = position - 5;
                EpisodeVO vo = workVO.getEpisodeList().get(nIndex);

                TextView episodeTitleView = holder.itemView.findViewById(R.id.episodeTitleView);
                TextView postAvailableView = holder.itemView.findViewById(R.id.postAvailableView);

                TextView dateTimeView = holder.itemView.findViewById(R.id.dateTimeView);
                TextView startPointView = holder.itemView.findViewById(R.id.startPointView);
                TextView hitsCountView = holder.itemView.findViewById(R.id.hitsCountView);
                TextView commentCountView = holder.itemView.findViewById(R.id.commentCountView);
               // LinearLayout chatCountLayout = holder.itemView.findViewById(R.id.chatCountLayout);
               // TextView chatCountView = holder.itemView.findViewById(R.id.chatCountView);
                TextView hitCount = holder.itemView.findViewById(R.id.hitCount);

                episodeTitleView.setText(vo.getStrTitle());

                String strTitle = vo.getStrTitle();
                String[] titleArr = strTitle.split("???");

                boolean isNum = titleArr[0].matches("[+-]?\\d*(\\.\\d+)?");

                if (isNum) {
                    String strIndex = titleArr[0] + "???";
                    SpannableStringBuilder sb = new SpannableStringBuilder();
                    sb.append(strTitle);
                    sb.setSpan(new StyleSpan(Typeface.BOLD), 0, strIndex.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    episodeTitleView.setText(sb);
                } else {
                    episodeTitleView.setText(vo.getStrTitle());
                }

//                if(titleArr.length >= 2) {
//                    if(titleArr[0].length() <= 4) {
//                        String strIndex = titleArr[0] + "???";
//                        SpannableStringBuilder sb = new SpannableStringBuilder();
//                        sb.append(strTitle);
//                        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, strIndex.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        episodeTitleView.setText(sb);
//                    }
//                }

                dateTimeView.setText(vo.getStrDate().substring(0, 10));
                startPointView.setText(String.format("%.1f", vo.getfStarPoint()));
                //hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
                commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));

                hitCount.setText(CommonUtils.getPointCount(vo.getnHitsCount()));


                ImageView menuBtn = holder.itemView.findViewById(R.id.menuBtn);

                if(pref.getString("ADMIN", "N").equals("N")) {
                  //  chatCountLayout.setVisibility(View.GONE);


                    menuBtn.setVisibility(View.VISIBLE);
                    postAvailableView.setVisibility(View.GONE);

                    menuBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(WorkMainActivity.this, menuBtn);
                            popup.getMenuInflater().inflate(R.menu.work_write_list_comment_menu, popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent intent = null;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkMainActivity.this);
                                    AlertDialog alertDialog = null;

                                    switch (item.getItemId()) {
                                        case R.id.comment:
                                            intent = new Intent(WorkMainActivity.this, EpisodeCommentActivity.class);
                                            intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                            startActivity(intent);
                                            break;
                                        case R.id.report:
                                            builder.setTitle("?????? ?????? ??????");
                                            builder.setMessage("????????? ?????? ?????????????????????????");
                                            builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(WorkMainActivity.this, ReportSelectActivity.class);
                                                    intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                                    startActivity(intent);
                                                }
                                            });

                                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });

                                            alertDialog = builder.create();
                                            alertDialog.show();

                                            break;
                                    }
                                    return true;
                                }
                            });

                            popup.show();//showing popup menu
                        }
                    });
                } else {
                   // chatCountLayout.setVisibility(View.VISIBLE);
                    if(pref.getString("ADMIN", "N").equals("Y")) {
                        postAvailableView.setVisibility(View.VISIBLE);
                        if(vo.getStrSubmit().equals("N")) {
                            postAvailableView.setText("????????????");
                            postAvailableView.setTextColor(Color.parseColor("#d1d1d1"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_writing);
                        } else if(vo.getStrSubmit().equals("W")) {
                            postAvailableView.setText("????????????");
                            postAvailableView.setTextColor(Color.parseColor("#ff5700"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_waiting);
                        } else if(vo.getStrSubmit().equals("Y")) {
                            postAvailableView.setText("?????????");
                            postAvailableView.setTextColor(Color.parseColor("#6c8fff"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_complete);
                        } else if(vo.getStrSubmit().equals("F")) {
                            postAvailableView.setText("????????????");
                            postAvailableView.setTextColor(Color.parseColor("#000000"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_deny);
                        }
                    }

              //      chatCountView.setText("" + vo.getnChatCount());

                    menuBtn.setVisibility(View.VISIBLE);
                    menuBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(WorkMainActivity.this, menuBtn);

                            if(pref.getString("ADMIN", "N").equals("Y")) {
                                popup.getMenuInflater().inflate(R.menu.work_write_list_admin_menu, popup.getMenu());
                            } else
                                popup.getMenuInflater().inflate(R.menu.work_write_list_menu, popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent intent = null;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkMainActivity.this);
                                    AlertDialog alertDialog = null;

                                    switch(item.getItemId()) {
                                        case R.id.delete:
                                            builder.setTitle("?????? ??????");
                                            builder.setMessage("????????? ????????? ?????????????????????????");
                                            builder.setPositiveButton("???", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    requestDeleteEpisode(vo.getnEpisodeID());
                                                }
                                            });

                                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });

                                            alertDialog = builder.create();
                                            alertDialog.show();
                                            break;
                                        case R.id.comment:
                                            intent = new Intent(WorkMainActivity.this, EpisodeCommentActivity.class);
                                            intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                            startActivity(intent);
                                            break;
                                        case R.id.aprove:
                                            if(vo.getStrSubmit().equals("N")) {
                                                CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                                return true;
                                            } else if(vo.getStrSubmit().equals("Y")) {
                                                CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                                return true;
                                            }

                                            builder.setTitle("?????? ?????? ??????");
                                            builder.setMessage("????????? ?????? ????????? ?????????????????????????");
                                            builder.setPositiveButton("???", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    requestWorkAprove(vo.getnEpisodeID());
                                                }
                                            });

                                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });

                                            alertDialog = builder.create();
                                            alertDialog.show();
                                            break;
                                        case R.id.cancel:
                                            if(vo.getStrSubmit().equals("N")) {
                                                CommonUtils.makeText(WorkMainActivity.this, "?????? ????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                                return true;
                                            }

                                            intent = new Intent(WorkMainActivity.this, EpisodeAproveCancelPopup.class);
                                            intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                            startActivity(intent);

                                            break;
                                    }
                                    return true;
                                }
                            });

                            popup.show();//showing popup menu
                        }
                    });
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class WorkViewHolder extends RecyclerView.ViewHolder {
            public WorkViewHolder(View view) {
                super(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int nPosition = getAdapterPosition();
                        if(nPosition > 4) {
                            int nIndex = nPosition - 5;


                            EpisodeVO episodeVO = workVO.getEpisodeList().get(nIndex);
                            if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // ????????? ??????????????? ???????????? ?????? ???????????? ??????. ???, ?????? ????????? ???????????? ??????
                                checkInteractionSelect(nIndex);
                                return;
                            }

                            if(type == 0)
                            {
                                Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                                if (!bDesc) {
                                    intent.putExtra("EPISODE_INDEX", nIndex);
                                } else {
                                    intent.putExtra("EPISODE_INDEX", workVO.getEpisodeList().size() - nIndex - 1);
                                }
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(WorkMainActivity.this, WebWorkViewerActivity.class);
                                ViewerActivity.workVO = workVO;
                                if (!bDesc) {
                                //    intent.putExtra("EPISODE_INDEX", nIndex);
                                    intent.putExtra("EPISODE_INDEX", workVO.getEpisodeList().size() - nIndex - 1);
                                    intent.putExtra("EPISODE_SORT", bDesc);

                                } else {
                                    intent.putExtra("EPISODE_INDEX", workVO.getEpisodeList().size() - nIndex - 1);
                                    intent.putExtra("EPISODE_SORT", bDesc);
                                }
                            //    intent.putExtra("WORK", (Serializable) workVO);

                                startActivity(intent);

                            }

                        }
                    }
                });
            }
        }
    }
    public void onClickProfile(View view) {

        Intent intent = new Intent(WorkMainActivity.this, MyPageActivity.class);
        ViewerActivity.workVO = workVO;
        intent.putExtra("WRITER_ID", workVO.getStrWriterID());

        //    intent.putExtra("WORK", (Serializable) workVO);

        startActivity(intent);
    }
}