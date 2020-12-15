package com.Whowant.Tokki.UI.Fragment.Friend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.ContestVO;
import com.Whowant.Tokki.VO.MessageVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MessageDetailActivity extends AppCompatActivity {
    private int nThreadID;
    private String strReceiverID;
    private ArrayList<MessageVO> messageList = new ArrayList<>();
    private CMessageArrayAdapter adapter;
    private ListView messageListView;
    private EditText inputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        inputTextView = findViewById(R.id.inputTextView);
        messageListView = findViewById(R.id.chattingListView);
        nThreadID = getIntent().getIntExtra("THREAD_ID", 0);
        strReceiverID = getIntent().getStringExtra("RECEIVER_ID");
        adapter = new CMessageArrayAdapter(this, R.layout.left_message_row, messageList);
        messageListView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageList();
    }

    public void onClickSendBtn(View view) {
        String strText = inputTextView.getText().toString();

        if(strText.length() == 0) {
            Toast.makeText(this, "채팅 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        requestSendMessage(strText);
    }

    private void requestSendMessage(String strText) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        CommonUtils.showProgressDialog(MessageDetailActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean bResult = HttpClient.requestSendMessage(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strReceiverID, strText, nThreadID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(bResult == false) {
                            Toast.makeText(MessageDetailActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        getMessageList();
                        inputTextView.setText("");
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void getMessageList() {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        messageList.clear();

        CommonUtils.showProgressDialog(MessageDetailActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageList = HttpClient.getMessageList(new OkHttpClient(), nThreadID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(messageList == null) {
                            Toast.makeText(MessageDetailActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.setData(messageList);
                        messageListView.setSelection(adapter.getCount() - 1);
                    }
                });
            }
        }).start();
    }

    public class CMessageArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;
        private ArrayList<MessageVO> dataList = new ArrayList<>();

        CMessageArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.dataList = (ArrayList<MessageVO>) titles;
        }

        public void setData(ArrayList<MessageVO> list) {
            this.dataList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            MessageVO vo = dataList.get(position);
            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
            String strUserID = pref.getString("USER_ID", "Guest");

            if(strUserID.equals(vo.getSenderID())) {
                convertView = mLiInflater.inflate(R.layout.right_message_row, parent, false);
                TextView contentsView = convertView.findViewById(R.id.contentsTextView);
                contentsView.setText(vo.getMsgContents());
            } else {
                convertView = mLiInflater.inflate(R.layout.left_message_row, parent, false);
                ImageView faceView = convertView.findViewById(R.id.faceView);
                faceView.setClipToOutline(true);
                String strPhoto = vo.getSenderPhoto();
                if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                    if(!strPhoto.startsWith("http"))
                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                    Glide.with(MessageDetailActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strPhoto)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                } else {
                    Glide.with(MessageDetailActivity.this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(faceView);
                }

                TextView nameView = convertView.findViewById(R.id.nameView);
                nameView.setText(vo.getSenderName());
                TextView contentsView = convertView.findViewById(R.id.contentsTextView);
                contentsView.setText(vo.getMsgContents());
            }

            return convertView;
        }
    }
}