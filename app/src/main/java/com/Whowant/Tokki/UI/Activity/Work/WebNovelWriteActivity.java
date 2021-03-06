package com.Whowant.Tokki.UI.Activity.Work;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Admin.AproveWaitingEpisodeListActivity;
import com.Whowant.Tokki.UI.Popup.ChangeTitlePopup;
import com.Whowant.Tokki.UI.Popup.EpisodeAproveCancelPopup;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ExcelReader;
import com.Whowant.Tokki.VO.CharacterVO;
import com.Whowant.Tokki.VO.ChatVO;
import com.Whowant.Tokki.VO.WebWorkVO;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import static com.Whowant.Tokki.Utils.CommonUtils.from;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_SPACE_IMG;

public class WebNovelWriteActivity extends AppCompatActivity {

    private TextView mTextView;
    private String strTitle = "";
    private int nEpisodeID;
    private int nWorkID;

    private TextView titleView;


    TextView episodeNumView;

    private EditText content;
    TextView page;

    private ProgressDialog mProgressDialog;
    int nPage = 0;
    int readPageCnt = 0;
    int nEpisodeOrder = 0;

    Boolean bPublish = false;
    Button moreBt;

    ArrayList<WebWorkVO> novels = new ArrayList<WebWorkVO>();
    ArrayList<WebWorkVO> publishContent = new ArrayList<WebWorkVO>();

