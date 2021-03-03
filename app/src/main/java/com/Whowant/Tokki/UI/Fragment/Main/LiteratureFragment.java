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

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkWriteMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class LiteratureFragment extends Fragment {                                          // 3번탭 작성 시작하는 페이지.
    private ArrayList<WorkVO> workList;
    private ListView listView;
    private TextView emptyView;
    private CWorkListAdapter aa;
    private boolean bVisible = false;
    int nTarget = 0;

    public static Fragment newInstance() {
        LiteratureFragment fragment = new LiteratureFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.activity_literature_list, container, false);

        listView = inflaterView.findViewById(R.id.listView);
        emptyView = inflaterView.findViewById(R.id.emptyView);
        emptyView.setVisibility(View.INVISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WorkVO workVO = workList.get(position);
                Intent intent = new Intent(getActivity(), WorkWriteMainActivity.class);
                intent.putExtra("WORK_ID", "" + workVO.getnWorkID());
                nTarget = workVO.getnTarget();
                intent.putExtra("NOVEL_TYPE", nTarget);

                startActivity(intent);

//                startActivity(new Intent(getActivity(), WorkRegSummaryActivity.class));
            }
        });

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bVisible)
            GetAllWorkList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            FirebaseAnalytics.getInstance(getContext()).setCurrentScreen(getActivity(), "LiteratureFragment", null);
            bVisible = true;
            GetAllWorkList();
        } else {
            bVisible = false;
        }
    }

    private void GetAllWorkList() {                                                                                     // 내가 쓴 작품 목록을 리스트로 표시. 클릭하면 해당 작품 글을 작성/수정하러 이동.
                                                                                                                        // MainActivity 에서 우측상단 버튼 클릭 시에는 새로운 작품을 생성하러 이동
        CommonUtils.showProgressDialog(getActivity(), "서버에서 작품 목록을 가져오고 있습니다. 잠시만 기다려주세요.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                workList = HttpClient.GetAllWorkListWithID(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    //    CommonUtils.hideProgressDialog();

                        if(workList == null) {
                            emptyView.setVisibility(View.VISIBLE);
                            CommonUtils.hideProgressDialog();

                            return;
                        }

                        aa = new CWorkListAdapter(getActivity(), R.layout.row_literature, workList);
//                        aa = new CWorkListAdapter(getActivity(), R.round_squre_stroke_gray_bg.my_work_write_row, workList);
                        listView.setAdapter(aa);

                        if(workList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                        CommonUtils.hideProgressDialog();

                    }
                });
            }
        }).start();
    }

    public class CWorkListAdapter extends ArrayAdapter<Object>
    {
        private int mCellLayout;
        private LayoutInflater mLiInflater;
        private SharedPreferences.Editor editor;

        CWorkListAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mCellLayout = layout;
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            WorkVO workVO = workList.get(position);

            if(convertView == null) {
                convertView = mLiInflater.inflate(R.layout.row_literature, parent, false);
//                convertView = mLiInflater.inflate(R.round_squre_stroke_gray_bg.my_work_write_row, parent, false);
            }

            ImageView coverImgView = convertView.findViewById(R.id.iv_row_literature_photo);
            TextView titleView = convertView.findViewById(R.id.tv_row_literature_title);
            TextView synopsisView = convertView.findViewById(R.id.tv_row_literature_contents);
//            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
            TextView dateView = convertView.findViewById(R.id.tv_row_literature_date);
            coverImgView.setClipToOutline(true);
//            ImageView coverImgView = convertView.findViewById(R.id.coverImgView);
//            TextView titleView = convertView.findViewById(R.id.titleView);
//            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
//            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
//            TextView dateView = convertView.findViewById(R.id.dateView);

            String strImgUrl = workVO.getCoverFile();
            nTarget = workVO.getnTarget();

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster)
//                        .apply(new RequestOptions().override(800, 800))
                        .into(coverImgView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverImgView);
            }

            titleView.setText(workVO.getTitle());
            synopsisView.setText(workVO.getSynopsis());
//            writerNameView.setText("by " + workVO.getStrWriterName());
            dateView.setText(workVO.getCreatedDate().substring(0, 10));

            return convertView;
        }
    }
}
