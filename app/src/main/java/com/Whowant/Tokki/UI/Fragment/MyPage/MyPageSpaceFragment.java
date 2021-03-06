package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.ChatActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.SpacePostCommentActivity;
import com.Whowant.Tokki.UI.Activity.Work.ReportSelectActivity;
import com.Whowant.Tokki.UI.Fragment.Friend.MessageDetailActivity;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MessageThreadVO;
import com.Whowant.Tokki.VO.SpaceVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.Whowant.Tokki.Utils.Constant.CONTENTS_TYPE.TYPE_SPACE_IMG;

public class MyPageSpaceFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView addPhotoBtn;
    TextView textView;
    EditText editText;
    MyPageSpaceAdapter adapter;
    ArrayList<SpaceVO> postList = new ArrayList<>();

    String strFilePath;
    boolean isThreadRunning = false;

    private InputMethodManager imm;

    RelativeLayout plusFrame;
    ImageView plusImg;

    public static String strUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_page_space, container, false);
        getSpacePosts();
        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new MyPageSpaceAdapter(getContext(), postList);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        Bundle extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();
            String path = extra.getString("URI");
            if (path != null) {
                strFilePath = CommonUtils.getRealPathFromURI(getActivity(), Uri.parse(path)); // ?????? ????????? ??? ??? ???

                Glide.with(MyPageSpaceFragment.this)
                        .asBitmap()
                        .load(path)
                        .placeholder(R.drawable.circle_cccccc)
                        .into(addPhotoBtn);
            }

        }

        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                return false;
            }
        });

        addPhotoBtn = v.findViewById(R.id.contentsAddBtn);
        plusFrame = v.findViewById(R.id.plusFrame);

        plusImg = v.findViewById(R.id.plusImg);

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(getActivity(), MediaSelectPopup.class);
                                intent.putExtra("TYPE", TYPE_SPACE_IMG.ordinal());
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                Toast.makeText(getActivity(), "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        editText = v.findViewById(R.id.inputTextView);

        textView = v.findViewById(R.id.sendBtn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSpacePost();

                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        String latitude = "";
        if(getArguments() != null)
        {
  //          strUri =  getArguments().getString("URI");
        }

        if(strUri != null)
        {
            strFilePath = CommonUtils.getRealPathFromURI(getActivity(), Uri.parse(strUri)); // ?????? ????????? ??? ??? ???

            Glide.with(MyPageSpaceFragment.this)
                    .asBitmap()
                    .load(strUri)
                    .placeholder(R.drawable.circle_cccccc)
                    .into(addPhotoBtn);

            plusImg.setVisibility(View.INVISIBLE);

        }
        getSpacePosts();
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager { // IndexOutOfBoundsException ?????????

        public WrapContentLinearLayoutManager(Context context, int vertical, boolean b) {
            super(context, vertical, b);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyPageSpaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<SpaceVO> postList;

        public MyPageSpaceAdapter(Context context, ArrayList<SpaceVO> arrayList) {
            this.context = context;
            this.postList = arrayList;
        }

        public void setData(ArrayList<SpaceVO> postList) {
            this.postList = postList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.space_row, parent, false);
            return new MyPageSpaceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (postList.size() - 1 < position)
                return;

            SpaceVO item = postList.get(position);
            MyPageSpaceViewHolder viewHolder = (MyPageSpaceViewHolder) holder;

            String strPhoto = item.getPoster();
            viewHolder.posterView.setImageResource(0);
            if (strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if (!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(context)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .into(viewHolder.posterView);
            } else {
//                Glide.with(context)
//                        .asBitmap() // some .jpeg files are actually gif
//                        .load(R.drawable.no_poster_horizontal)
//                        .into(viewHolder.posterView);
            }

            String userPhoto = item.getUserPhoto();
            String recvName =  item.getUserName();


            if (!TextUtils.isEmpty(userPhoto)) {
                if (!userPhoto.startsWith("http"))
                    userPhoto = CommonUtils.strDefaultUrl + "images/" + userPhoto;

                Glide.with(context)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(userPhoto)
                        .placeholder(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(viewHolder.faceView);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final boolean bResult = isLike(item.getPostID(), SimplePreference.getStringPreference(context, "USER_INFO", "USER_ID", "Guest"));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bResult) {
                                viewHolder.heartBtnView.setImageResource(R.drawable.ic_i_heart_red);
                            } else {
                                viewHolder.heartBtnView.setImageResource(R.drawable.ic_i_heart);
                            }
                        }
                    });
                }
            }).start();

            viewHolder.nameView.setText(item.getUserName());
            viewHolder.dateView.setText(item.getDateTime()); // ?????? ?????? ???????????? ???
            viewHolder.contentsView.setText(item.getDescription());
            viewHolder.likeCountView.setText(item.getLikeCount() + "");
            viewHolder.commentCountView.setText(item.getCommentCount() + "");

            viewHolder.heartBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    clickLikeBtn(item.getPostID(), SimplePreference.getStringPreference(context, "USER_INFO", "USER_ID", "Guest"));
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getSpacePosts();
//                                    /*
//                                    if (likeCount >= 0) {
//                                        viewHolder.likeCountView.setText(likeCount + "");
//                                        return;
//                                    } else
//                                        Toast.makeText(context, "????????? ????????? ?????????????????????. ?????? ??? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
//                                     */
//                                }
//                            });
//                        }
//                    }).start();
                }
            });

            viewHolder.commentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SpacePostCommentActivity.class);
                    intent.putExtra("POST_ID", item.getPostID());
                    startActivity(intent);
                }
            });

            viewHolder.menuLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                    if (item.getUserID().equals(SimplePreference.getStringPreference(context, "USER_INFO", "USER_ID", "Guest")) || SimplePreference.getStringPreference(context, "USER_INFO", "ADMIN", "N").equals("Y")) {
                        // ?????? ??????
                        int nPostID =  item.getPostID();

                        popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getItemId() == R.id.delete) {

                                    deletePost(String.valueOf(nPostID));
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    } else {
                        // ?????? ?????? (????????? ?????????, ????????? ??????, ????????? ??????)
                        popupMenu.getMenuInflater().inflate(R.menu.space_row_menu, popupMenu.getMenu());
                        String posterID =  item.getUserID();
                        int nPostID =  item.getPostID();

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.message:
                                        sendMessage(posterID,recvName);
                                        break;
                                    case R.id.block:
                                        setBlockUser(posterID);
                                        break;

                                    case R.id.report:
                                        report(String.valueOf(nPostID));
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                }
            });
        }

        void deleteProcess(String postID)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() == null) {
                        isThreadRunning = false;
                        return;
                    }
                    String userID =  SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");

                    Boolean ret = HttpClient.deleteSpacePost(new OkHttpClient(), userID,postID);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(ret == true)
                            {
                                Toast.makeText(getActivity(), "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                getSpacePosts();

                            }
                        }
                    });
                }
            }).start();
        }
        void deletePost(String postID)
        {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("????????? ?????? ??????");
                    builder.setMessage("???????????? ?????? ?????????????????????????");
                    builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            deleteProcess(postID);
                        }
                    });

                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });


        }

        void report(String postId)
        {
            Intent intent = new Intent(getActivity(), ReportSelectActivity.class);
            intent.putExtra("POST_ID", postId);
            startActivity(intent);

        }
        void sendMessage(String recvId, String name)
        {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() == null) {
                        isThreadRunning = false;
                        return;
                    }
                    String userID =  SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");

                    MessageThreadVO ret = HttpClient.getMessageThreadByID(new OkHttpClient(), userID,recvId);

                    int nThreadId = ret.getThreadID();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                            intent.putExtra("THREAD_ID", nThreadId);
                            intent.putExtra("RECEIVER_ID", recvId);
                            intent.putExtra("RECEIVER_NAME", name);



                            startActivity(intent);
                        }
                    });
                }
            }).start();
        }


        void setBlockUser(String blockId)
        {



            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("????????? ?????? ??????");
                    builder.setMessage("??? ???????????? ?????? ?????????????????????????");
                    builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() == null) {
                                        isThreadRunning = false;
                                        return;
                                    }
                                    String userID =  SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");

                                    boolean ret = HttpClient.setSpaceBlockUser(new OkHttpClient(), userID,blockId);

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            getSpacePosts();
                                            Toast.makeText(getActivity(), "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).start();
                        }
                    });

                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return postList.get(position).getPostID();
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }
    }

    public class MyPageSpaceViewHolder extends RecyclerView.ViewHolder {

        ImageView faceView;
        final ImageView heartBtnView;
        TextView nameView;
        TextView dateView;
        TextView contentsView;
        TextView likeCountView;
        TextView commentCountView;
        ImageView posterView;
        RelativeLayout menuLayout;
        LinearLayout commentView;

        public MyPageSpaceViewHolder(@NonNull View itemView) {
            super(itemView);

            faceView = itemView.findViewById(R.id.faceView);
            heartBtnView = itemView.findViewById(R.id.heartBtn);
            nameView = itemView.findViewById(R.id.nameView);
            dateView = itemView.findViewById(R.id.dateTimeView);
            contentsView = itemView.findViewById(R.id.textContentsView);
            likeCountView = itemView.findViewById(R.id.heartPointView);
            commentView = itemView.findViewById(R.id.commentView);
            commentCountView = itemView.findViewById(R.id.commentCountView);
            posterView = itemView.findViewById(R.id.imageView);
            menuLayout = itemView.findViewById(R.id.menuBtn);
        }
    }

    public void imageSetting(Intent intent) {
        String imgUri = intent.getStringExtra("URI");

        /*
        if (imgUri != null) {
            strFilePath = CommonUtils.getRealPathFromURI(getActivity(), Uri.parse(imgUri)); // ?????? ????????? ??? ??? ???

            Glide.with(MyPageSpaceFragment.this)
                    .asBitmap()
                    .load(imgUri)
                    .placeholder(R.drawable.circle_cccccc)
                    .into(addPhotoBtn);

            plusImg.setVisibility(View.INVISIBLE);

        }

         */
    }

    private void CreateSpacePost() {
        try {
            String url = CommonUtils.strDefaultUrl + "PanAppCreateSpace.jsp";
            RequestBody requestBody = null;

            File sourceFile = null;

            MultipartBody.Builder builder = new MultipartBody.Builder();
            SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("USER_ID", pref.getString("USER_ID", "Guest"))
                    .addFormDataPart("USER_NAME", SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_NAME", ""));

            String strDescription = editText.getText().toString();

            if (strDescription.length() > 0)
                builder.addFormDataPart("DESCRIPTION", strDescription);
            else
                builder.addFormDataPart("DESCRIPTION", "");

            if (strFilePath != null) {
                sourceFile = new File(strFilePath);
                String filename = strFilePath.substring(strFilePath.lastIndexOf("/") + 1);
                builder.addFormDataPart("POSTER", filename, RequestBody.create(MultipartBody.FORM, sourceFile));
            }

            requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(getActivity(), "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    Toast.makeText(getActivity(), "?????????????????????.", Toast.LENGTH_LONG).show();
                                    plusImg.setVisibility(View.VISIBLE);

                                    addPhotoBtn.setImageResource(R.drawable.round_blue_space_add);
                                    plusImg.setImageResource(R.drawable.i_plus_white);
                                    plusFrame.setBackgroundResource(R.drawable.round_blue_space_add);
                                    editText.setText("");
                                    getSpacePosts();
                                } else {
                                    Toast.makeText(getActivity(), "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void getSpacePosts() {
        isThreadRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    isThreadRunning = false;
                    return;
                }
                String userID =  SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");
                ArrayList<SpaceVO> arrayList;
                String order = "desc";
                arrayList = HttpClient.getSpacePosts(new OkHttpClient(), order,userID);

                if (arrayList != null) {
                    postList.clear();
                    postList.addAll(arrayList);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.hideProgressDialog();

                        if (postList == null) {
                            Toast.makeText(getActivity(), "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                            isThreadRunning = false;
                            return;
                        }

                        recyclerView.getRecycledViewPool().clear();
                        adapter.setData(postList);
                        isThreadRunning = false;
                    }
                });
            }
        }).start();
    }

    public void clickLikeBtn(final int postID, String userID) {
        if (isThreadRunning)
            return;

        isThreadRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestLikeSpace(new OkHttpClient(), postID, userID);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bResult) {
                            getSpacePosts();
                            //adapter.notifyDataSetChanged();
                        } else {
                            isThreadRunning = false;
                            Toast.makeText(getActivity(), "????????? ????????? ?????????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    public boolean isLike(int postID, String userID) {
        boolean bResult = HttpClient.checkIsLike(new OkHttpClient(), postID, userID);
        return bResult;
    }
}