    ArrayList<String> tempContent = new ArrayList<String>();
    RelativeLayout editer;


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View v =  activity.getCurrentFocus();
        if(v != null)
        {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(WebNovelWriteActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    void initVariable()
    {

    }

    void chaeckPermission()
    {
        TedPermission.with(WebNovelWriteActivity.this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(WebNovelWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    }
                })
                .setPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_novel_write);
        titleView = findViewById(R.id.episodeTitleView);
        page = findViewById(R.id.page);
        episodeNumView = findViewById(R.id.episodeNumView);
        moreBt = findViewById(R.id.moreBtn);
        editer =  findViewById(R.id.editer);
        content = findViewById(R.id.content);

        strTitle = getIntent().getStringExtra("EPISODE_TITLE");
        nWorkID = getIntent().getIntExtra("WORK_ID",0);
        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", -1);
        nEpisodeOrder = getIntent().getIntExtra("EPISODE_ORDER", 0);


        initVariable();

        if(strTitle != null && strTitle.length() > 0)
        {
            titleView.setText(strTitle);
        }
        episodeNumView.setText( nEpisodeOrder+ "???");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        //  oldIntent.putExtra("TITLE", strTitle);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // Enables Always-on
        //  imm.showSoftInput(content, 0);
        //   imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
        //   content.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //content.setImeOptions(EditorInfo.IME_ACTION_DONE);
        content.setRawInputType(InputType.TYPE_CLASS_TEXT);




        getEpisodeData();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupUI(editer);

            }
        });
        chaeckPermission();
    }


    @Override
    protected void onResume() {
        super.onResume();



    }
    void save()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(content.getText().length() > 0  )
                {
                    WebWorkVO work = new WebWorkVO();
                    work.setRaw(content.getText().toString());
                    work.setContent(content.getText().toString());

                    publishContent.set(nPage, work);

                    //    publishContent.add(work);
                }
                for(int i=0;i<publishContent.size();i++) {
                    String data = publishContent.get(i).getRaw();
                    if (data.equals("-1") == false) {
                        String content = publishContent.get(i).getRaw().replace("\n", "<br>");

                        boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(), String.valueOf(nEpisodeID), String.valueOf(nWorkID), content, String.valueOf(nPage), String.valueOf(i + 1));

                        //   while(ret == false)
                        {

                        }
                    } else {
                        break;
                    }
                }
                finish();

            }
        }).start();
    }
    void save0()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(content.getText().length() > 0  )
                {
                    WebWorkVO work = new WebWorkVO();
                    work.setRaw(content.getText().toString());
                    work.setContent(content.getText().toString());

                    publishContent.set(nPage, work);

                    //    publishContent.add(work);
                }
                for(int i=0;i<publishContent.size();i++) {
                    String data = publishContent.get(i).getRaw();
                    if (data.equals("-1") == false) {
                        String content = publishContent.get(i).getRaw().replace("\n", "<br>");

                        boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(), String.valueOf(nEpisodeID), String.valueOf(nWorkID), content, String.valueOf(nPage), String.valueOf(i + 1));

                        //   while(ret == false)
                        {

                        }
                    } else {
                        break;
                    }
                }


            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {

        initVariable();

        if(bPublish == false)
        {


            save();
        }




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

    private void requestDeleteEpisode(final int nEpisodeID) {
       // CommonUtils.showProgressDialog(WebNovelWriteActivity.this, "?????? ????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strResult = HttpClient.requestDeleteWebNovelEpisode(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      //  CommonUtils.hideProgressDialog();

                        if (strResult.equals("SUCCESS")) {
                            Toast.makeText(WebNovelWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            getEpisodeData();
                           // finish();
                        }
                         else {

                            content.setText("");
                            nPage = 0;
                            page.setText(String.valueOf(nPage+1));
                        //    publishContent.clear();
                            getEpisodeData();

                            Toast.makeText(WebNovelWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();


                            //   Toast.makeText(WebNovelWriteActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
    public void onClickMoreBtn(View view) {
        PopupMenu popup = new PopupMenu(WebNovelWriteActivity.this, moreBt);


         popup.getMenuInflater().inflate(R.menu.web_more_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {


                switch(item.getItemId()) {
                    case R.id.preview:
                        Intent intent = new Intent(WebNovelWriteActivity.this, WebWorkViewerActivity.class);
                     //   ViewerActivity.workVO = workVO;
                        //  intent.putExtra("WORK", (Serializable) workVO);
                       // ViewerActivity.workVO;
                        save0();
                        int index = ViewerActivity.workVO.getEpisodeList().size() - nEpisodeOrder ;
                        intent.putExtra("EPISODE_INDEX", index);
                        intent.putExtra("EPISODE_SORT", false);
                        intent.putExtra("PREVIEW", true);

                        intent.putExtra("LAST_ORDER", 0);
                        startActivity(intent);
                        break;
                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(WebNovelWriteActivity.this);
                        builder.setMessage("????????? ????????? ?????? ?????????????????????????");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                requestDeleteEpisode(nEpisodeID);
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;

                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    public void onClickPrev(View view) {

        nPage = nPage - 1;
        if(nPage < 0)
        {
            nPage = 0;
        }
        /*
        if(nPage < publishContent.size())
        {
          //  publishContent.remove(nPage);
            String data0 = content.getText().toString();
            if(data0.length() > 0)
            {
                WebWorkVO work0 = new WebWorkVO();
                work0.setRaw(data0);
                work0.setContent(content.getText().toString());
                publishContent.set(nPage+1, work0);
            }
            WebWorkVO work =  publishContent.get(nPage);
            String data = work.getRaw();
            data = data.replace("<br>","\n");
            content.setText(data);
        }
         */

      //  WebWorkVO workPrev =  publishContent.get(nPage + 1);

        if(content.getText().toString().length()> 0)
        {
            WebWorkVO work = new WebWorkVO();
            work.setRaw(content.getText().toString());
            work.setContent(content.getText().toString());

            publishContent.set(nPage+1, work);

            //    publishContent.add(work);
        }

        WebWorkVO work0 =  publishContent.get(nPage);
        if(work0 != null && work0.getRaw().equals("-1") == false)
        {
            String content0= work0.getRaw().replace("<br>", "\n");

            content.setText(content0);

        }


        page.setText(String.valueOf(nPage+1));
/*
        if(novels.size() > 0)
        {
            WebWorkVO work =  novels.get(nPage);
            content.setText(work.getRaw());
        }
        if(publishContent.size() > 0)
        {
            if(nPage <= publishContent.size()) {
                publishContent.remove(nPage);
            }
        }
 */

    }
    public void onClickNext(View view) {




        if(content.getText().toString().length() == 0)
        {
            Toast.makeText(WebNovelWriteActivity.this, "?????? ??????????????? ?????????????????? ????????????.", Toast.LENGTH_LONG).show();

            return;
        }




        WebWorkVO work0 =  publishContent.get(nPage + 1);
        if(work0 != null && work0.getRaw().equals("-1") == false)
        {

            WebWorkVO work = new WebWorkVO();
            String data1 = content.getText().toString();
            work.setRaw(data1);
            //  work.setContent(data1);
            publishContent.set(nPage, work);
            String content0= work0.getRaw().replace("<br>", "\n");

            content.setText(content0);
             /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //createNovelEpisode String workId,String content,String pages,String page)
                    String content= publishContent.get(nPage + 1).getRaw().replace("\n", "<br>");
               //     boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),content,String.valueOf(publishContent.size()),String.valueOf(nPage + 2));
                }
            }).start();
              */
        }
        else
        {
            WebWorkVO work = new WebWorkVO();
            String data1 = content.getText().toString();
            work.setRaw(data1);
            //  work.setContent(data1);
            publishContent.set(nPage, work);
/*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //createNovelEpisode String workId,String content,String pages,String page)
                    String content= publishContent.get(nPage).getRaw().replace("\n", "<br>");
                //    boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),content,String.valueOf(nPage),String.valueOf(nPage));
                }
            }).start();
 */
            content.setText("");

        }
        nPage++;

        page.setText(String.valueOf(nPage+1));

/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                //createNovelEpisode String workId,String content,String pages,String page)
                boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),content,String.valueOf(publishContent.size()),String.valueOf(i+1));
            }
        }).start();
 */

        /*
        WebWorkVO work0 =  publishContent.get(nPage);
        if(work0 != null)
        {
            String data = work0.getRaw();
            if(data.equals("-1") == false)
            {
                data = data.replace("<br>","\n");
                content.setText(data);
            }
            else
            {
                WebWorkVO work = new WebWorkVO();
                String data1 = content.getText().toString();
                work.setRaw(data1);
                work.setContent(data1);
                publishContent.set(nPage-1, work);
                // publishContent.add(work);
                content.setText("");
            }
        }
         */
        /*
        if(nPage < publishContent.size())
        {
            //  publishContent.remove(nPage);
            WebWorkVO work0 =  publishContent.get(nPage);
            if(work0 != null)
            {
                String data = work0.getRaw();
                if(data.equals("-1") == false)
                {
                    data = data.replace("<br>","\n");
                    content.setText(data);
                }
            }
        }
        else
        {
            WebWorkVO work = new WebWorkVO();
            String data = content.getText().toString();
            work.setRaw(data);
            work.setContent(content.getText().toString());
            publishContent.set(nPage-1, work);
            // publishContent.add(work);
            content.setText("");
        }
*/



/*
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
 */

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                                 // ????????? ?????? ?????? ???????????? ?????????
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1100) {                    // ?????? ??????
                strTitle = data.getStringExtra("TITLE");
                titleView.setText(strTitle);
            }
        }

    }

    void getEpisodeData()
    {
        if(publishContent != null)
        {
            publishContent.clear();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //createNovelEpisode String workId,String content,String pages,String page)
                publishContent = HttpClient.getEpisodeNovelEditData(new OkHttpClient(),nEpisodeID);

                int start = publishContent.size();
                for(int i=start - 1;i<1000;i++)
                {
                    WebWorkVO work = new WebWorkVO();
                    String data = "-1";
                    work.setRaw(data);
                    work.setContent(data);
                    publishContent.add(work);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(publishContent.size() > 0 && start != 0)
                        {

                            WebWorkVO work = publishContent.get(0);
                            String data = work.getRaw();
                            if(data.equals("-1") == false)
                            {
                                data = data.replace("<br>","\n");
                                content.setText(data);
                                content.setSelection(data.length() );
                            }

                        }
                        else
                        {
                            if(start == 0)
                            {
                                content.setText("");

                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void sendEpisodePost() {
        mProgressDialog.setMessage("????????? ?????? ????????????.");
        mProgressDialog.show();


        if(content.getText().length() > 0  )
        {
            WebWorkVO work = new WebWorkVO();
            work.setRaw(content.getText().toString());
            work.setContent(content.getText().toString());

            publishContent.set(nPage, work);

            //    publishContent.add(work);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                //createNovelEpisode String workId,String content,String pages,String page)

                for(int i=0;i<publishContent.size();i++)
                {
                    String data = publishContent.get(i).getRaw();
                    if(data.equals("-1") == false)
                    {
                        String content= publishContent.get(i).getRaw().replace("\n", "<br>");

                        boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),content,String.valueOf(nPage),String.valueOf(i+1));

                        //   while(ret == false)
                        {

                        }
                    }
                    else
                    {
                        break;
                    }


                }


/*
                if(nPage == 0 || nPage == (publishContent.size() - 1 ))
                {
                    String temp =  content.getText().toString();
                    if(temp.length() > 0)
                    {
                        boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),temp,String.valueOf(publishContent.size()+1),String.valueOf(nPage + 1));
                        while(ret == false)
                        {
                        }
                    }
                }
 */

                //    boolean ret = HttpClient.createNovelEpisode(new OkHttpClient(),String.valueOf(nEpisodeID), String.valueOf(nWorkID),content.getText().toString(),String.valueOf(1),String.valueOf(1));


                JSONObject resultObject = HttpClient.requestEpisodePost(new OkHttpClient(), nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();

                            if (resultObject == null) {
                                Toast.makeText(WebNovelWriteActivity.this, "???????????? ????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                Toast.makeText(WebNovelWriteActivity.this, "?????????????????????.", Toast.LENGTH_LONG).show();
                                bPublish = true;
                                finish();
                            } else {
                                Toast.makeText(WebNovelWriteActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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