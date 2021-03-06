package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Friend.FriendRecommendFragment;
import com.Whowant.Tokki.UI.Fragment.Friend.FriendListFragment;
import com.Whowant.Tokki.UI.Fragment.Friend.FriendMessageFragment;

import java.util.ArrayList;

public class FriendAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();

    public FriendAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.mContext = context;

        fragments.add(new FriendRecommendFragment());
        fragments.add(new FriendListFragment());
        fragments.add(new FriendMessageFragment());

        titles.add("친구 추천");
        titles.add("친구 목록");
        titles.add("메시지");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
