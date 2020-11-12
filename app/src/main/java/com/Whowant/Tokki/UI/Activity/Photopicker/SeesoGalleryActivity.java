package com.Whowant.Tokki.UI.Activity.Photopicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureWriteActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.cropper.CropImage;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_BG;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_CONTENTS_IMG;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_CONTENTS_IMG_NAR;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER_IMG_MODIFY;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_COVER_THUMB;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_FACE_IMG;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_PROFILE;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_MODIFY_THUMB;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_BG_CROP;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_IMG_CROP;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_MODIFY;

public class SeesoGalleryActivity extends AppCompatActivity {
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private MyAdapter mAdapter;
    private GridView mGridView;

//    public static final int TYPE_FACE_IMG = 0;
//    public static final int TYPE_CONTENTS_IMG = 1;
//    public static final int TYPE_COVER_IMG = 2;
//    public static final int TYPE_VIDEO = 3;
//    public static final int TYPE_BG = 4;
//    public static final int TYPE_PROFILE = 5;
//    public static final int TYPE_COVER_IMG_MODIFY = 7;
//    public static final int TYPE_CONTENTS_IMG_NAR = 8;          // 나레이션 이미지 추가
//    public static final int TYPE_COVER_THUMB_IMG = 9;
//    public static final int TYPE_THUMBNAIL_MODIFY = 10;

    private boolean bEdit = false;
    private int     nOrder = -1;
    private int     nType = 0;

    private ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        nType = getIntent().getIntExtra("TYPE", 0);
        bEdit = getIntent().getBooleanExtra("EDIT", false);
        nOrder = getIntent().getIntExtra("ORDER", -1);

        imageList = new ArrayList<>();
        mAdapter = new MyAdapter(SeesoGalleryActivity.this, R.layout.image_row, imageList);
        mGridView = (GridView)findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strUrl = CommonUtils.strDefaultUrl + "talk_image/" + imageList.get(position);
                Uri uri = Uri.parse(strUrl);

                CropImage.ActivityBuilder cropImgBuilder = CropImage.activity(uri);

                cropImgBuilder
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.RECTANGLE);

                if(nType == TYPE_FACE_IMG.ordinal()) {
                    cropImgBuilder.setAspectRatio(1, 1)
                            .setCropShape(CropImageView.CropShape.OVAL);
                } else if(nType == TYPE_COVER.ordinal()) {
                    ThumbnailPreviewActivity.nNextType = TYPE_COVER.ordinal();
                } else if(nType == TYPE_COVER_THUMB.ordinal()) {
                    ThumbnailPreviewActivity.nNextType = TYPE_COVER_THUMB.ordinal();
                    cropImgBuilder.setAspectRatio(25, 20);
                } else if(nType == TYPE_COVER_IMG_MODIFY.ordinal()) {
                    ThumbnailPreviewActivity.nNextType = TYPE_MODIFY.ordinal();
                } else if(nType == TYPE_MODIFY_THUMB.ordinal()) {
                    ThumbnailPreviewActivity.nNextType = TYPE_MODIFY_THUMB.ordinal();
                    cropImgBuilder.setAspectRatio(25, 20);
                } else if(nType == TYPE_PROFILE.ordinal()) {
                    ThumbnailPreviewActivity.nNextType = TYPE_PROFILE.ordinal();
                    cropImgBuilder.setAspectRatio(1, 1)
                            .setCropShape(CropImageView.CropShape.OVAL);
                } else if(nType == TYPE_CONTENTS_IMG.ordinal() || nType == TYPE_CONTENTS_IMG_NAR.ordinal()) {
                    ThumbnailPreviewActivity.nNextType = TYPE_IMG_CROP.ordinal();
                    ThumbnailPreviewActivity.bEdit = bEdit;
                    ThumbnailPreviewActivity.nType = nType;
                    ThumbnailPreviewActivity.nOrder = nOrder;
                } else if(nType == TYPE_BG.ordinal()) {
                    ThumbnailPreviewActivity.nNextType = TYPE_BG_CROP.ordinal();
                    ThumbnailPreviewActivity.bEdit = bEdit;
                    ThumbnailPreviewActivity.nOrder = nOrder;
                }

                cropImgBuilder.start(SeesoGalleryActivity.this);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getGalleryData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void getGalleryData() {
        CommonUtils.showProgressDialog(SeesoGalleryActivity.this, "토키 갤러리 데이터를 가져오고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                imageList = HttpClient.getGalleryData(new OkHttpClient());
//                imageList.addAll(HttpClient.getGalleryData(new OkHttpClient()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(imageList == null || imageList.size() == 0) {
                            Toast.makeText(SeesoGalleryActivity.this, "갤러리를 불러오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAdapter = new MyAdapter(SeesoGalleryActivity.this, R.layout.image_row, imageList);
                        mGridView.setAdapter(mAdapter);

                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =  (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    class MyAdapter extends BaseAdapter {
        Context context;
        int layout;
        ArrayList<String> imgList;
        LayoutInflater inf;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public MyAdapter(Context context, int layout, ArrayList<String> imgList) {
            this.context = context;
            this.layout = layout;
            this.imgList = imgList;
            inf = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public Object getItem(int position) {
            return imgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null)
                convertView = inf.inflate(R.layout.image_row, null);

            convertView.setLayoutParams(mImageViewLayoutParams);
            ImageView iv = (ImageView)convertView.findViewById(R.id.imageView);
            String strUrl = CommonUtils.strDefaultUrl + "talk_image/" + imgList.get(position);

            Glide.with(SeesoGalleryActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strUrl)
                    .into(iv);

            return convertView;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
