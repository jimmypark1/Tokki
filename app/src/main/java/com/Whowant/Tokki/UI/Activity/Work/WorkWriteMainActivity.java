package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Custom.FlowLayout;
import com.Whowant.Tokki.UI.Popup.EpisodeAproveCancelPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class WorkWriteMainActivity extends AppCompatActivity {                       // 작품 작성 메인페이지.  한개의 작품의 회차들 목록을 표시.  변수명과 펑션명 보면 알 수 있음 ;)

    private String strWorkID;
    private WorkVO workVO;
    private EpisodeVO episodeVO;

    private SharedPreferences pref;
    private ProgressDialog mProgressDialog;

    private ArrayList<String> tagList, genreList;
    private RecyclerView listView;
    private ImageView coverImgView;
    private TextView titleView, writerNameView, genreView;
    private TextView synopsisView;
    private EpisodeAdapter aa;
    //    private CEpisodeListAdapter aa;
    private FlowLayout taglayout;
    private Button newEpsisodeBtn;
    private LinearLayout topEditLayout;
    private ArrayList<String> showingList;
    private boolean bDesc = false;
    private boolean isClickedList = false;
    private float fX, fY;

    Activity mActivity;

    int nTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_summary);

        nTarget = getIntent().getIntExtra("NOVEL_TYPE", 0);


        mActivity = this;

        initView();

        pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        strWorkID = getIntent().getStringExtra("WORK_ID");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("서버에서 작품 목록을 가져오고 있습니다...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        coverImgView = findViewById(R.id.coverImgView);
        writerNameView = findViewById(R.id.writerNameView);
        titleView = findViewById(R.id.workTitleView);
        synopsisView = findViewById(R.id.synopsisView);
        taglayout = findViewById(R.id.taglayout);
        newEpsisodeBtn = findViewById(R.id.newEpsisodeBtn);
        listView = findViewById(R.id.listView);
        genreView = findViewById(R.id.genreView);

        coverImgView.setClipToOutline(true);                    // 라운드 처리.  xml 에서 ImageView 의 Background 로 round 처리되어 있음 xml 을 주었을때, clipToOutline 을 true 로 설정해주면 해당 xml 밤위 밖으로 이미지를 표시하지 않음. 토키 코드에서 많이 사용함
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("회차 쓰기");

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        findViewById(R.id.ib_top_layout_dot).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_dot).setOnClickListener((v) -> {
            PopupMenu popup = new PopupMenu(mActivity, v);
            popup.getMenuInflater().inflate(R.menu.work_writer_main, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.modify: {
                            WorkRegActivity.workVO = workVO;
                            Intent intent = new Intent(WorkWriteMainActivity.this, WorkRegActivity.class);
                            startActivity(intent);

//                            WorkEditActivity.workVO = workVO;
//                            Intent intent = new Intent(WorkWriteMainActivity.this, WorkEditActivity.class);
//                            startActivity(intent);
                        }
                        break;
                        case R.id.delete: {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
                            builder.setTitle("작품 삭제");
                            builder.setMessage("작품을 삭제하면 작성했던 모든 회차 정보도 함께 삭제됩니다. 정말 삭제하시겠습니까?");
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    requestDeleteWork();
                                }
                            });

                            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        break;
                    }
                    return true;
                }
            });

            popup.show();
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//            case R.id.modify:
//                WorkEditActivity.workVO = workVO;
//                Intent intent = new Intent(WorkWriteMainActivity.this, WorkEditActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.delete:
//                AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
//                builder.setTitle("작품 삭제");
//                builder.setMessage("작품을 삭제하면 작성했던 모든 회차 정보도 함께 삭제됩니다. 정말 삭제하시겠습니까?");
//                builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        requestDeleteWork();
//                    }
//                });
//
//                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                });
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onResume() {
        super.onResume();
        getWorkInfo();
    }

    private void requestDeleteEpisode(final int nEpisodeID) {
        CommonUtils.showProgressDialog(WorkWriteMainActivity.this, "작품 삭제를 요청중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strResult = HttpClient.requestDeleteEpisode(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (strResult.equals("SUCCESS")) {
                            Toast.makeText(WorkWriteMainActivity.this, "회차가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            getWorkInfo();
                        } else if (strResult.equals("INTERACTION")) {
                            Toast.makeText(WorkWriteMainActivity.this, "해당 회차에 인터렉션이 설정되어 있습니다. 인터렉션을 먼저 삭제해 주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WorkWriteMainActivity.this, "회차 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteWork() {
        CommonUtils.showProgressDialog(WorkWriteMainActivity.this, "작품 삭제를 요청중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteWork(new OkHttpClient(), workVO.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult) {
                            Toast.makeText(WorkWriteMainActivity.this, "작품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(WorkWriteMainActivity.this, "작품 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void getTagData() {
        tagList = new ArrayList<>();
        genreList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getWorkTagWithID(new OkHttpClient(), "" + workVO.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultObject == null) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(WorkWriteMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            CommonUtils.hideProgressDialog();
                            return;
                        }

                        try {
                            JSONArray tagArray = resultObject.getJSONArray("TAG_LIST");

                            for (int i = 0; i < tagArray.length(); i++) {
                                JSONObject object = tagArray.getJSONObject(i);
                                tagList.add(object.getString("TAG_TITLE"));
                            }

                            JSONArray genreArray = resultObject.getJSONArray("GENRE_LIST");
                            for (int i = 0; i < genreArray.length(); i++) {
                                JSONObject object = genreArray.getJSONObject(i);
                                genreList.add(object.getString("GENRE_NAME"));
                            }

                            initViews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void getWorkInfo() {
        CommonUtils.showProgressDialog(WorkWriteMainActivity.this, "작품 정보를 가져오고 있습니다.");
        showingList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                workVO = HttpClient.getWriterWorkWithID(new OkHttpClient(), strWorkID, pref.getString("USER_ID", "Guest"), bDesc);

                int nEpisodeCount = 0;
                boolean bComplete = false;

                if (workVO != null && workVO.getEpisodeList() != null && workVO.getEpisodeList().size() > 0) {
                    nEpisodeCount = workVO.getEpisodeList().size();
                    bComplete = workVO.isbComplete();
                    String strEpisodeCount = "총 " + nEpisodeCount + "화 / " + (bComplete == true ? "완결" : "미완결") + "";
                    showingList.add(strEpisodeCount);

                    for (EpisodeVO vo : workVO.getEpisodeList()) {
                        showingList.add("EPISODE");
                    }
                } else {
                    String strEpisodeCount = "총 0화";
                    showingList.add(strEpisodeCount);
                }

                if (showingList.size() == 1) {
                    showingList.add("EMPTY");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        if (workVO == null) {
                            Toast.makeText(WorkWriteMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            CommonUtils.hideProgressDialog();
                            return;
                        }

                        getTagData();
                    }
                });
            }
        }).start();
    }

    private void initViews() {
        titleView.setText(workVO.getTitle());
        synopsisView.setText(workVO.getSynopsis());
        writerNameView.setText(workVO.getStrWriterName());

        coverImgView.setClipToOutline(true);

//        if (workVO.getStrThumbFile() != null && !workVO.getStrThumbFile().equals("null") && workVO.getStrThumbFile().length() > 0) {
//            Glide.with(this)
//                    .asBitmap() // some .jpeg files are actually gif
//                    .placeholder(R.drawable.ic_i_artwork_empty)
//                    .load(CommonUtils.strDefaultUrl + "images/" + workVO.getStrThumbFile())
//                    .into(coverImgView);
//        } else {
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.ic_i_artwork_empty)
                    .load(CommonUtils.strDefaultUrl + "images/" + workVO.getCoverFile())
                    .into(coverImgView);
//        }


        if (workVO.getEpisodeList() != null) {
            int nOrder = 0;
            int nOldOrder = 0;
            if (bDesc) {
                nOldOrder = workVO.getEpisodeList().size() + 1;
            }

            for (int i = 0; i < workVO.getEpisodeList().size(); i++) {
                EpisodeVO vo = workVO.getEpisodeList().get(i);
                nOrder = vo.getnOrder();

                if (!bDesc) {
                    if (nOldOrder == nOrder - 1) {
                        nOldOrder = nOrder;
                        newEpsisodeBtn.setText("새로운 회차 쓰기");
                    } else {
                        nOrder = nOldOrder + 1;
                        newEpsisodeBtn.setText(nOrder + "화 쓰기");
                        break;
                    }
                } else {
                    if (nOldOrder == nOrder + 1) {
                        nOldOrder = nOrder;
                        newEpsisodeBtn.setText("새로운 회차 쓰기");
                    } else {
                        nOrder = nOldOrder - 1;
                        newEpsisodeBtn.setText(nOrder + "화 쓰기");
                        break;
                    }
                }

            }

            aa = new EpisodeAdapter(WorkWriteMainActivity.this, showingList);
            listView.setAdapter(aa);
            listView.setLayoutManager(new LinearLayoutManager(WorkWriteMainActivity.this, LinearLayoutManager.VERTICAL, false));
        }

        taglayout.removeAllViews();

        for (String strTag : tagList) {
            TextView tv = new TextView(WorkWriteMainActivity.this);
            tv.setText(strTag);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15);
            tv.setIncludeFontPadding(false);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
            tv.setLayoutParams(params);
            taglayout.addView(tv);
        }

//        genreLayout.removeAllViews();

        int nIndex = 0;
        String strGenre = "";
        for (String genre : genreList) {
            if(strGenre.length() > 0)
                strGenre = strGenre + " / ";
            strGenre = strGenre + genre;
//            TextView tv = new TextView(WorkWriteMainActivity.this);
//            if (nIndex > 0)
//                strGenre = " / " + strGenre;
//
//            tv.setText(strGenre);
//            tv.setTextColor(Color.BLACK);
//            tv.setTextSize(15);
//            tv.setIncludeFontPadding(false);
//            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
//            tv.setLayoutParams(params);
//            genreLayout.addView(tv);
//            nIndex++;
        }

        genreView.setText(strGenre);

        Log.d("asdf", "INIT End");
        CommonUtils.hideProgressDialog();
    }

    public void OnClickNewEpisodeBtn(View view) {                                           // 하단의 회차 생성 버튼 클릭
        requestCreateEpisode();
    }

    private void requestCreateEpisode() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("회차 생성을 요청 중입니다...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int nEpisodeID = HttpClient.requestCreateEpisode(new OkHttpClient(), strWorkID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nEpisodeID == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(WorkWriteMainActivity.this, "회차 생성을 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        getWorkWithIDForEpisode(nEpisodeID);
                    }
                });
            }
        }).start();
    }

    private void getWorkWithIDForEpisode(final int nEpisodeID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                workVO = HttpClient.getWriterWorkWithID(new OkHttpClient(), strWorkID, pref.getString("USER_ID", "Guest"), bDesc);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        if (workVO == null) {
                            Toast.makeText(WorkWriteMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        initViews();

                        if(nTarget == 0)
                        {
                            Intent intent = new Intent(WorkWriteMainActivity.this, LiteratureWriteActivity.class);                              // 회차 생성 클릭하여 회차 생성하면서 작성창으로 이동
                            LiteratureWriteActivity.workVO = workVO;
                            intent.putExtra("EPISODE_ID", nEpisodeID);

                            int nOrder = 0;
                            String strTitle = "";
                            for (int i = 0; i < workVO.getEpisodeList().size(); i++) {
                                EpisodeVO vo = workVO.getEpisodeList().get(i);
                                if (nEpisodeID == vo.getnEpisodeID()) {
                                    nOrder = i;
                                    strTitle = vo.getStrTitle();
                                    intent.putExtra("SUBMIT", vo.getStrSubmit());
                                    intent.putExtra("EXCEL_UPLOADED", vo.isExcelUploaded());
                                    break;
                                }
                            }

                            intent.putExtra("EPISODE_INDEX", nOrder);
                            intent.putExtra("EPISODE_ORDER", nOrder+1);
                            intent.putExtra("EPISODE_TITLE", strTitle);
                            startActivity(intent);
                        }
                        else if(nTarget == 1) // 웹소설
                        {
                            Intent intent = new Intent(WorkWriteMainActivity.this, WebNovelWriteActivity.class);                              // 회차 생성 클릭하여 회차 생성하면서 작성창으로 이동
                            startActivity(intent);

                        }
                        else if(nTarget == 2) // e소설
                        {

                        }
                        else if(nTarget == 3) // 스토리
                        {
                            Intent intent = new Intent(WorkWriteMainActivity.this, WebNovelWriteActivity.class);                              // 회차 생성 클릭하여 회차 생성하면서 작성창으로 이동
                            startActivity(intent);

                        }

                    }
                });
            }
        }).start();
    }

    public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {
        private ArrayList<String> itemsList;
        private Activity mContext;

        public EpisodeAdapter(Activity context, ArrayList<String> itemsList) {
            this.mContext = context;
            this.itemsList = itemsList;
        }

        @Override
        public EpisodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = null;

            if (position == 0) {                                                                                     // 첫번째 행은 서버에서 받아온 회차 정보가 아닌 몇개의 회차가 있는지, 정렬 설정 등으로 활용
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_order_row, viewGroup, false);
            } else if (position == 1) {                                                                              // 두번째 행부터 회차 목록 표시
                if (showingList.get(1).equals("EMPTY")) {
                    v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_row, viewGroup, false);
                } else {
                    v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_episode_row, viewGroup, false);
                }
            } else if (position > 1) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_episode_row, viewGroup, false);
            }

            EpisodeViewHolder vh = new EpisodeViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(EpisodeViewHolder holder, int position) {
            if (position >= showingList.size())
                return;

            if (position == 0) {
                TextView episodeCountView = holder.itemView.findViewById(R.id.episodeCountView);
                episodeCountView.setText(showingList.get(position));

                LinearLayout orderBtn = holder.itemView.findViewById(R.id.orderBtn);
                orderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(WorkWriteMainActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.work_new_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.asc_order:
                                        bDesc = true;
                                        getWorkInfo();
                                        break;
                                    case R.id.desc_order:
                                        bDesc = false;
                                        getWorkInfo();
                                        break;
                                }
                                return true;
                            }
                        });

                        popup.show();//showing popup menu
                    }
                });
            } else {
//                LinearLayout writeLl = holder.itemView.findViewById(R.id.ll_work_main_episode_row_write);
//                writeLl.setVisibility(itemsList.size() - 1 == position ? View.VISIBLE : View.GONE);
//                writeLl.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        requestCreateEpisode();
//                    }
//                });
//                TextView writeTv = holder.itemView.findViewById(R.id.tv_work_main_episode_row_write);
//                writeTv.setText(newEpsisodeBtn.getText().toString());

                if (showingList.get(1).equals("EMPTY"))
                    return;

                int nIndex = position - 1;
                EpisodeVO vo = workVO.getEpisodeList().get(nIndex);

                TextView episodeTitleView = holder.itemView.findViewById(R.id.episodeTitleView);
                TextView postAvailableView = holder.itemView.findViewById(R.id.postAvailableView);

                TextView dateTimeView = holder.itemView.findViewById(R.id.dateTimeView);
                TextView startPointView = holder.itemView.findViewById(R.id.startPointView);
                TextView hitsCountView = holder.itemView.findViewById(R.id.hitsCountView);
                TextView commentCountView = holder.itemView.findViewById(R.id.commentCountView);
//                LinearLayout chatCountLayout = holder.itemView.findViewById(R.id.chatCountLayout);
                TextView chatCountView = holder.itemView.findViewById(R.id.chatCountView);
//                TextView tabCountView = holder.itemView.findViewById(R.id.tabCountView);

                episodeTitleView.setText(vo.getStrTitle());
                dateTimeView.setText(vo.getStrDate().substring(0, 10));
                startPointView.setText(String.format("%.1f", vo.getfStarPoint()));
                hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
                commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));

                ImageView menuBtn = holder.itemView.findViewById(R.id.menuBtn);

                postAvailableView.setVisibility(View.VISIBLE);
                menuBtn.setVisibility(View.VISIBLE);

                chatCountView.setText("" + vo.getnChatCount());
