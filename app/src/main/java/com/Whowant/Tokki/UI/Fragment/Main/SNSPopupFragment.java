package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.EventVO;

public class SNSPopupFragment extends Fragment {
    private EventVO eventVO;

    public SNSPopupFragment(EventVO vo) {
        eventVO = vo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.sns_popup_row, container, false);

        ImageView instaBtn = inflaterView.findViewById(R.id.instaBtn);
        ImageView facebookBtn = inflaterView.findViewById(R.id.fbBtn);

        instaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.instagram.com/tokkinovel"));
                startActivity(intent);
            }
        });

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tokkinovel"));
                startActivity(intent);
            }
        });

        return inflaterView;
    }
}