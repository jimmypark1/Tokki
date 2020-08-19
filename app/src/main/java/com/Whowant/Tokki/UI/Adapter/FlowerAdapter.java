package com.Whowant.Tokki.UI.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.MyFollowerFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MyFollowingFragment;

public class FlowerAdapter extends FragmentPagerAdapter {
    private int nFollowingCount = 0;
    private int nFollowerCount = 0;
    private String strWriterID;
    private MyFollowerFragment myFollowerFragment;
    private MyFollowingFragment myFollowingFragment;

    public FlowerAdapter(@NonNull FragmentManager fm, int nFollowerCount, int nFollowingCount, String strWriterID) {
        super(fm);

        myFollowerFragment = new MyFollowerFragment(strWriterID);
        myFollowingFragment = new MyFollowingFragment(strWriterID);
        this.nFollowerCount = nFollowerCount;
        this.nFollowingCount = nFollowingCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 1)
            return myFollowingFragment;
        else
            return myFollowerFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return nFollowerCount + " 팔로워";
        else if(position == 1)
            return nFollowingCount + " 팔로잉";

        return "";
    }
}