//                tabCountView.setText("" + vo.getnTapCount());

                if (vo.getStrSubmit().equals("N")) {                                                        // 작품 제출상태(심사요청 상태) 표시. N(None), W(Waiting), Y(Yes), F(Failed) 로 구분하여 사용
                    postAvailableView.setText("제출대기");
                    postAvailableView.setTextColor(Color.parseColor("#d1d1d1"));
                    postAvailableView.setBackgroundResource(R.drawable.badge_writing);
                } else if (vo.getStrSubmit().equals("W")) {
                    postAvailableView.setText("승인대기");
                    postAvailableView.setTextColor(Color.parseColor("#ff5700"));
                    postAvailableView.setBackgroundResource(R.drawable.badge_waiting);
                } else if (vo.getStrSubmit().equals("Y")) {
                    postAvailableView.setText("게시중");
                    postAvailableView.setTextColor(Color.parseColor("#6c8fff"));
                    postAvailableView.setBackgroundResource(R.drawable.badge_complete);
                } else if (vo.getStrSubmit().equals("F")) {
                    postAvailableView.setText("승인거절");
                    postAvailableView.setTextColor(Color.parseColor("#000000"));
                    postAvailableView.setBackgroundResource(R.drawable.badge_deny);
                }

                menuBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(WorkWriteMainActivity.this, menuBtn);

                        if (pref.getString("ADMIN", "N").equals("Y")) {                                 // 관리자인지 아닌지 여부에 따라 표출하는 팝업메뉴 다르게 사용
                            popup.getMenuInflater().inflate(R.menu.work_write_admin_menu, popup.getMenu());
                        } else
                            popup.getMenuInflater().inflate(R.menu.comment_admin_menu, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = null;
                                AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
                                AlertDialog alertDialog = null;

                                switch (item.getItemId()) {
                                    case R.id.delete:
                                        if (pref.getString("ADMIN", "N").equals("Y")) {
                                            builder.setTitle("회차 삭제");
                                            builder.setMessage("회차를 삭제하면 작성했던 모든 회차 정보도 함께 삭제됩니다. 정말 삭제하시겠습니까?");
                                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    requestDeleteEpisode(vo.getnEpisodeID());
                                                }
                                            });

                                            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });

                                            alertDialog = builder.create();
                                            alertDialog.show();
                                            break;
                                        } else {
                                            if (workVO.getnUserAuthority() < 99 && vo.getnEditAuthority() == 99) {
                                                Toast.makeText(WorkWriteMainActivity.this, "접근 권한이 없습니다", Toast.LENGTH_SHORT).show();
                                                return true;
                                            } else {
                                                builder.setTitle("회차 삭제");
                                                builder.setMessage("화차를 삭제하면 작성했던 모든 회차 정보도 함께 삭제됩니다. 정말 삭제하시겠습니까?");
                                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        requestDeleteEpisode(vo.getnEpisodeID());
                                                    }
                                                });

                                                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                    }
                                                });

                                                alertDialog = builder.create();
                                                alertDialog.show();
                                                break;
                                            }
                                        }
                                    case R.id.comment:
                                        intent = new Intent(WorkWriteMainActivity.this, EpisodeCommentActivity.class);
                                        intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                        startActivity(intent);
                                        break;
                                    case R.id.aprove:
                                        if (vo.getStrSubmit().equals("N")) {
                                            Toast.makeText(WorkWriteMainActivity.this, "아직 작품이 제출되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                            return true;
                                        } else if (vo.getStrSubmit().equals("Y")) {
                                            Toast.makeText(WorkWriteMainActivity.this, "이미 게시된 작품입니다.", Toast.LENGTH_SHORT).show();
                                            return true;
                                        }

                                        builder.setTitle("작품 게시 승인");
                                        builder.setMessage("작품을 게시 상태로 전환하시겠습니까?");
                                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                requestWorkAprove(vo.getnEpisodeID());
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
                                    case R.id.cancel:
                                        if (vo.getStrSubmit().equals("N")) {
                                            Toast.makeText(WorkWriteMainActivity.this, "아직 작품이 제출되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                            return true;
                                        }

                                        intent = new Intent(WorkWriteMainActivity.this, EpisodeAproveCancelPopup.class);
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

        private void requestWorkAprove(final int nEpisodeID) {
            CommonUtils.showProgressDialog(WorkWriteMainActivity.this, "회차 게시를 요청중입니다.");               // 게시 제출(심사 요청)

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean bResult = HttpClient.requestWorkAprove(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nEpisodeID);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if (bResult) {
                                Toast.makeText(WorkWriteMainActivity.this, "게시 승인되었습니다.", Toast.LENGTH_SHORT).show();
                                getWorkInfo();
                            } else {
                                Toast.makeText(WorkWriteMainActivity.this, "게시 승인에 실패하였습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).start();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class EpisodeViewHolder extends RecyclerView.ViewHolder {
            public EpisodeViewHolder(View view) {
                super(view);

                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (isClickedList)
                                    return false;
                                fX = motionEvent.getX();
                                fY = motionEvent.getY();
                                break;
                            case MotionEvent.ACTION_MOVE: {
                                float fEndX = motionEvent.getX();
                                float fEndY = motionEvent.getY();

                                if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                                    return false;
                                }
                                break;
                            }
                            case MotionEvent.ACTION_CANCEL:
                                return false;
                            case MotionEvent.ACTION_UP: {
                                float fEndX = motionEvent.getX();
                                float fEndY = motionEvent.getY();

                                if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                                    return false;
                                } else {
                                    int nPosition = getAdapterPosition();
                                    if (showingList.get(nPosition).equals("EMPTY"))
                                        return false;

                                    if (nPosition == 0 || isClickedList == true)
                                        return false;

                                    isClickedList = true;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            isClickedList = false;
                                        }
                                    }, 1000);
                                    nPosition -= 1;
                                    EpisodeVO vo = workVO.getEpisodeList().get(nPosition);
                                    if (pref.getString("ADMIN", "N").equals("Y")) {

                                        if(nTarget == 0)
                                        {
                                            Intent intent = new Intent(WorkWriteMainActivity.this, LiteratureWriteActivity.class);                          // 내 작품 회차 클릭하여 작성창으로 이동
                                            LiteratureWriteActivity.workVO = workVO;
                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
                                            intent.putExtra("EPISODE_INDEX", nPosition);
                                            intent.putExtra("EPISODE_ORDER", workVO.getEpisodeList().get(nPosition).getnOrder());
                                            intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());
                                            intent.putExtra("SUBMIT", workVO.getEpisodeList().get(nPosition).getStrSubmit());
                                            intent.putExtra("EXCEL_UPLOADED", workVO.getEpisodeList().get(nPosition).isExcelUploaded());
                                            startActivity(intent);
                                        }
                                        else if(nTarget == 1)
                                        {
                                            Intent intent = new Intent(WorkWriteMainActivity.this, WebNovelWriteActivity.class);
                                            intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
                                            intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());
                                            startActivity(intent);

                                        }
                                        else if(nTarget == 2)
                                        {

                                        }
                                        else if(nTarget == 3)
                                        {
                                            Intent intent = new Intent(WorkWriteMainActivity.this, WebNovelWriteActivity.class);
                                            intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
                                            intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());
                                            startActivity(intent);

                                        }


                                    } else {
                                        if (workVO.getnUserAuthority() < 99 && vo.getnEditAuthority() == 99) {
                                            Toast.makeText(WorkWriteMainActivity.this, "접근 권한이 없습니다", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if(nTarget == 0) {
                                                Intent intent = new Intent(WorkWriteMainActivity.this, LiteratureWriteActivity.class);                          // 내 작품 회차 클릭하여 작성창으로 이동
                                                LiteratureWriteActivity.workVO = workVO;
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
                                                intent.putExtra("EPISODE_INDEX", nPosition);
                                                intent.putExtra("EPISODE_ORDER", workVO.getEpisodeList().get(nPosition).getnOrder());
                                                intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());
                                                intent.putExtra("SUBMIT", workVO.getEpisodeList().get(nPosition).getStrSubmit());
                                                intent.putExtra("EXCEL_UPLOADED", workVO.getEpisodeList().get(nPosition).isExcelUploaded());
                                                startActivity(intent);
                                            }
                                            else if(nTarget == 1)
                                            {
                                                Intent intent = new Intent(WorkWriteMainActivity.this, WebNovelWriteActivity.class);
                                                intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
                                                intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());

                                                startActivity(intent);

                                            }
                                            else if(nTarget == 2)
                                            {

                                            }
                                            else if(nTarget == 3)
                                            {
                                                Intent intent = new Intent(WorkWriteMainActivity.this, WebNovelWriteActivity.class);
                                                intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
                                                intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());
                                                startActivity(intent);

                                            }


                                        }
                                    }
                                }
                            }
                            break;
                        }

                        return true;
                    }
                });
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(showingList.size() <= 0) {
//                            Log.d("RETURNED", "SIZE = 0");
//                            return;
//                        }
//
//                        if(isClickedList) {
//                            Log.d("RETURNED", "CLICKED = true");
//                            return;
//                        }
//
//                        isClickedList = true;
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                isClickedList = false;
//                            }
//                        }, 1000);
//
//                        int nPosition = getAdapterPosition();
//
//                        if(showingList.get(nPosition).equals("EMPTY"))
//                            return;
//
//                        if(nPosition == 0)
//                            return;
//
//                        nPosition -= 1;
//
//
//                        Intent intent = new Intent(WorkWriteMainActivity.this, LiteratureWriteActivity.class);                          // 내 작품 회차 클릭하여 작성창으로 이동
//                        LiteratureWriteActivity.workVO = workVO;
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
//                        intent.putExtra("EPISODE_INDEX", nPosition);
//                        intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());
//                        intent.putExtra("SUBMIT", workVO.getEpisodeList().get(nPosition).getStrSubmit());
//                        intent.putExtra("EXCEL_UPLOADED", workVO.getEpisodeList().get(nPosition).isExcelUploaded());
//                        startActivity(intent);
//                    }
//                });
            }
        }
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickTopEditBtn(View view) {
        WorkEditActivity.workVO = workVO;
        if (pref.getString("ADMIN", "N").equals("Y")) {
            Intent intent = new Intent(WorkWriteMainActivity.this, WorkEditActivity.class);
            startActivity(intent);
        } else {
            if (workVO.getnUserAuthority() < 99 && workVO.getnEditAuthority() == 99) {
                Toast.makeText(this, "수정 권한이 없습니다.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(WorkWriteMainActivity.this, CommonPopup.class);
//            intent.putExtra("TITLE", "권한 제한 안내");
//            intent.putExtra("CONTENTS", "수정 권한이 없습니다.");
//            startActivity(intent);
            } else {
                Intent intent = new Intent(WorkWriteMainActivity.this, WorkEditActivity.class);
                startActivity(intent);
            }
        }
    }

    public void onClickTopDelBtn(View view) {
        if (pref.getString("ADMIN", "N").equals("Y")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
            builder.setTitle("작품 삭제");
            builder.setMessage("작품을 삭제하면 작성했던 모든 회차 정보도 함께 삭제됩니다. 정말 삭제하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    requestDeleteWork();
                }
            });

            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            if (workVO.getnUserAuthority() < 99 && workVO.getnEditAuthority() == 99) {
                Toast.makeText(this, "삭제 권한이 없습니다.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(WorkWriteMainActivity.this, CommonPopup.class);
//            intent.putExtra("TITLE", "권한 제한 안내");
//            intent.putExtra("CONTENTS", "삭제 권한이 없습니다.");
//            startActivity(intent);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
                builder.setTitle("작품 삭제");
                builder.setMessage("작품을 삭제하면 작성했던 모든 회차 정보도 함께 삭제됩니다. 정말 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        requestDeleteWork();
                    }
                });

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }
}
