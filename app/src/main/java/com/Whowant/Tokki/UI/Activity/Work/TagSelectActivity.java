package com.Whowant.Tokki.UI.Activity.Work;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Custom.FlowLayout;
import com.Whowant.Tokki.UI.Custom.TagFlowLayout;
import com.Whowant.Tokki.Utils.CommonUtils;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class TagSelectActivity extends AppCompatActivity {
    private ArrayList<String> tagList;
    private ArrayList<View> tagViewList;
    private TextView tagView;
    private TagFlowLayout tagsLayout;
    private TextView okBtn;
    private boolean[] bSelectedArray;
    private String strCurrentTag = "";
    private int nSelectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_select);

        tagView = findViewById(R.id.tagView);
        tagsLayout = findViewById(R.id.tagsLayout);
        okBtn = findViewById(R.id.okBtn);

        strCurrentTag = getIntent().getStringExtra("TAG");
        getTagList();
    }

    private void getTagList() {                                             // tag 는 FlowLayout 이라는 Custom View 를 활용하여 add view 하여 사용
        tagList = new ArrayList<>();
        CommonUtils.showProgressDialog(TagSelectActivity.this, "서버와 통신중입니다. 잠시만 기다려주세요.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                tagList = HttpClient.getAllTagList(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(tagList == null) {
                            Toast.makeText(TagSelectActivity.this, "태그 목록을 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        bSelectedArray = new boolean[tagList.size()];
                        for(int i = 0 ; i < bSelectedArray.length ; i++) {
                            bSelectedArray[i] = false;
                        }

                        tagViewList = new ArrayList<>();
                        for(int i = 0 ; i < tagList.size() ; i++) {
                            final int nPosition = i;
                            View view = (View)getLayoutInflater().inflate(R.layout.tag_row, null);
                            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(20, 15);
                            view.setLayoutParams(params);

                            final RelativeLayout bgView = view.findViewById(R.id.bgView);
                            final TextView genreNameView = view.findViewById(R.id.genreNameView);
                            final ImageView iconView = view.findViewById(R.id.iconView);

                            String strGenre = tagList.get(i);
                            genreNameView.setText(strGenre);

                            tagsLayout.addView(view);
                            tagViewList.add(view);

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    boolean bSelected = bSelectedArray[nPosition];

                                    if(!bSelected) {
                                        if(nSelectedCount < 10) {
                                            bSelected = true;
                                            nSelectedCount ++;

                                            if(strCurrentTag.length() > 0)
                                                strCurrentTag += " ";

                                            strCurrentTag += tagList.get(nPosition);
                                            bSelectedArray[nPosition] = bSelected;

                                            bgView.setBackgroundResource(R.drawable.genre_selected_bg);
                                            genreNameView.setTextColor(ContextCompat.getColor(TagSelectActivity.this, R.color.colorPrimary));
                                            iconView.setBackgroundResource(R.drawable.check);
                                        }
                                    } else {
                                        bSelected = false;
                                        nSelectedCount --;

                                        String[] tagArr = strCurrentTag.split(" ");
                                        String strNewGenre = "";
                                        for(String tag : tagArr) {
                                            if(!tag.equals(tagList.get(nPosition))) {
                                                if(strNewGenre.length() > 0)
                                                    strNewGenre += " ";
                                                strNewGenre += tag;
                                            }
                                        }

                                        strCurrentTag = strNewGenre;
                                        bSelectedArray[nPosition] = bSelected;

                                        bgView.setBackgroundResource(R.drawable.genre_gray_bg);
                                        genreNameView.setTextColor(Color.parseColor("#969696"));
                                        iconView.setBackgroundResource(R.drawable.plus_button);
                                    }

                                    tagView.setText(strCurrentTag);
                                    if(strCurrentTag.length() > 0) {
                                        okBtn.setBackgroundColor(ContextCompat.getColor(TagSelectActivity.this, R.color.colorPrimary));
                                        okBtn.setTextColor(ContextCompat.getColor(TagSelectActivity.this, R.color.colorWhite));
                                        okBtn.setEnabled(true);
                                    } else {
                                        okBtn.setBackgroundColor(Color.parseColor("#e8e8e8"));
                                        okBtn.setTextColor(Color.parseColor("#969696"));
                                        okBtn.setEnabled(false);
                                    }
                                }
                            });
                        }

                        if(strCurrentTag.length() > 0) {
                            String[] tagArr = strCurrentTag.split(" ");

                            for(int index = 0 ; index < tagArr.length ; index++) {
                                String strTag = tagArr[index];

                                for(int j = 0 ; j < tagList.size() ; j++) {
                                    String tag = tagList.get(j);

                                    if(strTag.equals(tag)) {
                                        bSelectedArray[j] = true;
                                        nSelectedCount ++;
                                        View currentView = tagViewList.get(j);
                                        RelativeLayout currentBGView = currentView.findViewById(R.id.bgView);
                                        TextView currentNameView = currentView.findViewById(R.id.genreNameView);
                                        ImageView currentIconView = currentView.findViewById(R.id.iconView);

                                        currentBGView.setBackgroundResource(R.drawable.genre_selected_bg);
                                        currentNameView.setTextColor(ContextCompat.getColor(TagSelectActivity.this, R.color.colorPrimary));
                                        currentIconView.setBackgroundResource(R.drawable.check);
                                    }
                                }
                            }

                            tagView.setText(strCurrentTag);
                        }
                    }
                });
            }
        }).start();
    }

    private void initViews() {

    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void OnClickOkBtn(View view) {
        Intent oldIntent = getIntent();
        oldIntent.putExtra("TAG", strCurrentTag);
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}