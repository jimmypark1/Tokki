package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

public class MarketAddEditActivity extends AppCompatActivity {

    WorkVO work;
    TextView title;
    TextView synopsis;
    TextView date;

    ImageView cover;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_add_edit);

        title = findViewById(R.id.tv_row_literature_title);
        cover = findViewById(R.id.iv_row_literature_photo);

        date = findViewById(R.id.tv_row_literature_date);
        synopsis = findViewById(R.id.tv_row_literature_contents);
        work = (WorkVO)getIntent().getSerializableExtra("WORK");

        title.setText(work.getStrTitle());
        synopsis.setText(work.getSynopsis());
        date.setText(work.getCreatedDate());

        String strCover = CommonUtils.strDefaultUrl + "images/" + work.getCoverFile();


        Glide.with(this)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster)
                .load(strCover)
                .into(cover);

    }
    public void onClickTopLeftBtn(View view) {
        finish();
    }

}