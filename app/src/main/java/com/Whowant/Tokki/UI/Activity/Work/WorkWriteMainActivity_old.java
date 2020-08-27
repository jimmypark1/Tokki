package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Custom.FlowLayout;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import okhttp3.OkHttpClient;

public class WorkWriteMainActivity_old extends AppCompatActivity {                       // 작품 페이지
    private String strWorkID;
    private WorkVO workVO;

    private SharedPreferences pref;
    private ProgressDialog mProgressDialog;

    private ArrayList<String> tagList;

    private ImageView coverImgView;
    private TextView titleView;
    private TextView synopsisView;
    private ListView episodeView;
    private CEpisodeListAdapter aa;
    private FlowLayout taglayout;
    private TextView emptyView;
    private Button newEpsisodeBtn;

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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("작품 쓰기");

        View mHeader = getLayoutInflater().inflate(R.layout.work_write_main_header, null, false);
//        episodeView = findViewById(R.id.episodeView);
        episodeView.addHeaderView(mHeader);

        strWorkID = getIntent().getStringExtra("WORK_ID");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("서버에서 작품 목록을 가져오고 있습니다...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        coverImgView = mHeader.findViewById(R.id.coverImgView);
        titleView = mHeader.findViewById(R.id.workTitleView);
        synopsisView = mHeader.findViewById(R.id.synopsisView);
        taglayout = mHeader.findViewById(R.id.taglayout);
        emptyView = findViewById(R.id.emptyView);
        newEpsisodeBtn = findViewById(R.id.newEpsisodeBtn);
        coverImgView.setClipToOutline(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.work_mofidy_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.modify:
                WorkEditActivity.workVO = workVO;
                Intent intent = new Intent(WorkWriteMainActivity_old.this, WorkEditActivity.class);
                startActivity(intent);
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity_old.this);
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        getWorkInfo();
    }

