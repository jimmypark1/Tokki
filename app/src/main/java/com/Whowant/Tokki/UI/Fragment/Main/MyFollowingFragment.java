package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Writer.FollowingWriterListActivity;
import com.Whowant.Tokki.UI.Activity.Writer.WriterMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WriterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MyFollowingFragment extends Fragment {                 // 내가 팔로잉 하는 사람들
    private ListView listView;
    private TextView emptyView;
    private ArrayList<WriterVO> writerList;
    private CFollowArrayAdapter aa;
    private String strUserID;
    private SharedPreferences pref;

    public MyFollowingFragment(String strUserID) {
        this.strUserID = strUserID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.follow_list_fragment, container, false);
        listView = inflaterView.findViewById(R.id.listView);
        emptyView = inflaterView.findViewById(R.id.emptyView);
        writerList = new ArrayList<WriterVO>();
        pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        getWriterList();

        return inflaterView;
    }

    private void getWriterList() {
        CommonUtils.showProgressDialog(getActivity(), "팔로우 리스트를 가져오고 있습니다.");
        writerList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                writerList.addAll(HttpClient.getMyFollowingList(new OkHttpClient(), strUserID));
//                String strMyID = pref.getString("USER_ID", "Guest");
                for(int i = 0 ; i < writerList.size() ; i++) {
                    WriterVO vo = writerList.get(i);
                    if(vo.getStrWriterID().equals(strUserID)) {
                        writerList.remove(i);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(writerList == null || writerList.size() == 0) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        aa = new CFollowArrayAdapter(getActivity(), R.layout.follow_writer_row, writerList);
                        listView.setAdapter(aa);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                WriterVO vo = writerList.get(position);
                                Intent intent = new Intent(getActivity(), WriterMainActivity.class);
                                intent.putExtra("USER_ID", vo.getStrWriterID());
                                startActivity(intent);
                            }
                        });

                        if(writerList.size() > 0) {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    public class CFollowArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CFollowArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.follow_writer_row, parent, false);

            WriterVO vo = writerList.get(position);

            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView writerCommentView = convertView.findViewById(R.id.writerCommentView);
            TextView followCountView = convertView.findViewById(R.id.followerView);
            TextView followingCountView = convertView.findViewById(R.id.followingView);

            String strPhoto = vo.getStrWriterPhoto();

            if(!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(getActivity())
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);

            nameView.setText(vo.getStrWriterName());

            String strComment = vo.getStrWriterComment();
            if(strComment == null || strComment.length() == 0 || strComment.equals("null"))
                strComment = "소개글이 없습니다.";

            writerCommentView.setText(strComment);
            followCountView.setText(CommonUtils.getPointCount(vo.getnFollowcount()));
            followingCountView.setText(CommonUtils.getPointCount(vo.getnFollowingCount()));

            return convertView;
        }
    }
}
