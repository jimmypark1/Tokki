package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterPageActivity;
import com.Whowant.Tokki.UI.TypeOnClickListener;
import com.Whowant.Tokki.UI.ViewHolder.SearchResultViewHolder;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MyPageFeedVo;
import com.Whowant.Tokki.VO.WorkVO;
import com.Whowant.Tokki.VO.WriterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyPageFeedFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageFeedAdapter adapter;
    ArrayList<WorkVO> myArrayList = new ArrayList<>();
    ArrayList<WorkVO> readArrayList = new ArrayList<>();
    ArrayList<WriterVO> followArrayList = new ArrayList<>();

    ArrayList<MyPageFeedVo> mArrayList = new ArrayList<>();

    String writerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            writerId = bundle.getString("writerId");
        } else {
            writerId = SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_page_feed, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new MyPageFeedAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                MyPageFeedVo item = mArrayList.get(position);

                if (item.getWorkVO() != null) {
                    Intent intent = new Intent(getContext(), WorkMainActivity.class);
                    intent.putExtra("WORK_ID", item.getWorkVO().getnWorkID());
                    getContext().startActivity(intent);
                }
            }
        });

        getAllWorkWithWriterID();

        return v;
    }

    public class MyPageFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<MyPageFeedVo> arrayList;
        String descTmp = "";

        public MyPageFeedAdapter(Context context, ArrayList<MyPageFeedVo> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;
            switch (viewType) {
                case 0:
                    v = LayoutInflater.from(context).inflate(R.layout.row_my_page_feed_message, parent, false);
                    return new MyPageFeedCommentViewHolder(v, new TypeOnClickListener() {
                        @Override
                        public void onClick(int type, int position) {
                            MyPageFeedVo item = arrayList.get(position);

                            requestUpdateUserDesc(item.getUserDesc());
                        }
                    });
//                case 1:
//                    v = LayoutInflater.from(context).inflate(R.round_squre_stroke_gray_bg.row_search_category, parent, false);
//                    return new SearchResultViewHolder(v, new TypeOnClickListener() {
//                        @Override
//                        public void onClick(int type, int position) {
//
//                        }
//                    });
                case 2:
                    v = LayoutInflater.from(context).inflate(R.layout.row_my_page_feed_follow, parent, false);
                    return new MyPageFeedFollowViewHolder(v);
                case 1:
                case 3:
                    v = LayoutInflater.from(context).inflate(R.layout.row_search_category, parent, false);
                    return new SearchResultViewHolder(v, new TypeOnClickListener() {
                        @Override
                        public void onClick(int type, int position) {

                        }
                    });
                default:
                    v = LayoutInflater.from(context).inflate(R.layout.row_friend_find_fragment, parent, false);
            }

            return new MyPageFeedViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyPageFeedVo vo = arrayList.get(position);

            if (holder instanceof MyPageFeedCommentViewHolder) {
                MyPageFeedCommentViewHolder viewHolder = (MyPageFeedCommentViewHolder) holder;

                viewHolder.commentEt.setText(vo.getUserDesc());
                viewHolder.commentEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s != null) {
                            vo.setUserDesc(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else if (holder instanceof SearchResultViewHolder) {
                SearchResultViewHolder viewHolder = (SearchResultViewHolder) holder;
                WorkVO item = vo.getWorkVO();

                String strImgUrl = item.getCoverFile();

                if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(R.drawable.no_poster_vertical)
                            .into(viewHolder.photoIv);
                } else {
                    if (!strImgUrl.startsWith("http")) {
                        strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                    }

                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(strImgUrl)
                            .into(viewHolder.photoIv);
                }

                viewHolder.titleTv.setText(item.getTitle());
                viewHolder.writerTv.setText("by " + item.getStrWriterName());
                viewHolder.heartTv.setText(CommonUtils.getPointCount(item.getnKeepcount()));
                viewHolder.tabTv.setText(CommonUtils.getPointCount(item.getnTapCount()));
                viewHolder.synopsisTv.setText(item.getSynopsis());

                if (!TextUtils.isEmpty(vo.getNoti())) {
                    viewHolder.notiLl.setVisibility(View.VISIBLE);
                    viewHolder.notiTv.setText(vo.getNoti());
                } else {
                    viewHolder.notiLl.setVisibility(View.GONE);
                }

                viewHolder.optionIv.setVisibility(View.GONE);

                float fStarPoint = item.getfStarPoint();

                if (fStarPoint == 0)
                    viewHolder.starTv.setText("0");
                else
                    viewHolder.starTv.setText(String.format("%.1f", fStarPoint));
            } else if (holder instanceof MyPageFeedFollowViewHolder) {
                MyPageFeedFollowViewHolder viewHolder = (MyPageFeedFollowViewHolder) holder;

                viewHolder.subArrayList.clear();
                viewHolder.subArrayList.addAll(vo.getFollow());
                viewHolder.adapter.notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return arrayList.get(position).getType();
        }
    }

    public class MyPageFeedCommentViewHolder extends RecyclerView.ViewHolder {

        EditText commentEt;
        ImageView writeIv;

        public MyPageFeedCommentViewHolder(@NonNull View itemView, TypeOnClickListener listener) {
            super(itemView);

            commentEt = itemView.findViewById(R.id.et_row_my_page_feed_comment);
            writeIv = itemView.findViewById(R.id.iv_row_my_page_feed_write);
            writeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(0, getAdapterPosition());
                    }
                }
            });
        }
    }

    public class MyPageFeedFollowViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        MyPageFeedFollowAdapter adapter;
        ArrayList<WriterVO> subArrayList = new ArrayList<>();

        public MyPageFeedFollowViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.row_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);

                    int position = parent.getChildAdapterPosition(view);
                    int itemCount = state.getItemCount();

                    outRect.left = DeviceUtils.dpToPx(getContext(), 20);

                    if (position == itemCount - 1) {
                        outRect.right = DeviceUtils.dpToPx(getContext(), 20);
                    }
                }
            });
            adapter = new MyPageFeedFollowAdapter(subArrayList);
            recyclerView.setAdapter(adapter);
            ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView parent, View view, int position, long id) {
                    WriterVO item = followArrayList.get(position);

                    Intent intent = new Intent(getActivity(), WriterPageActivity.class);
                    intent.putExtra("writerId", item.getStrWriterID());
                    startActivity(intent);
                }
            });
        }

        public class MyPageFeedFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            ArrayList<WriterVO> arrayList;

            public MyPageFeedFollowAdapter(ArrayList<WriterVO> arrayList) {
                this.arrayList = arrayList;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_page_feed_follow_sub, parent, false);
                return new MyPageFeedFollowSubViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                WriterVO item = arrayList.get(position);

                if (holder instanceof MyPageFeedFollowSubViewHolder) {
                    MyPageFeedFollowSubViewHolder viewHolder = (MyPageFeedFollowSubViewHolder) holder;

                    String photoUrl = item.getStrWriterPhoto();
                    if (!TextUtils.isEmpty(photoUrl)) {
                        if (!photoUrl.startsWith("http"))
                            photoUrl = CommonUtils.strDefaultUrl + "images/" + photoUrl;

                        Glide.with(getActivity())
                                .asBitmap() // some .jpeg files are actually gif
                                .placeholder(R.drawable.user_icon)
                                .load(photoUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(viewHolder.photoIv);
                    }

                    viewHolder.nameTv.setText(item.getStrWriterName());
                }
            }

            @Override
            public int getItemCount() {
                return arrayList.size();
            }
        }

        public class MyPageFeedFollowSubViewHolder extends RecyclerView.ViewHolder {
            ImageView photoIv;
            TextView nameTv;

            public MyPageFeedFollowSubViewHolder(@NonNull View itemView) {
                super(itemView);

                photoIv = itemView.findViewById(R.id.iv_row_my_page_feed_sub_photo);
                nameTv = itemView.findViewById(R.id.tv_row_my_page_feed_sub_name);
            }
        }
    }

    public class MyPageFeedViewHolder extends RecyclerView.ViewHolder {

        public MyPageFeedViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // 내가 쓴 작품 목록
    private void getAllWorkWithWriterID() {
        if (SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest").equals(writerId)) {
            MyPageFeedVo desc = new MyPageFeedVo();
            desc.setUserDesc(SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_DESC", ""));
            desc.setType(0);
            mArrayList.add(desc);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null)
                    return;

                myArrayList = HttpClient.GetAllWorkListWithWriterID(new OkHttpClient(), writerId);

                if (myArrayList != null) {
                    for (int i = 0; i < myArrayList.size(); i++) {
                        WorkVO workVO = myArrayList.get(i);
                        MyPageFeedVo myPageFeedVo = new MyPageFeedVo();
                        myPageFeedVo.setType(1);
                        myPageFeedVo.setWorkVO(workVO);

                        if (i == 0) {
                            myPageFeedVo.setNoti("쓰고있는 작품");
                        }
                        mArrayList.add(myPageFeedVo);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myArrayList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        getMyFollowingList();
                    }
                });
            }
        }).start();
    }

    // 내가 읽은 작품 목록
    private void getReadListData() {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");
        readArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<WorkVO> responseVo = HttpClient.getReadWorkList(new OkHttpClient(), writerId, "UPDATE");
                if (responseVo != null) {
                    readArrayList.addAll(responseVo);
                }

                if (readArrayList != null) {
                    for (int i = 0; i < readArrayList.size(); i++) {
                        WorkVO workVO = readArrayList.get(i);
                        MyPageFeedVo myPageFeedVo = new MyPageFeedVo();
                        myPageFeedVo.setType(3);
                        myPageFeedVo.setWorkVO(workVO);

                        if (i == 0) {
                            myPageFeedVo.setNoti("읽고있는 작품");
                        }
                        mArrayList.add(myPageFeedVo);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (readArrayList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    // 내가 팔로잉 중인 사람들
    private void getMyFollowingList() {
        CommonUtils.showProgressDialog(getActivity(), "팔로우 리스트를 가져오고 있습니다.");
        followArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                followArrayList.addAll(HttpClient.getMyFollowingList(new OkHttpClient(), writerId));

                for (int i = 0; i < followArrayList.size(); i++) {
                    WriterVO vo = followArrayList.get(i);
                    if (vo.getStrWriterID().equals(writerId)) {
                        followArrayList.remove(i);
                    }
                }

                if (followArrayList.size() > 0) {
                    MyPageFeedVo vo = new MyPageFeedVo();
                    vo.setType(2);
                    vo.setFollow(followArrayList);
                    mArrayList.add(vo);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.hideProgressDialog();

                        if (followArrayList == null) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        getReadListData();
                    }
                });
            }
        }).start();
    }

    private void requestUpdateUserDesc(String desc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest"), "USER_DESC", desc);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "내 소개글이 변경되었습니다.", Toast.LENGTH_SHORT).show();

                        SimplePreference.setPreference(getContext(), "USER_INFO", "USER_DESC", desc);
                    }
                });
            }
        }).start();
    }
}
