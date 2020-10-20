package com.Whowant.Tokki.UI.Fragment.Friend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.FriendVO;

import java.util.ArrayList;

public class FriendInviteFragment extends Fragment {

    RecyclerView recyclerView;
    FriendInviteAdapter adapter;
    ArrayList<FriendVO.FriendInviteVO> mArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_invite, container, false);

        initData();

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new FriendInviteAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return v;
    }

    private void initData() {
        mArrayList.clear();

        FriendVO.FriendInviteVO item = new FriendVO.FriendInviteVO();
        item.setName("트위터");
        mArrayList.add(item);
        item = new FriendVO.FriendInviteVO();
        item.setName("문자");
        mArrayList.add(item);
        item = new FriendVO.FriendInviteVO();
        item.setName("링크 복사");
        mArrayList.add(item);
        item = new FriendVO.FriendInviteVO();
        item.setName("다른 앱");
        mArrayList.add(item);
    }

    public class FriendInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<FriendVO.FriendInviteVO> arrayList;

        public FriendInviteAdapter(Context context, ArrayList<FriendVO.FriendInviteVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_friend_invite_fragment, parent, false);
            return new FriendInviteViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof FriendInviteViewHolder) {
                FriendInviteViewHolder viewHolder = (FriendInviteViewHolder) holder;
                FriendVO.FriendInviteVO item = arrayList.get(position);

                viewHolder.nameTv.setText(item.getName());
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class FriendInviteViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;

        public FriendInviteViewHolder(@NonNull View itemView) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_friend_invite_photo);
            nameTv = itemView.findViewById(R.id.tv_row_friend_invite_name);
        }
    }
}
