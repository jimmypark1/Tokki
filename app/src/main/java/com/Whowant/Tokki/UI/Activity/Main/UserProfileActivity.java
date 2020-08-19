package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.EmailAuthActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.UI.Custom.MyDatePickerDialogFragment;
import com.Whowant.Tokki.UI.Popup.ProfileBirthdayPopup;
import com.Whowant.Tokki.UI.Popup.ProfileEmailPopup;
import com.Whowant.Tokki.UI.Popup.ProfileGenderPopup;
import com.Whowant.Tokki.UI.Popup.ProfileNamePopup;
import com.Whowant.Tokki.UI.Popup.ProfilePhoneNumPopup;
import com.Whowant.Tokki.UI.Popup.ProfilePhotoPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.Utils.cropper.CropImage;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;

public class UserProfileActivity extends AppCompatActivity {
    private SharedPreferences pref;

    private ImageView faceView;
    private TextView nameView;
    private TextView emailView;
    private TextView genderView;
    private TextView birthdayView;
    private TextView phoneNumView;

    private String strUserBirthday;
    private String strNewBirthday;

    private boolean bCamera = false;
    private static final int FROM_POPUP = 1000;
    private static final int FROM_EMAIL_AUTH = 1010;
    private static final int FROM_CAMERA = 1020;
    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("사용자 프로필");
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        strUserBirthday = pref.getString("USER_BIRTHDAY", "");
        if(strUserBirthday.length() == 6)
            strUserBirthday = "19" + strUserBirthday;

        faceView = findViewById(R.id.faceView);
        nameView = findViewById(R.id.nameView);
        emailView = findViewById(R.id.emailView);
        genderView = findViewById(R.id.genderView);
        birthdayView = findViewById(R.id.birthdayView);
        phoneNumView = findViewById(R.id.phoneNumView);

//        editor.putString("USER_ID", strID);
//        editor.putString("USER_PW", strPW1);
//        editor.putString("USER_NAME", strName);
//        editor.putString("USER_EMAIL", strEmail);
//        editor.putString("USER_PHONENUM", strPhoneNum);
//        editor.putString("USER_PHOTO", strProfileImageUrl);
//        editor.putInt("REGISTER_SNS", nSNS);
//        editor.putString("SNS_ID", strSNSID);
//        editor.putString("ADMIN", "N");
//        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String strUri = intent.getStringExtra("URI");

