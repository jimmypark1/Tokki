package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.TokkiGalleryFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class TokkiGalleryAdapter extends FragmentPagerAdapter {
    private ArrayList<String> folderNameList;
    private ArrayList<TokkiGalleryFragment> fragmentList = new ArrayList<>();
    private Context mContext;

    public TokkiGalleryAdapter(Context context, @NonNull FragmentManager fm, ArrayList<String> folderNameList) {
        super(fm);

        this.folderNameList = folderNameList;
        this.mContext = context;
        for(String currentName : this.folderNameList) {
            TokkiGalleryFragment fragment = new TokkiGalleryFragment(this.mContext, currentName);
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
        String currentName = folderNameList.get(position);
        return currentName;
    }
}
