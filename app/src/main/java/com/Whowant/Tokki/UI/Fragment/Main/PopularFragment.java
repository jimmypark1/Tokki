package com.Whowant.Tokki.UI.Fragment.Main;

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
import com.Whowant.Tokki.UI.Activity.Main.GenreRankingActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.Whowant.Tokki.VO.WriterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;

public class PopularFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Context mContext;
    private ListView listView;
    private TextView emptyView;
    private ArrayList<WorkVO> workList;
    private CGenreRankingArrayAdapter aa;
    private String strGenreID;
    private SharedPreferences pref;

    public PopularFragment(Context context, String genreID) {
        this.mContext = context;
        this.strGenreID = genreID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.popular_fragment, container, false);
        listView = inflaterView.findViewById(R.id.listView);
        emptyView = inflaterView.findViewById(R.id.emptyView);
        getGenreRankingData();

        listView.setOnItemClickListener(this);

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getGenreRankingData() {
        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                workList = HttpClient.getGenreRankingList(new OkHttpClient(), strGenreID);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(workList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            emptyView.setVisibility(View.VISIBLE);
                            return;
                        }

                        if(workList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }

                        aa = new CGenreRankingArrayAdapter(getActivity(), R.layout.best_row, workList);
                        listView.setAdapter(aa);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                WorkVO vo = workList.get(position);
                                Intent intent = new Intent(getActivity(), WorkMainActivity.class);
                                intent.putExtra("WORK_ID", vo.getnWorkID());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WorkVO vo = workList.get(position);
            Intent intent = new Intent(getActivity(), WorkMainActivity.class);
            intent.putExtra("WORK_ID", vo.getnWorkID());
            startActivity(intent);
    }


    public class CGenreRankingArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CGenreRankingArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.best_row, parent, false);

            WorkVO vo = workList.get(position);

            String strImgUrl = vo.getCoverFile();

            ImageView coverView = convertView.findViewById(R.id.coverImgView);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            TextView starPointView = convertView.findViewById(R.id.startPointView);
            TextView hitsCountView = convertView.findViewById(R.id.hitsCountView);
            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            TextView subcriptionCountView = convertView.findViewById(R.id.subcriptionCountView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
            coverView.setClipToOutline(true);

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster_vertical)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(getActivity())
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster_vertical)
                        .load(strImgUrl)
                        .apply(new RequestOptions().centerCrop())
                        .into(coverView);
            }

            titleView.setText(vo.getTitle());
            synopsisView.setText(vo.getSynopsis());

            float fStarPoint = vo.getfStarPoint();
            if(fStarPoint == 0)
                starPointView.setText("0");
            else
                starPointView.setText(String.format("%.1f", vo.getfStarPoint()));

            writerNameView.setText(vo.getStrWriterName());
            hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
            commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));
            subcriptionCountView.setText(CommonUtils.getPointCount(vo.getnKeepcount()));

            return convertView;
        }
    }
}
