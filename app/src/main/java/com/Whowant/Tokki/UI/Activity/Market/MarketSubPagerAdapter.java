package com.Whowant.Tokki.UI.Activity.Market;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.Whowant.Tokki.R;

import java.util.ArrayList;
import java.util.List;

public class MarketSubPagerAdapter  extends FragmentStatePagerAdapter {

    List<Fragment> fragments=new ArrayList<>();

    public MarketSubPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new MarketSubFragment());

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