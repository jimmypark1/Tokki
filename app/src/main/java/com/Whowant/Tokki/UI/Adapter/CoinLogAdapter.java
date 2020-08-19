package com.Whowant.Tokki.UI.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.CoinPurchaseLogFragment;
import com.Whowant.Tokki.UI.Fragment.Main.CoinUseLogFragment;

public class CoinLogAdapter extends FragmentPagerAdapter {
    private CoinPurchaseLogFragment coinPurchaseLogFragment;
    private CoinUseLogFragment coinUseLogFragment;

    public CoinLogAdapter(@NonNull FragmentManager fm) {
        super(fm);

        coinPurchaseLogFragment = (CoinPurchaseLogFragment) CoinPurchaseLogFragment.newInstance();
        coinUseLogFragment = (CoinUseLogFragment) CoinUseLogFragment.newInstance();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return coinPurchaseLogFragment;
        else if(position == 1)
            return coinUseLogFragment;

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "충전내역";
        else if(position == 1)
            return "사용내역";

        return "";
    }
}