        if(strUri != null) {
            mPhotoUri = Uri.parse(strUri);
            String strPhotoPath = CommonUtils.getRealPathFromURI(UserProfileActivity.this, mPhotoUri);
            requestSendPhoto(strPhotoPath);
        }
    }

    @Override
    protected  void onResume() {
        super.onResume();

        if(bCamera) {
            takePhoto();
        }

        String strPhoto = pref.getString("USER_PHOTO", "");

        if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if(!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(UserProfileActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }

        nameView.setText(pref.getString("USER_NAME", ""));
        emailView.setText(pref.getString("USER_EMAIL", ""));
        genderView.setText(pref.getInt("USER_GENDER", 0) == 0 ? "남" : "여");

        if(strUserBirthday != null && strUserBirthday.length() > 0 && !strUserBirthday.equals("null")) {
            String strBirthday = strUserBirthday.substring(0, 4) + "년 " + strUserBirthday.substring(4, 6) + "월 " + strUserBirthday.substring(6, 8) + "일";
            birthdayView.setText(strBirthday);
        }

        phoneNumView.setText(pref.getString("USER_PHONENUM", ""));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FROM_POPUP) {
                bCamera = data.getBooleanExtra("CAMERA", false);

                if (!bCamera) {
                    Intent intent = new Intent(UserProfileActivity.this, PhotoPickerActivity.class);
                    intent.putExtra("TYPE", PhotoPickerActivity.TYPE_PROFILE);
                    startActivity(intent);
                }
            } else if (requestCode == FROM_EMAIL_AUTH) {
                Intent intent = new Intent(UserProfileActivity.this, ProfileEmailPopup.class);
                intent.putExtra("USER_ID", pref.getString("USER_ID", "Guest"));
                intent.putExtra("USER_EMAIL", pref.getString("USER_EMAIL", ""));
                startActivity(intent);
            } else if (requestCode == FROM_CAMERA) {
                ThumbnailPreviewActivity.bProfile = true;
                CropImage.activity(mPhotoUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .start(UserProfileActivity.this);
            }
        }
    }

    private void requestSendPhoto(String strPhotoPath) {
        CommonUtils.showProgressDialog(UserProfileActivity.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserPhoto(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strPhotoPath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        
                        if(bResult) {
                            String filename = strPhotoPath.substring(strPhotoPath.lastIndexOf("/")+1);

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("USER_PHOTO", filename);
                            editor.commit();

                            String strPhoto = pref.getString("USER_PHOTO", "");

                            if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
                                if(!strPhoto.startsWith("http"))
                                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                                Glide.with(UserProfileActivity.this)
                                        .asBitmap() // some .jpeg files are actually gif
                                        .load(strPhoto)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(faceView);
                            }
                        } else {
                            Toast.makeText(UserProfileActivity.this, "사진 전송에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }


    public void takePhoto(){
        // 촬영 후 이미지 가져옴
        bCamera = false;
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null){
                File photoFile = null;

                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }

                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(this, "com.Whowant.Penapp.PhotoProvider", photoFile);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }

    public File createImageFile() throws IOException{
        String imgFileName = System.currentTimeMillis() + ".png";
        File imageFile= null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "Panbook");

        if(!storageDir.exists()){
            Log.v("알림","storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }

        Log.v("알림","storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mPhotoUri = Uri.fromFile(imageFile);

        return imageFile;
    }

    public void onClickPhotoLayout(View view) {
        TedPermission.with(UserProfileActivity.this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        startActivityForResult(new Intent(UserProfileActivity.this, ProfilePhotoPopup.class), FROM_POPUP);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(UserProfileActivity.this, "권한을 허용해주셔야 사진 설정이 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    public void onClickNameLayout(View view) {
        Intent intent = new Intent(UserProfileActivity.this, ProfileNamePopup.class);
        intent.putExtra("USER_ID", pref.getString("USER_ID", "Guest"));
        intent.putExtra("USER_NAME", pref.getString("USER_NAME", ""));
        startActivity(intent);
    }

    public void onClickEmailLayout(View vie) {
        Intent intent = new Intent(UserProfileActivity.this, EmailAuthActivity.class);
        intent.putExtra("PROFILE", true);
        intent.putExtra("USER_EMAIL", pref.getString("USER_EMAIL", ""));
        startActivityForResult(intent, FROM_EMAIL_AUTH);
    }

    public void onClickGenderLayout(View view) {
        Intent intent = new Intent(UserProfileActivity.this, ProfileGenderPopup.class);
        intent.putExtra("USER_ID", pref.getString("USER_ID", "Guest"));
        intent.putExtra("USER_GENDER", pref.getInt("USER_GENDER", 0));
        startActivity(intent);
    }

    public void onClickBirthdayLayout(View view) {
        Intent intent = new Intent(UserProfileActivity.this, ProfileBirthdayPopup.class);
//        intent.putExtra("USER_ID", pref.getString("USER_ID", ""));
//        intent.putExtra("USER_BIRTHDAY", pref.getString("USER_BIRTHDAY", ""));
//        startActivity(intent);

        int nYear = 1999;
        int nMonth = 0;
        int nDay = 1;

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
                    CommonUtils.makeText(UserProfileActivity.this, "미래를 선택하실 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                } else if(year == nTodayYear) {
                    if(month> nTodayMonth) {
                        CommonUtils.makeText(UserProfileActivity.this, "미래를 선택하실 수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    } else if(month == nTodayMonth && day > nTodayDay) {
                        CommonUtils.makeText(UserProfileActivity.this, "미래를 선택하실 수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                strNewBirthday = String.format("%d%02d%02d", year, month, day);
                String strBirthday = strNewBirthday.substring(0, 4) + "년 " + strNewBirthday.substring(4, 6) + "월 " + strNewBirthday.substring(6, 8) + "일";
                birthdayView.setText(strBirthday);
                sendBirthday();
            }
        });
        datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");

//        DatePickerDialog dialog = new DatePickerDialog(UserProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

//            }
//        }, nYear, nMonth, nDay);
//        dialog.show();
    }

    private void sendBirthday() {
        if(strNewBirthday.equals(strUserBirthday)) {
            Toast.makeText(this, "같은 생년월일로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(UserProfileActivity.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "USER_BIRTHDAY", strNewBirthday);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(UserProfileActivity.this, "생년월일 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
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
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickPhoneNumLayout(View view) {
        Intent intent = new Intent(UserProfileActivity.this, ProfilePhoneNumPopup.class);
        intent.putExtra("USER_ID", pref.getString("USER_ID", "Guest"));
        intent.putExtra("USER_PHONENUM", pref.getString("USER_PHONENUM", ""));
        startActivity(intent);
    }
}
