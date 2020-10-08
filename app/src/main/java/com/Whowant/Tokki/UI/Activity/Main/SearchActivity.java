package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.NoticeActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private int nSelect = 0;

    private final static int WORK_TAB = 0;
    private final static int WRITER_TAB = 1;
    private final static int TAG_TAB = 2;
    private final static int GENRE_TAB = 3;

    private TextView workTab;
    private TextView writerTab;
    private TextView tagTab;
    private TextView genreTab, recommendTitleView;
    private ImageView workSelectColor;
    private ImageView writerSelectColor;
    private ImageView tagSelectColor;
    private ImageView genreSelectColor;
    private EditText inputSearchTextView;

    private CSearchArrayAdapter aa;
    private ArrayList<WorkVO> searchList;
    private ArrayList<WorkVO> recommendList;

    private ListView listView;
    private String strSearchKeywork;
    private boolean bSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        workTab = findViewById(R.id.workTab);
        writerTab = findViewById(R.id.writerTab);
        tagTab = findViewById(R.id.tagTab);
        genreTab = findViewById(R.id.genreTab);
        workSelectColor = findViewById(R.id.workSelectColor);
        writerSelectColor = findViewById(R.id.writerSelectColor);
        tagSelectColor = findViewById(R.id.tagSelectColor);
        genreSelectColor = findViewById(R.id.genreSelectColor);
        inputSearchTextView = findViewById(R.id.inputSearchTextView);
        recommendTitleView = findViewById(R.id.recommendTitleView);

        inputSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_GO:
                    case EditorInfo.IME_ACTION_SEND:
                        strSearchKeywork = inputSearchTextView.getText().toString();

                        if(strSearchKeywork.length() == 0) {
                            Toast.makeText(SearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        getSearchListData();
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        getRecommendListData();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        WorkVO vo = null;

        if(searchList == null || searchList.size() == 0)
            vo = recommendList.get(position);
        else
            vo = searchList.get(position);

        Intent intent = new Intent(SearchActivity.this, WorkMainActivity.class);
        intent.putExtra("WORK_ID", vo.getnWorkID());
        startActivity(intent);
    }

    public void onClickSearchBtn(View view) {
        finish();
//        strSearchKeywork = inputSearchTextView.getText().toString();
//
//        if(strSearchKeywork.length() == 0) {
//            Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        getKeepListData();
    }

    private void getRecommendListData() {
        CommonUtils.showProgressDialog(SearchActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");
        recommendList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                recommendList.addAll(HttpClient.getRecommandList(new OkHttpClient()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(recommendList == null) {
                            Toast.makeText(SearchActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(recommendList.size() == 0) {
                            Toast.makeText(SearchActivity.this, "이달의 추천 목록을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            recommendTitleView.setVisibility(View.VISIBLE);
                            aa = new CSearchArrayAdapter(SearchActivity.this, R.layout.search_row, recommendList);
                            listView.setAdapter(aa);
                        }
//                        if(searchList.size() == 0) {
//                            listView.setVisibility(View.INVISIBLE);
//                            emptyView.setText("검색 결과가 없습니다.");
//                            emptyView.setVisibility(View.VISIBLE);
//                        } else {
//                            bSearch = true;
//                            aa.notifyDataSetChanged();
//                            listView.setVisibility(View.VISIBLE);
//                            emptyView.setVisibility(View.INVISIBLE);
//                        }
                    }
                });
            }
        }).start();
    }

    private void getSearchListData() {
        searchList = new ArrayList<>();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputSearchTextView.getWindowToken(), 0);

        CommonUtils.showProgressDialog(SearchActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                searchList.addAll(HttpClient.getSearchWorkList(new OkHttpClient(), strSearchKeywork, nSelect));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(searchList == null || searchList.size() == 0) {
                            bSearch = false;
                            recommendTitleView.setVisibility(View.VISIBLE);
                            aa = new CSearchArrayAdapter(SearchActivity.this, R.layout.search_row, recommendList);
                            listView.setAdapter(aa);
                            Toast.makeText(SearchActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            recommendTitleView.setVisibility(View.GONE);
                            bSearch = true;
                            aa = new CSearchArrayAdapter(SearchActivity.this, R.layout.search_row, searchList);
                            listView.setAdapter(aa);
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickBackBtn(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputSearchTextView.getWindowToken(), 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

    public void onClickWorkTab(View view) {
        nSelect = WORK_TAB;
        setTab();
        strSearchKeywork = inputSearchTextView.getText().toString();
        if(strSearchKeywork.length() == 0) {
            return;
        }

        getSearchListData();
    }

    public void onClickDeleteBtn(View view) {
        inputSearchTextView.setText("");
    }

    public void onClickWriterTab(View view) {
        nSelect = WRITER_TAB;
        setTab();
        strSearchKeywork = inputSearchTextView.getText().toString();
        if(strSearchKeywork.length() == 0) {
            return;
        }

        getSearchListData();
    }

    public void onClickTagTab(View view) {
        nSelect = TAG_TAB;
        setTab();
        strSearchKeywork = inputSearchTextView.getText().toString();
        if(strSearchKeywork.length() == 0) {
            return;
        }

        getSearchListData();
    }

    public void onClickGenreTab(View view) {
        nSelect = GENRE_TAB;
        setTab();
        strSearchKeywork = inputSearchTextView.getText().toString();
        if(strSearchKeywork.length() == 0) {
            return;
        }

        getSearchListData();
    }

    private void setTab() {
        workTab.setTextColor(Color.parseColor("#e3e3e3"));
        writerTab.setTextColor(Color.parseColor("#e3e3e3"));
        tagTab.setTextColor(Color.parseColor("#e3e3e3"));
        genreTab.setTextColor(Color.parseColor("#e3e3e3"));
        workSelectColor.setVisibility(View.INVISIBLE);
        writerSelectColor.setVisibility(View.INVISIBLE);
        tagSelectColor.setVisibility(View.INVISIBLE);
        genreSelectColor.setVisibility(View.INVISIBLE);

        switch (nSelect) {
            case WORK_TAB:
                workSelectColor.setVisibility(View.VISIBLE);
                workTab.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case WRITER_TAB:
                writerSelectColor.setVisibility(View.VISIBLE);
                writerTab.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case TAG_TAB:
                tagSelectColor.setVisibility(View.VISIBLE);
                tagTab.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case GENRE_TAB:
                genreSelectColor.setVisibility(View.VISIBLE);
                genreTab.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }

    public class CSearchArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CSearchArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.search_row, parent, false);

            WorkVO vo = null;

            if(searchList == null || searchList.size() == 0) {
                vo = recommendList.get(position);
            } else {
                if(position >= searchList.size())
                    return convertView;

                vo = searchList.get(position);
            }

            String strImgUrl = vo.getCoverFile();

            ImageView coverView = convertView.findViewById(R.id.coverImgView);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            TextView starPointView = convertView.findViewById(R.id.startPointView);
            TextView hitsCountView = convertView.findViewById(R.id.hitsCountView);
            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
            coverView.setClipToOutline(true);

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(SearchActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(R.drawable.no_poster_vertical)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(SearchActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            }

            titleView.setText(vo.getTitle());
            synopsisView.setText(vo.getSynopsis());

            float fStarPoint = vo.getfStarPoint();
            if(fStarPoint == 0)
                starPointView.setText("0");
            else
                starPointView.setText(String.format("%.1f", vo.getfStarPoint()));

            hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
            commentCountView.setText("" + vo.getnCommentCount());
            writerNameView.setText(vo.getStrWriterName());

            return convertView;
        }
    }
}