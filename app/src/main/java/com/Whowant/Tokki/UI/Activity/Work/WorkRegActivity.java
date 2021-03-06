package com.Whowant.Tokki.UI.Activity.Work;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.GenreRegActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.TokkiGalleryActivity;
import com.Whowant.Tokki.UI.Activity.TagRegActivity;
import com.Whowant.Tokki.UI.Popup.CoverMediaSelectPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.VO.CharacterRegVo;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.vansuita.gaussianblur.GaussianBlur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER_THUMB;

public class WorkRegActivity extends AppCompatActivity {
    ImageView photoIv;
    EditText titleEt;
    EditText summaryEt;
    TextView genreTv;
    TextView tagTv;

    CharacterAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<CharacterRegVo> characterRegVoArrayList = new ArrayList<>();


    ImageView owner0;
    ImageView owner1;
    TextView owner0L;
    TextView owner1L;

    ImageView copyright0;
    ImageView copyright1;
    TextView copyright0L;
    TextView copyright1L;


    ImageView status0;
    ImageView status1;
    ImageView status2;
    TextView status0L;
    TextView status1L;
    TextView status2L;

    TextView career;



    private Uri coverImgUri = null;
    private int nThumbnail = 0;     // 0 = ??????, 1 = ???????????? ????????????, 2 = ??????????????? ????????? ?????????

    private Uri posterThumbUri = null;
    private Uri galleryThumbUri = null;
    private ProgressDialog mProgressDialog;
    private boolean isDeletePoster = false;

    public static WorkVO workVO;

    Activity mActivity;

    int nOwner = 0;
    int nCopyright = 0;
    int nStatus = 0;

