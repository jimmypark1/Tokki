package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;

import android.app.Activity;
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

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.VO.EventVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<EventVO> eventList;
    private ListView listView;
    private CEventArrayAdapter aa;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        emptyView = findViewById(R.id.emptyView);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("이벤트");

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        eventList = new ArrayList<>();
        listView = findViewById(R.id.listView);
        aa = new CEventArrayAdapter(this, R.layout.notice_row, eventList);
        listView.setAdapter(aa);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        CommonUtils.showProgressDialog(EventActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                eventList = HttpClient.getEventList(new OkHttpClient(), EventActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(eventList == null || eventList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                            return;
                        }

                        aa = new CEventArrayAdapter(EventActivity.this, R.layout.notice_row, eventList);
                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        EventVO vo = eventList.get(i);

        SharedPreferences pref = getSharedPreferences("EVENT_INFO", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("" + vo.getnEventID(), true);
        editor.commit();

        if(vo.getnEventType() == 0) {               // 일반 이벤틈
            Intent intent = new Intent(EventActivity.this, EventDetailActivity.class);
            intent.putExtra("EVENT_TITLE", vo.getStrEventTitle());
            intent.putExtra("EVENT_FILE", vo.getStrEventContentsFile());
            startActivity(intent);
        } else if(vo.getnEventType() == 10) {
            Intent intent = new Intent(EventActivity.this, ContestEventActivity.class);
            intent.putExtra("EVENT_TITLE", vo.getStrEventTitle());
            intent.putExtra("IMG_URL", vo.getStrEventContentsFile());
            startActivity(intent);
        }
    }

    public class CEventArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CEventArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.event_row, parent, false);

            EventVO vo = eventList.get(position);

            ImageView coverImgView = convertView.findViewById(R.id.coverImgView);
            coverImgView.setClipToOutline(true);
            String strPhoto = vo.getStrEventContentsFile();

            if(strPhoto != null && strPhoto.length() > 0 && !strPhoto.equals("null")) {
                if(!strPhoto.startsWith("http"))
                    strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

                Glide.with(EventActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(strPhoto)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverImgView);
            }

            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView contentsView = convertView.findViewById(R.id.contentsView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            ImageView newIconView = convertView.findViewById(R.id.newIconView);

            titleView.setText(vo.getStrEventTitle());
            contentsView.setText("");
            dateView.setText(vo.getStrRegisterDate());

            if(vo.isbRead()) {
                newIconView.setVisibility(View.INVISIBLE);
            } else {
                newIconView.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }
}
