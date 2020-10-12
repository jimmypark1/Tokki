package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.SeesoGalleryActivity;
import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.UI.Popup.CoverMediaSelectPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.Utils.cropper.CropImage;
import com.Whowant.Tokki.Utils.cropper.CropImageActivity;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateWorkActivity extends AppCompatActivity {                                         // 새로운 작품 생성 화면
    private EditText inputTitleView;
    private EditText inputSynopsisView;
    private EditText inputTagView;
    private EditText inputGenreView;
//    private TagEditText inputTagView;
    private ImageView coverImgView;
    private Uri      coverImgUri = null;
    private Uri      posterThumbUri = null;
    private Uri      galleryThumbUri = null;

    private RelativeLayout posterThumbnailLayout, galleryThumbnailLayout;
    private ImageView posterThumbnailView, galleryThumbnailView, posterCheckView, galleryCheckView;
    private TextView posterThumbnailTextView, galleryThumbnailTextView;
    private RelativeLayout coverImgBtn;

    private WorkVO workVO;

    private ProgressDialog mProgressDialog;
    private int nThumbnail = 0;     // 0 = 안함, 1 = 포스터를 썸네일로, 2 = 갤러리에서 썸네일 고르기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_renewal);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        posterThumbnailLayout = findViewById(R.id.posterThumbnailLayout);
        galleryThumbnailLayout = findViewById(R.id.galleryThumbnailLayout);
        posterThumbnailView = findViewById(R.id.posterThumbnailView);
        galleryThumbnailView = findViewById(R.id.galleryThumbnailView);
        posterCheckView = findViewById(R.id.posterCheckView);
        galleryCheckView = findViewById(R.id.galleryCheckView);
        posterThumbnailTextView = findViewById(R.id.posterThumbnailTextView);
        galleryThumbnailTextView = findViewById(R.id.galleryThumbnailTextView);
        inputTitleView = findViewById(R.id.inputTitleView);
        inputSynopsisView = findViewById(R.id.inputSynopsisView);
        coverImgView = findViewById(R.id.coverImgView);
        inputTagView = findViewById(R.id.inputTagView);
        inputGenreView = findViewById(R.id.inputGenreView);
        coverImgBtn = findViewById(R.id.coverImgBtn);

        inputTitleView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                        inputSynopsisView.requestFocus();
                        inputSynopsisView.setSelection(inputSynopsisView.getText().toString().length());
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });

        inputSynopsisView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                        inputTagView.requestFocus();
                        inputTagView.setSelection(inputTagView.getText().toString().length());
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });

        inputTagView.setOnClickListener(new View.OnClickListener() {                                            // 태크 부분을 클릭하면 태크 선택창으로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateWorkActivity.this, TagSelectActivity.class);
                intent.putExtra("TAG", inputTagView.getText().toString());
                startActivityForResult(intent, 1020);
            }
        });

        inputGenreView.setOnClickListener(new View.OnClickListener() {                                          // 장르 부분을 클릭하면 장르 선택창으로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateWorkActivity.this, GenreSelectActivity.class);
                intent.putExtra("GENRE", inputGenreView.getText().toString());
                startActivityForResult(intent, 1010);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {                                                             // 각종 사진 선택 시에 onNewIntent 로 선택된 사진 데이터 전달받음
        super.onNewIntent(intent);

        boolean bThumbnail = intent.getBooleanExtra("THUMBNAIL", false);

        if(bThumbnail) {
            String imgUri = intent.getStringExtra("IMG_URI");

            if(nThumbnail == 1) {           // 포스터에서 썸네일 고름
                galleryThumbUri = null;
                posterThumbUri = Uri.parse(imgUri);
                galleryThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                galleryCheckView.setImageResource(0);
                galleryThumbnailView.setImageResource(0);
                galleryThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));

                posterThumbnailLayout.setBackgroundResource(R.drawable.round_wthie_black_stroke_bg);
                posterCheckView.setImageResource(R.drawable.blue_check_view);
                posterThumbnailView.setClipToOutline(true);
                Glide.with(this)
                        .load(posterThumbUri)
                        .into(posterThumbnailView);
                posterThumbnailTextView.setTextColor(Color.parseColor("#000000"));
            } else if(nThumbnail == 2) {    // 갤러리에서 썸네일 고름
                posterThumbUri = null;
                galleryThumbUri = Uri.parse(imgUri);
                posterThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                posterCheckView.setImageResource(0);
                posterThumbnailView.setImageResource(0);
                posterThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));

                galleryThumbnailLayout.setBackgroundResource(R.drawable.round_wthie_black_stroke_bg);
                galleryCheckView.setImageResource(R.drawable.blue_check_view);
                galleryThumbnailView.setClipToOutline(true);
                Glide.with(this)
                        .load(galleryThumbUri)
                        .into(galleryThumbnailView);
                galleryThumbnailTextView.setTextColor(Color.parseColor("#000000"));
            }

            nThumbnail = 0;
        } else {                                                                                    // 사진 로그는 중인 부분. 선택한 사진을 Crop 하러 보낸다
            String imgUri = intent.getStringExtra("IMG_URI");
            coverImgUri = Uri.parse(imgUri);

            if(imgUri != null) {
                coverImgView.setClipToOutline(true);
                coverImgBtn.setVisibility(View.INVISIBLE);
                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(coverImgUri)
                        .into(coverImgView);

                nThumbnail = 1;
                ThumbnailPreviewActivity.bCoverThumb = true;
                CropImageActivity.bThumbnail = true;
                CropImage.activity(coverImgUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(25, 20)
                        .start(CreateWorkActivity.this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                                     //
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {                                                                                      // 사진 선택을 위해 데이터 오가는 부분
                int nType = data.getIntExtra("TYPE", 0);

                if (nType == 0)
                    return;
                else if (nType == 1) {
                    Intent intent = new Intent(CreateWorkActivity.this, SeesoGalleryActivity.class);
                    if(nThumbnail == 2)
                        intent.putExtra("TYPE", SeesoGalleryActivity.TYPE_COVER_THUMB_IMG);
                    else
                        intent.putExtra("TYPE", SeesoGalleryActivity.TYPE_COVER_IMG);

                    startActivity(intent);
                } else if (nType == 2) {
                    Intent intent = new Intent(CreateWorkActivity.this, PhotoPickerActivity.class);
                    if(nThumbnail == 2)
                        intent.putExtra("TYPE", PhotoPickerActivity.TYPE_COVER_THUMB_IMG);
                    else
                        intent.putExtra("TYPE", PhotoPickerActivity.TYPE_COVER_IMG);
                    startActivity(intent);
                }
            } else if(requestCode == 1010) {                                                                            // 장르 선택시
                String strGenre = data.getStringExtra("GENRE");
                inputGenreView.setText(strGenre);
            } else if(requestCode == 1020) {                                                                            // 태그 선택시
                String strTag = data.getStringExtra("TAG");
                inputTagView.setText(strTag);
            }
        }
    }

    public void onClickQuestionBtn(View view) {                                                                         // 중간에 추가된 기능으로, 작품의 '포스터' 이미지가 아닌 '썸네일' 이미지를 업로드 하는 기능이 추가됨.
        Intent intent = new Intent(CreateWorkActivity.this, CommonPopup.class);                         // 썸네일 이란 평소에 보이지는 않는 이미지 이나, '인기작' 혹은 '추천작' 에 올라가면 보이는 이미지로 일반적인 포스터와는 달리 가로로 약간 긴 형태의 이미지
        intent.putExtra("TITLE", "섬네일 등");
        intent.putExtra("CONTENTS", "이 기능은 메인 페이지의 인기작 섬네일을 등록하는 기능입니다.");
        intent.putExtra("TWOBTN", false);
        startActivity(intent);
    }

    public void OnClickOkBtn(View view) {
        String strTitle = inputTitleView.getText().toString();
        String strSynopsis = inputSynopsisView.getText().toString();

        if(strTitle.length() == 0) {
            Toast.makeText(CreateWorkActivity.this, "제목을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(strSynopsis.length() == 0) {
            Toast.makeText(CreateWorkActivity.this, "줄거리를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        String strGenre = inputGenreView.getText().toString();
        if(strGenre.length() == 0) {
            Toast.makeText(CreateWorkActivity.this, "장르를 선택해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        requestCreateWork();
    }

    private void requestCreateWork() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("작품을 생성중입니다");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateWork.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;
            String strFilePath = null;

            MultipartBody.Builder builder = new MultipartBody.Builder();
            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("WRITER_ID", pref.getString("USER_ID", "Guest"))
                    .addFormDataPart("WORK_TITLE", inputTitleView.getText().toString())
                    .addFormDataPart("WORK_SYNOPSIS", inputSynopsisView.getText().toString())
                    .addFormDataPart("WORK_TARGET", "");

            String strTags = inputTagView.getText().toString();
            if(strTags.length() > 0)
                builder.addFormDataPart("TAGS", strTags);

            String strGenres = inputGenreView.getText().toString();
            if(strGenres.length() > 0) {
                String genres[] = strGenres.split(" / ");
                strGenres = "";
                for(String genre : genres) {
                    if(strGenres.length() > 0)
                        strGenres += " ";

                    strGenres += genre;
                }
                builder.addFormDataPart("WORK_GENRE", strGenres);
            }

            if(coverImgUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(CreateWorkActivity.this, coverImgUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateWorkActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart("COVER_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            }

            if(posterThumbUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(CreateWorkActivity.this, posterThumbUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateWorkActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart("POSTER_THUMBNAIL_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            } else if(galleryThumbUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(CreateWorkActivity.this, galleryThumbUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateWorkActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart("GALLERY_THUMBNAIL_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            }

            requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateWorkActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();

                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strWorkID = resultObject.getString("WORK_ID");

                                    Intent intent = new Intent(CreateWorkActivity.this, WorkWriteMainActivity.class);
                                    intent.putExtra("WORK_ID", strWorkID);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(CreateWorkActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // response.body().string()
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateWorkActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void OnClickPhotoBtn(View view) {
        nThumbnail = 0;
        TedPermission.with(CreateWorkActivity.this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {             // 모든 권한 획득
            Intent intent = new Intent(CreateWorkActivity.this, CoverMediaSelectPopup.class);
            startActivityForResult(intent, 1000);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(CreateWorkActivity.this, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
        }
    };

    public void onClickGalleryThumbnailBtn(View view) {
        nThumbnail = 2;
        TedPermission.with(CreateWorkActivity.this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    public void onClickPosterThumbnailBtn(View view) {
        if(coverImgUri == null) {
            Toast.makeText(this, "포스터 이미지를 설정하셔야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        nThumbnail = 1;
        ThumbnailPreviewActivity.bCoverThumb = true;
        CropImageActivity.bThumbnail = true;
        CropImage.activity(coverImgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(25, 20)
                .start(CreateWorkActivity.this);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
}
