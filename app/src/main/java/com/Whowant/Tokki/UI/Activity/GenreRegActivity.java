package com.Whowant.Tokki.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class GenreRegActivity extends AppCompatActivity {

    LinearLayout genreLl;
    ArrayList<String> mArrayList = new ArrayList<>();
    ArrayList<String> checkList = new ArrayList<>();

    String[] genreSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_reg);

        getData();
        initView();
    }

    private void getData() {
        if(getIntent() != null && getIntent().getExtras() != null) {
            genreSelect = getIntent().getStringExtra("genre").split("/");
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("장르");

        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());
        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);

        String[] tmp = new String[]{
                "SF", "공포, 스릴러", "로맨스", "코미디", "드라마", "판타지", "리얼스토리"
        };

        mArrayList.addAll(Arrays.asList(tmp));

        genreLl = findViewById(R.id.ll_genre);

        setGenreView();
    }

    private void setGenreView() {
        int count = 0;

        LinearLayout genreChildLl = null;

        for (String genre : mArrayList) {
            if (count++ % 4 == 0) {
                if (genreChildLl != null) {
                    genreLl.addView(genreChildLl);
                }
                genreChildLl = addGenreLinearLayout();
            } else if (count == mArrayList.size()) {
                genreLl.addView(genreChildLl);
            }

            genreChildLl.addView(addGenreTextView(genre));
        }
    }

    private LinearLayout addGenreLinearLayout() {
        LinearLayout genreChildLl = new LinearLayout(this);
        genreChildLl.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = DeviceUtils.dpToPx(this, 10);
        genreChildLl.setLayoutParams(params);
        return genreChildLl;
    }

    private View addGenreTextView(String name) {
        View v = LayoutInflater.from(this).inflate(R.layout.view_genre, null);
        TextView nameTv = v.findViewById(R.id.tv_genre_child_name);
        nameTv.setText(name);
        LinearLayout childLl = v.findViewById(R.id.ll_genre_child);
        childLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = childLl.isSelected();

                checkList.clear();
                checkTagView(genreLl);

                if (checkList.size() == 3 && !isSelected) {
                    return;
                }

                childLl.setSelected(!isSelected);
                nameTv.setSelected(!isSelected);

                if (childLl.isSelected()) {
                    childLl.setTag(name);
                } else {
                    childLl.setTag(null);
                }
            }
        });

        for(int i=0; i<genreSelect.length; i++) {
            if(name.equals(genreSelect[i])) {
                childLl.setSelected(true);
                nameTv.setSelected(true);
                childLl.setTag(name);
            }
        }
        return v;
    }

    private void checkTagView(View v) {
        if (v != null) {
            if (v instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) v;

                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View v1 = viewGroup.getChildAt(i);

                    if (v1 instanceof ViewGroup) {
                        checkTagView(v1);
                    }

                    if (v1.getTag() != null) {
                        checkList.add((String) v1.getTag());
                    }
                }
            }
        }
    }

    public void btnSave(View v) {
        checkList.clear();
        checkTagView(genreLl);

        StringBuffer stringBuffer = new StringBuffer();
        for (String str : checkList) {
            if (stringBuffer.length() != 0) {
                stringBuffer.append(" / ");
            }
            stringBuffer.append(str);
        }
        Intent intent = new Intent();
        intent.putExtra("genre", stringBuffer.toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}