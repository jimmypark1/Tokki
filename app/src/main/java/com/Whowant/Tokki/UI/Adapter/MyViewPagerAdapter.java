package com.Whowant.Tokki.UI.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.MyWorkRecyclerFragment;
import com.Whowant.Tokki.UI.Fragment.Main.WriterCommentFragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    private MyWorkRecyclerFragment myWorkFragment;
//    private MyKeepFragment myKeepFragment;
    private WriterCommentFragment myCommentFragment;
    private String strUserID;

    public MyViewPagerAdapter(FragmentManager fm, String strUserID) {
        super(fm);

        this.strUserID = strUserID;

        myWorkFragment = new MyWorkRecyclerFragment(strUserID);
        myCommentFragment = new WriterCommentFragment(strUserID);
    }

    public void setUserID(String strUserID) {
        this.strUserID = strUserID;
        myWorkFragment.setUserID(strUserID);
        myCommentFragment.setUserID(strUserID);

    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return myWorkFragment;
            case 1:
                return myCommentFragment;

            default:
                return myCommentFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "내가 쓴 작품";
        else if(position == 1)
            return "내가 쓴 댓글";
        else if(position == 2) {
            return "내가 쓴 댓글";
        }

        return "";
    }
}
