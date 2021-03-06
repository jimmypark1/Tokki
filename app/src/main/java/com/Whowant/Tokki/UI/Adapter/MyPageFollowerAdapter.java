package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageFollowerFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageFollowingFragment;

import java.util.ArrayList;

public class MyPageFollowerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();


    public MyPageFollowerAdapter(Context context, @NonNull FragmentManager fm, String writerId, int type) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.mContext = context;

        MyPageFollowerFragment myPageFollowerFragment = new MyPageFollowerFragment();

        myPageFollowerFragment.type = type;
        MyPageFollowingFragment myPageFollowingFragment = new MyPageFollowingFragment();
        myPageFollowingFragment.type = type;

        Bundle bundle = new Bundle();
        bundle.putString("writerId", writerId);
        myPageFollowerFragment.setArguments(bundle);
        myPageFollowingFragment.setArguments(bundle);

        fragments.add(myPageFollowerFragment);
        fragments.add(myPageFollowingFragment);


        titles.add("팔로워");
        titles.add("팔로잉");
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
