package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.NoticeVO;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    private ArrayList<NoticeVO> noticeList;
    private ListView listView;
    private TextView emptyView;
    private CNoticeArrayAdapter aa;
    private ExpandAdapter ea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        noticeList = new ArrayList<>();
        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
//        ea = new ExpandAdapter(this, noticeList);
//        aa = new CNoticeArrayAdapter(this, R.layout.notice_row, noticeList);
//        listView.setAdapter(aa);

        getNoticeData();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getNoticeData() {
        CommonUtils.showProgressDialog(NoticeActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                noticeList = HttpClient.getNoticeList(new OkHttpClient(), NoticeActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        aa = new CNoticeArrayAdapter(NoticeActivity.this, R.layout.notice_row, noticeList);
                        listView.setAdapter(aa);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                NoticeVO vo = noticeList.get(position);
                                if(vo.getbExpand()) {
                                    vo.setbExpand(false);
                                } else {
                                    vo.setbExpand(true);
                                }

                                vo.setbRead(true);
                                noticeList.set(position, vo);
                                SharedPreferences pref = getSharedPreferences("NOTICE_INFO", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("" + vo.getnNoticeID(), true);
                                editor.commit();
                                aa.notifyDataSetChanged();
                            }
                        });

                        if(noticeList.size() > 0) {
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }

    public class CNoticeArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CNoticeArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.notice_row, parent, false);

            final NoticeVO vo = noticeList.get(position);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView contentsView = convertView.findViewById(R.id.contentsView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            ImageView arrowBtn = convertView.findViewById(R.id.arrowBtn);
            ImageView newIconView = convertView.findViewById(R.id.newIconView);

            titleView.setText(vo.getStrNoticeTitle());

            String strContents = vo.getStrNoticeContents();
            strContents = strContents.replaceAll("\\\\n", " ");
            contentsView.setText(strContents);

            if(vo.getbExpand()) {
                contentsView.setMaxLines(100);
                arrowBtn.setBackgroundResource(R.drawable.up_arrow_button);
            } else {
                contentsView.setMaxLines(1);
                arrowBtn.setBackgroundResource(R.drawable.down_arrow_btn);
            }

            dateView.setText(vo.getStrRegisterDate().substring(0, 10));

            arrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(vo.getbExpand()) {
                        vo.setbExpand(false);
                        aa.notifyDataSetChanged();
                    } else {
                        vo.setbExpand(true);
                        aa.notifyDataSetChanged();
                    }
                }
            });

            if(vo.getbRead())
                newIconView.setVisibility(View.INVISIBLE);
            else
                newIconView.setVisibility(View.VISIBLE);

            return convertView;
        }
    }

    public class ExpandAdapter extends BaseExpandableListAdapter {
        private ArrayList<NoticeVO> DataList;
        private LayoutInflater myinf = null;

        public ExpandAdapter(Context context, ArrayList<NoticeVO> DataList){
            this.DataList = DataList;
            this.myinf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView == null)
                convertView = myinf.inflate(R.layout.notice_row, parent, false);

            NoticeVO vo = noticeList.get(groupPosition);

            ImageView coverImgView = convertView.findViewById(R.id.coverImgView);
            coverImgView.setVisibility(View.GONE);

            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView contentsView = convertView.findViewById(R.id.contentsView);
            TextView dateView = convertView.findViewById(R.id.dateView);

            titleView.setText(vo.getStrNoticeTitle());

            String strContents = vo.getStrNoticeContents();
            strContents = strContents.replaceAll("\\\\n", " ");
            contentsView.setText(strContents);
            dateView.setText(vo.getStrRegisterDate());

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView == null){
                convertView = myinf.inflate(R.layout.notice_detail_row, parent, false);
            }

            NoticeVO vo = noticeList.get(groupPosition);

            TextView detailContentsView = convertView.findViewById(R.id.detailContentsView);
            String strContents = vo.getStrNoticeContents();
            strContents = strContents.replaceAll("\\\\n", "\n");
            detailContentsView.setText(strContents);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return DataList.get(groupPosition).getStrNoticeContents();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public NoticeVO getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return DataList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return DataList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

    }
}
