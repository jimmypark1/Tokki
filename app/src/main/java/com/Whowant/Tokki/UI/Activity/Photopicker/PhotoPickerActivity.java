package com.Whowant.Tokki.UI.Activity.Photopicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureWriteActivity;
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.Utils.cropper.CropImage;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PhotoPickerActivity extends AppCompatActivity {
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private MyAdapter mAdapter;
    private GridView mGridView;

    public static final int TYPE_FACE_IMG = 0;
    public static final int TYPE_CONTENTS_IMG = 1;
    public static final int TYPE_COVER_IMG = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_BG = 4;
    public static final int TYPE_PROFILE = 5;
    public static final int TYPE_COVER_IMG_MODIFY = 7;
    public static final int TYPE_CONTENTS_IMG_NAR = 8;          // 나레이션 이미지 추가
    public static final int TYPE_COVER_THUMB_IMG = 9;           // 커버 썸네일 이미지 추가
    public static final int TYPE_THUMBNAIL_MODIFY = 10;

    private boolean bEdit = false;
    private int     nOrder = -1;

    Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    String thumb_IMAGE_ID = MediaStore.Images.Thumbnails._ID;

    private int     nType = 0;

    Cursor mCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        ImageButton closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        nType = getIntent().getIntExtra("TYPE", 0);
        bEdit = getIntent().getBooleanExtra("EDIT", false);
        nOrder = getIntent().getIntExtra("ORDER", -1);
    }

    public void onResume() {
        super.onResume();
        reloadDatas();
    }

    private void reloadDatas() {
        String[] projection = new String[1];

        if(nType == TYPE_VIDEO) {
            sourceUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            thumb_IMAGE_ID = MediaStore.Video.Thumbnails._ID;
            projection[0] = MediaStore.Video.Media._ID;
//            projection[1] = MediaStore.Video.Media.DATA;
        } else {
            projection[0] = MediaStore.Images.Media._ID;
//            projection[1] = MediaStore.Images.Media.DATA;
        }

        String[] from = {thumb_IMAGE_ID};
        int[] to = {android.R.id.text1};

        final String orderBy = MediaStore.Images.Media._ID + " DESC";
        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(sourceUri, projection, null, null, orderBy);

        mAdapter = new MyAdapter(
                PhotoPickerActivity.this,
                R.layout.image_row,
                mCursor,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mGridView = (GridView)findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCursor.moveToPosition(position);

                int nImgID = mCursor.getInt(mCursor.getColumnIndex("_id"));
                Uri uri = ContentUris.withAppendedId(sourceUri, nImgID);

                if(nType == TYPE_FACE_IMG) {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setAspectRatio(1, 1)
                            .start(PhotoPickerActivity.this);
                } else if(nType == TYPE_VIDEO) {
                    Intent intent = new Intent(PhotoPickerActivity.this, LiteratureWriteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("VIDEO_URI", uri.toString());
                    intent.putExtra("EDIT", bEdit);
                    intent.putExtra("ORDER", nOrder);
                    startActivity(intent);
                } else if(nType == TYPE_COVER_IMG) {
                    ThumbnailPreviewActivity.bCover = true;

                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .start(PhotoPickerActivity.this);
                } else if(nType == TYPE_COVER_THUMB_IMG) {
                    ThumbnailPreviewActivity.bCoverThumb = true;

                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(25, 20)
                            .start(PhotoPickerActivity.this);
                }
                else if(nType == TYPE_COVER_IMG_MODIFY) {
                    ThumbnailPreviewActivity.bModify = true;

                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .start(PhotoPickerActivity.this);
                } else if(nType == TYPE_THUMBNAIL_MODIFY) {
                    ThumbnailPreviewActivity.bModifyThumb = true;

                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(25, 20)
                            .start(PhotoPickerActivity.this);
                } else if(nType == TYPE_PROFILE) {
                    ThumbnailPreviewActivity.bProfile = true;
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setAspectRatio(1, 1)
                            .start(PhotoPickerActivity.this);
                } else if(nType == TYPE_CONTENTS_IMG || nType == TYPE_CONTENTS_IMG_NAR) {
                    ThumbnailPreviewActivity.bImgCrop = true;
                    ThumbnailPreviewActivity.bEdit = bEdit;
                    ThumbnailPreviewActivity.nType = nType;
                    ThumbnailPreviewActivity.nOrder = nOrder;

                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .start(PhotoPickerActivity.this);
                } else if(nType == TYPE_BG) {
                    ThumbnailPreviewActivity.bBGCrop = true;
                    ThumbnailPreviewActivity.bEdit = bEdit;
                    ThumbnailPreviewActivity.nOrder = nOrder;

                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .start(PhotoPickerActivity.this);
                } else {
                    Intent intent = new Intent(PhotoPickerActivity.this, LiteratureWriteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    if(nType == TYPE_BG) {
                        intent.putExtra("BG_URI", uri.toString());
                        intent.putExtra("EDIT", bEdit);
                        intent.putExtra("ORDER", nOrder);
                    } else if(nType == TYPE_CONTENTS_IMG || nType == TYPE_CONTENTS_IMG_NAR) {
                        intent.putExtra("IMG_URI", uri.toString());
                        intent.putExtra("EDIT", bEdit);
                        intent.putExtra("TYPE", nType);
                        intent.putExtra("ORDER", nOrder);
                    }

                    startActivity(intent);
                }
            }
        });
    }

    private void readPhoto() {
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };

        Cursor cursor = getContentResolver().query(externalUri, projection, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "cursor null or cursor is empty");
            return;
        }

        do {
            int nImgID = cursor.getInt(cursor.getColumnIndex("_id"));
            Uri uri = ContentUris.withAppendedId(externalUri, nImgID);

            if(nType == TYPE_FACE_IMG) {
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .start(PhotoPickerActivity.this);
            }  else if(nType == TYPE_COVER_IMG) {
                ThumbnailPreviewActivity.bCover = true;

                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(PhotoPickerActivity.this);
            } else if(nType == TYPE_PROFILE) {
                ThumbnailPreviewActivity.bProfile = true;
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .start(PhotoPickerActivity.this);
            } else {
                Intent intent = new Intent(PhotoPickerActivity.this, LiteratureWriteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                if(nType == TYPE_BG) {
                    intent.putExtra("BG_URI", uri.toString());
                    intent.putExtra("EDIT", bEdit);
                    intent.putExtra("ORDER", nOrder);
                } else if(nType == TYPE_CONTENTS_IMG) {
                    intent.putExtra("IMG_URI", uri.toString());
                    intent.putExtra("EDIT", bEdit);
                    intent.putExtra("ORDER", nOrder);
                }

                startActivity(intent);
            }

        } while (cursor.moveToNext());
    }

    private void readVideo() {
        Uri externalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE
        };

        Cursor cursor = getContentResolver().query(externalUri, projection, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "cursor null or cursor is empty");
            return;
        }

        do {
            String contentUrl = externalUri.toString() + "/" + cursor.getString(0);

            int nImgID = cursor.getInt(cursor.getColumnIndex("_id"));
            Uri uri = ContentUris.withAppendedId(externalUri, nImgID);

            Intent intent = new Intent(PhotoPickerActivity.this, LiteratureWriteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("VIDEO_URI", uri.toString());
            intent.putExtra("EDIT", bEdit);
            intent.putExtra("ORDER", nOrder);
            startActivity(intent);
            return;
        } while (cursor.moveToNext());
    }

    private void readAudio() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("NewApi") @Override
                        public void onGlobalLayout() {
                            if (mAdapter.getNumColumns() == 0) {
                                final int numColumns = (int) Math.floor(mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                                if (numColumns > 0) {
                                    final int columnWidth =  (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                    mAdapter.setNumColumns(numColumns);
                                    mAdapter.setItemHeight(columnWidth);

                                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            }
                        }
                    });
        }
    }

    public String[] getPath(){
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, null);
        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i]= cursor.getString(dataColumnIndex);
        }

        cursor.close();
        return arrPath;
    }


    public class MyAdapter extends SimpleCursorAdapter {
        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
        private Cursor myCursor;

        public MyAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);

            myCursor = c;
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return myCursor.getCount();
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ImageView(mContext);
                ((ImageView) convertView).setScaleType(ImageView.ScaleType.CENTER_CROP);
                convertView.setLayoutParams(mImageViewLayoutParams);
            }

            // Check the height matches our calculated column width
            if (convertView.getLayoutParams().height != mItemHeight) {
                convertView.setLayoutParams(mImageViewLayoutParams);
            }

            myCursor.moveToPosition(position);
            int nImgID = myCursor.getInt(myCursor.getColumnIndex("_id"));
            Uri uri = ContentUris.withAppendedId(sourceUri, nImgID);

//            String strPath = myCursor.getString(myCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            Glide.with(mContext)
                    .load(uri)
                    .apply(new RequestOptions()
                            .override(mImageThumbSize)
                            .centerCrop())
                    .into((ImageView) convertView);

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