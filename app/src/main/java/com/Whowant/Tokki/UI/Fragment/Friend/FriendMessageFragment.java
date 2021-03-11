package com.Whowant.Tokki.UI.Fragment.Friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MessageThreadVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class FriendMessageFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendMessageAdapter adapter;
    private ArrayList<MessageThreadVO> messageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_message, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new FriendMessageAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        RelativeLayout floatingBtn = v.findViewById(R.id.floatingBtn);
        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FriendSelectActivity.class));
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageData();
    }

    private void getMessageData() {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        messageList.clear();

       // CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageList = HttpClient.getMessageThreadList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    //    CommonUtils.hideProgressDialog();
                        if(messageList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.setData(messageList);
                    }
                });
            }
        }).start();
    }

    public class FriendMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<MessageThreadVO> dataList = new ArrayList<>();

        public FriendMessageAdapter(Context context) {
            this.context = context;
        }

        public void setData(ArrayList<MessageThreadVO> list) {
            this.dataList = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_friend_message_fragment, parent, false);
            return new FriendMessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(dataList.size() <= position)
                return;

            MessageThreadVO vo = dataList.get(position);
            FriendMessageViewHolder viewHolder = (FriendMessageViewHolder)holder;

            String strPhoto = vo.getUserPhoto();
            viewHolder.photoIv.setClipToOutline(true);
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(viewHolder.photoIv);
            } else {
                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(viewHolder.photoIv);
            }

            viewHolder.nameTv.setText(vo.getUserName());

            if(vo.getLastMsg() != null && vo.getLastMsg().length() > 0 && !vo.getLastMsg().equals("null"))
                viewHolder.commentTv.setText(vo.getLastMsg());
//            else
//                viewHolder.itemView.setVisibility(View.GONE);
//                viewHolder.commentTv.setText("");

            viewHolder.dateTv.setText(vo.getCreatedDate());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                    String strMyID = pref.getString("USER_ID", "Guest");
                    int nThreadID = vo.getThreadID();
                    Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
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

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public class FriendMessageViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;
        TextView commentTv;
        TextView dateTv;

        public FriendMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_friend_message_photo);
            nameTv = itemView.findViewById(R.id.tv_row_friend_message_name);
            commentTv = itemView.findViewById(R.id.tv_row_friend_message_comment);
            dateTv = itemView.findViewById(R.id.tv_row_friend_message_date);
        }
    }
}
