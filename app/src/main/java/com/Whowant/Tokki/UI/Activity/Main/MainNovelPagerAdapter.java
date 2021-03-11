package com.Whowant.Tokki.UI.Activity.Main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.Whowant.Tokki.UI.Activity.Market.MarketContentsFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketGenreFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketTagFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class MainNovelPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments=new ArrayList<>();

    public MainNovelPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new MainFragment());
        fragments.add(new MainFragment());
        fragments.add(new MainFragment());
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}