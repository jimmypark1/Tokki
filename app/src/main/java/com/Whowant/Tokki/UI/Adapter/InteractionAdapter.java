package com.Whowant.Tokki.UI.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Work.InteractionMainFragment;

public class InteractionAdapter extends FragmentPagerAdapter {
    private InteractionMainFragment interactionMainFragment;
    private InteractionMainFragment  interactionSubFragment;

    public InteractionAdapter(FragmentManager fm) {
        super(fm);

        interactionMainFragment = (InteractionMainFragment)InteractionMainFragment.newInstance(1);
        interactionSubFragment = (InteractionMainFragment)InteractionMainFragment.newInstance(2);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return interactionMainFragment;
            case 1:
                return interactionSubFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "선택지 A";
        else if(position == 1)
            return "선택지 B";

        return "";
    }
}
