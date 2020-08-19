package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.CommentVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MyCommentFragment extends Fragment {
    private ListView listView;
    private TextView emptyView;

    private ArrayList<CommentVO> commentList;
    private CCommentArrayAdapter aa;
    private SharedPreferences pref;

    public MyCommentFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.list_fragment, container, false);

        listView = inflaterView.findViewById(R.id.listView);
        emptyView = inflaterView.findViewById(R.id.emptyView);
        pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        commentList = new ArrayList<>();
        aa = new CCommentArrayAdapter(getActivity(), R.layout.chat_comment_row, commentList);
        listView.setAdapter(aa);

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCommentData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {

        }
    }

    private void getCommentData() {
        if(commentList == null)
            return;

        CommonUtils.showProgressDialog(getActivity(), "댓글 목록을 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                commentList = new ArrayList<>();
                pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                JSONArray resultArray = HttpClient.getChatCommentByID(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(resultArray == null) {
                            Toast.makeText(getActivity(), "댓글 목록을 가져오는데 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            setNoComment();
                            return;
                        }

                        try {
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject object = resultArray.getJSONObject(i);

                                CommentVO vo = new CommentVO();

                                vo.setUserID(object.getString("USER_ID"));
                                vo.setChatID(object.getInt("CHAT_ID"));
                                vo.setCommentID(object.getInt("COMMENT_ID"));
                                vo.setEpisodeID(object.getInt("EPISODE_ID"));
                                vo.setParentID(object.getInt("PARENT_ID"));
                                vo.setStrComment(object.getString("COMMENT"));
                                vo.setRegisterDate(object.getString("REGISTER_DATE"));
                                vo.setUserName(object.getString("USER_NAME"));
                                vo.setUserPhoto(object.getString("USER_PHOTO"));

                                commentList.add(vo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(commentList.size() == 0) {
                            setNoComment();
                            return;
                        }

                        emptyView.setVisibility(View.INVISIBLE);
                        aa = new CCommentArrayAdapter(getActivity(), R.layout.chat_comment_row, commentList);
                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    private void setNoComment() {
        listView.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.VISIBLE);
    }

    public class CCommentArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CCommentArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.chat_comment_row, parent, false);

            CommentVO vo = commentList.get(position);

            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateTimeView);
            TextView commentView = convertView.findViewById(R.id.commentView);

//            if(vo.getParentID() > -1) {
//                subIconView.setVisibility(View.VISIBLE);
//                bgView.setBackgroundColor(getResources().getColor(R.color.colorCommonGray));
//            } else {
//                subIconView.setVisibility(View.GONE);
//                bgView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//            }

            String strPhoto = vo.getUserPhoto();
            if(strPhoto != null && !strPhoto.equals("null") && !strPhoto.equals("NULL") && strPhoto.length() > 0) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strPhoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            } else {
                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.user_icon)
                        .apply(new RequestOptions().circleCrop())
                        .into(faceView);
            }

            nameView.setText(vo.getUserName());
            dateView.setText(CommonUtils.strGetTime(vo.getRegisterDate()));
            commentView.setText(vo.getStrComment());

            return convertView;
        }
    }
}
