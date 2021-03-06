package com.Whowant.Tokki.UI.Activity.Mypage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.EmailAuthActivity;
import com.Whowant.Tokki.UI.Activity.Main.UserProfileActivity;
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Custom.MyDatePickerDialogFragment;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.UI.Popup.ProfileBirthdayPopup;
import com.Whowant.Tokki.UI.Popup.ProfileEmailPopup;
import com.Whowant.Tokki.UI.Popup.ProfileGenderPopup;
import com.Whowant.Tokki.UI.Popup.ProfileNamePopup;
import com.Whowant.Tokki.UI.Popup.ProfilePhotoPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.Utils.cropper.CropImage;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.Whowant.Tokki.VO.UserInfoVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.UserInfo;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_BG;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_PROFILE;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_PROFILE_BG;

public class MyPageAccountSettingActivity extends AppCompatActivity {

  //  ImageView levelIv;
  //  TextView levelTv;
    ImageView photoIv;
    ImageView bgIv;
    TextView nameEt;
    TextView fullNameEt;
    TextView genderEt;
    TextView birthEt;
    TextView phoneEt;
    TextView emailEt;
    EditText introduction;

    LinearLayout top;

    private String strUserBirthday;
    private String strNewBirthday;

    private int nCurrentCarrot = 0;                                                         // 현재 당근 갯수
    private int nTotalUsedCarrot = 0;                                                       // 총 당근 갯수
    private int nDonationCarrot = 0;                                                        // 후원받은 당근 갯수
    private int nLevel = 1;                                                                 // 당근 갯수에 따라 레벨 결정


    UserInfoVO userInfo = new UserInfoVO();

    int[] levelRes = new int[]{
            R.drawable.ic_i_level_1, R.drawable.ic_i_level_2, R.drawable.ic_i_level_3, R.drawable.ic_i_level_4, R.drawable.ic_i_level_5,
            R.drawable.ic_i_level_6, R.drawable.ic_i_level_7, R.drawable.ic_i_level_8, R.drawable.ic_i_level_9, R.drawable.ic_i_level_10
    };

    String[] levelName = new String[]{
            "LV.1", "LV.2", "LV.3", "LV.4", "LV.5", "LV.6", "LV.7", "LV.8", "LV.9", "LV.10",
    };

    private static final int FROM_POPUP = 1000;
    private static final int FROM_EMAIL_AUTH = 1010;
    private static final int FROM_CAMERA = 1020;
    private boolean bCamera = false;
    private SharedPreferences pref;
    private Uri mPhotoUri;
    private Uri mBackgroundUri;

    Activity mActivity;

    ImageView checkBtn1;
    ImageView checkBtn2;
    ImageView checkBtn3;

    ImageView menCk;
    ImageView womanCk;


    int nUserType = 0;

    String strPhotoPath = "";
    String strBackgroundPath = "";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String profileUri = intent.getStringExtra("URI");
        String bgUri = intent.getStringExtra("BG_URI");

        if (profileUri != null) {
//            photoIv.setBackgroundResource(0);


            mPhotoUri = Uri.parse(profileUri);
            strPhotoPath = CommonUtils.getRealPathFromURI(this, mPhotoUri);
           requestSendPhoto(strPhotoPath);
        }
        if (bgUri != null) {
            mBackgroundUri = Uri.parse(bgUri);
//            bgIv.setBackgroundResource(0);




            strBackgroundPath = CommonUtils.getRealPathFromURI(this, mBackgroundUri);

            requestSendBackgroundPhoto(strBackgroundPath);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bCamera) {
            takePhoto();
        } else {
            getMyCarrotInfo();
        }
      //  InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      //  imm.hideSoftInputFromWindow(introduction.getWindowToken(), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        introduction.clearFocus();

        emailEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_EMAIL", ""));

    }
    void initLayout()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ViewGroup.LayoutParams params = top.getLayoutParams();
        params.width = width;
        params.height = width;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_account_setting);



        mActivity = this;
        pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        initView();

        initData();

        //  imm.showSoftInput(introduction, 0);

    }

    void setMen()
    {
        menCk.setImageResource(R.drawable.i_chck_circle_2);
        womanCk.setImageResource(R.drawable.i_chck_circle_1);

        userInfo.setGender(0);
    }
    void setWoman()
    {
        menCk.setImageResource(R.drawable.i_chck_circle_1);
        womanCk.setImageResource(R.drawable.i_chck_circle_2);
        userInfo.setGender(1);

    }

    public void onClicMen(View view) {
        setMen();
    }
    public void onClicWoman(View view) {
        setWoman();
    }
    public void onClickWriter(View view) {
        setWriter();
    }
    void setWriter()
    {
        checkBtn1.setImageResource(R.drawable.i_chck_circle_2);
        checkBtn2.setImageResource(R.drawable.i_chck_circle_1);
        checkBtn3.setImageResource(R.drawable.i_chck_circle_1);
        nUserType = 1;
        userInfo.setType(nUserType);
    }
    public void onClickReader(View view) {

        setReader();
    }
    void setReader()
    {
        checkBtn1.setImageResource(R.drawable.i_chck_circle_1);
        checkBtn2.setImageResource(R.drawable.i_chck_circle_2);
        checkBtn3.setImageResource(R.drawable.i_chck_circle_1);
        nUserType = 0;
        userInfo.setType(nUserType);

    }
    public void onClickProducer(View view) {

        setProducer();

    }
    void setProducer()
    {
        checkBtn1.setImageResource(R.drawable.i_chck_circle_1);
        checkBtn2.setImageResource(R.drawable.i_chck_circle_1);
        checkBtn3.setImageResource(R.drawable.i_chck_circle_2);
        nUserType = 2;
        userInfo.setType(nUserType);

    }
    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("계정 설정");

        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());
        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);

       // levelIv = findViewById(R.id.iv_my_page_account_setting_level);
     //   levelTv = findViewById(R.id.tv_my_page_account_setting_level);
        photoIv = findViewById(R.id.iv_my_page_account_setting_photo);
        photoIv.setClipToOutline(true);
        bgIv = findViewById(R.id.back);
        bgIv.setClipToOutline(true);
        nameEt = findViewById(R.id.et_my_page_account_setting_name);
        fullNameEt = findViewById(R.id.et_my_page_account_setting_full_name);
     //   genderEt = findViewById(R.id.et_my_page_account_setting_gender);
        birthEt = findViewById(R.id.et_my_page_account_setting_birth);
        phoneEt = findViewById(R.id.et_my_page_account_setting_phone);
        emailEt = findViewById(R.id.et_my_page_account_setting_email);
        introduction = findViewById(R.id.introduction);
        top = findViewById(R.id.top);

        checkBtn1 = findViewById(R.id.writer_ck);
        checkBtn2 = findViewById(R.id.reader_ck);
        checkBtn3 = findViewById(R.id.producer_ck);
        menCk = findViewById(R.id.men_ck);
        womanCk = findViewById(R.id.woman_ck);


        initLayout();
    }

    private void initData() {
        // 마이 페이지 화면 갱신
        String photoUrl = SimplePreference.getStringPreference(this, "USER_INFO", "USER_PHOTO", "");
        String backUrl = SimplePreference.getStringPreference(this, "USER_INFO", "USER_BACKGROUND", "");

        nUserType = SimplePreference.getIntegerPreference(this, "USER_INFO", "USER_TYPE", 0);

        userInfo.setType(nUserType);


        String comment = SimplePreference.getStringPreference(this, "USER_INFO", "USER_COMMENT", "");

        String userID = SimplePreference.getStringPreference(this, "USER_INFO", "USER_ID", "");

        userInfo.setStrUserID(userID);
        userInfo.setComment(comment);


        int isSns = SimplePreference.getIntegerPreference(this, "USER_INFO", "IS_SNS", 0);

        if(isSns == 0)
        {
            phoneEt.setText(userID);

        }
        else
        {
            phoneEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_PHONENUM", ""));

        }
        if(comment.equals("null") == false)
            introduction.setText(comment);

        if(nUserType == 0)
        {
            setReader();
        }
        else if(nUserType == 1)
        {
            setWriter();
        }
        else if(nUserType == 2)
        {
            setProducer();
        }

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

        if (!TextUtils.isEmpty(backUrl)) {
            if (!backUrl.startsWith("http"))
                backUrl = CommonUtils.strDefaultUrl + "images/" + backUrl;

            Glide.with(mActivity)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(backUrl)
                    .placeholder(R.drawable.round_4_dddddd)
                    .apply(new RequestOptions().centerCrop())
                    .into(bgIv);
        }

        String strName = SimplePreference.getStringPreference(this, "USER_INFO", "USER_NAME", "");
        nameEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_NAME", ""));
    //    genderEt.setText(SimplePreference.getIntegerPreference(this, "USER_INFO", "USER_GENDER", -1) == 0 ? "남성" : "여성");
//        birthEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_BIRTHDAY", ""));
        String strNewBirthday = SimplePreference.getStringPreference(this, "USER_INFO", "USER_BIRTHDAY", "");
        String strBirthday = strNewBirthday.substring(0, 4) + "년 " + strNewBirthday.substring(4, 6) + "월 " + strNewBirthday.substring(6, 8) + "일";
        birthEt.setText(strBirthday);
        emailEt.setText(SimplePreference.getStringPreference(this, "USER_INFO", "USER_EMAIL", ""));

        userInfo.setBirthday(strBirthday);
        userInfo.setStrUserEmail(SimplePreference.getStringPreference(this, "USER_INFO", "USER_EMAIL", ""));


        userInfo.setStrUserName(SimplePreference.getStringPreference(this, "USER_INFO", "USER_NAME", ""));

        userInfo.setGender(SimplePreference.getIntegerPreference(this, "USER_INFO", "USER_GENDER", -1));

        int nGender = SimplePreference.getIntegerPreference(this, "USER_INFO", "USER_GENDER", -1);
        if(nGender == 0)
        {
            setMen();

        }
        else
        {
            setWoman();
        }

    }

    public void onClickNameView(View view) {
        Intent intent = new Intent(MyPageAccountSettingActivity.this, ProfileNamePopup.class);
        intent.putExtra("USER_NAME", SimplePreference.getStringPreference(this, "USER_INFO", "USER_NAME", ""));
        intent.putExtra("USER_ID", SimplePreference.getStringPreference(this, "USER_INFO", "USER_ID", ""));
        startActivity(intent);
    }

    public void onClickGender(View view) {
        Intent intent = new Intent(MyPageAccountSettingActivity.this, ProfileGenderPopup.class);
        startActivity(intent);
    }

    public void onClickBirthday(View view) {
        Intent intent = new Intent(MyPageAccountSettingActivity.this, ProfileBirthdayPopup.class);
//        intent.putExtra("USER_ID", pref.getString("USER_ID", ""));
//        intent.putExtra("USER_BIRTHDAY", pref.getString("USER_BIRTHDAY", ""));
//        startActivity(intent);

        int nYear = 1999;
        int nMonth = 0;
        int nDay = 1;

        strUserBirthday = SimplePreference.getStringPreference(MyPageAccountSettingActivity.this, "USER_INFO", "USER_BIRTHDAY", "");
        if(strUserBirthday != null && strUserBirthday.length() > 0 && !strUserBirthday.equals("null")) {
            nYear = Integer.valueOf(strUserBirthday.substring(0, 4));
            nMonth = Integer.valueOf(strUserBirthday.substring(4, 6));
            nDay = Integer.valueOf(strUserBirthday.substring(6, 8));
        }

        MyDatePickerDialogFragment datePickerDialogFragment = new MyDatePickerDialogFragment();
        datePickerDialogFragment.setSelectedDate(nYear, nMonth, nDay);
        datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
            @Override
            public void onDateChoose(int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                int nTodayYear = calendar.get(Calendar.YEAR);
                int nTodayMonth = calendar.get(Calendar.MONTH) + 1;
                int nTodayDay = calendar.get(Calendar.DAY_OF_MONTH);

                if(year > nTodayYear) {
                    CommonUtils.makeText(MyPageAccountSettingActivity.this, "미래를 선택하실 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if(year == nTodayYear) {
                    if(month> nTodayMonth) {
                        CommonUtils.makeText(MyPageAccountSettingActivity.this, "미래를 선택하실 수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    } else if(month == nTodayMonth && day > nTodayDay) {
                        CommonUtils.makeText(MyPageAccountSettingActivity.this, "미래를 선택하실 수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                strNewBirthday = String.format("%d%02d%02d", year, month, day);
                String strBirthday = strNewBirthday.substring(0, 4) + "년 " + strNewBirthday.substring(4, 6) + "월 " + strNewBirthday.substring(6, 8) + "일";
                birthEt.setText(strBirthday);
                sendBirthday();
            }
        });

        datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
    }

    private void sendBirthday() {

        userInfo.setBirthday(strNewBirthday);
         /*
        if(strNewBirthday.equals(strUserBirthday)) {
            Toast.makeText(this, "같은 생년월일로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(MyPageAccountSettingActivity.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "USER_BIRTHDAY", strNewBirthday);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(MyPageAccountSettingActivity.this, "생년월일 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_BIRTHDAY", strNewBirthday);
                        editor.commit();
                        strUserBirthday = strNewBirthday;
                    }
                });
            }
        }).start();

         */
    }

    public void onClickEmailLayout(View vie) {
        Intent intent = new Intent(MyPageAccountSettingActivity.this, EmailAuthActivity.class);
        intent.putExtra("PROFILE", true);
        intent.putExtra("USER_EMAIL", SimplePreference.getStringPreference(this, "USER_INFO", "USER_EMAIL", ""));
        startActivityForResult(intent, FROM_EMAIL_AUTH);
    }

    // 프로필 변경 버튼
    public void btnProfilePhoto(View v) {
        TedPermission.with(mActivity)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        startActivityForResult(new Intent(mActivity, ProfilePhotoPopup.class), FROM_POPUP);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(mActivity, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        intent.putExtra("TYPE", TYPE_PROFILE_BG.ordinal());
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(mActivity, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 저장
    public void btnSave(View v) {


        /*
        String comment = introduction.getText().toString();
        if(comment.length() > 0)
        {
            CommonUtils.showProgressDialog(MyPageAccountSettingActivity.this, "데이터를 전송하고 있습니다.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //
                    boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "USER_COMMENT",comment);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(!bResult) {
                                Toast.makeText(MyPageAccountSettingActivity.this, "자기소개글 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("USER_COMMENT", comment);
                            editor.commit();
                            finish();

                        }
                    });
                }
            }).start();
        }


         */
        if(introduction.getText().toString().length() > 0)
            userInfo.setComment(introduction.getText().toString());

        CommonUtils.showProgressDialog(MyPageAccountSettingActivity.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //
                boolean bResult = HttpClient.updateProfile(new OkHttpClient(), userInfo);

                if(strPhotoPath.length() > 0) {
                    boolean bResult1 =  HttpClient.requestSendUserPhoto(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strPhotoPath);
                    if(bResult1)
                    {
                        String filename = strPhotoPath.substring(strPhotoPath.lastIndexOf("/") + 1);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_PHOTO", filename);
                        editor.commit();
                    }
                }

                if(strBackgroundPath.length() > 0)
                {
                    boolean bResult2 =  HttpClient.requestSendUserBackground(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strBackgroundPath);
                    if(bResult2)
                    {
                        String filename = strBackgroundPath.substring(strBackgroundPath.lastIndexOf("/") + 1);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_BACKGROUND", filename);
                        editor.commit();

                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(MyPageAccountSettingActivity.this, "변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("USER_GENDER", userInfo.getGender());
                        editor.putString("USER_BIRTHDAY", userInfo.getBirthday());

                        editor.commit();

                        finish();

                    }
                });
            }
        }).start();

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

                         //   nLevel = CommonUtils.getLevel(nDonationCarrot);
                         //   levelIv.setImageResource(levelRes[nLevel - 1]);
                          //  levelTv.setText(levelName[nLevel - 1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FROM_POPUP) {
                bCamera = data.getBooleanExtra("CAMERA", false);
                boolean bDefault = data.getBooleanExtra("DEFAULT", false);

                if (bDefault) {
                    requestSendPhotoDefault();
                } else if (!bCamera) {
                    Intent intent = new Intent(mActivity, PhotoPickerActivity.class);
                    intent.putExtra("TYPE", TYPE_PROFILE.ordinal());
                    startActivity(intent);
                }
            } else if (requestCode == FROM_EMAIL_AUTH) {
                Intent intent = new Intent(mActivity, ProfileEmailPopup.class);
                intent.putExtra("USER_ID", SimplePreference.getStringPreference(this, "USER_INFO", "USER_ID", ""));
                intent.putExtra("USER_EMAIL", data.getStringExtra("NEW_EMAIL"));
                startActivity(intent);
            } else if (requestCode == FROM_CAMERA) {
                ThumbnailPreviewActivity.nNextType = TYPE_PROFILE.ordinal();
                CropImage.activity(mPhotoUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .start(mActivity);
            }
        }
    }

    public void takePhoto() {
        // 촬영 후 이미지 가져옴
        bCamera = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;

                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(mActivity, "com.Whowant.Tokki.PhotoProvider", photoFile);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        } else {
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }

    public File createImageFile() throws IOException {                                   // 사진 촬영시 해당 사진이 담길 파일 생성
        String imgFileName = System.currentTimeMillis() + ".png";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "Panbook");

        if (!storageDir.exists()) {
            Log.v("알림", "storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }

        Log.v("알림", "storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir, imgFileName);
        mPhotoUri = Uri.fromFile(imageFile);

        return imageFile;
    }

    private void requestSendPhotoDefault() {                                // 사진 초기화
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfileImageDefault(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_PHOTO", "");
                        editor.commit();

                        photoIv.setImageResource(R.drawable.user_icon);
                    }
                });
            }
        }).start();
    }


    private void requestSendPhoto(String strPhotoPath) {



      //   String filename = strPhotoPath.substring(strPhotoPath.lastIndexOf("/") + 1);

        userInfo.setStrUserPhoto(strPhotoPath);

        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MyPageAccountSettingActivity.this)
                                .asBitmap()
                                .apply(new RequestOptions().circleCrop())
                                .placeholder(R.drawable.user_icon)

                                .load(strPhotoPath)
                                .into(photoIv);
                    }
                });
            }
        }).start();
        /*

        CommonUtils.showProgressDialog(mActivity, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserPhoto(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strPhotoPath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult) {
                            String filename = strPhotoPath.substring(strPhotoPath.lastIndexOf("/") + 1);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("USER_PHOTO", filename);
                            editor.commit();

                            String strPhoto = pref.getString("USER_PHOTO", "");
                            if (strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
                                if (!strPhoto.startsWith("http"))
                                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                                Glide.with(mActivity)
                                        .asBitmap() // some .jpeg files are actually gif
                                        .load(strPhoto)
                                        .apply(new RequestOptions().circleCrop())
                                        .placeholder(R.drawable.user_icon)
                                        .into(photoIv);

                            } else {
                                photoIv.setImageResource(R.drawable.user_icon);
                            }
                        } else {
                            Toast.makeText(mActivity, "사진 전송에 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();


         */

    }

    private void requestSendBackgroundPhoto(String strBackgroundPath) {

      //  String filename = strBackgroundPath.substring(strBackgroundPath.lastIndexOf("/") + 1);

        userInfo.setBackground(strBackgroundPath);




        /*
        String strBack = pref.getString("USER_BACKGROUND","");
        if (strBack != null && strBack.length() > 0 && !strBack.equals("null")) {
            if (!strBack.startsWith("http"))
                strBack = CommonUtils.strDefaultUrl + "images/" + strBack;

            Glide.with(mActivity)
                    .asBitmap()
                    .load(strBack)
                    .placeholder(R.drawable.round_4_dddddd)
                    .into(bgIv);
        } else
            bgIv.setImageResource(R.drawable.round_4_dddddd);


         */
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MyPageAccountSettingActivity.this)
                                .asBitmap()
                                .placeholder(R.drawable.round_4_dddddd)
                                .load(strBackgroundPath)
                                .into(bgIv);
                    }
                });
            }
        }).start();

/*

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserBackground(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strBackgroundPath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bResult) {
                            String filename = strBackgroundPath.substring(strBackgroundPath.lastIndexOf("/") + 1);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("USER_BACKGROUND", filename);
                            editor.commit();

                            String strBack = pref.getString("USER_BACKGROUND","");
                            if (strBack != null && strBack.btnlength() > 0 && !strBack.equals("null")) {
                                if (!strBack.startsWith("http"))
                                    strBack = CommonUtils.strDefaultUrl + "images/" + strBack;

                                Glide.with(mActivity)
                                        .asBitmap()
                                        .load(strBack)
                                        .placeholder(R.drawable.round_4_dddddd)
                                        .into(bgIv);
                            } else
                                bgIv.setImageResource(R.drawable.round_4_dddddd);
                        } else
                            Toast.makeText(mActivity, "사진 전송에 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();



 */


    }
}