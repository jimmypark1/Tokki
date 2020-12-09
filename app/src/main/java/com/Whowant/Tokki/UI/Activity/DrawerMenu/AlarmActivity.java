package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.AlarmVO;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class AlarmActivity extends AppCompatActivity {
    private ArrayList<AlarmVO> bestList;
    private ListView listView;
    private CAlarmArrayAdapter aa;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("알림");

        bestList = new ArrayList<>();

        listView = findViewById(R.id.listView);
        aa = new CAlarmArrayAdapter(this, R.layout.alarm_row, bestList);
        listView.setAdapter(aa);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlarmVO vo = bestList.get(position);

                String strWorkID = vo.getStrObjectID();
                setRead(vo.getnAlarmID());
                try{
                    int nWorkID = Integer.valueOf(strWorkID);
                    Intent intent = new Intent(AlarmActivity.this, WorkMainActivity.class);
                    intent.putExtra("WORK_ID", nWorkID);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(AlarmActivity.this, "알림의 동작이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void setRead(int nAlarmID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient.setAlarmRead(new OkHttpClient(), pref.getString("USER_ID", ""), nAlarmID);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        getAlarmList();
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

    public class CAlarmArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CAlarmArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.notice_row, parent, false);

            AlarmVO vo = bestList.get(position);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView contentsView = convertView.findViewById(R.id.contentsView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            ImageView arrowBtn = convertView.findViewById(R.id.arrowBtn);
            arrowBtn.setVisibility(View.GONE);
            contentsView.setMaxLines(100);
//            TextView titleView = convertView.findViewById(R.id.titleView);
//            TextView descView = convertView.findViewById(R.id.descView);
//            TextView dateTimeView = convertView.findViewById(R.id.dateTimeView);
            ImageView newIconView = convertView.findViewById(R.id.newIconView);

            titleView.setText(vo.getAlarmTitle());
            contentsView.setText(vo.getAlarmContents());
            dateView.setText(vo.getStrDateTime().substring(0, 10));

            if(vo.isbRead()) {
                newIconView.setVisibility(View.INVISIBLE);
            } else {
                newIconView.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    private void getAlarmList() {
        CommonUtils.showProgressDialog(AlarmActivity.this, "알림 정보를 가져오고 있습니다.");
        bestList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AlarmVO> list = HttpClient.getAlarmList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                if(list != null)
                    bestList.addAll(list);

                if (list.size() == 0 || list == null) {
                    TextView emptyView = findViewById(R.id.emptyView);
                    emptyView.setVisibility(View.VISIBLE);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(list == null) {
                            Toast.makeText(AlarmActivity.this, "서버와의 연결이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        aa.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}
