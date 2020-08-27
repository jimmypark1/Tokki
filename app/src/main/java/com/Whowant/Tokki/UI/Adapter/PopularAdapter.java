package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.MyFollowerFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MyFollowingFragment;
import com.Whowant.Tokki.UI.Fragment.Main.PopularFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class PopularAdapter extends FragmentPagerAdapter {
    private ArrayList<HashMap<String, String>> genreList;
    private ArrayList<PopularFragment> fragmentList = new ArrayList<>();
    private Context mContext;

    public PopularAdapter(Context context, @NonNull FragmentManager fm, ArrayList<HashMap<String, String>> genreList) {             // genreList -> 타이틀, genreID
        super(fm);

        this.genreList = genreList;
        this.mContext = context;
        for(HashMap<String, String> currentMap : this.genreList) {
            PopularFragment fragment = new PopularFragment(this.mContext, currentMap.get("GENRE_ID"));
            this.fragmentList.add(fragment);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        HashMap<String, String> currentMap = genreList.get(position);
        return currentMap.get("GENRE_NAME");
    }
}
