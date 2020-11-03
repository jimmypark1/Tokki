package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.SeesoGalleryActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class TokkiGalleryFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Context mContext;
    private GridView gridView;
    private ArrayList<String> imageList;
    private String folderName;
    private MyAdapter mAdapter;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    public TokkiGalleryFragment(Context context, String folderName) {
        this.mContext = context;
        this.folderName = folderName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.activity_photo_picker, container, false);
        gridView = inflaterView.findViewById(R.id.gridView);
        getTokkiGalleryList();
        gridView.setOnItemClickListener(this);

        return inflaterView;
    }

    private void getTokkiGalleryList() {
        CommonUtils.showProgressDialog(getActivity(), "토키 갤러리 데이터를 가져오고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                imageList = HttpClient.getTokkiGalleryData(new OkHttpClient(), folderName);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(imageList == null || imageList.size() == 0) {
                            Toast.makeText(getActivity(), "갤러리를 불러오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // gridview에 뿌리기
                        mAdapter = new MyAdapter(getActivity(), R.layout.image_row, imageList);
                        gridView.setAdapter(mAdapter);

                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(gridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =  (gridView.getWidth() / numColumns) - mImageThumbSpacing;
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
            String strUrl = CommonUtils.strDefaultUrl + "tokki_gallery/" + folderName + "/" + imgList.get(position);

            Glide.with(getActivity())
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