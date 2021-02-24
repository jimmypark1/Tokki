package com.Whowant.Tokki.UI.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.Main.CoinChargeFragment;
import com.Whowant.Tokki.UI.Fragment.Main.CoinPurchaseFragment;

public class CoinPurchaseAdapter extends FragmentPagerAdapter {
    private CoinPurchaseFragment coinPurchaseFragment;
    private CoinChargeFragment coinChargeFragment;

    public CoinPurchaseAdapter(@NonNull FragmentManager fm) {
        super(fm);

//        coinPurchaseFragment = (CoinPurchaseFragment) CoinPurchaseFragment.newInstance();
        coinChargeFragment = (CoinChargeFragment) CoinChargeFragment.newInstance();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return coinPurchaseFragment;
        else if(position == 1)
            return coinChargeFragment;

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "코인구매";
        else if(position == 1)
            return "코인충전";

        return "";
    }
}
