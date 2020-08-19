package com.Whowant.Tokki.UI.Activity.Media;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Activity.Work.CreateCharacterActivity;
import com.Whowant.Tokki.UI.Activity.Work.CreateWorkActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkEditActivity;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ThumbnailPreviewActivity extends AppCompatActivity {
    private ImageView cropedImageView;
    public static CropImageView.CropResult result;
    public static boolean bProfile = false;
    public static boolean bCover = false;
    public static boolean bModify = false;
    public static boolean bImgCrop = false;
//    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail_preview);

        cropedImageView = (ImageView)findViewById(R.id.cropedImageView);
//        httpClient = new OkHttpClient();

        final String resultUri = result.getUri().toString();

        if(bCover || bModify) {
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
                if(bProfile) {
                    bProfile = false;
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if(bCover) {
                    bCover = false;
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, CreateWorkActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if(bModify) {
                    bModify = false;
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, WorkEditActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, CreateCharacterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });

        Button okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bProfile) {
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("URI", resultUri);
                    startActivity(intent);
                    bProfile = false;
                } else if(bCover) {
                    bCover = false;
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, CreateWorkActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("IMG_URI", resultUri);
                    startActivity(intent);
                } else if(bModify) {
                    bModify = false;
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, WorkEditActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("IMG_URI", resultUri);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ThumbnailPreviewActivity.this, CreateCharacterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("URI", resultUri);
                    startActivity(intent);
                }
            }
        });
    }
}