package com.Whowant.Tokki.UI.Fragment.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;

public class CoinUseLogFragment extends Fragment {
    private TextView emptyView;

    public static Fragment newInstance() {
        CoinUseLogFragment fragment = new CoinUseLogFragment();
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

        View inflaterView = inflater.inflate(R.layout.list_fragment, container, false);

        emptyView = inflaterView.findViewById(R.id.emptyView);
        emptyView.setText("사용 내역이 없습니다.");
        return inflaterView;
    }
}
