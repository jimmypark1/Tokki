package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class ProfileGenderPopup extends AppCompatActivity {
    private int nGender;
    private String strUserID;
    private ImageView maleCheck;
    private ImageView femaleCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gender_popup);

        strUserID = getIntent().getStringExtra("USER_ID");
        nGender = getIntent().getIntExtra("USER_GENDER", 0);

        maleCheck = findViewById(R.id.maleCheck);
        femaleCheck = findViewById(R.id.femaleCheck);

        resetViews();
    }

    private void resetViews() {
        if(nGender == 0) {
            maleCheck.setBackgroundResource(R.drawable.radiobutton_active);
            femaleCheck.setBackgroundResource(R.drawable.radiobutton);
        } else {
            maleCheck.setBackgroundResource(R.drawable.radiobutton);
            femaleCheck.setBackgroundResource(R.drawable.radiobutton_active);
        }
    }

    public void onClickCloseBtn(View veiw) {
        finish();
    }

    public void OnClickMaleBtn(View view) {
        nGender = 0;
        resetViews();
    }

    public void OnClickFemaleBtn(View view) {
        nGender = 1;
        resetViews();
    }

    public void onClickOKBtn(View view) {
        CommonUtils.showProgressDialog(ProfileGenderPopup.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), strUserID, "USER_GENDER", "" + nGender);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(ProfileGenderPopup.this, "성별 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("USER_GENDER", nGender);
                        editor.commit();

                        finish();
                    }
                });
            }
        }).start();
    }
}
