package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Whowant.Tokki.R;

public class InteractionPopup extends AppCompatActivity {
    private RelativeLayout interaction1Layout;
    private RelativeLayout interaction2Layout;
    private ImageView    interaction1Bar;
    private ImageView    interaction2Bar;
    private TextView     interaction1ContentsView, interaction2ContentsView, interactionAView, interactionBView;

    private String strName;
    private String strContents1;
    private String strContents2;
    private int nType;
    private String strPhoto;
    private Intent oldIntent;
    private int nSelect = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_popup);

        oldIntent = getIntent();

        nType = oldIntent.getIntExtra("TYPE", 0);
        strName = oldIntent.getStringExtra("NAME");
        strPhoto = oldIntent.getStringExtra("PHOTO");
        strContents1 = oldIntent.getStringExtra("CONTENTS_1");
        strContents2 = oldIntent.getStringExtra("CONTENTS_2");
        initViews();
    }

    private void initViews() {
        interaction1Layout = findViewById(R.id.interaction1Layout);
        interaction2Layout = findViewById(R.id.interaction2Layout);
        interaction1ContentsView = findViewById(R.id.interaction1ContentsView);
        interaction2ContentsView = findViewById(R.id.interaction2ContentsView);
        interaction1Bar = findViewById(R.id.interaction1Bar);
        interaction2Bar = findViewById(R.id.interaction2Bar);
        interactionAView = findViewById(R.id.interactionAView);
        interactionBView = findViewById(R.id.interactionBView);

        interaction1ContentsView.setText(strContents1);
        interaction2ContentsView.setText(strContents2);
//        interaction1Container = findViewById(R.id.interaction1Container);
//        interaction2Container = findViewById(R.id.interaction2Container);
//        interaction1Check = findViewById(R.id.interaction1Check);
//        interaction2Check = findViewById(R.id.interaction2Check);
//
//        if(nType == 1) {                    // left
//            initLeftRows();
//        } else if(nType == 2){              // right
//            initRightRows();
//        } else {                            // middle
//            initMiddleRows();
//        }
//
        interaction1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nSelect = 1;
                interaction1ContentsView.setTextColor(getResources().getColor(R.color.colorPrimary));
                interactionAView.setTextColor(getResources().getColor(R.color.colorPrimary));
                interaction1Bar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                interaction2ContentsView.setTextColor(getResources().getColor(R.color.colorBlack));
                interactionBView.setTextColor(getResources().getColor(R.color.colorBlack));
                interaction2Bar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            }
        });

        interaction2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nSelect = 2;
                interaction2ContentsView.setTextColor(getResources().getColor(R.color.colorPrimary));
                interactionBView.setTextColor(getResources().getColor(R.color.colorPrimary));
                interaction2Bar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                interaction1ContentsView.setTextColor(getResources().getColor(R.color.colorBlack));
                interactionAView.setTextColor(getResources().getColor(R.color.colorBlack));
                interaction1Bar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            }
        });
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

//    private void initLeftRows() {
//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view = inflater.inflate(R.layout.left_interaction_row, null);
//        TextView nameView = view.findViewById(R.id.nameView);
//        ImageView faceView = view.findViewById(R.id.faceView);
//        TextView contentsTextView = view.findViewById(R.id.contentsTextView);
//
//        nameView.setText(strName);
//
//        if(strPhoto != null && !strPhoto.equals("null")) {
//            String strUrl = strPhoto;
////            strUrl = strUrl.replaceAll(" ", "");
//
//            if(!strUrl.startsWith("http"))
//                strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;
//
//            Glide.with(InteractionPopup.this)
//                    .asBitmap() // some .jpeg files are actually gif
//                    .load(strUrl)
//                    .apply(new RequestOptions().circleCrop())
//                    .into(faceView);
//        } else {
//            faceView.setImageResource(0);
//        }
//
//        contentsTextView.setText(strContents1);
//        interaction1Container.addView(view);
//
//        View view2 = inflater.inflate(R.layout.left_interaction_row, null);
//        TextView nameView2 = view2.findViewById(R.id.nameView);
//        ImageView faceView2 = view2.findViewById(R.id.faceView);
//        TextView contentsTextView2 = view2.findViewById(R.id.contentsTextView);
//
//        nameView2.setText(strName);
//
//        if(strPhoto != null && !strPhoto.equals("null")) {
//            String strUrl = strPhoto;
////            strUrl = strUrl.replaceAll(" ", "");
//
//            if(!strUrl.startsWith("http"))
//                strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;
//
//            Glide.with(InteractionPopup.this)
//                    .asBitmap() // some .jpeg files are actually gif
//                    .load(strUrl)
//                    .apply(new RequestOptions().circleCrop())
//                    .into(faceView2);
//        } else {
//            faceView.setImageResource(0);
//        }
//
//        contentsTextView2.setText(strContents2);
//        interaction2Container.addView(view2);
//    }
//
//    private void initRightRows() {
//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view = inflater.inflate(R.layout.right_interaction_row, null);
//        TextView nameView = view.findViewById(R.id.nameView);
//        ImageView faceView = view.findViewById(R.id.faceView);
//        TextView contentsTextView = view.findViewById(R.id.contentsTextView);
//
//        nameView.setText(strName);
//
//        if(strPhoto != null && !strPhoto.equals("null")) {
//            String strUrl = strPhoto;
////            strUrl = strUrl.replaceAll(" ", "");
//
//            if(!strUrl.startsWith("http"))
//                strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;
//
//            Glide.with(InteractionPopup.this)
//                    .asBitmap() // some .jpeg files are actually gif
//                    .load(strUrl)
//                    .apply(new RequestOptions().circleCrop())
//                    .into(faceView);
//        } else {
//            faceView.setImageResource(0);
//        }
//
//        contentsTextView.setText(strContents1);
//        interaction1Container.addView(view);
//
//        View view2 = inflater.inflate(R.layout.right_interaction_row, null);
//        TextView nameView2 = view2.findViewById(R.id.nameView);
//        ImageView faceView2 = view2.findViewById(R.id.faceView);
//        TextView contentsTextView2 = view2.findViewById(R.id.contentsTextView);
//
//        nameView2.setText(strName);
//
//        if(strPhoto != null && !strPhoto.equals("null")) {
//            String strUrl = strPhoto;
////            strUrl = strUrl.replaceAll(" ", "");
//
//            if(!strUrl.startsWith("http"))
//                strUrl = CommonUtils.strDefaultUrl + "images/" + strUrl;
//
//            Glide.with(InteractionPopup.this)
//                    .asBitmap() // some .jpeg files are actually gif
//                    .load(strUrl)
//                    .apply(new RequestOptions().circleCrop())
//                    .into(faceView2);
//        } else {
//            faceView2.setImageResource(0);
//        }
//
//        contentsTextView2.setText(strContents2);
//        interaction2Container.addView(view2);
//    }
//
//    private void initMiddleRows() {
//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view = inflater.inflate(R.layout.middle_interaction_row, null);
//        TextView contentsTextView = view.findViewById(R.id.contentsTextView);
//        contentsTextView.setText(strContents1);
//        interaction1Container.addView(view);
//
//        View view2 = inflater.inflate(R.layout.middle_interaction_row, null);
//        TextView contentsTextView2 = view2.findViewById(R.id.contentsTextView);
//        contentsTextView2.setText(strContents2);
//        interaction2Container.addView(view2);
//    }

    public void onClickOKBtn(View view) {
        oldIntent.putExtra("RESULT", nSelect);
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}
