package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_popup);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        strToday = sdf.format(date);
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        for(EventVO vo : eventList) {
            String strDate = pref.getString("" + vo.getnEventID(), "");

            if(strDate.length() == 0 || !strDate.equals(strToday)) {
                currentList.add(vo);
            }
        }

        viewPager = findViewById(R.id.viewPager);
        adapter = new EventPopupAdapter(getSupportFragmentManager(), currentList);
        viewPager.setAdapter(adapter);
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