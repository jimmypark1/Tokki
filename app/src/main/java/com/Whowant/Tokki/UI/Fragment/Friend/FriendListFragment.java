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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Writer.WriterPageActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.FriendVO;
import com.Whowant.Tokki.VO.MessageThreadVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendInviteAdapter adapter;
    private ArrayList<FriendVO> friendList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_invite, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new FriendInviteAdapter(getActivity(), friendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendList();
    }

    public void getFriendList() {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        friendList.clear();

        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                friendList = HttpClient.getFriendList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(friendList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.setData(friendList);
                    }
                });
            }
        }).start();
    }

    private void getMessageData(final String strPartnerID) {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageThreadVO vo = HttpClient.getMessageThreadByID(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strPartnerID);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();
                        if(vo == null) {
                            Toast.makeText(getActivity(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                        String strMyID = pref.getString("USER_ID", "Guest");
                        int nThreadID = vo.getThreadID();
                        Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                        intent.putExtra("THREAD_ID", nThreadID);

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

    public class FriendInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<FriendVO> dataList = new ArrayList<>();

        public FriendInviteAdapter(Context context, ArrayList<FriendVO> friendList) {
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
            View v = LayoutInflater.from(context).inflate(R.layout.friend_list_row, parent, false);
            return new FriendInviteViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(dataList.size() <= position)
                return;

            FriendVO vo = dataList.get(position);
            FriendInviteViewHolder viewHolder = (FriendInviteViewHolder) holder;
            String strPhoto = vo.getUserPhoto();
            viewHolder.photoIv.setClipToOutline(true);
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .placeholder(R.drawable.user_icon)
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
            viewHolder.addLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getMessageData(vo.getUserId());
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String writerId = vo.getUserId();
                    Intent intent = new Intent(getActivity(), WriterPageActivity.class);
                    intent.putExtra("writerId", writerId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public class FriendInviteViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;
        LinearLayout addLl;

        public FriendInviteViewHolder(@NonNull View itemView) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_friend_find_photo);
            nameTv = itemView.findViewById(R.id.tv_row_friend_find_name);
            addLl = itemView.findViewById(R.id.ll_row_friend_find_add);
        }
    }
}
