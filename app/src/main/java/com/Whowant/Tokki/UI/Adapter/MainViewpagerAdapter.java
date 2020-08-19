package com.Whowant.Tokki.UI.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.KeepFragment;
import com.Whowant.Tokki.UI.Fragment.Main.LiteratureFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MainFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MyFragment;

public class MainViewpagerAdapter extends FragmentPagerAdapter {
    private MainFragment mainFragment;
    private KeepFragment keepFragment;
    private LiteratureFragment literatureFragment;
    private MyFragment myFragment;

    public MainViewpagerAdapter(FragmentManager fm) {
        super(fm);

//        mainFragment = (MainFragment)MainFragment.newInstance();
//        keepFragment = (KeepFragment) KeepFragment.newInstance();
//        literatureFragment = (LiteratureFragment)LiteratureFragment.newInstance();
//        myFragment = (MyFragment)MyFragment.newInstance();
        mainFragment = new MainFragment();
        keepFragment = new KeepFragment();
        literatureFragment = new LiteratureFragment();
        myFragment = new MyFragment();

    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return mainFragment;
            case 1:
                return keepFragment;
            case 2:
                return literatureFragment;
            case 3:
                return myFragment;
            default:
                return mainFragment;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "Panbook";
        else if(position == 1)
            return "보관함";
        else if(position == 2) {
            return "작품쓰기";
        } else if(position == 3) {
            return "마이 페이지";
        }

        return "";
    }
}
