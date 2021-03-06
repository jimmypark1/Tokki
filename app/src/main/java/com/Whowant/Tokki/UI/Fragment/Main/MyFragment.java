package com.Whowant.Tokki.UI.Fragment.Main;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Carrot.TotalCarrotListActivity;
import com.Whowant.Tokki.UI.Activity.Carrot.UsedCarrotListActivity;
import com.Whowant.Tokki.UI.Activity.Main.ChatActivity;
import com.Whowant.Tokki.UI.Activity.Main.FollowerActivity;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Activity.Main.UserProfileActivity;
import com.Whowant.Tokki.UI.Activity.Media.ThumbnailPreviewActivity;
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Adapter.MyWorkRecyclerAdapter;
import com.Whowant.Tokki.UI.Popup.ProfileEmailPopup;
import com.Whowant.Tokki.UI.Popup.ProfilePhotoPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.cropper.CropImage;
import com.Whowant.Tokki.Utils.cropper.CropImageView;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_PROFILE;

public class MyFragment extends Fragment {                                                  // 4?????? ???????????????
    private TextView followingView;                                                         // ?????? ?????????
    private TextView followerView;                                                          // ?????? ?????????

    private ImageView faceView;                                                             // ????????? ??????
    private TextView editBtn;
    private TextView nameView, typeView;                                                    // ??????, ????????? ?????? ???????????? ?????? ???
    private TextView descView;                                                              // ??? ?????? ???
    private TextView myRecommendCodeView, tokkiTokBtn;                                      // ????????????, ?????????(?????????) ??????
    private EditText inputDescView;                                                         // ??? ?????? ???????????? ???????????? ??????

    private SharedPreferences pref;
    private LinearLayout followingLayout;
    private LinearLayout followerLayout;

    private int nFollowerCount = 0;
    private int nFollowingCount = 0;
    private int nCurrentCarrot = 0;                                                         // ?????? ?????? ??????
    private int nTotalUsedCarrot = 0;                                                       // ??? ?????? ??????
    private int nDonationCarrot = 0;                                                        // ???????????? ?????? ??????
    private int nLevel = 1;                                                                 // ?????? ????????? ?????? ?????? ??????
    private int nTotalAcquireCarrot = 0;                                                    // ?????? ?????? ??????
    private String strRecommendCode = "";
    private boolean bVisible = false;
    private boolean bEdit = false;

    private ProgressBar currentCarrotProgressbar;
    private ProgressBar cumulativeUsageCarrotProgressbar;
    private ProgressBar totalCarrotProgressbar;
    private ImageButton copyRecommendCodeBtn;

    private RelativeLayout levelBGView;
    private ImageView lv1IconView, lv5IconView, lv10IconView, emptyIconView;                // ????????? ?????? ?????? ???????????? ???????????? ?????? ????????????
    private TextView currentCarrotCountView, cumulativeUsageCarrotCountView, totalCarrotCountView;

    private RecyclerView myWorkRecyclerView;                                                // ?????? ????????? ?????? ????????? ???????????? ??????????????? ???
    private MyWorkRecyclerAdapter adapter;
    private ArrayList<WorkVO> workList;

    private boolean bCamera = false;
    private static final int FROM_POPUP = 1000;
    private static final int FROM_EMAIL_AUTH = 1010;
    private static final int FROM_CAMERA = 1020;
    private Uri mPhotoUri;
    private String strMyDesc;

