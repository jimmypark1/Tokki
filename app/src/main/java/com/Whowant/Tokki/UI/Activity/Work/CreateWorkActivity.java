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
import com.Whowant.Tokki.UI.Activity.Photopicker.TokkiGalleryActivity;
import com.Whowant.Tokki.UI.Fragment.Main.TokkiGalleryFragment;
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

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER_THUMB;

public class CreateWorkActivity extends AppCompatActivity {                                         // ????????? ?????? ?????? ??????
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
    private int nThumbnail = 0;     // 0 = ??????, 1 = ???????????? ????????????, 2 = ??????????????? ????????? ?????????
    private String writerID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_renewal);

        writerID = getIntent().getStringExtra("WRITER_ID");

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
                        // ?????? ????????? ??????
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
                        // ?????? ????????? ??????
                        return false;
                }
                return true;
            }
        });

        inputTagView.setOnClickListener(new View.OnClickListener() {                                            // ?????? ????????? ???????????? ?????? ??????????????? ??????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateWorkActivity.this, TagSelectActivity.class);
                intent.putExtra("TAG", inputTagView.getText().toString());
                startActivityForResult(intent, 1020);
            }
        });

        inputGenreView.setOnClickListener(new View.OnClickListener() {                                          // ?????? ????????? ???????????? ?????? ??????????????? ??????
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateWorkActivity.this, GenreSelectActivity.class);
                intent.putExtra("GENRE", inputGenreView.getText().toString());
                startActivityForResult(intent, 1010);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {                                                             // ?????? ?????? ?????? ?????? onNewIntent ??? ????????? ?????? ????????? ????????????
        super.onNewIntent(intent);

        boolean bThumbnail = intent.getBooleanExtra("THUMBNAIL", false);

        if(bThumbnail) {
            String imgUri = intent.getStringExtra("IMG_URI");

            if(nThumbnail == 1) {           // ??????????????? ????????? ??????
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
            } else if(nThumbnail == 2) {    // ??????????????? ????????? ??????
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
        } else {                                                                                    // ?????? ????????? ?????? ??????. ????????? ????????? Crop ?????? ?????????
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
                ThumbnailPreviewActivity.nNextType = TYPE_COVER_THUMB.ordinal();
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
            if (requestCode == 1000) {                                                                                      // ?????? ????????? ?????? ????????? ????????? ??????
                int nType = data.getIntExtra("TYPE", 0);

                if (nType == 0)
                    return;
                else if (nType == 1) {
//                    Intent intent = new Intent(CreateWorkActivity.this, SeesoGalleryActivity.class);
                    Intent intent = new Intent(CreateWorkActivity.this, TokkiGalleryActivity.class);
                    if(nThumbnail == 2)
                        intent.putExtra("TYPE", TYPE_COVER_THUMB.ordinal());
                    else
                        intent.putExtra("TYPE", TYPE_COVER.ordinal());

                    startActivity(intent);
                } else if (nType == 2) {
                    Intent intent = new Intent(CreateWorkActivity.this, PhotoPickerActivity.class);
                    if(nThumbnail == 2)
                        intent.putExtra("TYPE", TYPE_COVER_THUMB.ordinal());
                    else
                        intent.putExtra("TYPE", TYPE_COVER.ordinal());
                    startActivity(intent);
                }
            } else if(requestCode == 1010) {                                                                            // ?????? ?????????
                String strGenre = data.getStringExtra("GENRE");
                inputGenreView.setText(strGenre);
            } else if(requestCode == 1020) {                                                                            // ?????? ?????????
                String strTag = data.getStringExtra("TAG");
                inputTagView.setText(strTag);
            }
        }
    }

    public void onClickQuestionBtn(View view) {                                                                         // ????????? ????????? ????????????, ????????? '?????????' ???????????? ?????? '?????????' ???????????? ????????? ?????? ????????? ?????????.
        Intent intent = new Intent(CreateWorkActivity.this, CommonPopup.class);                         // ????????? ?????? ????????? ???????????? ?????? ????????? ??????, '?????????' ?????? '?????????' ??? ???????????? ????????? ???????????? ???????????? ??????????????? ?????? ????????? ?????? ??? ????????? ?????????
        intent.putExtra("TITLE", "????????? ???");
        intent.putExtra("CONTENTS", "??? ????????? ?????? ???????????? ????????? ???????????? ???????????? ???????????????.");
        intent.putExtra("TWOBTN", false);
        startActivity(intent);
    }

    public void OnClickOkBtn(View view) {
        String strTitle = inputTitleView.getText().toString();
        String strSynopsis = inputSynopsisView.getText().toString();

        if(strTitle.length() == 0) {
            Toast.makeText(CreateWorkActivity.this, "????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        if(strSynopsis.length() == 0) {
            Toast.makeText(CreateWorkActivity.this, "???????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        String strGenre = inputGenreView.getText().toString();
        if(strGenre.length() == 0) {
            Toast.makeText(CreateWorkActivity.this, "????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        requestCreateWork();
    }

    private void requestCreateWork() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("????????? ??????????????????");
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

            if (writerID == null) {
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("WRITER_ID", pref.getString("USER_ID", "Guest"))
                        .addFormDataPart("WORK_TITLE", inputTitleView.getText().toString())
                        .addFormDataPart("WORK_SYNOPSIS", inputSynopsisView.getText().toString())
                        .addFormDataPart("WORK_TARGET", "");
            } else {
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("WRITER_ID", writerID)
                        .addFormDataPart("WORK_TITLE", inputTitleView.getText().toString())
                        .addFormDataPart("WORK_SYNOPSIS", inputSynopsisView.getText().toString())
                        .addFormDataPart("WORK_TARGET", "");
            }

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
                    Toast.makeText(CreateWorkActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(CreateWorkActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart("POSTER_THUMBNAIL_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            } else if(galleryThumbUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(CreateWorkActivity.this, galleryThumbUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(CreateWorkActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(CreateWorkActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(CreateWorkActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(CreateWorkActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
        public void onPermissionGranted() {             // ?????? ?????? ??????
            Intent intent = new Intent(CreateWorkActivity.this, CoverMediaSelectPopup.class);
            startActivityForResult(intent, 1000);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(CreateWorkActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "????????? ???????????? ??????????????? ?????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        nThumbnail = 1;
        ThumbnailPreviewActivity.nNextType = TYPE_COVER_THUMB.ordinal();
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
