package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Report.ReportActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.WriterChatVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyPageTalkFragment extends Fragment {

    RecyclerView recyclerView;
    MyPageTalkAdapter adapter;

    private ArrayList<WriterChatVO> commentList = new ArrayList<>();
    private ArrayList<ArrayList<WriterChatVO>> subCommentList = new ArrayList<>();

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
        adapter = new MyPageTalkAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "position : " + position, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getWriterChat();
    }

    public class MyPageTalkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;

        public MyPageTalkAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_my_page_talk_my, parent, false);

            if (viewType == 1) {
                v = LayoutInflater.from(context).inflate(R.layout.row_my_page_talk_your, parent, false);
                return new MyPageTalkYourViewHolder(v);
            }

            return new MyPageTalkViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }

        @Override
        public int getItemViewType(int position) {
            int type = 0;

            if (position == 3 || position == 4) {
                type = 1;
            }

            return type;
        }
    }

    public class MyPageTalkViewHolder extends RecyclerView.ViewHolder {

        public MyPageTalkViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MyPageTalkYourViewHolder extends RecyclerView.ViewHolder {

        ImageView optionIv;

        public MyPageTalkYourViewHolder(@NonNull View itemView) {
            super(itemView);

            optionIv = itemView.findViewById(R.id.iv_row_my_page_talk_your_option);
            optionIv.setOnClickListener((v) -> {
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
            });
        }
    }

    private void getWriterChat() {
        CommonUtils.showProgressDialog(getContext(), "목록을 가져오고 있습니다. 잠시만 기다려주세요.");
        commentList.clear();
        subCommentList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray resultArray = HttpClient.getWriterChatComment(new OkHttpClient(), writerId);

                if (resultArray != null) {
                    reOrderCommentList(resultArray);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (resultArray == null) {
                            Toast.makeText(getContext(), "목록을 가져오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//                            setNoComment();
                            return;
                        }

                        if (commentList.size() == 0) {
//                            setNoComment();
                            return;
                        }

//                        listView.setVisibility(View.VISIBLE);
//                        emptyLayout.setVisibility(View.INVISIBLE);

//                        expandableAdapter = new ChatActivity.CExpandableListviewAdapter();
//                        expandableAdapter.parentItems = commentList;
//                        expandableAdapter.childItems = subCommentList;
//                        listView.setAdapter(expandableAdapter);
                    }
                });
            }
        }).start();
    }

    private void reOrderCommentList(JSONArray array) {
        ArrayList<WriterChatVO> subList = new ArrayList<>();

        try {
            for (int i = 0; i < array.length(); i++) {                                     // Parent Group 생성
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

                int nParentID = object.getInt("PARENT_ID");
                if (nParentID > -1) {
                    continue;
                }

                commentList.add(vo);
                subCommentList.add(null);
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

                int nParentID = object.getInt("PARENT_ID");
                if (nParentID > -1) {
                    for (int j = 0; j < commentList.size(); j++) {
                        WriterChatVO tempVO = commentList.get(j);
                        subList = subCommentList.get(j);
                        if (subList == null)
                            subList = new ArrayList<>();

                        int nCommentID = tempVO.getCommentID();

                        if (nParentID == nCommentID) {
                            WriterChatVO subVO = new WriterChatVO();
                            subVO.setCommentID(object.getInt("COMMENT_ID"));
                            subVO.setStrWriterID(object.getString("WRITER_ID"));
                            subVO.setParentID(object.getInt("PARENT_ID"));
                            subVO.setStrComment(object.getString("COMMENT"));
                            subVO.setRegisterDate(object.getString("REGISTER_DATE"));
                            subVO.setUserName(object.getString("USER_NAME"));
                            subVO.setUserPhoto(object.getString("USER_PHOTO"));
                            subVO.setUserID(object.getString("USER_ID"));
                            subVO.setnUserDonationCarrot(object.getInt("DONATION_CARROT"));
                            subVO.setParentID(nParentID);
                            subList.add(subVO);
                            subCommentList.set(j, subList);

                            tempVO.setHasChild(true);
                            commentList.set(j, tempVO);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
