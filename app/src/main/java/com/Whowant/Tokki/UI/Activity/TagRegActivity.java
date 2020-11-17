package com.Whowant.Tokki.UI.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Custom.FlowLayout;
import com.Whowant.Tokki.UI.Custom.TagFlowLayout;
import com.Whowant.Tokki.UI.TypeOnClickListener;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.VO.TagVo;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class TagRegActivity extends AppCompatActivity {

    private ArrayList<TagSelectVo> tagList = new ArrayList<>();
    ArrayList<TagVo> mArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    TagRegAdapter adapter;

    EditText tagEt;
    TextView regTv;

    private TagFlowLayout tagsLayout;
    private ArrayList<View> tagViewList;
    private String strCurrentTag = "";
    private int nSelectedCount = 0;
    ScrollView scrollView;

    Activity mActivity;

    public class TagSelectVo {
        public String name;
        public boolean isSelected;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_reg);

        mActivity = this;

        getData();
        initView();

        setTagSelected();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            strCurrentTag = getIntent().getStringExtra("TAG");

            if (!TextUtils.isEmpty(strCurrentTag)) {
                String[] tags = strCurrentTag.split(" ");

                for (String str : tags) {
                    TagSelectVo vo = new TagSelectVo();
                    vo.setName(str);
                    vo.setSelected(true);
                    tagList.add(vo);
                }
            }
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("태그");

        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());
        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);

        scrollView = findViewById(R.id.sv_tag_reg);

        regTv = findViewById(R.id.tv_tag_reg);
        regTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(tagEt.getText().toString())) {
                    addTag(tagEt.getText().toString());
                }
            }
        });

        tagEt = findViewById(R.id.et_tag_reg);
        tagEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    getTagInfo(s.toString());
                    regTv.setTextColor(Color.parseColor("#5a9aff"));
                } else {
                    recyclerView.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    regTv.setTextColor(Color.parseColor("#cccccc"));
                    mArrayList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                outRect.bottom = DeviceUtils.dpToPx(mActivity, 10);
            }
        });
        adapter = new TagRegAdapter(mActivity, mArrayList);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                TagVo item = mArrayList.get(position);

                addTagSelected(item.getTAG_NAME());
            }
        });

        tagsLayout = findViewById(R.id.tagsLayout);
//        setGenreView();
    }

    private void addTagSelected(String tagName) {
        boolean isCheck = false;

        for (TagSelectVo vo : tagList) {
            if (tagName.equals(vo.getName())) {
                isCheck = true;
                break;
            }
        }

        if (!isCheck) {
            TagSelectVo vo = new TagSelectVo();
            vo.setName(tagName);
            vo.setSelected(true);
            tagList.add(vo);
        }

        setTagSelected();
    }

    public class TagRegAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<TagVo> arrayList;

        public TagRegAdapter(Context context, ArrayList<TagVo> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag_reg, parent, false);
            return new TagRegViewHolder(v, new TypeOnClickListener() {
                @Override
                public void onClick(int type, int position) {
                    TagVo item = arrayList.get(position);
                    String tag = item.getTAG_NAME();
//                    if (tag.startsWith("#")) {
//                        tag = tag.substring(1);
//                    }
                    tagEt.setText(tag);
                    tagEt.setSelection(tagEt.getText().toString().length());
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TagVo item = arrayList.get(position);

            if (holder instanceof TagRegViewHolder) {
                TagRegViewHolder viewHolder = (TagRegViewHolder) holder;

                viewHolder.nameTv.setText(item.getTAG_NAME());
                viewHolder.countTv.setText(item.getWORK_COUNT() + "개");
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class TagRegViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView countTv;

        public TagRegViewHolder(@NonNull View itemView, TypeOnClickListener listener) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.tv_row_tag_reg_name);
            countTv = itemView.findViewById(R.id.tv_row_tag_reg_count);

            itemView.setOnClickListener((v) -> {
                if (listener != null) {
                    listener.onClick(0, getAdapterPosition());
                }
            });
        }
    }

    private void getTagInfo(String tag) {
        mArrayList.clear();

        if (tag.startsWith("#")) {
            tag = tag.substring(1);
        }

        final String tagName = tag;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mArrayList.addAll(HttpClient.getTagInfo(new OkHttpClient(), tagName));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void addTag(String tag) {
        if (!tag.startsWith("#")) {
            tag = "#" + tag;
        }

        final String tagName = tag;

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = HttpClient.addTag(new OkHttpClient(), tagName);

                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            addTagSelected(tagName);
                        }
                    });
                } else {
                    Toast.makeText(mActivity, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    // 저장 버튼
    public void btnSave(View v) {
        StringBuffer stringBuffer = new StringBuffer();

        for (TagSelectVo vo : tagList) {
            if (!TextUtils.isEmpty(stringBuffer)) {
                stringBuffer.append(" ");
            }

            if (vo.isSelected()) {
                stringBuffer.append(vo.getName());
            }
        }

        Intent intent = new Intent();
        intent.putExtra("tag", stringBuffer.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setTagSelected() {
        if (tagList.size() == 0) {
            return;
        }

        tagViewList = new ArrayList<>();
        tagsLayout.removeAllViews();

        for (int i = 0; i < tagList.size(); i++) {
            final int nPosition = i;
            View view = (View) getLayoutInflater().inflate(R.layout.tag_row, null);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 15);
            view.setLayoutParams(params);

            final RelativeLayout bgView = view.findViewById(R.id.bgView);
            final TextView genreNameView = view.findViewById(R.id.genreNameView);
            final ImageView iconView = view.findViewById(R.id.iconView);

            String strGenre = tagList.get(i).getName();
            genreNameView.setText(strGenre);

            if (tagList.get(i).isSelected()) {
                bgView.setBackgroundResource(R.drawable.genre_selected_bg);
                genreNameView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                iconView.setBackgroundResource(R.drawable.check);
            } else {
                bgView.setBackgroundResource(R.drawable.genre_gray_bg);
                genreNameView.setTextColor(Color.parseColor("#969696"));
                iconView.setBackgroundResource(R.drawable.plus_button);
            }

            tagsLayout.addView(view);
            tagViewList.add(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean bSelected = tagList.get(nPosition).isSelected();

                    if (!bSelected) {
                        if (nSelectedCount < 10) {
                            bSelected = true;
                            nSelectedCount++;

                            if (strCurrentTag.length() > 0)
                                strCurrentTag += " ";

                            strCurrentTag += tagList.get(nPosition).getName();

                            bgView.setBackgroundResource(R.drawable.genre_selected_bg);
                            genreNameView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                            iconView.setBackgroundResource(R.drawable.check);

                            tagList.get(nPosition).setSelected(true);
                        }
                    } else {
                        bSelected = false;
                        nSelectedCount--;

                        String[] tagArr = strCurrentTag.split(" ");
                        String strNewGenre = "";
                        for (String tag : tagArr) {
                            if (!tag.equals(tagList.get(nPosition).getName())) {
                                if (strNewGenre.length() > 0)
                                    strNewGenre += " ";
                                strNewGenre += tag;
                            }
                        }

                        strCurrentTag = strNewGenre;

                        bgView.setBackgroundResource(R.drawable.genre_gray_bg);
                        genreNameView.setTextColor(Color.parseColor("#969696"));
                        iconView.setBackgroundResource(R.drawable.plus_button);

                        tagList.get(nPosition).setSelected(false);
                    }
                }
            });
        }

        recyclerView.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

        tagEt.setText("");
        adapter.notifyDataSetChanged();
    }
}