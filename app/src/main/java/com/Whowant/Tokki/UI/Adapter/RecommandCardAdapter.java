package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.RecommandCardFragment;
import com.Whowant.Tokki.VO.WorkVO;

import java.util.ArrayList;

public class RecommandCardAdapter extends FragmentPagerAdapter {
    private ArrayList<RecommandCardFragment> fragmentList;

    public RecommandCardAdapter(FragmentManager fm, ArrayList<WorkVO> workList, Context context) {
        super(fm);

        fragmentList = new ArrayList<>();

        for(int i = 0 ; i < workList.size() ; i++) {
            RecommandCardFragment fragment = new RecommandCardFragment(workList.get(i), context);
            fragmentList.add(fragment);
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
}