    public static Fragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.activity_mypage_renewal_layout, container, false);

        faceView = inflaterView.findViewById(R.id.profileImgView);
        editBtn = inflaterView.findViewById(R.id.editBtn);
        nameView = inflaterView.findViewById(R.id.nameView);
        descView = inflaterView.findViewById(R.id.descView);
        inputDescView = inflaterView.findViewById(R.id.inputDescView);
        followingView = inflaterView.findViewById(R.id.followingView);
        followerView = inflaterView.findViewById(R.id.followerView);
        followingLayout = inflaterView.findViewById(R.id.followingLayout);
        followerLayout = inflaterView.findViewById(R.id.followerLayout);
        myWorkRecyclerView = inflaterView.findViewById(R.id.myWorkRecyclerView);
        currentCarrotProgressbar = inflaterView.findViewById(R.id.currentCarrotProgressbar);
        cumulativeUsageCarrotProgressbar = inflaterView.findViewById(R.id.cumulativeUsageCarrotProgressbar);
        totalCarrotProgressbar = inflaterView.findViewById(R.id.totalCarrotProgressbar);
        currentCarrotCountView = inflaterView.findViewById(R.id.currentCarrotCountView);
        cumulativeUsageCarrotCountView = inflaterView.findViewById(R.id.cumulativeUsageCarrotCountView);
        totalCarrotCountView = inflaterView.findViewById(R.id.totalCarrotCountView);
        myRecommendCodeView = inflaterView.findViewById(R.id.myRecommendCodeView);
        copyRecommendCodeBtn = inflaterView.findViewById(R.id.copyRecommendCodeBtn);
        tokkiTokBtn = inflaterView.findViewById(R.id.tokkiTokBtn);
        typeView = inflaterView.findViewById(R.id.typeView);
        levelBGView = inflaterView.findViewById(R.id.levelBGView);
        lv1IconView = inflaterView.findViewById(R.id.lv1IconView);
        lv5IconView = inflaterView.findViewById(R.id.lv5IconView);
        lv10IconView = inflaterView.findViewById(R.id.lv10IconView);
        emptyIconView = inflaterView.findViewById(R.id.emptyIconView);

        LinearLayout usedMoreLayout = inflaterView.findViewById(R.id.usedMoreLayout);
        LinearLayout totalMoreLayout = inflaterView.findViewById(R.id.totalMoreLayout);

        usedMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UsedCarrotListActivity.class));
            }
        });

        totalMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TotalCarrotListActivity.class));
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {                               // ??? ????????? ????????? ??? descView ?????? ???????????? ???????????? wrap_content ??? ??????????????? ????????? ??????
                String strDesc = inputDescView.getText().toString();
                descView.setText(strDesc);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        inputDescView.addTextChangedListener(textWatcher);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bEdit)
                    onClickEditBtn();
                else {
                    bEdit = false;
                    editBtn.setText("");
                    editBtn.setBackgroundResource(R.drawable.info_edit_button);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputDescView.getWindowToken(), 0);

                    String desc = inputDescView.getText().toString();
                    if(desc.equals(strMyDesc) || desc.length() == 0) {
                        Toast.makeText(getActivity(), "??? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        descView.setText(strMyDesc);
                        inputDescView.setText(strMyDesc);
                        descView.setVisibility(View.VISIBLE);
                        inputDescView.setVisibility(View.INVISIBLE);
                        return;
                    }

                    requestUpdateUserDesc();
                }
            }
        });

//        inputDescView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    if(event.getAction() == KeyEvent.ACTION_DOWN)
//                        return true;
//
//                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(inputDescView.getWindowToken(), 0);
//
//                    String desc = inputDescView.getText().toString();
//                    if(desc.equals(strMyDesc) || desc.length() == 0) {
//                        Toast.makeText(getActivity(), "??? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
//                        descView.setText(strMyDesc);
//                        descView.setVisibility(View.VISIBLE);
//                        inputDescView.setVisibility(View.INVISIBLE);
//                        return true;
//                    }
//
//                    requestUpdateUserDesc();
//                    return true;
//                }
//
//                return false;
//            }
//        });

        pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        DecimalFormat format = new DecimalFormat("###,###");//??????
        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), FollowingWriterListActivity.class));
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                intent.putExtra("FOLLOWER_COUNT", nFollowerCount);
                intent.putExtra("FOLLOWING_COUNT", nFollowingCount);
                intent.putExtra("WRITER_ID", pref.getString("USER_ID", ""));
                intent.putExtra("WRITER_NAME", pref.getString("USER_NAME", ""));
                intent.putExtra("SELECTED_INDEX", 1);
                startActivity(intent);
            }
        });

        followerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), FollowingWriterListActivity.class));
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                intent.putExtra("FOLLOWER_COUNT", nFollowerCount);
                intent.putExtra("FOLLOWING_COUNT", nFollowingCount);
                intent.putExtra("WRITER_ID", pref.getString("USER_ID", ""));
                intent.putExtra("WRITER_NAME", pref.getString("USER_NAME", ""));
                intent.putExtra("SELECTED_INDEX", 2);
                startActivity(intent);
            }
        });

        copyRecommendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(strRecommendCode != null || strRecommendCode.length() > 0) {
                    ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("????????? ??????", strRecommendCode);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getActivity(), "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(pref.getString("ADMIN", "N").equals("Y")) {
            typeView.setText("?????????");
        } else {
            typeView.setText("????????????");
        }

        tokkiTokBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("WRITER_ID", pref.getString("USER_ID", ""));
                startActivity(intent);
            }
        });

