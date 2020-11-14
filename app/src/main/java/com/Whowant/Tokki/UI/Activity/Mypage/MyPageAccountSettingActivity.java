package com.Whowant.Tokki.UI.Activity.Mypage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;

public class MyPageAccountSettingActivity extends AppCompatActivity {

    ImageView levelIv;
    TextView levelTv;
    ImageView photoIv;
    ImageView bgIv;
    EditText nameEt;
    EditText fullNameEt;
    EditText genderEt;
    EditText birthEt;
    EditText phoneEt;
    EditText emailEt;

    private int nCurrentCarrot = 0;                                                         // 현재 당근 갯수
    private int nTotalUsedCarrot = 0;                                                       // 총 당근 갯수
    private int nDonationCarrot = 0;                                                        // 후원받은 당근 갯수
    private int nLevel = 1;                                                                 // 당근 갯수에 따라 레벨 결정

    int[] levelRes = new int[]{
            R.drawable.ic_i_level_1, R.drawable.ic_i_level_2, R.drawable.ic_i_level_3, R.drawable.ic_i_level_4, R.drawable.ic_i_level_5,
            R.drawable.ic_i_level_6, R.drawable.ic_i_level_7, R.drawable.ic_i_level_8, R.drawable.ic_i_level_9, R.drawable.ic_i_level_10
    };

    String[] levelName = new String[]{
            "LV.1", "LV.2", "LV.3", "LV.4", "LV.5", "LV.6", "LV.7", "LV.8", "LV.9", "LV.10",
    };

    Activity mActivity;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String profileUri = intent.getStringExtra("URI");
        String bgUri = intent.getStringExtra("BG_URI");

        if (profileUri != null) {
//            photoIv.setBackgroundResource(0);
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(Uri.parse(profileUri))
                    .apply(new RequestOptions().circleCrop())
                    .into(photoIv);
        }
        if (bgUri != null) {
//            bgIv.setBackgroundResource(0);
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(Uri.parse(bgUri))
                    .into(bgIv);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_account_setting);

        mActivity = this;

        initView();
        initData();

        getMyCarrotInfo();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("계정 설정");

        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());
        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);

        levelIv = findViewById(R.id.iv_my_page_account_setting_level);
        levelTv = findViewById(R.id.tv_my_page_account_setting_level);
        photoIv = findViewById(R.id.iv_my_page_account_setting_photo);
        photoIv.setClipToOutline(true);
        bgIv = findViewById(R.id.iv_my_page_account_setting_bg);
        bgIv.setClipToOutline(true);
        nameEt = findViewById(R.id.et_my_page_account_setting_name);
        fullNameEt = findViewById(R.id.et_my_page_account_setting_full_name);
        genderEt = findViewById(R.id.et_my_page_account_setting_gender);
        birthEt = findViewById(R.id.et_my_page_account_setting_birth);
        phoneEt = findViewById(R.id.et_my_page_account_setting_phone);
        emailEt = findViewById(R.id.et_my_page_account_setting_email);
    }

    private void initData() {
        // 마이 페이지 화면 갱신
        String photoUrl = SimplePreference.getStringPreference(this, "USER_INFO", "USER_PHOTO", "");

        if (!TextUtils.isEmpty(photoUrl)) {
            if (!photoUrl.startsWith("http"))
                photoUrl = CommonUtils.strDefaultUrl + "images/" + photoUrl;

            Glide.with(mActivity)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(photoUrl)
                    .placeholder(R.drawable.user_icon)
                    .apply(new RequestOptions().circleCrop())
                    .into(photoIv);
        }

        nameEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_NAME", ""));
        genderEt.setText(SimplePreference.getIntegerPreference(this, "USER_INFO", "USER_GENDER", -1) == 0 ? "남성" : "여성");
        birthEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_BIRTHDAY", ""));
        phoneEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_PHONENUM", ""));
        emailEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_EMAIL", ""));
    }

    // 프로필 변경 버튼
    public void btnProfilePhoto(View v) {
        TedPermission.with(mActivity)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(mActivity, MediaSelectPopup.class);
                        intent.putExtra("TYPE", PhotoPickerActivity.TYPE_SETTING_PROFILE);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(mActivity, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 배경화면 변경 버튼
    public void btnProfileBg(View v) {
        TedPermission.with(mActivity)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(mActivity, MediaSelectPopup.class);
                        intent.putExtra("TYPE", PhotoPickerActivity.TYPE_SETTING_BG);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(mActivity, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 저장
    public void btnSave(View v) {
        Toast.makeText(this, "저장", Toast.LENGTH_SHORT).show();
    }

    private void getMyCarrotInfo() {
        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getMyCarrotInfo(new OkHttpClient(), userId);
                CommonUtils.hideProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        try {
                            if (resultObject == null) {
                                return;
                            }

                            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                nCurrentCarrot = resultObject.getInt("CURRENT_CARROT");
//                                carrotTv.setText(resultObject.getString("CURRENT_CARROT") + " 개");
//                                nTotalUsedCarrot = resultObject.getInt("USED_CARROT");
                                nDonationCarrot = resultObject.getInt("DONATION_CARROT");
//                                nTotalAcquireCarrot = resultObject.getInt("ACQUIRE_CARROT");
//                                strRecommendCode = resultObject.getString("RECOMMEND_CODE");
//                                currentCarrotCountView.setText(CommonUtils.comma(nCurrentCarrot));
//                                cumulativeUsageCarrotCountView.setText(CommonUtils.comma(nTotalUsedCarrot));
//                                totalCarrotCountView.setText(CommonUtils.comma(nTotalAcquireCarrot));
                            }

                            nLevel = CommonUtils.getLevel(nDonationCarrot);
                            levelIv.setImageResource(levelRes[nLevel - 1]);
                            levelTv.setText(levelName[nLevel - 1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}