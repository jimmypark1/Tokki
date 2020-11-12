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
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureWriteActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.cropper.CropImage;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static android.app.Activity.RESULT_OK;
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

public class TokkiGalleryFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Context mContext;
    private GridView gridView;
    private ArrayList<String> imageList;
    private String folderName;
    private MyAdapter mAdapter;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

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

    private Intent oldIntent;


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

        nType = getActivity().getIntent().getIntExtra("TYPE", 0);
        bEdit = getActivity().getIntent().getBooleanExtra("EDIT", false);
        nOrder = getActivity().getIntent().getIntExtra("ORDER", -1);

        oldIntent = getActivity().getIntent();
        nType = oldIntent.getIntExtra("TYPE", 0);

        int interaction = getArguments().getInt("Interaction");

        imageList = new ArrayList<>();
        mAdapter = new MyAdapter(getActivity(), R.layout.image_row, imageList);
        gridView = inflaterView.findViewById(R.id.gridView);
        getTokkiGalleryList();
        gridView.setAdapter(mAdapter);

        if (interaction == 100) { // 인터랙션일 때
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String strUrl = CommonUtils.strDefaultUrl + "talk_image/" + imageList.get(position);
                    Uri uri = Uri.parse(strUrl);

                    oldIntent.putExtra("URI", uri.toString());
                    getActivity().setResult(RESULT_OK, oldIntent);
                    getActivity().finish();
                }
            });
        } else { // 일반 작성창일 때
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String strUrl = CommonUtils.strDefaultUrl + "tokki_gallery/" + folderName + "/" + imageList.get(position);
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

                    cropImgBuilder.start(getActivity());

//                    if (nType == TYPE_FACE_IMG) {
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.OVAL)
//                                .setAspectRatio(1, 1)
//                                .start(getActivity());
//                    } else if (nType == TYPE_VIDEO) {
//                        Intent intent = new Intent(getActivity(), LiteratureWriteActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.putExtra("VIDEO_URI", uri.toString());
//                        intent.putExtra("EDIT", bEdit);
//                        intent.putExtra("ORDER", nOrder);
//                        startActivity(intent);
//                    } else if (nType == TYPE_COVER_IMG) {
//                        ThumbnailPreviewActivity.bCover = true;
//
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .start(getActivity());
//                    } else if (nType == TYPE_COVER_THUMB_IMG) {
//                        ThumbnailPreviewActivity.bCoverThumb = true;
//
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setAspectRatio(25, 20)
//                                .start(getActivity());
//                    } else if (nType == TYPE_COVER_IMG_MODIFY) {
//                        ThumbnailPreviewActivity.bModify = true;
//
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .start(getActivity());
//                    } else if (nType == TYPE_THUMBNAIL_MODIFY) {
//                        ThumbnailPreviewActivity.bModifyThumb = true;
//
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setAspectRatio(25, 20)
//                                .start(getActivity());
//                    } else if (nType == TYPE_PROFILE) {
//                        ThumbnailPreviewActivity.bProfile = true;
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.OVAL)
//                                .setAspectRatio(1, 1)
//                                .start(getActivity());
//                    } else if (nType == TYPE_CONTENTS_IMG || nType == TYPE_CONTENTS_IMG_NAR) {
//                        ThumbnailPreviewActivity.bImgCrop = true;
//                        ThumbnailPreviewActivity.bEdit = bEdit;
//                        ThumbnailPreviewActivity.nType = nType;
//                        ThumbnailPreviewActivity.nOrder = nOrder;
//
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .start(getActivity());
//                    } else if (nType == TYPE_BG) {
//                        ThumbnailPreviewActivity.bBGCrop = true;
//                        ThumbnailPreviewActivity.bEdit = bEdit;
//                        ThumbnailPreviewActivity.nOrder = nOrder;
//
//                        CropImage.activity(uri)
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setActivityTitle("My Crop")
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .start(getActivity());
//                    } else {
//                        Intent intent = new Intent(getActivity(), LiteratureWriteActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                        if (nType == TYPE_BG) {
//                            intent.putExtra("BG_URI", uri.toString());
//                            intent.putExtra("EDIT", bEdit);
//                            intent.putExtra("ORDER", nOrder);
//                        } else if (nType == TYPE_CONTENTS_IMG || nType == TYPE_CONTENTS_IMG_NAR) {
//                            intent.putExtra("IMG_URI", uri.toString());
//                            intent.putExtra("EDIT", bEdit);
//                            intent.putExtra("TYPE", nType);
//                            intent.putExtra("ORDER", nOrder);
//                        }
//
//                        startActivity(intent);
//                    }
                }
            });
        }
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