//        commentTabLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clickTab(2);
//            }
//        });
//
//        coinBtnLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), PurchaseActivity.class));
//            }`
//        });

        faceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), UserProfileActivity.class));
                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                startActivityForResult(new Intent(getActivity(), ProfilePhotoPopup.class), FROM_POPUP);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(getActivity(), "????????? ?????????????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UserProfileActivity.class));
            }
        });
//        RelativeLayout topLayout = inflaterView.findViewById(R.id.topLayout);
//        topLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), UserProfileActivity.class));
//            }
//        });

//        ImageButton editDescBtn = inflaterView.findViewById(R.id.editDescBtn);
//        editDescBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), EditMyCommentPopup.class);
//                startActivity(intent);
//            }
//        });

        return inflaterView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FROM_POPUP) {
                bCamera = data.getBooleanExtra("CAMERA", false);
                boolean bDefault = data.getBooleanExtra("DEFAULT", false);

                if(bDefault) {
                    requestSendPhotoDefault();
                } else if (!bCamera) {
                    Intent intent = new Intent(getActivity(), PhotoPickerActivity.class);
                    intent.putExtra("TYPE", TYPE_PROFILE.ordinal());
                    startActivity(intent);
                }
            } else if (requestCode == FROM_EMAIL_AUTH) {
                Intent intent = new Intent(getActivity(), ProfileEmailPopup.class);
                intent.putExtra("USER_ID", pref.getString("USER_ID", "Guest"));
                intent.putExtra("USER_EMAIL", pref.getString("USER_EMAIL", ""));
                startActivity(intent);
            } else if (requestCode == FROM_CAMERA) {
                ThumbnailPreviewActivity.nNextType = TYPE_PROFILE.ordinal();
                CropImage.activity(mPhotoUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("My Crop")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .start(getActivity());
            }
        }
    }

