package com.Whowant.Tokki.UI.Fragment.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;

public class CoinChargeFragment extends Fragment {
    public static Fragment newInstance() {
        CoinChargeFragment fragment = new CoinChargeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.coin_list_fragment, container, false);

        return inflaterView;
    }
}
