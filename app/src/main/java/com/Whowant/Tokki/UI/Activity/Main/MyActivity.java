package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MyActivity extends AppCompatActivity {
    private ImageView faceView;
    private TextView nameView;
    private TextView descView;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        faceView = findViewById(R.id.faceView);
        nameView = findViewById(R.id.nameView);
        descView = findViewById(R.id.descView);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        String strPhoto = pref.getString("USER_PHOTO", "");

        if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if(!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(MyActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }

        nameView.setText(pref.getString("USER_NAME", ""));
        descView.setText(pref.getString("USER_DESC", ""));
    }
}