    private void requestDeleteEpisode(final int nEpisodeID) {
        CommonUtils.showProgressDialog(WorkWriteMainActivity_old.this, "작품 삭제를 요청중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strResult = HttpClient.requestDeleteEpisode(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(strResult.equals("SUCCESS")) {
                            Toast.makeText(WorkWriteMainActivity_old.this, "회차가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            getWorkInfo();
                        } else if(strResult.equals("INTERACTION")) {
                            Toast.makeText(WorkWriteMainActivity_old.this, "해당 회차에 인터렉션이 설정되어 있습니다. 인터렉션을 먼저 삭제해 주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WorkWriteMainActivity_old.this, "회차 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void requestDeleteWork() {
        CommonUtils.showProgressDialog(WorkWriteMainActivity_old.this, "작품 삭제를 요청중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestDeleteWork(new OkHttpClient(), workVO.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(WorkWriteMainActivity_old.this, "작품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(WorkWriteMainActivity_old.this, "작품 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void getTagData() {
        tagList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getWorkTagWithID(new OkHttpClient(), "" + workVO.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(resultObject == null) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(WorkWriteMainActivity_old.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONArray tagArray = resultObject.getJSONArray("TAG_LIST");

                            for(int i = 0 ; i < tagArray.length() ; i++) {
                                JSONObject object = tagArray.getJSONObject(i);
                                tagList.add(object.getString("TAG_TITLE"));
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                workVO = HttpClient.getWorkWithID(new OkHttpClient(), strWorkID, pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();

                        if(workVO == null) {
                            Toast.makeText(WorkWriteMainActivity_old.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        getTagData();
//

//                        aa = new LiteratureListActivity.CWorkListAdapter(LiteratureListActivity.this, R.layout.work_list_row, workList);
//                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    private void initViews() {
        titleView.setText(workVO.getTitle());
        synopsisView.setText(workVO.getSynopsis());

        Glide.with(this)
                .asBitmap() // some .jpeg files are actually gif
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

            aa = new CEpisodeListAdapter(WorkWriteMainActivity_old.this, R.layout.work_list_row, workVO.getEpisodeList());
            episodeView.setAdapter(aa);

            episodeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if(position == 0)
                        return;

                    position -= 1;
                    Intent intent = new Intent(WorkWriteMainActivity_old.this, LiteratureWriteActivity.class);
                    LiteratureWriteActivity.workVO = workVO;
                    intent.putExtra("EPISODE_ID", workVO.getEpisodeList().get(position).getnEpisodeID());
                    intent.putExtra("EPISODE_INDEX", position);
                    intent.putExtra("EPISODE_TITLE", workVO.getEpisodeList().get(position).getStrTitle());
                    intent.putExtra("SUBMIT", workVO.getEpisodeList().get(position).getStrSubmit());
                    startActivity(intent);
                }
            });

            if(workVO.getEpisodeList().size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.INVISIBLE);
            }
        }

        taglayout.removeAllViews();

        for(String strTag : tagList) {
            TextView tv = new TextView(WorkWriteMainActivity_old.this);
            tv.setText(strTag);
            tv.setTextColor(Color.BLACK);
//            tv.setBackgroundResource(R.drawable.gray_tag_bg);
            tv.setTextSize(15);
            tv.setIncludeFontPadding(false);
//            tv.setPadding(20, 20, 20, 20);

            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 20);
            tv.setLayoutParams(params);
            taglayout.addView(tv);
        }
    }

    public class CEpisodeListAdapter extends ArrayAdapter<Object>
    {
        private int mCellLayout;
        private LayoutInflater mLiInflater;
        private SharedPreferences.Editor editor;

        CEpisodeListAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mCellLayout = layout;
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            EpisodeVO vo = workVO.getEpisodeList().get(position);

            if(convertView == null) {
                convertView = mLiInflater.inflate(R.layout.work_main_episode_row, parent, false);
            }

            LinearLayout pointLayout = convertView.findViewById(R.id.pointLayout);
            pointLayout.setVisibility(View.INVISIBLE);
            TextView postAvailableView = convertView.findViewById(R.id.postAvailableView);
            postAvailableView.setVisibility(View.VISIBLE);
            TextView titleView = convertView.findViewById(R.id.episodeTitleView);
            TextView synopsisView = convertView.findViewById(R.id.dateTimeView);
//            TextView writerNameView = convertView.findViewById(R.id.writerNameView);

            titleView.setText(vo.getStrTitle());
//            synopsisView.setText(CommonUtils.strGetTime(vo.getStrDate()));
            synopsisView.setText(vo.getStrDate().substring(0, 16));

            if(vo.getStrSubmit().equals("N")) {
                postAvailableView.setText("제출대기");
                postAvailableView.setBackgroundResource(R.drawable.gray_tag_bg);
            } else if(vo.getStrSubmit().equals("W")) {
                postAvailableView.setText("승인대기");
                postAvailableView.setBackgroundResource(R.drawable.selected_add_person_btn);
            } else if(vo.getStrSubmit().equals("Y")) {
                postAvailableView.setText("게시중");
                postAvailableView.setBackgroundResource(R.drawable.common_selected_rounded_btn_bg);
            } else if(vo.getStrSubmit().equals("F")) {
                postAvailableView.setText("게시거절");
                postAvailableView.setBackgroundResource(R.drawable.aprove_cancel_bg);
            }

            ImageView menuBtn = convertView.findViewById(R.id.menuBtn);
            if(workVO.getnWriterID().equals(pref.getString("USER_ID", "Guest")) || pref.getString("ADMIN", "N").equals("Y")) {
                menuBtn.setVisibility(View.VISIBLE);
                menuBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(WorkWriteMainActivity_old.this, menuBtn);
                        popup.getMenuInflater().inflate(R.menu.work_write_list_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = null;

                                switch(item.getItemId()) {
                                    case R.id.delete:
                                        if(workVO.getnInteractionEpisodeID() == vo.getnEpisodeID()) {       // 삭제하려는게 인터렉션 시작 회차라면
                                            AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity_old.this);
                                            builder.setTitle("인터렉션 회차 삭제");
                                            builder.setMessage("인터렉션이 시작된 회차를 삭제하면 이후 작성된 모든 회차가 삭제됩니다. 삭제하시겠습니까?");
                                            builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    requestDeleteEpisode(vo.getnEpisodeID());
                                                }
                                            });

                                            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });

                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(WorkWriteMainActivity_old.this);
                                            builder.setTitle("회차 삭제");
                                            builder.setMessage("회차를 삭제하면 작성했던 모든 내용이 삭제됩니다. 삭제하시겠습니까?");
                                            builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    requestDeleteEpisode(vo.getnEpisodeID());
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

                                        break;
                                    case R.id.comment:
                                        intent = new Intent(WorkWriteMainActivity_old.this, EpisodeCommentActivity.class);
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
                menuBtn.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    public void OnClickNewEpisodeBtn(View view) {
//        Intent intent = new Intent(WorkWriteMainActivity_old.this, LiteratureWriteActivity.class);
//        LiteratureWriteActivity.workVO = workVO;
//        startActivity(intent);
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
                            Toast.makeText(WorkWriteMainActivity_old.this, "회차 생성을 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        getWorkWithIDForEpisode(nEpisodeID);
//

//                        aa = new LiteratureListActivity.CWorkListAdapter(LiteratureListActivity.this, R.layout.work_list_row, workList);
//                        listView.setAdapter(aa);
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
                            Toast.makeText(WorkWriteMainActivity_old.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        initViews();

                        Intent intent = new Intent(WorkWriteMainActivity_old.this, LiteratureWriteActivity.class);
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
}
