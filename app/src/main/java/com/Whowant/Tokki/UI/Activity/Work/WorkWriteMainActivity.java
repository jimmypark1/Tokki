package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class WorkWriteMainActivity extends AppCompatActivity {                       // 작품 페이지
    private String strWorkID;
    private WorkVO workVO;

    private SharedPreferences pref;
    private ProgressDialog mProgressDialog;

    private ArrayList<String> tagList, genreList;
    private RecyclerView listView;
    private ImageView coverImgView;
    private TextView titleView, writerNameView;
    private TextView synopsisView;
    private EpisodeAdapter aa;
//    private CEpisodeListAdapter aa;
    private FlowLayout taglayout, genreLayout;
    private Button newEpsisodeBtn;
    private LinearLayout topEditLayout;
    private ArrayList<String> showingList;
    private boolean bDesc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_summary);

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
        genreLayout = findViewById(R.id.genreLayout);
        newEpsisodeBtn = findViewById(R.id.newEpsisodeBtn);
        listView = findViewById(R.id.listView);

        coverImgView.setClipToOutline(true);
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

                        if(strResult.equals("SUCCESS")) {
                            Toast.makeText(WorkWriteMainActivity.this, "회차가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            getWorkInfo();
                        } else if(strResult.equals("INTERACTION")) {
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

                        if(bResult) {
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
                        if(resultObject == null) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(WorkWriteMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONArray tagArray = resultObject.getJSONArray("TAG_LIST");

                            for(int i = 0 ; i < tagArray.length() ; i++) {
                                JSONObject object = tagArray.getJSONObject(i);
                                tagList.add(object.getString("TAG_TITLE"));
                            }

                            JSONArray genreArray = resultObject.getJSONArray("GENRE_LIST");
                            for(int i = 0 ; i < genreArray.length() ; i++) {
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
        showingList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                workVO = HttpClient.getWorkWithID(new OkHttpClient(), strWorkID, pref.getString("USER_ID", "Guest"), bDesc);

                int nEpisodeCount = 0;
                boolean bComplete = false;

                if(workVO.getEpisodeList() != null && workVO.getEpisodeList().size() > 0) {
                    nEpisodeCount = workVO.getEpisodeList().size();
                    bComplete = workVO.isbComplete();
                    String strEpisodeCount = "총 " + nEpisodeCount + "화 /" + (bComplete == true ? "완결" : "미완결") + "";
                    showingList.add(strEpisodeCount);

                    for(EpisodeVO vo : workVO.getEpisodeList()) {
                        showingList.add("EPISODE");
                    }
                } else {
                    String strEpisodeCount = "총 0화";
                    showingList.add(strEpisodeCount);
                }

                if(showingList.size() == 1) {
                    showingList.add("EMPTY");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        if(workVO == null) {
                            Toast.makeText(WorkWriteMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
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
        Glide.with(this)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster_horizontal)
                .load(CommonUtils.strDefaultUrl + "images/" + workVO.getCoverFile())
                .into(coverImgView);

        if(workVO.getEpisodeList() != null) {
            int nOrder = 0;
            int nOldOrder = 0;

            for(int i = 0 ; i < workVO.getEpisodeList().size() ; i++) {
                EpisodeVO vo = workVO.getEpisodeList().get(i);
                nOrder = vo.getnOrder();

                if(nOldOrder == nOrder-1) {
                    nOldOrder = nOrder;
                    newEpsisodeBtn.setText("새로운 회차 쓰기");
                } else {
                    nOrder = nOldOrder+1;
                    newEpsisodeBtn.setText(nOrder + "회차 쓰기");
                    break;
                }
            }

            aa = new EpisodeAdapter(WorkWriteMainActivity.this, showingList);
            listView.setAdapter(aa);
            listView.setLayoutManager(new LinearLayoutManager(WorkWriteMainActivity.this, LinearLayoutManager.VERTICAL, false));
        }

        taglayout.removeAllViews();

        for(String strTag : tagList) {
            TextView tv = new TextView(WorkWriteMainActivity.this);
            tv.setText(strTag);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15);
            tv.setIncludeFontPadding(false);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
            tv.setLayoutParams(params);
            taglayout.addView(tv);
        }

        genreLayout.removeAllViews();

        int nIndex = 0;
        for(String strGenre : genreList) {
            TextView tv = new TextView(WorkWriteMainActivity.this);
            if(nIndex > 0)
                strGenre = " / " + strGenre;

            tv.setText(strGenre);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15);
            tv.setIncludeFontPadding(false);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
            tv.setLayoutParams(params);
            genreLayout.addView(tv);
            nIndex++;
        }
    }

    public void OnClickNewEpisodeBtn(View view) {
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
                        if(nEpisodeID == 0) {
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
                workVO = HttpClient.getWorkWithID(new OkHttpClient(), strWorkID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        if(workVO == null) {
                            Toast.makeText(WorkWriteMainActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        initViews();

                        Intent intent = new Intent(WorkWriteMainActivity.this, LiteratureWriteActivity.class);
                        LiteratureWriteActivity.workVO = workVO;
                        intent.putExtra("EPISODE_ID", nEpisodeID);

                        int nOrder = 0;
                        String strTitle = "";
                        for(int i = 0 ; i < workVO.getEpisodeList().size() ; i++) {
                            EpisodeVO vo = workVO.getEpisodeList().get(i);
                            if(nEpisodeID == vo.getnEpisodeID()) {
                                nOrder = i;
                                strTitle = vo.getStrTitle();
                                intent.putExtra("SUBMIT", vo.getStrSubmit());
                                break;
                            }
                        }

                        intent.putExtra("EPISODE_INDEX", nOrder);
                        intent.putExtra("EPISODE_TITLE", strTitle);

//                        if(workVO.getEpisodeList() != null && workVO.getEpisodeList().size() > 0) {
//                            intent.putExtra("EPISODE_INDEX", nOrder);
//                            intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(workVO.getEpisodeList().size() - 1).getStrTitle());
//                        } else {
//                            intent.putExtra("EPISODE_INDEX", 1);
//                            intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(0).getStrTitle());
//                        }

                        startActivity(intent);
                    }
                });
            }
        }).start();
    }

    public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>{
        private ArrayList<String> itemsList;
        private Activity mContext;

        public EpisodeAdapter(Activity context, ArrayList<String> itemsList) {
            this.mContext = context;
            this.itemsList = itemsList;
        }

        @Override
        public EpisodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = null;

            if(position == 0) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_order_row, viewGroup, false);
            } else if(position == 1) {
                if(showingList.get(1).equals("EMPTY")) {
                    v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_row, viewGroup, false);
                } else {
                    v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_episode_row, viewGroup, false);
                }
            }
            else if(position > 1) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_episode_row, viewGroup, false);
            }

            EpisodeViewHolder vh = new EpisodeViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(EpisodeViewHolder holder, int position) {
            if(position == 0) {
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
                                switch(item.getItemId()) {
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
                if(showingList.get(1).equals("EMPTY"))
                    return;

                int nIndex = position - 1;
                EpisodeVO vo = workVO.getEpisodeList().get(nIndex);

                TextView episodeTitleView = holder.itemView.findViewById(R.id.episodeTitleView);
                TextView postAvailableView = holder.itemView.findViewById(R.id.postAvailableView);

                TextView dateTimeView = holder.itemView.findViewById(R.id.dateTimeView);
                TextView startPointView = holder.itemView.findViewById(R.id.startPointView);
                TextView hitsCountView = holder.itemView.findViewById(R.id.hitsCountView);
                TextView commentCountView = holder.itemView.findViewById(R.id.commentCountView);

                episodeTitleView.setText(vo.getStrTitle());
                dateTimeView.setText(vo.getStrDate().substring(0, 10));
                startPointView.setText(String.format("%.1f", vo.getfStarPoint()));
                hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
                commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));

                ImageView menuBtn = holder.itemView.findViewById(R.id.menuBtn);

                if(pref.getString("ADMIN", "N").equals("N")) {
                    menuBtn.setVisibility(View.VISIBLE);
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
                        postAvailableView.setText("게시거절");
                        postAvailableView.setTextColor(Color.parseColor("#000000"));
                        postAvailableView.setBackgroundResource(R.drawable.badge_deny);
                    }

                    menuBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(WorkWriteMainActivity.this, menuBtn);
                            popup.getMenuInflater().inflate(R.menu.comment_admin_menu, popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent intent = null;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
                                    AlertDialog alertDialog = null;

                                    switch(item.getItemId()) {
                                        case R.id.delete:
                                            requestDeleteEpisode(vo.getnEpisodeID());
                                            break;
                                        case R.id.comment:
                                            intent = new Intent(WorkWriteMainActivity.this, EpisodeCommentActivity.class);
                                            intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                            startActivity(intent);
                                            break;
                                        case R.id.aprove:
                                            if(vo.getStrSubmit().equals("N")) {
                                                Toast.makeText(WorkWriteMainActivity.this, "아직 작품이 제출되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                                return true;
                                            } else if(vo.getStrSubmit().equals("Y")) {
                                                Toast.makeText(WorkWriteMainActivity.this, "이미 게시된 작품입니다.", Toast.LENGTH_SHORT).show();
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
                } else {
                    postAvailableView.setVisibility(View.VISIBLE);
                    menuBtn.setVisibility(View.VISIBLE);

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
                        postAvailableView.setText("게시거절");
                        postAvailableView.setTextColor(Color.parseColor("#000000"));
                        postAvailableView.setBackgroundResource(R.drawable.badge_deny);
                    }

                    menuBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(WorkWriteMainActivity.this, menuBtn);

                            if(pref.getString("ADMIN", "N").equals("Y")) {
                                popup.getMenuInflater().inflate(R.menu.work_write_admin_menu, popup.getMenu());
                            } else
                                popup.getMenuInflater().inflate(R.menu.comment_admin_menu, popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent intent = null;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
                                    AlertDialog alertDialog = null;

                                    switch(item.getItemId()) {
                                        case R.id.delete:
                                            requestDeleteEpisode(vo.getnEpisodeID());
                                            break;
                                        case R.id.comment:
                                            intent = new Intent(WorkWriteMainActivity.this, EpisodeCommentActivity.class);
                                            intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                            startActivity(intent);
                                            break;
                                        case R.id.aprove:
                                            if(vo.getStrSubmit().equals("N")) {
                                                Toast.makeText(WorkWriteMainActivity.this, "아직 작품이 제출되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                                return true;
                                            } else if(vo.getStrSubmit().equals("Y")) {
                                                Toast.makeText(WorkWriteMainActivity.this, "이미 게시된 작품입니다.", Toast.LENGTH_SHORT).show();
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
        }

        private void requestWorkAprove(final int nEpisodeID) {
            CommonUtils.showProgressDialog(WorkWriteMainActivity.this, "회차 게시를 요청중입니다.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean bResult = HttpClient.requestWorkAprove(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nEpisodeID);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(bResult) {
                                Toast.makeText(WorkWriteMainActivity.this, "게시 승인 되었습니다.", Toast.LENGTH_SHORT).show();
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

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int nPosition = getAdapterPosition();

                        if(showingList.get(nPosition).equals("EMPTY"))
                            return;

                        if(nPosition == 0)
                            return;

                        nPosition -= 1;


                        Intent intent = new Intent(WorkWriteMainActivity.this, LiteratureWriteActivity.class);
                        LiteratureWriteActivity.workVO = workVO;
                        intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(nPosition).getnEpisodeID());
                        intent.putExtra("EPISODE_INDEX", nPosition);
                        intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(nPosition).getStrTitle());
                        intent.putExtra("SUBMIT", workVO.getEpisodeList().get(nPosition).getStrSubmit());
                        startActivity(intent);
                    }
                });
            }
        }
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickTopEditBtn(View view) {
        WorkEditActivity.workVO = workVO;
        Intent intent = new Intent(WorkWriteMainActivity.this, WorkEditActivity.class);
        startActivity(intent);
    }

    public void onClickTopDelBtn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity.this);
        builder.setTitle("작품 삭제");
        builder.setMessage("작품을 삭제하면 작성했던 모든 회차 정보도 함께 삭제됩니다. 정말 삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                requestDeleteWork();
            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
