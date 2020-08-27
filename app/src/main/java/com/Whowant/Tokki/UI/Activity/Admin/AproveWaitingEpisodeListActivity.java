package com.Whowant.Tokki.UI.Activity.Admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.ViewerActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Popup.EpisodeAproveCancelPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WaitingVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class AproveWaitingEpisodeListActivity extends AppCompatActivity {
    private ListView listView;
    private TextView emptyView;

    private ArrayList<WaitingVO> waitingVOList;
    private SharedPreferences pref;
    private CWaitingArrayList aa;
    private InputMethodManager imm;
    private boolean bModeComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprove_waiting_episode_list);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        waitingVOList = new ArrayList<>();
        aa = new CWaitingArrayList(this, R.layout.waiting_episode_row, waitingVOList);
        listView.setAdapter(aa);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                getWorkInfo(position);
//                Intent intent = new Intent(AproveWaitingEpisodeListActivity.this, WorkMainActivity.class);
//                intent.putExtra("WORK_ID", waitingVOList.get(position).getnWorkID());
//                startActivity(intent);
            }
        });

//        inputSearchTextView = findViewById(R.id.inputSearchTextView);
//        inputSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                switch (actionId) {
//                    case EditorInfo.IME_ACTION_SEARCH:
//                        requestSearch();
//                        break;
//                    case 0:
//                        requestSearch();
//                        break;
//                    default:
//                        return false;
//                }
//                return true;
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getWaitingData();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void getWaitingData() {
        waitingVOList.clear();
        CommonUtils.showProgressDialog(AproveWaitingEpisodeListActivity.this, "게시 대기중인 작품 목록을 가져오고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<WaitingVO> waitingList = null;

                if(!bModeComplete)
                    waitingList = HttpClient.getWaitingList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));
                else
                    waitingList = HttpClient.getCompleteList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                if(waitingList != null)
                    waitingVOList.addAll(waitingList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        
                        if(waitingVOList == null) {
                            Toast.makeText(AproveWaitingEpisodeListActivity.this, "서버와의 통신에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        aa.notifyDataSetChanged();

                        if(waitingVOList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

//    private void requestSearch() {
//        imm.hideSoftInputFromWindow(inputSearchTextView.getWindowToken(), 0);
//        String strKeyword = inputSearchTextView.getText().toString();
//
//        if(strKeyword.length() == 0) {
//            Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        waitingVOList.clear();
//        CommonUtils.showProgressDialog(this, "서버와 통신중입니다. 잠시만 기다려주세요.");
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<WaitingVO> list = HttpClient.getSearchWaitingListByAdmin(new OkHttpClient(), strKeyword, pref.getString("USER_ID", "Guest"));
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(list != null) {
//                            waitingVOList.addAll(list);
//                            aa.notifyDataSetChanged();
//
//                            if(list.size() == 0) {
//                                emptyView.setVisibility(View.VISIBLE);
//                            } else {
//                                emptyView.setVisibility(View.INVISIBLE);
//                            }
//
//                            CommonUtils.hideProgressDialog();
//                        } else {
//                            CommonUtils.hideProgressDialog();
//                            Toast.makeText(AproveWaitingEpisodeListActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }).start();
//    }

    public class CWaitingArrayList extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CWaitingArrayList(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.waiting_episode_row, parent, false);

//            191개
            if(position > waitingVOList.size()-1)
                return convertView;

            WaitingVO vo = waitingVOList.get(position);

            ImageView coverView = convertView.findViewById(R.id.coverImgView);
            TextView workTitleView = convertView.findViewById(R.id.titleView);
//            TextView episodeTitleView = convertView.findViewById(R.id.episodeTitleView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
//            TextView dateTimeView = convertView.findViewById(R.id.dateTimeView);
            TextView episodeNumView = convertView.findViewById(R.id.episodeNumView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            ImageView menuBtn = convertView.findViewById(R.id.menuBtn);
            TextView stateView = convertView.findViewById(R.id.stateView);

            String strImgUrl = vo.getStrCoverImg();
            coverView.setClipToOutline(true);

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(AproveWaitingEpisodeListActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster_vertical)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(AproveWaitingEpisodeListActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            }

            if(bModeComplete) {
                stateView.setBackgroundResource(R.drawable.confirm_complete_bg);
                stateView.setTextColor(ContextCompat.getColor(AproveWaitingEpisodeListActivity.this, R.color.colorPrimary));
                stateView.setText("승인완료");
            } else {
                stateView.setBackgroundResource(R.drawable.confirm_waiting_bg);
                stateView.setTextColor(Color.parseColor("#ff5700"));
                stateView.setText("승인대기");
            }

            workTitleView.setText(vo.getStrWorkTitle());
            writerNameView.setText("By" + vo.getWriterName());
            episodeNumView.setText(vo.getnEpisodeOrder() + "화");

            if(vo.getnEpisodeOrder() < 10) {
                synopsisView.setText("        " + vo.getStrSynopsis());
            } else {
                synopsisView.setText("          " + vo.getStrSynopsis());
            }

            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(AproveWaitingEpisodeListActivity.this, menuBtn);

                    if(bModeComplete)
                        popup.getMenuInflater().inflate(R.menu.work_write_list_admin_cancel_menu, popup.getMenu());
                    else
                        popup.getMenuInflater().inflate(R.menu.aprove_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent intent = null;
                            AlertDialog.Builder builder = new AlertDialog.Builder(AproveWaitingEpisodeListActivity.this);
                            AlertDialog alertDialog = null;

                            switch(item.getItemId()) {
                                case R.id.aprove:
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
                                    intent = new Intent(AproveWaitingEpisodeListActivity.this, EpisodeAproveCancelPopup.class);
                                    intent.putExtra("EPISODE_ID", vo.getnEpisodeID());
                                    if(bModeComplete)
                                        intent.putExtra("CANCEL", true);
                                    startActivity(intent);
                                    break;
                                case R.id.info:
                                    intent = new Intent(AproveWaitingEpisodeListActivity.this, WorkMainActivity.class);
                                    intent.putExtra("WORK_ID", vo.getnWorkID());
                                    startActivity(intent);
                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });

            return convertView;
        }
    }

    private void requestWorkAprove(final int nEpisodeID) {
        CommonUtils.showProgressDialog(AproveWaitingEpisodeListActivity.this, "게시 승인을 요청중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestWorkAprove(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(bResult) {
                            Toast.makeText(AproveWaitingEpisodeListActivity.this, "게시 승인 되었습니다.", Toast.LENGTH_SHORT).show();
                            getWaitingData();
                        } else {
                            Toast.makeText(AproveWaitingEpisodeListActivity.this, "게시 승인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void getWorkInfo(final int nPosition) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WaitingVO vo = waitingVOList.get(nPosition);
                WorkVO workVO = HttpClient.getWorkWithID(new OkHttpClient(), "" + vo.getnWorkID(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AproveWaitingEpisodeListActivity.this, ViewerActivity.class);
                        ViewerActivity.workVO = workVO;
                        intent.putExtra("EPISODE_INDEX", vo.getnEpisodeOrder() - 1);
                        intent.putExtra("PREVIEW", true);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
//    public void onClickSearchBtn(View view) {
//        requestSearch();
//    }

    public void clickWaitingList(View view) {
        TextView waitingTextView = findViewById(R.id.waitingTextView);
        ImageView waitingLineView = findViewById(R.id.waitingLineView);
        TextView completeTextView = findViewById(R.id.completeTextView);
        ImageView completeLineView = findViewById(R.id.completeLineView);

        bModeComplete = false;
        waitingTextView.setTextColor(Color.parseColor("#6c8fff"));
        waitingLineView.setBackgroundColor(Color.parseColor("#6c8fff"));
        completeTextView.setTextColor(Color.parseColor("#d1d1d1"));
        completeLineView.setBackgroundColor(Color.parseColor("#d1d1d1"));
        getWaitingData();
    }

    public void clickCompleteList(View view) {
        TextView waitingTextView = findViewById(R.id.waitingTextView);
        ImageView waitingLineView = findViewById(R.id.waitingLineView);
        TextView completeTextView = findViewById(R.id.completeTextView);
        ImageView completeLineView = findViewById(R.id.completeLineView);

        bModeComplete = true;
        waitingTextView.setTextColor(Color.parseColor("#d1d1d1"));
        waitingLineView.setBackgroundColor(Color.parseColor("#d1d1d1"));
        completeTextView.setTextColor(Color.parseColor("#6c8fff"));
        completeLineView.setBackgroundColor(Color.parseColor("#6c8fff"));
        getWaitingData();
    }
}
