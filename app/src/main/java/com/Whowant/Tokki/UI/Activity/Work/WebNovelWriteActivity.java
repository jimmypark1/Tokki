package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Popup.ChangeTitlePopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ExcelReader;
import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static com.Whowant.Tokki.Utils.CommonUtils.from;

public class WebNovelWriteActivity extends AppCompatActivity {

    private TextView mTextView;
    private String strTitle = "";
    private int nEpisodeID;
    private TextView titleView;

    private EditText content;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_novel_write);
        titleView = findViewById(R.id.episodeTitleView);

        strTitle = getIntent().getStringExtra("EPISODE_TITLE");
        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", -1);

        if(strTitle != null && strTitle.length() > 0)
        {
            titleView.setText(strTitle);
        }
      //  oldIntent.putExtra("TITLE", strTitle);


        // Enables Always-on

    }
    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void clickEditTitleBtn(View view) {
        Intent intent = new Intent(WebNovelWriteActivity.this, ChangeTitlePopup.class);
        intent.putExtra("TITLE", strTitle);
        intent.putExtra("EPISODE_ID", nEpisodeID);
        startActivityForResult(intent, 1100);
    }
    public void onClickSubmitBtn(View view) {
        /*
        if (isExcelUploaded || workVO.getnUserStatus() == 10 || workVO.getnUserStatus() == 20) {
            requestEpisodeSubmit();
        } else {
            requestEpisodePost();
        }

         */
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                                 // 이미지 설정 등의 데이터가 거쳐감
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1100) {                    // 제목 변경
                strTitle = data.getStringExtra("TITLE");
                titleView.setText(strTitle);
            }
        }

    }

}