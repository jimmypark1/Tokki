package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Fragment.Friend.MessageDetailActivity;
import com.Whowant.Tokki.UI.Popup.EditMyCommentPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MessageVO;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class MainCompletePopup extends AppCompatActivity {

    Intent oldIntent;
    MessageVO message;
    Boolean downComplete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_complete_popup);

        oldIntent = getIntent();

        message = (MessageVO)getIntent().getSerializableExtra("MESSAGE_DATA");
    }

    public void onClickCloseBtn(View view) {

        finish();
    }
    public void onClickOKBtn(View view) {

        if(downComplete == true)
        {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = SimplePreference.getStringPreference(MainCompletePopup.this, "USER_INFO", "USER_ID", "Guest");

                    JSONObject resultObject = HttpClient.getUserInfo(new OkHttpClient(), userId);
                    // JSONObject resultObject = HttpClient.getMyInfo(new OkHttpClient(), userId);
                    try {
                        if (resultObject == null) {
                            return;
                        }
                        int nMyCarrot = resultObject.getInt("CARROT_ACCUMULATION");
                        int nCarrot = message.getCarrot();
                        // nMyCarrot = 3000;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(nCarrot <= nMyCarrot)
                                {
                                    oldIntent.putExtra("MESSAGE_END","결제가 완료되었습니다.\n");
                                    oldIntent.putExtra("CARROT_END",message.getCarrot());
                                    setResult(RESULT_OK, oldIntent);

                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(MainCompletePopup.this, "당근이 부족합니다.충전하시고 결제하세요", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                           // YourActivity.this.finish();
                                            //TransactionActivity
                                            Intent intent = new Intent(MainCompletePopup.this, TransactionActivity.class);

                                            intent.putExtra("INTO_MSG_INFO",1);
                                            startActivity(intent);
                                        }
                                    }, 1000);
                                }
                            }
                        });



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


        }
        else
        {
            Toast.makeText(MainCompletePopup.this, "계약서 다운로드를 먼저 눌러주세요.", Toast.LENGTH_SHORT).show();


        }

    }
    public void onClickDownBtn(View view) {


       String email = SimplePreference.getStringPreference(this, "USER_INFO", "USER_EMAIL", "");



       if(email == null || email.length() == 0)
       {
           Toast.makeText(MainCompletePopup.this, "등록된 메일이 없습니다. 설정에서 메일을 등록하셔야 메일로 계약서를 보내드립니다.", Toast.LENGTH_SHORT).show();
           return;

       }
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ret = HttpClient.requestMarketSendMail(new OkHttpClient(),email);



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(ret == true)
                        {
                            downComplete = true;
                            Toast.makeText(MainCompletePopup.this, "계약서 파일을 등록된 메일로 보내드렸습니다.", Toast.LENGTH_SHORT).show();

                        }

                        //    getMyFollowingList();
                    }
                });
            }
        }).start();
    }

}