    int nTarget = 0;
    Bitmap blurredBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_reg);

        nTarget = getIntent().getIntExtra("NOVEL_TYPE", 0);
        mActivity = this;

        initView();

        if (workVO != null) {
            setData();
        }
    }

    private void setData() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("?????? ??????");

        titleEt.setText(workVO.getTitle());
        summaryEt.setText(workVO.getSynopsis());

        if (workVO.getCoverFile() != null && workVO.getCoverFile().length() > 0 && !workVO.getCoverFile().equals("null")) {
            photoIv.setClipToOutline(true);
            coverImgUri = Uri.parse(CommonUtils.strDefaultUrl + "images/" + workVO.getCoverFile());
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .placeholder(R.drawable.no_poster)
                    .load(CommonUtils.strDefaultUrl + "images/" + workVO.getCoverFile())
               //     .into(photoIv);
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            photoIv.setImageBitmap(resource);

                           // GaussianBlur.with(WorkRegActivity.this).size(300).radius(20).put(resource, photoIv);
                            blurredBitmap = GaussianBlur.with(WorkRegActivity.this).size(300).radius(20).render(resource);
//                            photoIv.setImageBitmap(blurredBitmap);

                        }
                    });


            isDeletePoster = false;


        }

        getTagData();
    }

    @Override
    protected void onNewIntent(Intent intent) {                                                             // ?????? ?????? ?????? ?????? onNewIntent ??? ????????? ?????? ????????? ????????????
        super.onNewIntent(intent);

        boolean bThumbnail = intent.getBooleanExtra("THUMBNAIL", false);

        if (bThumbnail) {
            /*String imgUri = intent.getStringExtra("IMG_URI");

            if (nThumbnail == 1) {           // ??????????????? ????????? ??????
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
            } else if (nThumbnail == 2) {    // ??????????????? ????????? ??????
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

            nThumbnail = 0;*/
        } else {                                                                                    // ?????? ????????? ?????? ??????. ????????? ????????? Crop ?????? ?????????
            photoIv.setImageResource(R.drawable.ic_i_artwork_empty);
            photoIv.setClipToOutline(true);
            String imgUri = intent.getStringExtra("IMG_URI");
            coverImgUri = Uri.parse(imgUri);

            if (imgUri != null) {
//                coverImgBtn.setVisibility(View.INVISIBLE);
                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(coverImgUri)
                     //   .into(photoIv);
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                photoIv.setImageBitmap(resource);

                                // GaussianBlur.with(WorkRegActivity.this).size(300).radius(20).put(resource, photoIv);
                                blurredBitmap = GaussianBlur.with(WorkRegActivity.this).size(300).radius(20).render(resource);
//                            photoIv.setImageBitmap(blurredBitmap);

                            }
                        });
                /*nThumbnail = 1;
                ThumbnailPreviewActivity.bCoverThumb = true;
                CropImageActivity.bThumbnail = true;
                CropImage.activity(coverImgUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(25, 20)
                        .start(mActivity);*/
            }
        }
    }

    void initUI()
    {
        if(nOwner == 0)
        {
            owner0.setImageResource(R.drawable.i_radio_2);
            owner1.setImageResource(R.drawable.i_radio_1);

        }
        else
        {
            owner0.setImageResource(R.drawable.i_radio_1);
            owner1.setImageResource(R.drawable.i_radio_2);

        }

        if(nCopyright == 0)
        {
            copyright0.setImageResource(R.drawable.i_radio_2);
            copyright1.setImageResource(R.drawable.i_radio_1);

        }
        else
        {
            copyright0.setImageResource(R.drawable.i_radio_1);
            copyright1.setImageResource(R.drawable.i_radio_2);

        }

        if(nStatus == 0)
        {
            status0.setImageResource(R.drawable.i_radio_2);
            status1.setImageResource(R.drawable.i_radio_1);
            status2.setImageResource(R.drawable.i_radio_1);

        }
        else if(nStatus == 1)
        {
            status0.setImageResource(R.drawable.i_radio_1);
            status1.setImageResource(R.drawable.i_radio_2);
            status2.setImageResource(R.drawable.i_radio_1);

        }

        else
        {
            status0.setImageResource(R.drawable.i_radio_1);
            status1.setImageResource(R.drawable.i_radio_1);
            status2.setImageResource(R.drawable.i_radio_2);


        }
        status0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status0.setImageResource(R.drawable.i_radio_2);
                status1.setImageResource(R.drawable.i_radio_1);
                status2.setImageResource(R.drawable.i_radio_1);

                nStatus = 0;
            }
        });
    }
    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("?????? ??????");

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        photoIv = findViewById(R.id.iv_work_reg_photo);
        photoIv.setClipToOutline(true);
        titleEt = findViewById(R.id.et_work_reg_title);
        summaryEt = findViewById(R.id.et_work_reg_summary);
        genreTv = findViewById(R.id.tv_work_reg_genre);
        tagTv = findViewById(R.id.tv_work_reg_tag);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                outRect.right = DeviceUtils.dpToPx(mActivity, 10);
            }
        });
        adapter = new CharacterAdapter(this, characterRegVoArrayList);
        recyclerView.setAdapter(adapter);

        career = findViewById(R.id.career);

        owner0 = findViewById(R.id.owner0);
        owner1 = findViewById(R.id.owner1);

        owner0L = findViewById(R.id.owner0L);
        owner1L = findViewById(R.id.owner1L);

        copyright0 = findViewById(R.id.copyright0);
        copyright1 = findViewById(R.id.copyright1);

        copyright0L = findViewById(R.id.copyright0L);
        copyright1L = findViewById(R.id.copyright1L);


        status0 = findViewById(R.id.status0);
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);

        status0L = findViewById(R.id.status0L);
        status1L = findViewById(R.id.status1L);
        status2L = findViewById(R.id.status2L);


        if(workVO != null)
        {
            nStatus =  workVO.getStatus();
            nCopyright =  workVO.getCopyright();
            nOwner =  workVO.getOwner();

            career.setText(workVO.getStrCareer());
        }
        initUI();

        status1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status0.setImageResource(R.drawable.i_radio_1);
                status1.setImageResource(R.drawable.i_radio_2);
                status2.setImageResource(R.drawable.i_radio_1);
                nStatus = 1;
            }
        });
        status2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status0.setImageResource(R.drawable.i_radio_1);
                status1.setImageResource(R.drawable.i_radio_1);
                status2.setImageResource(R.drawable.i_radio_2);
                nStatus = 2;

            }
        });
        status0L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status0.setImageResource(R.drawable.i_radio_2);
                status1.setImageResource(R.drawable.i_radio_1);
                status2.setImageResource(R.drawable.i_radio_1);
                nStatus = 0;

            }
        });
        status1L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status0.setImageResource(R.drawable.i_radio_1);
                status1.setImageResource(R.drawable.i_radio_2);
                status2.setImageResource(R.drawable.i_radio_1);
                nStatus = 1;

            }
        });
        status2L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status0.setImageResource(R.drawable.i_radio_1);
                status1.setImageResource(R.drawable.i_radio_1);
                status2.setImageResource(R.drawable.i_radio_2);
                nStatus = 2;

            }
        });
        owner0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                owner0.setImageResource(R.drawable.i_radio_2);
                owner1.setImageResource(R.drawable.i_radio_1);
                nOwner = 0;
            }
        });
        owner0L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                owner0.setImageResource(R.drawable.i_radio_2);
                owner1.setImageResource(R.drawable.i_radio_1);
                nOwner = 0;
            }
        });

        owner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                owner0.setImageResource(R.drawable.i_radio_1);
                owner1.setImageResource(R.drawable.i_radio_2);
                nOwner = 1;
            }
        });
        owner1L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                owner0.setImageResource(R.drawable.i_radio_1);
                owner1.setImageResource(R.drawable.i_radio_2);
                nOwner = 1;
            }
        });
        ////

        copyright0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyright0.setImageResource(R.drawable.i_radio_2);
                copyright1.setImageResource(R.drawable.i_radio_1);
                nCopyright = 0;
            }
        });
        copyright0L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyright0.setImageResource(R.drawable.i_radio_2);
                copyright1.setImageResource(R.drawable.i_radio_1);
                nCopyright = 0;
            }
        });

        copyright1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyright0.setImageResource(R.drawable.i_radio_1);
                copyright1.setImageResource(R.drawable.i_radio_2);
                nCopyright = 1;
            }
        });
        copyright1L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyright0.setImageResource(R.drawable.i_radio_1);
                copyright1.setImageResource(R.drawable.i_radio_2);
                nCopyright = 1;
            }
        });




    }

    public class CharacterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<CharacterRegVo> arrayList;

        public CharacterAdapter(Context context, ArrayList<CharacterRegVo> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_work_reg, null);
            return new CharacterViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CharacterRegVo item = arrayList.get(position);

            if (holder instanceof CharacterViewHolder) {
                CharacterViewHolder viewHolder = (CharacterViewHolder) holder;

                if (!TextUtils.isEmpty(item.getImgUri())) {
                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(item.getImgUri())
                            .apply(new RequestOptions().circleCrop())
                            .into(viewHolder.photoIv);
                }

                viewHolder.nameTv.setText(item.getName());
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_work_reg_photo);
            nameTv = itemView.findViewById(R.id.tv_row_work_reg_name);
        }
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {             // ?????? ?????? ??????
            Intent intent = new Intent(mActivity, CoverMediaSelectPopup.class);
            startActivityForResult(intent, 1000);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(mActivity, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
        }
    };

    // ?????? ????????? ??????
    public void btnPhotoChange(View v) {
        nThumbnail = 0;

        TedPermission.with(mActivity)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // ???????????? ??????
    public void btnCharaterReg(View v) {
        Intent intent = new Intent(mActivity, CharacterRegActivity.class);
        startActivityForResult(intent, 887);
    }

    // ?????? ??????
    public void btnSave(View v) {

        String strTitle = titleEt.getText().toString();
        String strSynopsis = summaryEt.getText().toString();

        if (strTitle.length() == 0) {
            Toast.makeText(mActivity, "????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        if (strSynopsis.length() == 0) {
            Toast.makeText(mActivity, "???????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        String strGenre = genreTv.getText().toString();
        if (strGenre.length() == 0) {
            Toast.makeText(mActivity, "????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        if (workVO != null) {
            requestModifyData();
        } else {
            requestCreateWork();
        }
    }

    // ?????? ??????
    public void btnGenre(View v) {
        Intent intent = new Intent(mActivity, GenreRegActivity.class);
        intent.putExtra("genre", genreTv.getText().toString().replace(" / ", "/"));
        startActivityForResult(intent, 911);
    }

    // ?????? ??????
    public void btnTag(View v) {
        Intent intent = new Intent(mActivity, TagRegActivity.class);
        intent.putExtra("TAG", tagTv.getText().toString());
        startActivityForResult(intent, 922);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                                     //
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 887 && resultCode == RESULT_OK) {
            if (data != null) {
                String returnData = data.getStringExtra("data");

                CharacterRegVo vo = new Gson().fromJson(returnData, CharacterRegVo.class);
                characterRegVoArrayList.add(vo);

                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == 911 && resultCode == RESULT_OK) {
            if (data != null) {
                String genre = data.getStringExtra("genre");
                genreTv.setText(genre);
            }
        } else if (requestCode == 922 && resultCode == RESULT_OK) {
            if (data != null) {
                String tag = data.getStringExtra("tag");
                tagTv.setText(tag);
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {                                                                                      // ?????? ????????? ?????? ????????? ????????? ??????
                int nType = data.getIntExtra("TYPE", 0);

                if (nType == 0)
                    return;
                else if (nType == 1) {
                    Intent intent = new Intent(mActivity, TokkiGalleryActivity.class);
                    if (nThumbnail == 2)
                        intent.putExtra("TYPE", TYPE_COVER_THUMB.ordinal());
                    else
                        intent.putExtra("TYPE", TYPE_COVER.ordinal());

                    startActivity(intent);
                    isDeletePoster = false;
                } else if (nType == 2) {
                    Intent intent = new Intent(mActivity, PhotoPickerActivity.class);
                    if (nThumbnail == 2)
                        intent.putExtra("TYPE", TYPE_COVER_THUMB.ordinal());
                    else
                        intent.putExtra("TYPE", TYPE_COVER.ordinal());
                    startActivity(intent);
                    isDeletePoster = false;
                } else {
                    isDeletePoster = true;
                    photoIv.setImageResource(0);
                    photoIv.setImageResource(R.drawable.ic_i_artwork_empty);
                    coverImgUri = null;
                }
            }/* else if(requestCode == 1010) {                                                                            // ?????? ?????????
                String strGenre = data.getStringExtra("GENRE");
                inputGenreView.setText(strGenre);
            } else if(requestCode == 1020) {                                                                            // ?????? ?????????
                String strTag = data.getStringExtra("TAG");
                inputTagView.setText(strTag);
            }*/
        }
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
            File sourceFile0 = null;

            String strFilePath = null;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String filename0 = currentDateandTime ;


            MultipartBody.Builder builder = new MultipartBody.Builder();
            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
/*
        multipartFormData.append(String(data.status).data(using: String.Encoding.utf8)!, withName: "STATUS")

            multipartFormData.append(data.career.data(using: String.Encoding.utf8)!, withName: "CAREER")
            multipartFormData.append(String(data.ownership).data(using: String.Encoding.utf8)!, withName: "OWNERSHIP")
            multipartFormData.append(String(data.copyright).data(using: String.Encoding.utf8)!, withName: "COPYRIGHT")
 */

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("WRITER_ID", pref.getString("USER_ID", "Guest"))
                    .addFormDataPart("WORK_TITLE", titleEt.getText().toString())
                    .addFormDataPart("WORK_SYNOPSIS", summaryEt.getText().toString())
                    .addFormDataPart("STATUS", String.valueOf(nStatus))
                    .addFormDataPart("COPYRIGHT", String.valueOf(nCopyright))
                    .addFormDataPart("OWNERSHIP", String.valueOf(nOwner))
                    .addFormDataPart("CAREER", career.getText().toString())
                    .addFormDataPart("WORK_TARGET", String.valueOf(nTarget));

            String strTags = tagTv.getText().toString();
            if (strTags.length() > 0)
                builder.addFormDataPart("TAGS", strTags);

            String strGenres = genreTv.getText().toString();
            if (strGenres.length() > 0) {
                String genres[] = strGenres.split(" / ");
                strGenres = "";
                for (String genre : genres) {
                    if (strGenres.length() > 0)
                        strGenres += " ";

                    strGenres += genre;
                }
                builder.addFormDataPart("WORK_GENRE", strGenres);
            }

            if (coverImgUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(mActivity, coverImgUri);
                sourceFile = new File(strFilePath);
                if(blurredBitmap != null)
                {
                    String savePath = saveBitmapToJpeg(this,blurredBitmap,filename0);

                    sourceFile0 = new File(savePath);
                    builder.addFormDataPart("COVER_IMG", filename0, RequestBody.create(MultipartBody.FORM, sourceFile0));

                }

                if (!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(mActivity, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
                builder.addFormDataPart("COVER_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));

                builder.addFormDataPart("BACKGROUND", filename0 + ".jpg", RequestBody.create(MultipartBody.FORM, sourceFile0      ));

            }

            if (posterThumbUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(mActivity, posterThumbUri);
                sourceFile = new File(strFilePath);

                if (!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(mActivity, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
                builder.addFormDataPart("POSTER_THUMBNAIL_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            } else if (galleryThumbUri != null) {
                strFilePath = CommonUtils.getRealPathFromURI(mActivity, galleryThumbUri);
                sourceFile = new File(strFilePath);

                if (!sourceFile.exists()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(mActivity, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
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
                    Toast.makeText(mActivity, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strWorkID = resultObject.getString("WORK_ID");

                                    Intent intent = new Intent(mActivity, WorkWriteMainActivity.class);
                                    intent.putExtra("WORK_ID", strWorkID);
                                    intent.putExtra("NOVEL_TYPE", nTarget);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(mActivity, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(mActivity, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getTagData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getWorkTagWithID(new OkHttpClient(), "" + workVO.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultObject == null) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(mActivity, "?????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONArray tagArray = resultObject.getJSONArray("TAG_LIST");

                            StringBuffer tagBuffer = new StringBuffer();
                            for (int i = 0; i < tagArray.length(); i++) {
                                JSONObject object = tagArray.getJSONObject(i);
                                if (i != 0) {
                                    tagBuffer.append(" ");
                                }
                                tagBuffer.append(object.getString("TAG_TITLE"));
//                                tagList.add(object.getString("TAG_TITLE"));
                            }

                            tagTv.setText(tagBuffer.toString());

                            JSONArray genreArray = resultObject.getJSONArray("GENRE_LIST");

                            StringBuffer genreBuffer = new StringBuffer();
                            for (int i = 0; i < genreArray.length(); i++) {
                                JSONObject object = genreArray.getJSONObject(i);
                                if (i != 0) {
                                    genreBuffer.append(" / ");
                                }
                                genreBuffer.append(object.getString("GENRE_NAME"));
//                                genreList.add(object.getString("GENRE_NAME"));
                            }

                            genreTv.setText(genreBuffer.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
    public String saveBitmapToJpeg(Context context,Bitmap bitmap, String name){

        File storage = context.getCacheDir(); // ??? ????????? ???????????? ?????? ??????

        String fileName = name + ".jpg";  // ??????????????? ????????????!

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // ????????? ???????????????

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out);  // ?????? ?????? bitmap??? jpeg(????????????)?????? ????????????

            out.close(); // ???????????? ???????????????.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();   // ???????????? ??????????????? ??????????????? ???!
    }
    private void requestModifyData() {
        CommonUtils.showProgressDialog(mActivity, "????????? ???????????? ????????????.");

        try {
            String url = CommonUtils.strDefaultUrl + "PanBookModifyWork.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;
            File sourceFile0 = null;

            String strFilePath = null;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String filename0 = currentDateandTime ;

            String savePath = "";
            if(blurredBitmap != null)
                savePath =  saveBitmapToJpeg(this,blurredBitmap,filename0);

            MultipartBody.Builder builder = new MultipartBody.Builder();
            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("WORK_ID", "" + workVO.getnWorkID())
                    .addFormDataPart("WRITER_ID", pref.getString("USER_ID", "Guest"))
                    .addFormDataPart("WORK_TITLE", titleEt.getText().toString())
                    .addFormDataPart("WORK_SYNOPSIS", summaryEt.getText().toString())
//                    .addFormDataPart("WORK_COMPLETE", bComplete == true ? "Y" : "N")
                    .addFormDataPart("WORK_COMPLETE", "N")
                    .addFormDataPart("WORK_TARGET", String.valueOf(nTarget))
//                    .addFormDataPart("DELETE_THUMBNAIL", isDeleteThumbnail == true ? "Y" : "N")
                    .addFormDataPart("DELETE_THUMBNAIL", "N")
                    .addFormDataPart("STATUS", String.valueOf(nStatus))
                    .addFormDataPart("COPYRIGHT", String.valueOf(nCopyright))
                    .addFormDataPart("OWNERSHIP", String.valueOf(nOwner))
                    .addFormDataPart("CAREER", career.getText().toString())
                    .addFormDataPart("DELETE_POSTER", isDeletePoster? "Y" : "N");

         //   BACKGROUND

            //

            String strTags = tagTv.getText().toString();
            if (strTags.length() > 0)
                builder.addFormDataPart("TAGS", strTags);

            String strGenres = genreTv.getText().toString();
            if (strGenres.length() > 0) {
                String genres[] = strGenres.split(" / ");
                strGenres = "";
                for (String genre : genres) {
                    if (strGenres.length() > 0)
                        strGenres += " ";

                    strGenres += genre;
                }
                builder.addFormDataPart("WORK_GENRE", strGenres);
            }
//      GaussianBlur.with(this).size(300).radius(10).put(R.drawable.user_icon, photoIv);
            if (coverImgUri != null && !coverImgUri.toString().startsWith("http")) {
                strFilePath = CommonUtils.getRealPathFromURI(mActivity, coverImgUri);
                sourceFile = new File(strFilePath);
                sourceFile0 = new File(savePath);

                if (!sourceFile.exists()) {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(mActivity, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
                builder.addFormDataPart("COVER_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));

                builder.addFormDataPart("BACKGROUND", filename0 + ".jpg", RequestBody.create(MultipartBody.FORM, sourceFile0      ));

            }


//            if (posterThumbUri != null) {
//                strFilePath = CommonUtils.getRealPathFromURI(mActivity, posterThumbUri);
//                sourceFile = new File(strFilePath);
//
//                if (!sourceFile.exists()) {
//                    mProgressDialog.dismiss();
//                    Toast.makeText(mActivity, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
//                builder.addFormDataPart("POSTER_THUMBNAIL_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
//            } else if (galleryThumbUri != null) {
//                strFilePath = CommonUtils.getRealPathFromURI(mActivity, galleryThumbUri);
//                sourceFile = new File(strFilePath);
//
//                if (!sourceFile.exists()) {
//                    mProgressDialog.dismiss();
//                    Toast.makeText(mActivity, "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
//                builder.addFormDataPart("GALLERY_THUMBNAIL_IMG", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
//            }

            requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    CommonUtils.hideProgressDialog();
                    Toast.makeText(mActivity, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                CommonUtils.makeText(mActivity, "???????????? ????????? ???????????? ????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
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

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    Toast.makeText(mActivity, "?????????????????????.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(mActivity, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(mActivity, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(mActivity, "?????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}