package com.Whowant.Tokki.UI.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Activity.Mypage.MyPageFragment;
import com.Whowant.Tokki.UI.Fragment.Main.FriendFragment;
import com.Whowant.Tokki.UI.Fragment.Main.KeepFragment;
import com.Whowant.Tokki.UI.Fragment.Main.LiteratureFragment;
import com.Whowant.Tokki.UI.Fragment.Main.MainFragment;
import com.Whowant.Tokki.UI.Fragment.Main.SearchFragment;
import com.Whowant.Tokki.UI.Fragment.Main.StorageBoxFragment;

public class MainViewpagerAdapter extends FragmentPagerAdapter {
    private MainFragment mainFragment;
    private SearchFragment searchFragment;
    private KeepFragment keepFragment;
    private StorageBoxFragment storageBoxFragment;
    private MyPageFragment myPageFragment;
    private LiteratureFragment literatureFragment;
    private FriendFragment friendFragment;

    public MainViewpagerAdapter(FragmentManager fm) {
        super(fm);

//        mainFragment = (MainFragment)MainFragment.newInstance();
//        keepFragment = (KeepFragment) KeepFragment.newInstance();
//        literatureFragment = (LiteratureFragment)LiteratureFragment.newInstance();
//        myFragment = (MyFragment)MyFragment.newInstance();
        mainFragment = new MainFragment();
        searchFragment = new SearchFragment();
//        keepFragment = new KeepFragment();
//        storageBoxFragment = new StorageBoxFragment();
        myPageFragment = new MyPageFragment();
        literatureFragment = new LiteratureFragment();
        friendFragment = new FriendFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mainFragment;
            case 1:
                return searchFragment;
            case 2:
//                return keepFragment;
//                return storageBoxFragment;
                return myPageFragment;
            case 3:
                return literatureFragment;
            case 4:
                return friendFragment;
            default:
                return mainFragment;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Panbook";
        else if (position == 1)
            return "보관함";
        else if (position == 2) {
            return "작품쓰기";
        } else if (position == 3) {
            return "마이 페이지";
        } else if (position == 4) {
            return "친구";
        }

        return "";
    }
}
