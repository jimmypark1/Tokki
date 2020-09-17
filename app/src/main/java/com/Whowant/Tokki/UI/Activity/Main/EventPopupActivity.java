package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.EventVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventPopupActivity extends AppCompatActivity {
    public static ArrayList<EventVO> eventList;
    private ArrayList<EventVO> currentList = new ArrayList<EventVO>();

    private ViewPager viewPager;
    private EventPopupAdapter adapter;
    private String strToday;
    private TextView confirmTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_popup);

        confirmTextView = findViewById(R.id.confirmTextView);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        strToday = sdf.format(date);
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        for(EventVO vo : eventList) {
            String strDate = pref.getString("" + vo.getnEventID(), "");

            if(vo.getnEventID() == -10 && strDate.length() == 0) {
                currentList.add(vo);
                confirmTextView.setText("다시 보지 않기");
            } else if(strDate.length() == 0 || !strDate.equals(strToday)) {
                currentList.add(vo);
            }
        }

        viewPager = findViewById(R.id.viewPager);
        adapter = new EventPopupAdapter(getSupportFragmentManager(), currentList);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                EventVO vo = eventList.get(position);

                if(vo.getnEventType() == 100) {
                    confirmTextView.setText("다시 보지 않기");
                } else {
                    confirmTextView.setText("오늘 하루 이 내용 보지 않기");
                }
                //confirmTextView
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickRemoveBtn(View view) {
        int nIndex = viewPager.getCurrentItem();

        SharedPreferences pref = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("" + currentList.get(nIndex).getnEventID(), strToday);
        editor.commit();

        currentList.remove(nIndex);
        eventList.remove(nIndex);
        if(eventList == null || eventList.size() == 0) {
            finish();
        }

        adapter.setEventList(eventList);
    }
}