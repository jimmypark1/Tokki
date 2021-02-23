package com.Whowant.Tokki.UI.Activity.Market;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Friend.FriendListFragment;
import com.Whowant.Tokki.UI.Fragment.Friend.FriendMessageFragment;
import com.Whowant.Tokki.UI.Fragment.Friend.FriendRecommendFragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class TransactionAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments=new ArrayList<>();

    public TransactionAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new TransactionProgressingFragment());
        fragments.add(new TransactionCompleteFragment());
        fragments.add(new CarrotBuyFragment());
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