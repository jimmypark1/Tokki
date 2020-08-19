package com.Whowant.Tokki.UI.Activity.Photopicker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class SeesoGalleryInteractionActivity extends AppCompatActivity {
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

    private int     nType = 0;

    private Intent oldIntent;
    private ArrayList<String> imageList;

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

        oldIntent = getIntent();
        nType = oldIntent.getIntExtra("TYPE", 0);



        imageList = new ArrayList<>();
        mAdapter = new MyAdapter(SeesoGalleryInteractionActivity.this, R.layout.image_row, imageList);
        mGridView = (GridView)findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strUrl = CommonUtils.strDefaultUrl + "talk_image/" + imageList.get(position);
                Uri uri = Uri.parse(strUrl);

                oldIntent.putExtra("URI", uri.toString());
                setResult(RESULT_OK, oldIntent);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getGalleryData();
    }

    private void getGalleryData() {
        CommonUtils.showProgressDialog(SeesoGalleryInteractionActivity.this, "Seeso 갤러리 데이터를 가져오고 있습니다.");

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
                            Toast.makeText(SeesoGalleryInteractionActivity.this, "갤러리를 불러오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAdapter = new MyAdapter(SeesoGalleryInteractionActivity.this, R.layout.image_row, imageList);
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

            Glide.with(SeesoGalleryInteractionActivity.this)
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
