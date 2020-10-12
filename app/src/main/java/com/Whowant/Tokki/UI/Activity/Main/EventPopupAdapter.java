package com.Whowant.Tokki.UI.Activity.Main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.EventPopupFragment;
import com.Whowant.Tokki.UI.Fragment.Main.SNSPopupFragment;
import com.Whowant.Tokki.VO.EventVO;

import java.util.ArrayList;

public class EventPopupAdapter extends FragmentStatePagerAdapter {
    private ArrayList<EventVO> eventList = new ArrayList<EventVO>();

    public EventPopupAdapter(FragmentManager fm, ArrayList<EventVO> list) {
        super(fm);
        eventList = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(eventList.get(position).getnEventType() == 100) {
            return new SNSPopupFragment(eventList.get(position));
        } else {
            return new EventPopupFragment(eventList.get(position));
        }
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setEventList(ArrayList<EventVO> list) {
        eventList = list;
        notifyDataSetChanged();
    }
}
