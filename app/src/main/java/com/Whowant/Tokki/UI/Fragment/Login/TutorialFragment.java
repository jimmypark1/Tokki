package com.Whowant.Tokki.UI.Fragment.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.TutorialActivity;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;

public class TutorialFragment extends Fragment {
    private int nIndex = 0;
    private TextView nextBtn;
    private TextView beforeBtn;

    public TutorialFragment(int nIndex) {
        this.nIndex = nIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = null;

        if(nIndex == 0)
            inflaterView = inflater.inflate(R.layout.fragment_tutorial_01, container, false);
        else if(nIndex == 1)
            inflaterView = inflater.inflate(R.layout.fragment_tutorial_02, container, false);
        else
            inflaterView = inflater.inflate(R.layout.fragment_tutorial_03, container, false);

        nextBtn = inflaterView.findViewById(R.id.nextBtn);
        beforeBtn = inflaterView.findViewById(R.id.beforeBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nIndex < 2) {
                    ((TutorialActivity)getActivity()).setCurrent(nIndex+1);
                } else {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("FIRST", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        beforeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nIndex == 0) {
                    getActivity().finish();
                } else {
                    ((TutorialActivity)getActivity()).setCurrent(nIndex-1);
                }
            }
        });
        return inflaterView;
    }
}
