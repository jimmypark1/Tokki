package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.TokkiGalleryActivity;
import com.Whowant.Tokki.UI.Fragment.Main.TokkiGalleryFragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkEditActivity extends AppCompatActivity {
    private EditText inputTitleView;
    private EditText inputSynopsisView;
    private EditText inputTagView;
    private EditText inputGenreView;
    //    private TagEditText inputTagView;
    private ImageView coverImgView;
    private Uri      originCoverImgUri = null;
    private Uri      coverImgUri = null;
    private Uri      posterThumbUri = null;
    private Uri      galleryThumbUri = null;
    private RelativeLayout coverImgBtn;
    private ArrayList<String> tagList, genreList;
    private ImageView checkbox1, checkbox2;
    private TextView unCompleteTitleview, completeTitleview, completeTitleView;
    private LinearLayout check1Layout, check2Layout;

    private RelativeLayout posterThumbnailLayout, galleryThumbnailLayout;
    private ImageView posterThumbnailView, galleryThumbnailView, posterCheckView, galleryCheckView;
    private TextView posterThumbnailTextView, galleryThumbnailTextView;
    private int nCurrentThumbnail = 0;   // 0 = 안함, 1 = 포스터를 썸네일로, 2 = 갤러리에서 썸네일 고르기
    private int nThumbnail = 0;     // 0 = 안함, 1 = 포스터를 썸네일로, 2 = 갤러리에서 썸네일 고르기
    private boolean isDeletePoster = false;
    private boolean isDeleteThumbnail = false;
    private boolean bComplete = false;

    public static WorkVO workVO;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_renewal);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("작품 수정");

        inputTitleView = findViewById(R.id.inputTitleView);
        inputSynopsisView = findViewById(R.id.inputSynopsisView);
        coverImgView = findViewById(R.id.coverImgView);
        inputTagView = findViewById(R.id.inputTagView);
        inputGenreView = findViewById(R.id.inputGenreView);
        coverImgBtn = findViewById(R.id.coverImgBtn);
        checkbox1 = findViewById(R.id.checkbox1);
        checkbox2 = findViewById(R.id.checkbox2);
        completeTitleView = findViewById(R.id.completeTitleView);
        unCompleteTitleview = findViewById(R.id.unCompleteTitleview);
        completeTitleview = findViewById(R.id.completeTitleview);
        check1Layout = findViewById(R.id.check1Layout);
        check2Layout = findViewById(R.id.check2Layout);
        posterThumbnailLayout = findViewById(R.id.posterThumbnailLayout);
        galleryThumbnailLayout = findViewById(R.id.galleryThumbnailLayout);
        posterThumbnailView = findViewById(R.id.posterThumbnailView);
        galleryThumbnailView = findViewById(R.id.galleryThumbnailView);
        posterCheckView = findViewById(R.id.posterCheckView);
        galleryCheckView = findViewById(R.id.galleryCheckView);
        posterThumbnailTextView = findViewById(R.id.posterThumbnailTextView);
        galleryThumbnailTextView = findViewById(R.id.galleryThumbnailTextView);

        checkbox1.setImageResource(R.drawable.check_box_on);
        unCompleteTitleview.setTextColor(Color.BLACK);
        completeTitleview.setTextColor(Color.BLACK);
        completeTitleView.setTextColor(Color.BLACK);

        check1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bComplete = false;
                setComplete();
            }
        });

        check2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bComplete = true;
                setComplete();
            }
        });

        inputTitleView.setText(workVO.getTitle());
        inputSynopsisView.setText(workVO.getSynopsis());

        bComplete = workVO.isbComplete();
        setComplete();

        if(workVO.getCoverFile() != null && workVO.getCoverFile().length() > 0 && !workVO.getCoverFile().equals("null")) {
            coverImgView.setClipToOutline(true);
            coverImgBtn.setVisibility(View.INVISIBLE);
            originCoverImgUri = Uri.parse(CommonUtils.strDefaultUrl + "images/" + workVO.getCoverFile());
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(CommonUtils.strDefaultUrl + "images/" + workVO.getCoverFile())
                    .into(coverImgView);
            isDeletePoster = false;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

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

        inputTagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkEditActivity.this, TagSelectActivity.class);
                intent.putExtra("TAG", inputTagView.getText().toString());
                startActivityForResult(intent, 1020);
            }
        });

        inputGenreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkEditActivity.this, GenreSelectActivity.class);
                intent.putExtra("GENRE", inputGenreView.getText().toString());
                startActivityForResult(intent, 1010);
            }
        });

        if(workVO.getStrThumbFile() != null && !workVO.getStrThumbFile().equals("null")) {
            if(workVO.isbPosterThumbnail()) {           // 포스터에서 썸네일 고름
                nCurrentThumbnail = 1;
                galleryThumbUri = null;
//                posterThumbUri = Uri.parse(workVO.getStrThumbFile());
                galleryThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                galleryCheckView.setImageResource(0);
                galleryThumbnailView.setImageResource(R.drawable.empty_thumbnail_img);
                galleryThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));

                posterThumbnailLayout.setBackgroundResource(R.drawable.round_wthie_black_stroke_bg);
                posterCheckView.setImageResource(R.drawable.blue_check_view);
                posterThumbnailView.setClipToOutline(true);
                Glide.with(this)
                        .load(CommonUtils.strDefaultUrl + "images/" + workVO.getStrThumbFile())
                        .into(posterThumbnailView);
                posterThumbnailTextView.setTextColor(Color.parseColor("#000000"));
            } else  {    // 갤러리에서 썸네일 고름
                nCurrentThumbnail = 2;
                posterThumbUri = null;
//                galleryThumbUri = Uri.parse(workVO.getStrThumbFile());
                posterThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                posterCheckView.setImageResource(0);
                posterThumbnailView.setImageResource(0);
                posterThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));

                galleryThumbnailLayout.setBackgroundResource(R.drawable.round_wthie_black_stroke_bg);
                galleryCheckView.setImageResource(R.drawable.blue_check_view);
                galleryThumbnailView.setClipToOutline(true);
                Glide.with(this)
                        .load(CommonUtils.strDefaultUrl + "images/" + workVO.getStrThumbFile())
                        .into(galleryThumbnailView);
                galleryThumbnailTextView.setTextColor(Color.parseColor("#000000"));
            }
        }

        getTagData();
    }

    private void setComplete() {
        if(bComplete) {
            checkbox2.setImageResource(R.drawable.check_box_on);
            checkbox1.setImageResource(0);
        } else {
            checkbox1.setImageResource(R.drawable.check_box_on);
            checkbox2.setImageResource(0);
        }
    }

    public void onClickTopLeftBtn(View veiw) {
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        boolean bThumbnail = intent.getBooleanExtra("THUMBNAIL", false);

        if(bThumbnail) {
            String imgUri = intent.getStringExtra("IMG_URI");

            if(nThumbnail == 1) {           // 포스터에서 썸네일 고름
                nCurrentThumbnail = 1;
                galleryThumbUri = null;
                posterThumbUri = Uri.parse(imgUri);
                galleryThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                galleryCheckView.setImageResource(0);
                galleryThumbnailView.setImageResource(R.drawable.empty_thumbnail_img);
                galleryThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));

                isDeleteThumbnail = false;
                posterThumbnailLayout.setBackgroundResource(R.drawable.round_wthie_black_stroke_bg);
                posterCheckView.setImageResource(R.drawable.blue_check_view);
                posterThumbnailView.setClipToOutline(true);
                Glide.with(this)
                        .load(posterThumbUri)
                        .into(posterThumbnailView);
                posterThumbnailTextView.setTextColor(Color.parseColor("#000000"));
            } else if(nThumbnail == 2) {    // 갤러리에서 썸네일 고름
                nCurrentThumbnail = 2;
                posterThumbUri = null;
                galleryThumbUri = Uri.parse(imgUri);
                posterThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                posterCheckView.setImageResource(0);
                galleryThumbnailView.setImageResource(R.drawable.poster_sombnail);
                posterThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));

                galleryThumbnailLayout.setBackgroundResource(R.drawable.round_wthie_black_stroke_bg);
                galleryCheckView.setImageResource(R.drawable.blue_check_view);
                galleryThumbnailView.setClipToOutline(true);
                Glide.with(this)
                        .load(galleryThumbUri)
                        .into(galleryThumbnailView);
                isDeleteThumbnail = false;
                galleryThumbnailTextView.setTextColor(Color.parseColor("#000000"));
            }

            nThumbnail = 0;
        } else {
            String imgUri = intent.getStringExtra("IMG_URI");
            coverImgUri = Uri.parse(imgUri);
            originCoverImgUri = coverImgUri;

            if(imgUri != null) {
                coverImgView.setClipToOutline(true);
                coverImgBtn.setVisibility(View.INVISIBLE);
                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(coverImgUri)
                        .into(coverImgView);
                isDeletePoster = false;

                nThumbnail = 1;
                ThumbnailPreviewActivity.bModifyThumb = true;
                CropImageActivity.bThumbnail = true;
                CropImage.activity(coverImgUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(25, 20)
                        .start(WorkEditActivity.this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {                          //   캐릭터 추가
            if (requestCode == 1000) {
                int nType = data.getIntExtra("TYPE", 0);

                if (nType == 0)
                    return;
                else if (nType == 1) {
//                    Intent intent = new Intent(WorkEditActivity.this, SeesoGalleryActivity.class);
                    Intent intent = new Intent(WorkEditActivity.this, TokkiGalleryActivity.class);
                    if(nThumbnail == 0)
                        intent.putExtra("TYPE", TokkiGalleryFragment.TYPE_COVER_IMG_MODIFY);
                    else
                        intent.putExtra("TYPE", TokkiGalleryFragment.TYPE_THUMBNAIL_MODIFY);
                    startActivity(intent);
                } else if (nType == 2) {
                    Intent intent = new Intent(WorkEditActivity.this, PhotoPickerActivity.class);
                    if(nThumbnail == 0)
                        intent.putExtra("TYPE", TokkiGalleryFragment.TYPE_COVER_IMG_MODIFY);
                    else
                        intent.putExtra("TYPE", TokkiGalleryFragment.TYPE_THUMBNAIL_MODIFY);
                    startActivity(intent);
                } else if(nType == 3) {
                    if(nThumbnail == 0) {
                        coverImgView.setImageResource(0);
                        coverImgBtn.setVisibility(View.VISIBLE);
                        originCoverImgUri = null;
                        posterThumbnailView.setImageResource(R.drawable.empty_thumbnail_img);
                        coverImgUri = null;
                        posterThumbUri = null;


                        posterThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                        posterCheckView.setImageResource(0);
                        posterThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));

                        if(nCurrentThumbnail == 1) {
                            isDeleteThumbnail = true;
                            nCurrentThumbnail = 0;
                        }
                        isDeletePoster = true;
                    } else {
                        galleryThumbnailLayout.setBackgroundResource(R.drawable.round_shadow_btn_white_bg);
                        galleryCheckView.setImageResource(0);
                        galleryThumbnailView.setImageResource(R.drawable.poster_sombnail);
                        galleryThumbnailTextView.setTextColor(Color.parseColor("#d1d1d1"));
                        galleryThumbUri = null;
                        isDeleteThumbnail = true;
                        nCurrentThumbnail = 0;
                    }

                    nThumbnail = 0;
                }
            } else if(requestCode == 1010) {
                String strGenre = data.getStringExtra("GENRE");
                inputGenreView.setText(strGenre);
            } else if(requestCode == 1020) {
                String strTag = data.getStringExtra("TAG");
                inputTagView.setText(strTag);
            }
        }
    }


    public void OnClickOkBtn(View view) {
        String strTitle = inputTitleView.getText().toString();
        String strSynopsis = inputSynopsisView.getText().toString();

        if(strTitle.length() == 0) {
            Toast.makeText(WorkEditActivity.this, "제목을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(strSynopsis.length() == 0) {
            Toast.makeText(WorkEditActivity.this, "줄거리를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

//        if(coverImgUri == null) {
//            Toast.makeText(WorkEditActivity.this, "이미지를 선택해주세요.", Toast.LENGTH_LONG).show();
//            return;
//        }

        requestModifyData();
    }

    private void getTagData() {
        tagList = new ArrayList<>();
        genreList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getWorkTagWithID(new OkHttpClient(), "" + workVO.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(resultObject == null) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(WorkEditActivity.this, "작품 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONArray tagArray = resultObject.getJSONArray("TAG_LIST");

                            for(int i = 0 ; i < tagArray.length() ; i++) {
                                JSONObject object = tagArray.getJSONObject(i);
                                tagList.add(object.getString("TAG_TITLE"));
                            }

                            JSONArray genreArray = resultObject.getJSONArray("GENRE_LIST");
                            for(int i = 0 ; i < genreArray.length() ; i++) {
                                JSONObject object = genreArray.getJSONObject(i);
                                genreList.add(object.getString("GENRE_NAME"));
                            }

                            String strGenre = "";
                            for(String genre : genreList) {
                                if(strGenre.length() > 0)
                                    strGenre += " / ";

                                strGenre += genre;
                            }
//                            initViews();
                            inputGenreView.setText(strGenre);

                            String strTags = "";
                            for(String tag : tagList) {
                                if(strTags.length() > 0)
                                    strTags += " ";
                                strTags += tag;
                            }

                            inputTagView.setText(strTags);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
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
                strFilePath = CommonUtils.getRealPathFromURI(WorkEditActivity.this, coverImgUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(WorkEditActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart(filename, filename, RequestBody.create(MultipartBody.FORM, sourceFile));
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
                    Toast.makeText(WorkEditActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
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

                                    Intent intent = new Intent(WorkEditActivity.this, WorkWriteMainActivity.class);
                                    intent.putExtra("WORK_ID", strWorkID);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(WorkEditActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(WorkEditActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void OnClickPhotoBtn(View view) {
        nThumbnail = 0;
        TedPermission.with(WorkEditActivity.this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {             // 모든 권한 획득
            Intent intent = new Intent(WorkEditActivity.this, CoverMediaSelectPopup.class);
            startActivityForResult(intent, 1000);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(WorkEditActivity.this, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show();
        }
    };

    private void requestModifyData() {
        CommonUtils.showProgressDialog(WorkEditActivity.this, "작품을 수정하고 있습니다.");

        try {
            String url = CommonUtils.strDefaultUrl + "PanBookModifyWork.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;
            String strFilePath = null;

            MultipartBody.Builder builder = new MultipartBody.Builder();
            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("WORK_ID", "" + workVO.getnWorkID())
                    .addFormDataPart("WRITER_ID", pref.getString("USER_ID", "Guest"))
                    .addFormDataPart("WORK_TITLE", inputTitleView.getText().toString())
                    .addFormDataPart("WORK_SYNOPSIS", inputSynopsisView.getText().toString())
                    .addFormDataPart("WORK_COMPLETE", bComplete == true ? "Y" : "N")
                    .addFormDataPart("WORK_TARGET", "")
                    .addFormDataPart("DELETE_THUMBNAIL", isDeleteThumbnail == true ? "Y" : "N")
                    .addFormDataPart("DELETE_POSTER", isDeletePoster == true ? "Y" : "N");

            //

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
                strFilePath = CommonUtils.getRealPathFromURI(WorkEditActivity.this, coverImgUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(WorkEditActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart("COVER_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            }

            if(posterThumbUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(WorkEditActivity.this, posterThumbUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(WorkEditActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/")+1);
                builder.addFormDataPart("POSTER_THUMBNAIL_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            } else if(galleryThumbUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(WorkEditActivity.this, galleryThumbUri);
                sourceFile = new File(strFilePath);

                if(!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(WorkEditActivity.this, "이미지가 잘못되었습니다.", Toast.LENGTH_LONG).show();
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
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(WorkEditActivity.this, "작품 수정을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.code() != 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                CommonUtils.makeText(WorkEditActivity.this, "서버와의 연결이 원활하지 않습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    final String strResult = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    Toast.makeText(WorkEditActivity.this, "수정되었습니다.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(WorkEditActivity.this, "작품 수정을 실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(WorkEditActivity.this, "작품 수정을 실패하였습니다.", Toast.LENGTH_LONG).show();
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
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(WorkEditActivity.this, "작품 생성을 실패하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void onClickGalleryThumbnailBtn(View view) {
        nThumbnail = 2;
        TedPermission.with(WorkEditActivity.this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    public void onClickPosterThumbnailBtn(View view) {
        if(coverImgUri == null && originCoverImgUri == null) {
            Toast.makeText(this, "포스터 이미지를 설정하셔야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        nThumbnail = 1;
        ThumbnailPreviewActivity.bModifyThumb = true;
        CropImageActivity.bThumbnail = true;
        CropImage.activity(originCoverImgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(25, 20)
                .start(WorkEditActivity.this);
    }
}
