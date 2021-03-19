package com.Whowant.Tokki.UI.Fragment.Friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterPageActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.FriendVO;
import com.Whowant.Tokki.VO.MessageThreadVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class FriendSelectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FriendFindAdapter adapter;
    private ArrayList<FriendVO> friendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_select);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new FriendFindAdapter(this, friendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllFriendList();
    }

    public void getAllFriendList() {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        friendList.clear();

        CommonUtils.showProgressDialog(FriendSelectActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                friendList = HttpClient.getAllFriendList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(friendList == null) {
                            Toast.makeText(FriendSelectActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.setData(friendList);
                    }
                });
            }
        }).start();
    }

    private void getMessageData(final String strPartnerID) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        CommonUtils.showProgressDialog(FriendSelectActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageThreadVO vo = HttpClient.getMessageThreadByID(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strPartnerID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(vo == null) {
                            Toast.makeText(FriendSelectActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                        String strMyID = pref.getString("USER_ID", "Guest");
                        int nThreadID = vo.getThreadID();
                        Intent intent = new Intent(FriendSelectActivity.this, MessageDetailActivity.class);
                        intent.putExtra("THREAD_ID", nThreadID);
                        intent.putExtra("RECEIVER_NAME", vo.getUserName());

                        if(strMyID.equals(vo.getUserID()))
                            intent.putExtra("RECEIVER_ID", vo.getPartnerID());
                        else
                            intent.putExtra("RECEIVER_ID", vo.getUserID());
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public class FriendFindAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        ArrayList<FriendVO> dataList = new ArrayList<>();

        public FriendFindAdapter(Context context, ArrayList<FriendVO> friendList) {
            this.context = context;
            this.dataList = friendList;
        }

        public void setData(ArrayList<FriendVO> friendList) {
            this.dataList = friendList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_friend_find_fragment, parent, false);
            return new FriendFindViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(dataList.size() <= position)
                return;

            FriendVO vo = dataList.get(position);

            FriendFindViewHolder viewHolder = (FriendFindViewHolder) holder;
            String strPhoto = vo.getUserPhoto();
            viewHolder.photoIv.setClipToOutline(true);
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(FriendSelectActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .placeholder(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(viewHolder.photoIv);
            } else {
                Glide.with(FriendSelectActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(viewHolder.photoIv);
            }

            viewHolder.nameTv.setText(vo.getUserName());

            viewHolder.addLl.setVisibility(View.GONE);

//            if(vo.isFriend()) {
//                viewHolder.addLl.setBackgroundResource(R.drawable.round_4_5a9aff);
//                viewHolder.icFollow.setImageResource(R.drawable.ic_i_follow_white);
//            } else {
//                viewHolder.addLl.setBackgroundResource(R.drawable.round_4_ffffff_b1_33000000);
//                viewHolder.icFollow.setImageResource(R.drawable.ic_i_follow);
//            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strID = vo.getUserId();
                    getMessageData(strID);
//                    String writerId = vo.getUserId();
//                    Intent intent = new Intent(FriendSelectActivity.this, WriterPageActivity.class);
//                    intent.putExtra("writerId", writerId);
//                    startActivity(intent);


                }
            });
            viewHolder.photoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strID = vo.getUserId();
              
//                    String writerId = vo.getUserId();
//                    Intent intent = new Intent(FriendSelectActivity.this, WriterPageActivity.class);
//                    intent.putExtra("writerId", writerId);
//                    startActivity(intent);

                    Intent intent = new Intent(FriendSelectActivity.this, MyPageActivity.class);
                    //  ViewerActivity.workVO = vo;
                    intent.putExtra("WRITER_ID", vo.getUserId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public class FriendFindViewHolder extends RecyclerView.ViewHolder {
        ImageView photoIv;
        TextView nameTv;
        LinearLayout addLl;
        ImageView icFollow;
        public FriendFindViewHolder(@NonNull View itemView) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_friend_find_photo);
            nameTv = itemView.findViewById(R.id.tv_row_friend_find_name);
            addLl = itemView.findViewById(R.id.ll_row_friend_find_add);
            icFollow = itemView.findViewById(R.id.icFollow);
        }
    }
}