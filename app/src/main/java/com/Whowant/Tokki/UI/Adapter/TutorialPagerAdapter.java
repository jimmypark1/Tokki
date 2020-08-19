package com.Whowant.Tokki.UI.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Login.TutorialFragment;

public class TutorialPagerAdapter extends FragmentPagerAdapter {
    private TutorialFragment tutorial1;
    private TutorialFragment tutorial2;
    private TutorialFragment tutorial3;

    public TutorialPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

        tutorial1 = new TutorialFragment(0);
        tutorial2 = new TutorialFragment(1);
        tutorial3 = new TutorialFragment(2);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return tutorial1;
        else if(position == 1)
            return tutorial2;
        else
            return tutorial3;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
