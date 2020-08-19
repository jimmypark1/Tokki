package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CharacterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class InteractionSpeakerSelectActivity extends AppCompatActivity {
    private LinearLayout speakerAddLayout;
    public static ArrayList<CharacterVO> characterVOArrayList;
    private int nSelectedCharacterIndex = 0;                // 0 = 나레이
    private ArrayList<View> characterViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_speaker_select);

        speakerAddLayout = findViewById(R.id.speakerAddLayout);
    }

    @Override
    public void onDestroy() {
        characterVOArrayList = null;
        super.onDestroy();
    }

    private void initCharacterLayout() {
        characterViewList = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0 ; i < characterVOArrayList.size() ; i++) {
            final int nIndex = i;
            View view = inflater.inflate(R.layout.speaker_view, null);
            CharacterVO vo = characterVOArrayList.get(i);

            ImageView faceView = view.findViewById(R.id.faceView);
            TextView nameView = view.findViewById(R.id.nameView);

            if(i == 0) {
                faceView.setImageResource(R.drawable.section_icon1);
                nameView.setText("지문");

                RelativeLayout faceBGLayout = view.findViewById(R.id.faceBGLayout);
                faceBGLayout.setBackgroundResource(R.drawable.selected_naration_bg);
                nameView.setTextColor(ContextCompat.getColor(InteractionSpeakerSelectActivity.this, R.color.colorCommonSelected));
                nSelectedCharacterIndex = 0;
            } else {
                if(vo.getImage() != null && !vo.getImage().equals("null")) {
                    Glide.with(this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(vo.getImage())
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else if(vo.getStrImgFile() != null && !vo.getStrImgFile().equals("null")) {
                    String strUrl = vo.getStrImgFile();
//                    strUrl = strUrl.replaceAll(" ", "");

                    if(!strUrl.startsWith("http"))
                        strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;

                    Glide.with(InteractionSpeakerSelectActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    faceView.setImageResource(0);
                }

                nameView.setText(vo.getName());
            }

            LinearLayout faceLayout = view.findViewById(R.id.faceLayout);
            faceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i = 0 ; i < characterViewList.size() ; i++) {
                        View view = characterViewList.get(i);

                        RelativeLayout faceBGLayout = view.findViewById(R.id.faceBGLayout);
                        TextView nameView = view.findViewById(R.id.nameView);

                        if(i == nIndex) {
                            if(i == 0)
                                faceBGLayout.setBackgroundResource(R.drawable.selected_naration_bg);
                            else
                                faceBGLayout.setBackgroundResource(R.drawable.selected_face_bg);
                            nameView.setTextColor(ContextCompat.getColor(InteractionSpeakerSelectActivity.this, R.color.colorCommonSelected));
                        } else {
                            if(i == 0)
                                faceBGLayout.setBackgroundResource(R.drawable.gray_naration_bg);
                            else
                                faceBGLayout.setBackgroundResource(R.drawable.gray_face_bg);
                            nameView.setTextColor(Color.parseColor("#b6b7b8"));
                        }
                    }

                    nSelectedCharacterIndex = nIndex;
                }
            });

            speakerAddLayout.addView(view);
            characterViewList.add(view);
        }
    }
}
