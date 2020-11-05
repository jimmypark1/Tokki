package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterMainActivity;
import com.Whowant.Tokki.UI.Custom.FlowLayout;
import com.Whowant.Tokki.UI.Popup.CarrotDoneActivity;
import com.Whowant.Tokki.UI.Popup.EpisodeAproveCancelPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private int nLastEpisodeID = -1;                // 마지막으로 봤던 에피소드 ID
    private int nLastOrder = -1;
    private int nInteractionID = -1;
    private int nLastIndex = -1;                    // 마지막으로 봤던 에피소드의 인덱스값
    private String strBtnTitle = "무료로 첫화보기";
//    private ActionBar actionBar;
    private boolean bModify = false;
    private boolean bDesc = false;
    private TextView titleView, carrotCountView;
    private Button showBtn;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_main);

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();

        ImageButton leftBtn = findViewById(R.id.leftBtn);
        ImageButton rightBtn = findViewById(R.id.rightBtn);
        leftBtn.setImageResource(R.drawable.back_button);
        rightBtn.setVisibility(View.GONE);
        ImageView cenverLogoView = findViewById(R.id.cenverLogoView);
        cenverLogoView.setVisibility(View.INVISIBLE);
        titleView = findViewById(R.id.titleView);
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
//                    CommonUtils.makeText(this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
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
//                        "PanBook 에서 " + workVO.getTitle() + " 작품을 만나보세요!",
//                        LinkObject.newBuilder()
//                                .setWebUrl(strURL)
//                                .setMobileWebUrl(strURL)
//                                .setAndroidExecutionParams("WORK_ID=" + workVO.getnWorkID())
//                                .build()
//                )
//                        .setButtonTitle("Panbook 앱 바로가기")
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
        callbackParameters.put("title", "프로방스 자동차 여행 !@#$%");
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
        CommonUtils.showProgressDialog(WorkMainActivity.this, "보관함에 저장 중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.requestSetKeep(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "" + nWorkID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(resultObject == null) {
                            CommonUtils.makeText(WorkMainActivity.this, "서버와의 통신이 원활하지 않습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        bKeep = true;
                        CommonUtils.makeText(WorkMainActivity.this, "보관함에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        aa.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void requestUnKeepWork() {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "구독을 취소 중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.requestUnKeep(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "" + nWorkID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(resultObject == null) {
                            CommonUtils.makeText(WorkMainActivity.this, "서버와의 통신이 원활하지 않습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        bKeep = false;
                        CommonUtils.makeText(WorkMainActivity.this, "구독이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        aa.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void onClickCarrotBtn(View view) {
        if(workVO.getnWriterID().equals(pref.getString("USER_ID", "Guest"))) {
            CommonUtils.makeText(this, "내 작품에는 후원 하실수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(WorkMainActivity.this, CarrotDoneActivity.class);
        intent.putExtra("WORK_ID", workVO.getnWorkID());
        startActivity(intent);
    }

    private void getKeepData() {            // 보관함에 담았는지 여부
        CommonUtils.showProgressDialog(WorkMainActivity.this, "작품 정보를 가져오고 있습니다.");

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
                                Toast.makeText(WorkMainActivity.this, "서버와의 통신이 실패했습니다.", Toast.LENGTH_SHORT).show();
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
                            CommonUtils.makeText(WorkMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(WorkMainActivity.this, "서버와의 통신이 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }
                if(workVO.getnWriterID().equals(pref.getString("USER_ID", "Guest"))) {
                    bModify = true;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (workVO == null) {
                            CommonUtils.hideProgressDialog();
                            CommonUtils.makeText(WorkMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
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

                        String strEpisodeCount = "총 " + nEpisodeCount + "화 / " + (bComplete == true ? "완결" : "미완결") + "";
                        showingList.add(strEpisodeCount);

                        for(int i = 0 ; i < nEpisodeCount ; i++) {
                            if(nLastEpisodeID > -1) {
                                EpisodeVO vo = workVO.getEpisodeList().get(i);

                                if(nLastEpisodeID == vo.getnEpisodeID() && CommonUtils.bLocinCheck(pref)) {
                                    nLastIndex = vo.getnOrder();
                                    strBtnTitle = nLastIndex + "화 이어보기";
                                }
                            }

                            showingList.add("EPISODE");
                        }

                        CommonUtils.hideProgressDialog();

                        if(bFisrt)
                            bFisrt = false;

                        titleView.setText(workVO.getTitle());
                        aa = new WorkAdapter(WorkMainActivity.this, showingList);
                        listView.setAdapter(aa);
                        listView.setLayoutManager(new LinearLayoutManager(WorkMainActivity.this, LinearLayoutManager.VERTICAL, false));

                        showBtn.setText(strBtnTitle);
                        showBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(workVO.getEpisodeList().size() == 0) {
                                    CommonUtils.makeText(WorkMainActivity.this, "작품이 없습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(nLastIndex == -1) {
                                    int nIndex = 0;

                                    EpisodeVO episodeVO = workVO.getEpisodeList().get(nIndex);
                                    if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // 클릭한 에피소드가 분기보다 위의 에피소드 라면. 즉, 분기 이후의 에피소드 라면
                                        checkInteractionSelect(nIndex);
                                        return;
                                    }

                                    Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                    ViewerActivity.workVO = workVO;
                                    intent.putExtra("EPISODE_INDEX", nIndex);
                                    startActivity(intent);

//                            Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
//                            ViewerActivity.workVO = workVO;
//                            intent.putExtra("EPISODE_INDEX", 0);
//                            startActivity(intent);
                                } else {
                                    int nIndex = nLastIndex;

                                    EpisodeVO episodeVO = workVO.getEpisodeList().get(nIndex);
                                    if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // 클릭한 에피소드가 분기보다 위의 에피소드 라면. 즉, 분기 이후의 에피소드 라면
                                        checkInteractionSelect(nIndex);
                                        return;
                                    }

                                    Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                                    ViewerActivity.workVO = workVO;
                                    intent.putExtra("EPISODE_INDEX", nIndex);
                                    intent.putExtra("LAST_ORDER", nLastOrder);
                                    startActivity(intent);

//                            Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
//                            ViewerActivity.workVO = workVO;
//                            intent.putExtra("EPISODE_INDEX", nLastIndex);
//                            startActivity(intent);
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
            CommonUtils.makeText(WorkMainActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WorkMainActivity.this, LoginSelectActivity.class));
            return;
        }

        EpisodeVO episodeVO = workVO.getEpisodeList().get(nIndex);
        if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // 클릭한 에피소드가 분기보다 위의 에피소드 라면. 즉, 분기 이후의 에피소드 라면
            checkInteractionSelect(nIndex);
            return;
        }

        Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
        ViewerActivity.workVO = workVO;
        intent.putExtra("EPISODE_INDEX", nIndex);
        startActivity(intent);
    }

    private void checkInteractionSelect(final int nIndex) {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.checkInteractionSelect(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nWorkID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            CommonUtils.makeText(WorkMainActivity.this, "분기를 선택하지 않으셨습니다.  작품의 분기를 선택해주세요.", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                            ViewerActivity.workVO = workVO;
                            intent.putExtra("EPISODE_INDEX", nIndex);
                            intent.putExtra("INTERACTION", true);
                            startActivity(intent);
                        }
                    }
                });
            }
        }).start();
    }

    private void requestWorkAprove(final int nEpisodeID) {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "회차 게시를 요청중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestWorkAprove(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            CommonUtils.makeText(WorkMainActivity.this, "게시 승인 되었습니다.", Toast.LENGTH_SHORT).show();
                            getKeepData();
                        } else {
                            CommonUtils.makeText(WorkMainActivity.this, "게시 승인에 실패하였습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteEpisode(final int nEpisodeID) {
        CommonUtils.showProgressDialog(WorkMainActivity.this, "회차 삭제를 요청중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strResult = HttpClient.requestDeleteEpisode(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(strResult.equals("SUCCESS")) {
                            CommonUtils.makeText(WorkMainActivity.this, "회차가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            getKeepData();
                        } else if(strResult.equals("INTERACTION")) {
                            CommonUtils.makeText(WorkMainActivity.this, "해당 회차에 인터렉션이 설정되어 있습니다. 인터렉션을 먼저 삭제해 주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            CommonUtils.makeText(WorkMainActivity.this, "회차 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
                ImageButton subscribeBtn = holder.itemView.findViewById(R.id.heartBtn);
                String strImgUrl = workVO.getCoverFile();
                coverImgView.setClipToOutline(true);

                if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
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
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tokki 에서 " + workVO.getTitle() + " 작품을 만나보세요!"); // 제목
                        shareIntent.putExtra(Intent.EXTRA_TEXT, strURL); // 내용
                        startActivity(Intent.createChooser(shareIntent, "작품 공유")); // 공유창 제목
                    }
                });

                if(bKeep) {
                    subscribeBtn.setBackgroundResource(R.drawable.full_heart_button);
                } else {
                    subscribeBtn.setBackgroundResource(R.drawable.empty_heart);
                }

                subscribeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean bLogin = CommonUtils.bLocinCheck(pref);

                        if(!bLogin) {
                            CommonUtils.makeText(WorkMainActivity.this, "로그인이 필요한 기능입니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(WorkMainActivity.this, LoginSelectActivity.class));
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
                if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                    if(!strPhoto.startsWith("http"))
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
                tapCountView.setText(CommonUtils.getPointCount(workVO.getnTapCount()));
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

                FlowLayout genreLayout = holder.itemView.findViewById(R.id.genreLayout);
                genreLayout.removeAllViews();
                int nIndex = 0;
                for(String strTag : genreList) {
                    if(nIndex > 0) {
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
                for(String strTag : tagList) {
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
                episodeCountView.setText(showingList.get(position));

                ImageView orderBtn = holder.itemView.findViewById(R.id.orderBtn);
                orderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(WorkMainActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.work_new_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()) {
                                    case R.id.asc_order:
                                        getWorkData(true);
                                        break;
                                    case R.id.desc_order:
                                        getWorkData(false);
                                        break;
                                }
                                return true;
                            }
                        });

                        popup.show();//showing popup menu
                    }
                });
            } else if(position > 4) {
                int nIndex = position - 5;
                EpisodeVO vo = workVO.getEpisodeList().get(nIndex);

                TextView episodeTitleView = holder.itemView.findViewById(R.id.episodeTitleView);
                TextView postAvailableView = holder.itemView.findViewById(R.id.postAvailableView);

                TextView dateTimeView = holder.itemView.findViewById(R.id.dateTimeView);
                TextView startPointView = holder.itemView.findViewById(R.id.startPointView);
                TextView hitsCountView = holder.itemView.findViewById(R.id.hitsCountView);
                TextView commentCountView = holder.itemView.findViewById(R.id.commentCountView);
                LinearLayout chatCountLayout = holder.itemView.findViewById(R.id.chatCountLayout);
                TextView chatCountView = holder.itemView.findViewById(R.id.chatCountView);

                episodeTitleView.setText(vo.getStrTitle());

                String strTitle = vo.getStrTitle();
                String[] titleArr = strTitle.split("화 ");

                if(titleArr.length >= 2) {
                    if(titleArr[0].length() <= 4) {
                        String strIndex = titleArr[0] + "화";
                        SpannableStringBuilder sb = new SpannableStringBuilder();
                        sb.append(strTitle);
                        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, strIndex.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        episodeTitleView.setText(sb);
                    }
                }

                dateTimeView.setText(vo.getStrDate().substring(0, 10));
                startPointView.setText(String.format("%.1f", vo.getfStarPoint()));
                hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
                commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));

                ImageView menuBtn = holder.itemView.findViewById(R.id.menuBtn);

                if(pref.getString("ADMIN", "N").equals("N")) {
                    chatCountLayout.setVisibility(View.GONE);


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

                                    switch(item.getItemId()) {
                                        case R.id.comment:
                                            intent = new Intent(WorkMainActivity.this, EpisodeCommentActivity.class);
                                            intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                            startActivity(intent);
                                            break;
                                        case R.id.report:
                                            builder.setTitle("회차 신고 알림");
                                            builder.setMessage("회차를 정말 신고하시겠습니까?");
                                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(WorkMainActivity.this, EpisodeReportSelectActivity.class);
                                                    intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                                    startActivity(intent);
                                                }
                                            });

                                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
                    chatCountLayout.setVisibility(View.VISIBLE);
                    if(pref.getString("ADMIN", "N").equals("Y")) {
                        postAvailableView.setVisibility(View.VISIBLE);
                        if(vo.getStrSubmit().equals("N")) {
                            postAvailableView.setText("제출대기");
                            postAvailableView.setTextColor(Color.parseColor("#d1d1d1"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_writing);
                        } else if(vo.getStrSubmit().equals("W")) {
                            postAvailableView.setText("승인대기");
                            postAvailableView.setTextColor(Color.parseColor("#ff5700"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_waiting);
                        } else if(vo.getStrSubmit().equals("Y")) {
                            postAvailableView.setText("게시중");
                            postAvailableView.setTextColor(Color.parseColor("#6c8fff"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_complete);
                        } else if(vo.getStrSubmit().equals("F")) {
                            postAvailableView.setText("승인거절");
                            postAvailableView.setTextColor(Color.parseColor("#000000"));
                            postAvailableView.setBackgroundResource(R.drawable.badge_deny);
                        }
                    }

                    chatCountView.setText("" + vo.getnChatCount());

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
                                            builder.setTitle("작품 삭제");
                                            builder.setMessage("작품을 정말로 삭제하시겠습니까?");
                                            builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    requestDeleteEpisode(vo.getnEpisodeID());
                                                }
                                            });

                                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
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
                                                CommonUtils.makeText(WorkMainActivity.this, "아직 작품이 제출되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                                return true;
                                            } else if(vo.getStrSubmit().equals("Y")) {
                                                CommonUtils.makeText(WorkMainActivity.this, "이미 게시된 작품입니다.", Toast.LENGTH_SHORT).show();
                                                return true;
                                            }

                                            builder.setTitle("작품 게시 승인");
                                            builder.setMessage("작품을 게시 상태로 전환하시겠습니까?");
                                            builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    requestWorkAprove(vo.getnEpisodeID());
                                                }
                                            });

                                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });

                                            alertDialog = builder.create();
                                            alertDialog.show();
                                            break;
                                        case R.id.cancel:
                                            if(vo.getStrSubmit().equals("N")) {
                                                CommonUtils.makeText(WorkMainActivity.this, "아직 작품이 제출되지 않았습니다.", Toast.LENGTH_SHORT).show();
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
                            if(nInteractionID > -1 && episodeVO.getnEpisodeID() > nInteractionID) {                // 클릭한 에피소드가 분기보다 위의 에피소드 라면. 즉, 분기 이후의 에피소드 라면
                                checkInteractionSelect(nIndex);
                                return;
                            }

                            Intent intent = new Intent(WorkMainActivity.this, ViewerActivity.class);
                            ViewerActivity.workVO = workVO;
                            intent.putExtra("EPISODE_INDEX", nIndex);
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }
}