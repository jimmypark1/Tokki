package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WebNovelWriteActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class PWRegisterActivity extends AppCompatActivity {

    private EditText inputPWView;
    private EditText inputPWCheckView;
    private Button nextBtn;
    private String strID;
    private String strEmail;
    private String strSNSID;
    private String strNickName;
    private String strPhoto;
    private int    nSNS;
    int nAuthType = 0;

    private String pwValidation = "^.*(?=^.{9,15}$)(?=.*[0-9])(?=.*[a-zA-Z]).*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_w_register);

        nAuthType = getIntent().getIntExtra("AUTH_TYPE",0);

        strID = getIntent().getStringExtra("ID");
        strEmail = getIntent().getStringExtra("USER_EMAIL");
        strSNSID = getIntent().getStringExtra("SNS_ID");
        strNickName = getIntent().getStringExtra("USER_NAME");
        strPhoto = getIntent().getStringExtra("USER_PHOTO");
        nSNS = getIntent().getIntExtra("SNS", 0);

        inputPWView = findViewById(R.id.inputPWView);
        inputPWCheckView = findViewById(R.id.inputPWCheckView);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputPWView.getText().toString().length() > 0 && inputPWCheckView.getText().toString().length() > 0) {
                    nextBtn.setEnabled(true);
                    nextBtn.setBackgroundResource(R.drawable.common_btn_bg);
                    nextBtn.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    nextBtn.setEnabled(false);
                    nextBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                    nextBtn.setTextColor(Color.parseColor("#999999"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        inputPWView.addTextChangedListener(textWatcher);
        inputPWCheckView.addTextChangedListener(textWatcher);
    }

    public void onClickBackBtn(View view) {
        finish();
    }

    public void onClickNextBtn(View view) {
        String strPW = inputPWView.getText().toString();
        String strPW2 = inputPWCheckView.getText().toString();

        if(!strPW.equals(strPW2)) {
            Toast.makeText(this, "비밀번호가 서로 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String passWord = inputPWView.getText().toString().trim();
        if (!passWord.matches(pwValidation)) {
            Toast.makeText(PWRegisterActivity.this, "올바른 비밀번호 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(nAuthType == 0)
        {
            Intent intent = new Intent(PWRegisterActivity.this, PersonalInfoActivity.class);
            intent.putExtra("ID", strID);
            intent.putExtra("PASSWORD", strPW);
            intent.putExtra("SNS", nSNS);
            intent.putExtra("SNS_ID", strSNSID);
            intent.putExtra("USER_NAME", strNickName);
            intent.putExtra("USER_EMAIL", strEmail);
            intent.putExtra("USER_PHOTO", strPhoto);
            startActivity(intent);
        }
        else
        {
            //ChangePW
            //changePW

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //createNovelEpisode String workId,String content,String pages,String page)
                    boolean ret = HttpClient.changePW(new OkHttpClient(),strID, passWord);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(ret == true)
                            {
                                Intent intent = new Intent(PWRegisterActivity.this, PanbookLoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(PWRegisterActivity.this, "비밀번호가 재설정 되었습니다.", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                Toast.makeText(PWRegisterActivity.this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }
            }).start();



        }

    }
}