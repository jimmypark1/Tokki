package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.Whowant.Tokki.UI.TypeOnClickListener;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.WriterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyPageFollowerFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageFollowerFollowerAdapter adapter;
    private ArrayList<WriterVO> writerList = new ArrayList<>();

    String writerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            writerId = bundle.getString("writerId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page_feed, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new MyPageFollowerFollowerAdapter(getContext(), writerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                WriterVO item = writerList.get(position);

                Intent intent = new Intent(getContext(), WriterPageActivity.class);
                intent.putExtra("writerId", item.getStrWriterID());
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getMyFollowerList();
    }

    public class MyPageFollowerFollowerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        private ArrayList<WriterVO> arrayList;

        public MyPageFollowerFollowerAdapter(Context context, ArrayList<WriterVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_my_page_follower, parent, false);
            return new MyPageFollowerFollowerViewHolder(v, new TypeOnClickListener() {
                @Override
                public void onClick(int type, int position) {
                    WriterVO item = arrayList.get(position);

                    switch (type) {
                        // 팔로잉
                        case 0:
                            requestFollow(item.getStrWriterID(), false);
                            break;
                        // 팔로워
                        case 1:
                            requestFollow(item.getStrWriterID(), true);
                            break;
                    }
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            WriterVO item = arrayList.get(position);

            if (holder instanceof MyPageFollowerFollowerViewHolder) {
                MyPageFollowerFollowerViewHolder viewHolder = (MyPageFollowerFollowerViewHolder) holder;

                String photoUrl = item.getStrWriterPhoto();
                if (!TextUtils.isEmpty(photoUrl)) {
                    Glide.with(getActivity())
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.user_icon)
                            .load(photoUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(viewHolder.photoIv);
                }

                viewHolder.nameTv.setText(item.getStrWriterName());

                if (item.getnFollowcount() == 0) {
                    viewHolder.followerLl.setVisibility(View.GONE);
                    viewHolder.followingLl.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.followerLl.setVisibility(View.VISIBLE);
                    viewHolder.followingLl.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class MyPageFollowerFollowerViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;
        LinearLayout followingLl;
        LinearLayout followerLl;

        public MyPageFollowerFollowerViewHolder(@NonNull View itemView, TypeOnClickListener listener) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_my_page_follower_photo);
            nameTv = itemView.findViewById(R.id.tv_row_my_page_follower_name);
            followingLl = itemView.findViewById(R.id.ll_row_my_page_follower_following);
            followingLl.setOnClickListener((v) -> {
                if (listener != null) {
                    listener.onClick(0, getAdapterPosition());
                }
            });
            followerLl = itemView.findViewById(R.id.ll_row_my_page_follower_follower);
            followerLl.setOnClickListener((v) -> {
                if (listener != null) {
                    listener.onClick(1, getAdapterPosition());
                }
            });
        }
    }

    private void getMyFollowerList() {
        CommonUtils.showProgressDialog(getActivity(), "팔로우 리스트를 가져오고 있습니다.");
        writerList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");
                writerList.addAll(HttpClient.getMyFollowerList(new OkHttpClient(), writerId));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (writerList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (int i = 0; i < writerList.size(); i++) {
                            WriterVO vo = writerList.get(i);
                            if (vo.getStrWriterID().equals(userId)) {
                                writerList.remove(i);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void requestFollow(String strUserID, boolean bFollow) {
        CommonUtils.showProgressDialog(getActivity(), "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strMyID = SimplePreference.getStringPreference(getActivity(), "USER_INFO", "USER_ID", "Guest");

                boolean bResult = HttpClient.requestFollow(new OkHttpClient(), strMyID, strUserID, bFollow);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult) {
                            getMyFollowerList();
                        } else {
                            Toast.makeText(getActivity(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_LONG).show();
                            getMyFollowerList();
                        }
                    }
                });
            }
        }).start();
    }
}
