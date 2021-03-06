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
import android.widget.TextView;
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

    private TextView desc0;
    private TextView desc1;

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
        desc0 = findViewById(R.id.desc0);
        desc1 = findViewById(R.id.desc1);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setEnabled(false);

        String enableDescColor = "#6c8fff";
        String disableDescColor = "#ff5f0a";

        String enableBorderColor = "#6c8fff";
        String disableBorderColor = "#ff5800";


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
                    String passWord = inputPWView.getText().toString().trim();
                    String passWord1 = inputPWCheckView.getText().toString().trim();

                    if(passWord.equals(passWord1) && passWord.length() > 0 && passWord1.length() > 0)
                    {
                        desc0.setTextColor(Color.parseColor(enableDescColor));
                        desc1.setTextColor(Color.parseColor(enableDescColor));

                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_bg);
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_stroke_bg);

                        desc0.setText("???????????????");
                        desc1.setText("???????????????");

                    }
                    else
                    {
                        desc0.setTextColor(Color.parseColor(disableDescColor));
                        desc1.setTextColor(Color.parseColor(disableDescColor));

                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_dis_bg);
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_dis_stroke_bg);

                        desc0.setText("*??????, ????????? ????????? 9-15??? ??????");
                        desc1.setText("*??????????????? ?????? ????????????");

                    }
                    if (!passWord.matches(pwValidation)) {
                        desc0.setTextColor(Color.parseColor(disableDescColor));
                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_dis_bg);

                    }
                    else
                    {
                        desc0.setTextColor(Color.parseColor(enableDescColor));
                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_bg);

                    }
                    if (!passWord1.matches(pwValidation)) {
                        desc1.setTextColor(Color.parseColor(disableDescColor));
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_dis_stroke_bg);
                    }
                    else
                    {
                        desc1.setTextColor(Color.parseColor(enableDescColor));
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_stroke_bg);

                    }

                } else {
                    String passWord = inputPWView.getText().toString().trim();
                    String passWord1 = inputPWCheckView.getText().toString().trim();

                    if(passWord.equals(passWord1) && passWord.length() > 0 && passWord1.length() > 0)
                    {
                        desc0.setTextColor(Color.parseColor(enableDescColor));
                        desc1.setTextColor(Color.parseColor(enableDescColor));

                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_bg);
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_stroke_bg);

                        desc0.setText("???????????????");
                        desc1.setText("???????????????");

                    }
                    else
                    {
                        desc0.setTextColor(Color.parseColor(disableDescColor));
                        desc1.setTextColor(Color.parseColor(disableDescColor));

                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_dis_bg);
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_dis_stroke_bg);

                        desc0.setText("*??????, ????????? ????????? 9-15??? ??????");
                        desc1.setText("*??????????????? ?????? ????????????");

                    }
                    if (!passWord.matches(pwValidation)) {
                        desc0.setTextColor(Color.parseColor(disableDescColor));
                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_dis_bg);

                    }
                    else
                    {
                        desc0.setTextColor(Color.parseColor(enableDescColor));
                        inputPWView.setBackgroundResource(R.drawable.round_pw_stroke_bg);

                    }
                    if (!passWord1.matches(pwValidation)) {
                        desc1.setTextColor(Color.parseColor(disableDescColor));
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_dis_stroke_bg);
                    }
                    else
                    {
                        desc1.setTextColor(Color.parseColor(enableDescColor));
                        inputPWCheckView.setBackgroundResource(R.drawable.round_pwcheck_stroke_bg);

                    }
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
            Toast.makeText(this, "??????????????? ?????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        String passWord = inputPWView.getText().toString().trim();
        if (!passWord.matches(pwValidation)) {
            Toast.makeText(PWRegisterActivity.this, "????????? ???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PWRegisterActivity.this, "??????????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                Toast.makeText(PWRegisterActivity.this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }
            }).start();



        }

    }
}