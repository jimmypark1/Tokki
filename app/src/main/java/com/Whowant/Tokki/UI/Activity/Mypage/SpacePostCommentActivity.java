package com.Whowant.Tokki.UI.Activity.Mypage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Work.EpisodeCommentActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CommentVO;
import com.Whowant.Tokki.VO.SpaceCommentVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpacePostCommentActivity extends AppCompatActivity {
    private EditText inputTextView;
    private ListView listView;
    private Button sendBtn;
    private int postID;
    private String userID;
    private String strComment;
    private ArrayList<SpaceCommentVO> commentList;
    private SpacePostCommentAdapter aa;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_post_comment);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        userID = pref.getString("USER_ID", "Guest");
        postID = getIntent().getIntExtra("POST_ID", 0);

        commentList = new ArrayList<>();
        getCommentList(postID);

        listView = findViewById(R.id.listView);
        aa = new SpacePostCommentAdapter(this, R.layout.space_comment_row, commentList);
        listView.setAdapter(aa);

        inputTextView = findViewById(R.id.inputTextView);
        sendBtn = findViewById(R.id.sendBtn);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void OnClickSendBtn(View view) throws UnsupportedEncodingException {

        strComment = inputTextView.getText().toString();
        URLEncoder.encode(strComment, "UTF-8");


        if (strComment.length() == 0) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        requestSendComment(strComment);
        imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);
    }

    private void requestSendComment(final String strComment) {
        try {
            String url = CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=SetSpaceComment&POST_ID=" + postID + "&USER_ID=" + userID + "&COMMENT=" + strComment;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(SpacePostCommentActivity.this, "등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                    Toast.makeText(SpacePostCommentActivity.this, "등록되었습니다.", Toast.LENGTH_LONG).show();
                                    inputTextView.setText("");
                                    getCommentList(postID);
                                } else {
                                    Toast.makeText(SpacePostCommentActivity.this, "등록에 실패하였습니다.", Toast.LENGTH_LONG).show();
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
        }
    }

    public void getCommentList(int postID) {
        commentList.clear();
        String url = CommonUtils.strDefaultUrl + "PanAppWork.jsp?CMD=GetSpaceComments&POST_ID=" + postID;
        LinearLayout emptyView = findViewById(R.id.emptyLayout);

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(SpacePostCommentActivity.this, "댓글 목록을 가져오지 못했습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String strResult = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject resultObject = new JSONObject(strResult);

                                JSONArray resultArray = null;
                                resultArray = resultObject.getJSONArray("COMMENTS");

                                for (int i = 0; i < resultArray.length(); i++) {                                     // Parent Group 생성
                                    JSONObject object = resultArray.getJSONObject(i);

                                    SpaceCommentVO vo = new SpaceCommentVO();

                                    vo.setUserID(object.getString("USER_ID"));
                                    vo.setCommentID(object.getInt("COMMENT_ID"));
                                    vo.setComment(object.getString("COMMENT"));
                                    vo.setDate(object.getString("DATE"));
                                    vo.setUserName(object.getString("USER_NAME"));
                                    vo.setUserPhoto(object.getString("USER_PHOTO"));

                                    commentList.add(vo);
                                    aa.notifyDataSetChanged();
                                }

                                if (commentList.size() == 0) {
                                    emptyView.setVisibility(View.VISIBLE);
                                    return;
                                }

                                emptyView.setVisibility(View.INVISIBLE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SpacePostCommentAdapter extends ArrayAdapter<Object> {

        private LayoutInflater mLiInflater;

        public SpacePostCommentAdapter(@NonNull Context context, int layout, List listView) {
            super(context, layout, listView);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.space_comment_row, parent, false);

            SpaceCommentVO vo = commentList.get(position);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);
            TextView userName = convertView.findViewById(R.id.nameView);
            ImageView faceView = convertView.findViewById(R.id.faceView);

            dateView.setText(vo.getDate());
            commentView.setText(vo.getComment());
            userName.setText(vo.getUserName());

            String userPhoto = vo.getUserPhoto();

            if (!TextUtils.isEmpty(userPhoto)) {
                if (!userPhoto.startsWith("http"))
                    userPhoto = CommonUtils.strDefaultUrl + "images/" + userPhoto;

                Glide.with(SpacePostCommentActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(userPhoto)
                        .placeholder(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            return convertView;

        }
    }
}