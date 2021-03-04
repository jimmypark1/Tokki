package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.Whowant.Tokki.VO.WebWorkVO;
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
    TextView page;

    private ProgressDialog mProgressDialog;
    int nPage = 0;
    int readPageCnt = 0;

    ArrayList<WebWorkVO> novels = new ArrayList<WebWorkVO>();
    ArrayList<WebWorkVO> publishContent = new ArrayList<WebWorkVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_novel_write);
        titleView = findViewById(R.id.episodeTitleView);
        page = findViewById(R.id.page);

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
     //   content.setImeOptions(EditorInfo.IME_ACTION_DONE);

        content.setImeOptions(EditorInfo.IME_ACTION_DONE);
        content.setRawInputType(InputType.TYPE_CLASS_TEXT);


        getEpisodeData();
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

    public void onClickPrev(View view) {

        nPage = nPage - 1;
        if(nPage < 0)
        {
            nPage = 0;
        }
        page.setText(String.valueOf(nPage+1));

        if(novels.size() > 0)
        {
            WebWorkVO work =  novels.get(nPage);
            /*
            if(content.getText().length() > 0)
            {
                WebWorkVO work0 = new WebWorkVO();
                work0.setRaw(content.getText().toString());
                work0.setContent(content.getText().toString());

                novels.add(work0);
                readPageCnt = novels.size();
            }

             */
            content.setText(work.getRaw());
        }
        if(publishContent.size() > 0)
        {
            if(nPage <= publishContent.size()) {
                publishContent.remove(nPage);
            }
        }

    }
    public void onClickNext(View view) {





        WebWorkVO work = new WebWorkVO();
        work.setRaw(content.getText().toString());
        work.setContent(content.getText().toString());


        nPage++;

        publishContent.add(work);

        if(novels.size() > 0)
        {
            if(nPage < novels.size())
            {
                WebWorkVO work0 = novels.get(nPage);
                content.setText(work0.getRaw());

            }
            else
            {
                content.setText("");

            }

        }
        else
        {
            content.setText("");

        }
        page.setText(String.valueOf(nPage+1));

        /*
        if(nPage < readPageCnt)
        {
            WebWorkVO work = novels.get(nPage);
            content.setText(work.getRaw());

        }
        else if(nPage == readPageCnt)
        {
            content.setText("");

        }
        else
        {

            WebWorkVO work = new WebWorkVO();
            work.setRaw(content.getText().toString());
            work.setContent(content.getText().toString());

            novels.add(work);
            readPageCnt = novels.size();
            content.setText("");

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

    void getEpisodeData()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //createNovelEpisode String workId,String content,String pages,String page)
                novels = HttpClient.getEpisodeNovelEditData(new OkHttpClient(),nEpisodeID);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(novels.size() > 0)
                        {
                            readPageCnt = novels.size();

                            WebWorkVO work = novels.get(0);
                            content.setText(work.getRaw());
                            content.setSelection(work.getRaw().length() );
                        }
                    }
                });
            }
        }).start();
    }

    private void sendEpisodePost() {
        mProgressDialog.setMessage("작품을 게시 중입니다.");
        mProgressDialog.show();


        if(content.getText().length() > 0)
        {
            WebWorkVO work = new WebWorkVO();
            work.setRaw(content.getText().toString());
            work.setContent(content.getText().toString());

            publishContent.add(work);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                //createNovelEpisode String workId,String content,String pages,String page)

                for(int i=0;i<novels.size();i++)
                {
                    boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),publishContent.get(i).getRaw(),String.valueOf(novels.size()),String.valueOf(i+1));

                    while(ret == false)
                    {

                    }
                }
            //    boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),content.getText().toString(),String.valueOf(1),String.valueOf(1));


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