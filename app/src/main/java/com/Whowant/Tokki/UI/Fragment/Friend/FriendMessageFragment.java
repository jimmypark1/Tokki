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

public class FriendMessageFragment extends Fragment {

    RecyclerView recyclerView;
    FriendMessageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_message, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new FriendMessageAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return v;
    }

    public class FriendMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;

        public FriendMessageAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_friend_message_fragment, parent, false);
            return new FriendMessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
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