//    public void clickUsedMoreBtn(View view) {
//        startActivity(new Intent(getActivity(), UsedCarrotListActivity.class));
//    }
//
//    public void clickTotalMoreBtn(View view) {
//
//    }

    public void onClickEditBtn() {
        editBtn.setBackgroundResource(0);
        editBtn.setText("??????");
        bEdit = true;
        inputDescView.setText(descView.getText().toString());
        inputDescView.setVisibility(View.VISIBLE);
        inputDescView.requestFocus();
        inputDescView.setSelection(inputDescView.getText().toString().length());
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputDescView, InputMethodManager.SHOW_FORCED);
        descView.setVisibility(View.INVISIBLE);
    }

    public void setImage(String strUri) {
        mPhotoUri = Uri.parse(strUri);
        String strPhotoPath = CommonUtils.getRealPathFromURI(getActivity(), mPhotoUri);
        requestSendPhoto(strPhotoPath);
    }

    private void requestSendPhotoDefault() {                                // ?????? ?????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfileImageDefault(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_PHOTO", "");
                        editor.commit();

                        faceView.setImageResource(R.drawable.user_icon);
                        ((MainActivity) getActivity()).setMenuPhoto();
                    }
                });
            }
        }).start();
    }

    private void requestSendPhoto(String strPhotoPath) {
        CommonUtils.showProgressDialog(getActivity(), "???????????? ???????????? ????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserPhoto(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strPhotoPath);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            String filename = strPhotoPath.substring(strPhotoPath.lastIndexOf("/")+1);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("USER_PHOTO", filename);
                            editor.commit();

                            String strPhoto = pref.getString("USER_PHOTO", "");
                            if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
                                if(!strPhoto.startsWith("http"))
                                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                                Glide.with(getActivity())
                                        .asBitmap() // some .jpeg files are actually gif
                                        .load(strPhoto)
                                        .apply(new RequestOptions().circleCrop())
                                        .placeholder(R.drawable.user_icon)
                                        .into(faceView);

                                ((MainActivity) getActivity()).setMenuPhoto();
                            } else {
                                faceView.setImageResource(R.drawable.user_icon);
                                ((MainActivity) getActivity()).setMenuPhoto();
                            }
                        } else {
                            Toast.makeText(getActivity(), "?????? ????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void reloadData() {                                             // ?????? ????????? ?????? ??????
        String strPhoto = pref.getString("USER_PHOTO", "");

        if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
            if(!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(getActivity())
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strPhoto)
                    .placeholder(R.drawable.user_icon)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);
        }

        nameView.setText(pref.getString("USER_NAME", ""));
        String strDesc = pref.getString("USER_DESC", "");
        if(strDesc.equals("NULL") || strDesc.equals("null"))
            strDesc = "";

        strMyDesc = strDesc;
        descView.setText(strDesc);
        getMyData();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bVisible)
            reloadData();

        if(bCamera) {
            takePhoto();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            FirebaseAnalytics.getInstance(getContext()).setCurrentScreen(getActivity(), "MyFragment", null);
            bVisible = true;
            reloadData();
        } else {
            bVisible = false;
        }
    }

    private void GetAllWorkList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(getActivity() == null)
                    return;

                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                workList = HttpClient.GetAllWorkListWithWriterID(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(workList == null) {
                            Toast.makeText(getActivity(), "???????????? ????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapter = new MyWorkRecyclerAdapter(getActivity(), workList);
                        myWorkRecyclerView.setAdapter(adapter);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                        myWorkRecyclerView.setLayoutManager(mLayoutManager);
                    }
                });
            }
        }).start();
    }

    private void getMyData() {
        CommonUtils.showProgressDialog(getActivity(), "????????? ??????????????????. ????????? ????????? ?????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                String strMyID = pref.getString("USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getMyFollowInfo(new OkHttpClient(), strMyID);
                CommonUtils.hideProgressDialog();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        try {
                            if(resultObject == null)
                                return;

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                nFollowerCount = resultObject.getInt("FOLLOW_COUNT");
                                nFollowingCount = resultObject.getInt("FOLLOWING_COUNT");
                                followingView.setText(CommonUtils.getPointCount(resultObject.getInt("FOLLOWING_COUNT")));
                                followerView.setText(CommonUtils.getPointCount(resultObject.getInt("FOLLOW_COUNT")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getMyCarrotInfo();
                    }
                });
            }
        }).start();
    }

    private void getMyCarrotInfo() {
        CommonUtils.showProgressDialog(getActivity(), "????????? ??????????????????. ????????? ????????? ?????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                String strMyID = pref.getString("USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getMyCarrotInfo(new OkHttpClient(), strMyID);
                CommonUtils.hideProgressDialog();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        try {
                            if(resultObject == null) {
                                return;
                            }

                            if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                nCurrentCarrot = resultObject.getInt("CURRENT_CARROT");
                                nTotalUsedCarrot = resultObject.getInt("USED_CARROT");
                                nDonationCarrot = resultObject.getInt("DONATION_CARROT");
                                nTotalAcquireCarrot = resultObject.getInt("ACQUIRE_CARROT");
                                strRecommendCode = resultObject.getString("RECOMMEND_CODE");
                                currentCarrotCountView.setText(CommonUtils.comma(nCurrentCarrot));
                                cumulativeUsageCarrotCountView.setText(CommonUtils.comma(nTotalUsedCarrot));
                                totalCarrotCountView.setText(CommonUtils.comma(nTotalAcquireCarrot));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        currentCarrotProgressbar.setProgress(nCurrentCarrot);
                        cumulativeUsageCarrotProgressbar.setProgress(nTotalUsedCarrot);
                        totalCarrotProgressbar.setProgress(nTotalAcquireCarrot);

                        if(strRecommendCode != null && strRecommendCode.length() > 0)
                            myRecommendCodeView.setText(strRecommendCode);

                        nLevel = CommonUtils.getLevel(nDonationCarrot);
                        setLevelView();
                        GetAllWorkList();
                    }
                });
            }
        }).start();
    }

    private void setLevelView() {
        emptyIconView.setVisibility(View.GONE);
        lv1IconView.setVisibility(View.GONE);
        lv5IconView.setVisibility(View.GONE);
        lv10IconView.setVisibility(View.GONE);

        switch(nLevel) {
            case 1:
                levelBGView.setBackgroundResource(R.drawable.lv1_bg);
                lv1IconView.setVisibility(View.VISIBLE);
                break;
            case 2:
                levelBGView.setBackgroundResource(R.drawable.lv2_bg);
                emptyIconView.setVisibility(View.VISIBLE);
                break;
            case 3:
                levelBGView.setBackgroundResource(R.drawable.lv3_bg);
                emptyIconView.setVisibility(View.VISIBLE);
                break;
            case 4:
                levelBGView.setBackgroundResource(R.drawable.lv4_bg);
                emptyIconView.setVisibility(View.VISIBLE);
                break;
            case 5:
                levelBGView.setBackgroundResource(R.drawable.lv5_bg);
                lv5IconView.setVisibility(View.VISIBLE);
                break;
            case 6:
                levelBGView.setBackgroundResource(R.drawable.lv6_bg);
                emptyIconView.setVisibility(View.VISIBLE);
                break;
            case 7:
                levelBGView.setBackgroundResource(R.drawable.lv7_bg);
                emptyIconView.setVisibility(View.VISIBLE);
                break;
            case 8:
                levelBGView.setBackgroundResource(R.drawable.lv8_bg);
                emptyIconView.setVisibility(View.VISIBLE);
                break;
            case 9:
                levelBGView.setBackgroundResource(R.drawable.lv9_bg);
                emptyIconView.setVisibility(View.VISIBLE);
                break;
            case 10:
                levelBGView.setBackgroundResource(R.drawable.lv10_bg);
                lv10IconView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void takePhoto(){
        // ?????? ??? ????????? ?????????
        bCamera = false;
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                File photoFile = null;

                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }

                if(photoFile!=null) {
                    Uri providerURI = FileProvider.getUriForFile(getActivity(), "com.Whowant.Tokki.PhotoProvider", photoFile);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        }else{
            Log.v("??????", "??????????????? ?????? ?????????");
            return;
        }
    }

    public File createImageFile() throws IOException{                                   // ?????? ????????? ?????? ????????? ?????? ?????? ??????
        String imgFileName = System.currentTimeMillis() + ".png";
        File imageFile= null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "Panbook");

        if(!storageDir.exists()){
            Log.v("??????","storageDir ?????? x " + storageDir.toString());
            storageDir.mkdirs();
        }

        Log.v("??????","storageDir ????????? " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mPhotoUri = Uri.fromFile(imageFile);

        return imageFile;
    }

    private void requestUpdateUserDesc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "USER_DESC", inputDescView.getText().toString());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        descView.setText(inputDescView.getText());
                        strMyDesc = inputDescView.getText().toString();
                        descView.setVisibility(View.VISIBLE);
                        inputDescView.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "??? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_DESC", inputDescView.getText().toString());
                        editor.commit();
                    }
                });
            }
        }).start();
    }
}
