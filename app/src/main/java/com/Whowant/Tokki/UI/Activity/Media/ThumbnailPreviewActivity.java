package com.Whowant.Tokki.UI.Activity.Media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Activity.Work.CreateCharacterActivity;
import com.Whowant.Tokki.UI.Activity.Work.CreateWorkActivity;
import com.Whowant.Tokki.UI.Activity.Work.InteractionWriteActivity;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureWriteActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkEditActivity;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_BG_CROP;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER_THUMB;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_IMG_CROP;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_MODIFY;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_MODIFY_THUMB;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_PROFILE;

public class ThumbnailPreviewActivity extends AppCompatActivity {
    private ImageView cropedImageView;
    public static CropImageView.CropResult result;

    public static int     nNextType = -1;

    public static boolean bEdit = false;
    public static int     nType;
    public static int     nOrder;
    private String resultUri = "";
    public static boolean bInteraction = false;

//    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail_preview);

        cropedImageView = (ImageView)findViewById(R.id.cropedImageView);
        resultUri = result.getUri().toString();

        if (nNextType != TYPE_PROFILE.ordinal()) {
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(result.getUri())
                    .into(cropedImageView);
        } else {
            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(result.getUri())
                    .apply(new RequestOptions().circleCrop())
                    .into(cropedImageView);
        }

        ImageButton closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNext();
            }
        });
    }

    private void moveToNext() {
        Intent intent = null;
        if (nNextType == TYPE_PROFILE.ordinal()) {
            intent = new Intent(ThumbnailPreviewActivity.this, MainActivity.class);
            intent.putExtra("URI", resultUri);
        } else if (nNextType == TYPE_COVER.ordinal()) {
            intent = new Intent(ThumbnailPreviewActivity.this, CreateWorkActivity.class);
            intent.putExtra("IMG_URI", resultUri);
        } else if (nNextType == TYPE_COVER_THUMB.ordinal()) {
            intent = new Intent(ThumbnailPreviewActivity.this, CreateWorkActivity.class);
            intent.putExtra("THUMBNAIL", true);
            intent.putExtra("IMG_URI", resultUri);
        } else if (nNextType == TYPE_MODIFY.ordinal()) {
            intent = new Intent(ThumbnailPreviewActivity.this, WorkEditActivity.class);
            intent.putExtra("IMG_URI", resultUri);
        } else if (nNextType == TYPE_MODIFY_THUMB.ordinal()) {
            intent = new Intent(ThumbnailPreviewActivity.this, WorkEditActivity.class);
            intent.putExtra("THUMBNAIL", true);
            intent.putExtra("IMG_URI", resultUri);
        } else if (nNextType == TYPE_IMG_CROP.ordinal()) {
            if (bInteraction) {
                intent = new Intent(ThumbnailPreviewActivity.this, InteractionWriteActivity.class);
            } else {
                intent = new Intent(ThumbnailPreviewActivity.this, LiteratureWriteActivity.class);
            }
            intent.putExtra("IMG_URI", resultUri.toString());
            intent.putExtra("EDIT", bEdit);
            intent.putExtra("TYPE", nType);
            intent.putExtra("ORDER", nOrder);
        } else if (nNextType == TYPE_BG_CROP.ordinal()) {
            if (bInteraction) {
                intent = new Intent(ThumbnailPreviewActivity.this, InteractionWriteActivity.class);
            } else {
                intent = new Intent(ThumbnailPreviewActivity.this, LiteratureWriteActivity.class);
            }
            intent.putExtra("BG_URI", resultUri.toString());
            intent.putExtra("EDIT", bEdit);
            intent.putExtra("ORDER", nOrder);
        } else {
            intent = new Intent(ThumbnailPreviewActivity.this, CreateCharacterActivity.class);
            intent.putExtra("URI", resultUri);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        nNextType = -1;
        bInteraction = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        bEdit = false;
//        nType = -1;
//        nOrder = -1;
    }
}