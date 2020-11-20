package com.Whowant.Tokki.UI.Activity.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.ViewHolder.SearchResultViewHolder;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class SearchResultActivity extends AppCompatActivity {

    EditText searchEt;
    LinearLayout searchDeleteLl;
    ImageView searchIv;

    RecyclerView recyclerView;
    SearchResultAdapter adapter;
    ArrayList<WorkVO> mArrayList = new ArrayList<>();

    InputMethodManager imm;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mActivity = this;

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        initView();
        getData();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            searchEt.setText(getIntent().getStringExtra("search"));
            searchEt.setSelection(searchEt.getText().toString().length());
            getSearchWorkList(searchEt.getText().toString(), 0);
        }
    }

    private void initView() {

        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        searchEt = findViewById(R.id.et_result_search);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    searchDeleteLl.setVisibility(View.VISIBLE);
                } else {
                    searchDeleteLl.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchDeleteLl = findViewById(R.id.ll_result_search_delete);
        searchDeleteLl.setOnClickListener((v1) -> searchEt.setText(""));
        searchIv = findViewById(R.id.iv_result_search);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(searchEt.getText().toString())) {
                    getSearchWorkList(searchEt.getText().toString(), 0);
                }

                imm.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new SearchResultAdapter(mActivity, mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                WorkVO item = mArrayList.get(position);

                Intent intent = new Intent(mActivity, WorkMainActivity.class);
                intent.putExtra("WORK_ID", item.getnWorkID());
                startActivity(intent);
            }
        });
    }

    public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<WorkVO> arrayList;

        public SearchResultAdapter(Context context, ArrayList<WorkVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_category, parent, false);
            return new SearchResultViewHolder(v, null);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            WorkVO item = arrayList.get(position);

            if (holder instanceof SearchResultViewHolder) {
                SearchResultViewHolder viewHolder = (SearchResultViewHolder) holder;

                String strImgUrl = item.getCoverFile();

                if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(R.drawable.no_poster_vertical)
                            .into(viewHolder.photoIv);
                } else {
                    if (!strImgUrl.startsWith("http")) {
                        strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                    }

                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(strImgUrl)
                            .into(viewHolder.photoIv);
                }

                viewHolder.titleTv.setText(item.getTitle());
                viewHolder.writerTv.setText("by " + item.getStrWriterName());
                viewHolder.heartTv.setText(CommonUtils.getPointCount(item.getnKeepcount()));
                viewHolder.tabTv.setText(CommonUtils.getPointCount(item.getnTapCount()));
                viewHolder.synopsisTv.setText(item.getSynopsis());

                float fStarPoint = item.getfStarPoint();

                if (fStarPoint == 0)
                    viewHolder.starTv.setText("0");
                else
                    viewHolder.starTv.setText(String.format("%.1f", fStarPoint));

                viewHolder.optionIv.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private void getSearchWorkList(String serachKeyword, int select) {
        mArrayList.clear();

        CommonUtils.showProgressDialog(mActivity, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mArrayList.addAll(HttpClient.getSearchWorkList(new OkHttpClient(), serachKeyword, select));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (mArrayList == null || mArrayList.size() == 0) {
                            Toast.makeText(mActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}