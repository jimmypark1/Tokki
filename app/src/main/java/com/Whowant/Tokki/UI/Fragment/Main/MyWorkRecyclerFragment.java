package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.MyWorkRecyclerAdapter;
import com.Whowant.Tokki.VO.WorkVO;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyWorkRecyclerFragment extends Fragment {
    private RecyclerView myWorkRecyclerView;
    private MyWorkRecyclerAdapter adapter;
    private ArrayList<WorkVO> workList;
    private String strUserID;
    private boolean bVisible = false;

    public MyWorkRecyclerFragment(String strUserID) {
        this.strUserID = strUserID;
    }

    public void setUserID(String strUserID) {
        this.strUserID = strUserID;
        GetAllWorkList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.recycler_fragment, container, false);

        myWorkRecyclerView = inflaterView.findViewById(R.id.myWorkRecyclerView);
        myWorkRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bVisible)
            GetAllWorkList();

//        if(workList == null || workList.size() == 0)
//            GetAllWorkList();
}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            bVisible = true;
            GetAllWorkList();
        } else {
            bVisible = false;
        }
    }

    private void GetAllWorkList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(getActivity() == null)
                    return;

                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                workList = HttpClient.GetAllWorkListWithID(new OkHttpClient(), strUserID);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyWorkRecyclerAdapter(getActivity(), workList);
                        myWorkRecyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
}
