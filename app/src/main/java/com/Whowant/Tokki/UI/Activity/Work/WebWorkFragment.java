package com.Whowant.Tokki.UI.Activity.Work;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.WebWorkVO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebWorkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WebWorkVO webWork;

    WebView webview;

    public WebWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebWorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebWorkFragment newInstance(String param1, String param2) {
        WebWorkFragment fragment = new WebWorkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    //    webview = findViewById(R.id.webview);
    //    webview.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
         View v =  inflater.inflate(R.layout.fragment_web_work, container, false);

        webview = v.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용


        webview.loadData(webWork.getRaw(), "text/html; charset=utf-8", "UTF-8");

        return v;
    }
}