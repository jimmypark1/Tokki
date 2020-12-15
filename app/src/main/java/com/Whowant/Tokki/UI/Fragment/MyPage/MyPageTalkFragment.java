package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.ChatActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageActivity;
import com.Whowant.Tokki.UI.Activity.Report.ReportActivity;
import com.Whowant.Tokki.UI.TypeOnClickListener;
import com.Whowant.Tokki.UI.TypeViewOnClickListener;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.WriterChatVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyPageTalkFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageTalkAdapter adapter;
    ArrayList<WriterChatVO> mArrayList = new ArrayList<>();

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

        View v = inflater.inflate(R.layout.fragment_my_page_talk, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new MyPageTalkAdapter(getContext(), mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int position = parent.getChildAdapterPosition(view);
                int itemCount = state.getItemCount();

                if (position == itemCount - 1) {
                    outRect.bottom = DeviceUtils.dpToPx(getContext(), 20);
                }
            }
        });
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                WriterChatVO item = mArrayList.get(position);

                if (!SimplePreference.getStringPreference(getContext(), "USER_INFO", "USER_ID", "Guest").equals(item.getStrWriterID())) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("WRITER_ID", item.getStrWriterID());
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MyPageActivity activity = (MyPageActivity) getActivity();
            if(activity.isPopup()) {
                activity.clearPopup();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWriterChat();
    }

    public class MyPageTalkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<WriterChatVO> arrayList;

        public MyPageTalkAdapter(Context context, ArrayList<WriterChatVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_my_page_talk_my, parent, false);

            if (viewType == 1) {
                v = LayoutInflater.from(context).inflate(R.layout.row_my_page_talk_your, parent, false);
                return new MyPageTalkYourViewHolder(v, new TypeViewOnClickListener() {
                    @Override
                    public void onClick(View v, int type, int position) {
                        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                        popupMenu.getMenuInflater().inflate(R.menu.my_page_talk_card_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.report:
                                        Intent intent = new Intent(getActivity(), ReportActivity.class);
                                        intent.putExtra("title", "댓글 신고");
                                        startActivity(intent);
                                        break;
                                    case R.id.reply:
                                        Toast.makeText(getActivity(), "답장", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            return new MyPageTalkViewHolder(v, new TypeOnClickListener() {
                @Override
                public void onClick(int type, int position) {

                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            WriterChatVO item = arrayList.get(position);

            if (holder instanceof MyPageTalkViewHolder) {
                MyPageTalkViewHolder viewHolder = (MyPageTalkViewHolder) holder;

                String strPhoto = item.getUserPhoto();
                if (strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                    if (!strPhoto.startsWith("http"))
                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strPhoto)
                            .placeholder(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(viewHolder.photoIv);
                } else {
                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(viewHolder.photoIv);
                }

                viewHolder.nameTv.setText(item.getUserName());
                viewHolder.dateTv.setText(CommonUtils.strGetTime(item.getRegisterDate()));
                viewHolder.commentTv.setText(item.getStrComment());

                if (item.getUserID().equals(SimplePreference.getStringPreference(context, "USER_INFO", "USER_ID", "Guest")) || SimplePreference.getStringPreference(context, "USER_INFO", "ADMIN", "N").equals("Y")) {
                    viewHolder.reportTv.setText("삭제");
                } else {
                    viewHolder.reportTv.setText("신고");
                }
            } else if (holder instanceof MyPageTalkYourViewHolder) {
                MyPageTalkYourViewHolder viewHolder = (MyPageTalkYourViewHolder) holder;

                String strPhoto = item.getUserPhoto();
                if (strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                    if (!strPhoto.startsWith("http"))
                        strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(strPhoto)
                            .placeholder(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(viewHolder.photoIv);
                } else {
                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(R.drawable.user_icon)
                            .apply(new RequestOptions().circleCrop())
                            .into(viewHolder.photoIv);
                }

                viewHolder.nameTv.setText(item.getUserName());
                viewHolder.dateTv.setText(CommonUtils.strGetTime(item.getRegisterDate()));
                viewHolder.commentTv.setText(item.getStrComment());

                if (item.getUserID().equals(SimplePreference.getStringPreference(context, "USER_INFO", "USER_ID", "Guest")) || SimplePreference.getStringPreference(context, "USER_INFO", "ADMIN", "N").equals("Y")) {
                    viewHolder.optionIv.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.optionIv.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            int type = 0;

            if (arrayList.get(position).getParentID() > -1) {
                type = 1;
            }

            return type;
        }
    }

    public class MyPageTalkViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;
        TextView dateTv;
        TextView commentTv;
        TextView reportTv;

        public MyPageTalkViewHolder(@NonNull View itemView, TypeOnClickListener listener) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_my_page_talk_my_photo);
            nameTv = itemView.findViewById(R.id.tv_row_my_page_talk_my_name);
            dateTv = itemView.findViewById(R.id.tv_row_my_page_talk_my_date);
            commentTv = itemView.findViewById(R.id.tv_row_my_page_talk_my_comment);
            reportTv = itemView.findViewById(R.id.tv_row_my_page_talk_my_report);
            reportTv.setOnClickListener((v) -> {
                if (listener != null) {
                    listener.onClick(0, getAdapterPosition());
                }
            });
        }
    }

    public class MyPageTalkYourViewHolder extends RecyclerView.ViewHolder {

        ImageView photoIv;
        TextView nameTv;
        TextView dateTv;
        TextView commentTv;
        ImageView optionIv;

        public MyPageTalkYourViewHolder(@NonNull View itemView, TypeViewOnClickListener listener) {
            super(itemView);

            photoIv = itemView.findViewById(R.id.iv_row_my_page_talk_your_photo);
            nameTv = itemView.findViewById(R.id.tv_row_my_page_talk_your_name);
            dateTv = itemView.findViewById(R.id.tv_row_my_page_talk_your_date);
            commentTv = itemView.findViewById(R.id.tv_row_my_page_talk_your_comment);
            optionIv = itemView.findViewById(R.id.iv_row_my_page_talk_your_option);
            optionIv.setOnClickListener((v) -> {
                if (listener != null) {
                    listener.onClick(v, 0, getAdapterPosition());
                }
            });
        }
    }

    private void getWriterChat() {
        CommonUtils.showProgressDialog(getContext(), "목록을 가져오고 있습니다. 잠시만 기다려주세요.");
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray resultArray = HttpClient.getWriterChatComment(new OkHttpClient(), writerId);

                if (resultArray != null) {
                    reCommentList(resultArray);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (resultArray == null) {
                            Toast.makeText(getContext(), "목록을 가져오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (mArrayList.size() == 0) {
                            return;
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void reCommentList(JSONArray array) {
        try {
            ArrayList<WriterChatVO> subArray = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                if (object.getInt("COMMENT_ID") == 0)
                    continue;

                int nParentID = object.getInt("PARENT_ID");
                if (nParentID > -1) {
                    WriterChatVO vo = new WriterChatVO();
                    vo.setCommentID(object.getInt("COMMENT_ID"));
                    vo.setStrWriterID(object.getString("WRITER_ID"));
                    vo.setParentID(object.getInt("PARENT_ID"));
                    vo.setStrComment(object.getString("COMMENT"));
                    vo.setRegisterDate(object.getString("REGISTER_DATE"));
                    vo.setUserName(object.getString("USER_NAME"));
                    vo.setUserPhoto(object.getString("USER_PHOTO"));
                    vo.setUserID(object.getString("USER_ID"));
                    vo.setnUserDonationCarrot(object.getInt("DONATION_CARROT"));
                    subArray.add(vo);
                }
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                if (object.getInt("COMMENT_ID") == 0)
                    continue;

                WriterChatVO vo = new WriterChatVO();
                vo.setCommentID(object.getInt("COMMENT_ID"));
                vo.setStrWriterID(object.getString("WRITER_ID"));
                vo.setParentID(object.getInt("PARENT_ID"));
                vo.setStrComment(object.getString("COMMENT"));
                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                vo.setUserName(object.getString("USER_NAME"));
                vo.setUserPhoto(object.getString("USER_PHOTO"));
                vo.setUserID(object.getString("USER_ID"));
                vo.setnUserDonationCarrot(object.getInt("DONATION_CARROT"));
                if (vo.getParentID() == -1) {
                    mArrayList.add(vo);
                }

                for (int j = 0; j < subArray.size(); j++) {
                    if (object.getInt("COMMENT_ID") == subArray.get(j).getParentID()) {
                        mArrayList.add(subArray.get(j));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
