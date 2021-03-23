package com.Whowant.Tokki.SNS.Facebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.AgreementActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;

public class FacebookSignupActivity extends AppCompatActivity implements GetUserCallback.IGetUserResponse {
    private CallbackManager callbackManager;            // facebook

    private String strEmail;
    private String strNickName;
    private String strSNSID;
    private String strPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_signup);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                UserRequest.makeUserRequest(new GetUserCallback(FacebookSignupActivity.this).getCallback());
            }

            @Override
            public void onCancel() {
                Log.d("facebook", "cancel");
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookSignupActivity.this, "페이스북 로그인이 실패하였습니다.", Toast.LENGTH_LONG).show();
                finish();
                Log.d("facebook", "error");
            }
        });

        ArrayList<String> list = new ArrayList<>();
        list.add("public_profile");
        list.add("email");
        LoginManager.getInstance().logInWithReadPermissions(this, list);
    }

    @Override
    public void onCompleted(User user) {
        strEmail = user.getEmail();
        strNickName = user.getName();
        strSNSID = user.getId();
        strPhoto = user.getPicture().toString();

        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        HttpClient.checkSocialLogin(FacebookSignupActivity.this, strSNSID, pref.getString("FCM_TOKEN", ""));
//        private String strEmail;
//        private String strNickName;
//        private String strSNSID;
//        private String strPhoto;
//        UserRequest.makeUserRequest(new GetUserCallback(FacebookSignupActivity.this).getCallback());
//        sendLoginByFacebook(user.getId(), user.getEmail(), user.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(FacebookSignupActivity.this, AgreementActivity.class);
                intent.putExtra("USER_EMAIL", strEmail);
                intent.putExtra("SNS_ID", strSNSID);
                intent.putExtra("USER_NAME", strNickName);
                intent.putExtra("USER_PHOTO", strPhoto);
                intent.putExtra("SNS", 4);


                final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();

                editor.putInt("SNS_TYPE", 4);

                editor.commit();
                startActivity(intent);
            } else {
                finish();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
