package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static com.Whowant.Tokki.Utils.CommonUtils.from;

public class WebNovelWriteActivity extends AppCompatActivity {

    private TextView mTextView;
    private String strTitle = "";
    private int nEpisodeID;
    private int nWorkID;

    private TextView titleView;



    private EditText content;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_novel_write);
        titleView = findViewById(R.id.episodeTitleView);

        content = findViewById(R.id.content);

        strTitle = getIntent().getStringExtra("EPISODE_TITLE");
        nWorkID = getIntent().getIntExtra("WORK_ID",0);
        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", -1);

        if(strTitle != null && strTitle.length() > 0)
        {
            titleView.setText(strTitle);
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
      //  oldIntent.putExtra("TITLE", strTitle);


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // Enables Always-on
      //  imm.showSoftInput(content, 0);
        imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
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
        //PanAppCreateNovelEpisode
        sendEpisodePost();
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

    private void sendEpisodePost() {
        mProgressDialog.setMessage("작품을 게시 중입니다.");
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //createNovelEpisode String workId,String content,String pages,String page)
                boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),content.getText().toString(),String.valueOf(1),String.valueOf(1));


                JSONObject resultObject = HttpClient.requestEpisodePost(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();

                            if (resultObject == null) {
                                Toast.makeText(WebNovelWriteActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                Toast.makeText(WebNovelWriteActivity.this, "게시되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(WebNovelWriteActivity.this, "게시에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

}