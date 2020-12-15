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
import com.Whowant.Tokki.UI.Activity.Admin.CommentManagementActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterMainActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterPageActivity;
import com.Whowant.Tokki.UI.Fragment.Main.StorageBoxBookListFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.FriendVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class FriendRecommendFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendFindAdapter adapter;
    private ArrayList<FriendVO> friendList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_find, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new FriendFindAdapter(getContext(), friendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendRecommendList();
    }

    public void getFriendRecommendList() {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        friendList.clear();

        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                friendList = HttpClient.getFriendRecommendList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

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

    private void requestFollow(final String strUserID) {
        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                String strMyID = pref.getString("USER_ID", "Guest");

                boolean bResult = HttpClient.requestFollow(new OkHttpClient(), strMyID, strUserID, false);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            getFriendRecommendList();
                        } else {
                            Toast.makeText(getActivity(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
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
            viewHolder.addLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestFollow(vo.getUserId());
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

    public class FriendFindViewHolder extends RecyclerView.ViewHolder {
        ImageView photoIv;
        TextView nameTv;
        LinearLayout addLl;

        public FriendFindViewHolder(@NonNull View itemView) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_friend_find_photo);
            nameTv = itemView.findViewById(R.id.tv_row_friend_find_name);
            addLl = itemView.findViewById(R.id.ll_row_friend_find_add);
        }
    }
}
