package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.MyWorkRecyclerAdapter;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MyKeepFragment extends Fragment {
    private RecyclerView myWorkRecyclerView;
    private MyWorkRecyclerAdapter adapter;
    private ArrayList<WorkVO> workList;
    private boolean isVisible = false;

    public MyKeepFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.recycler_fragment, container, false);

        myWorkRecyclerView = inflaterView.findViewById(R.id.myWorkRecyclerView);
        myWorkRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return inflaterView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            isVisible = true;
            getKeepListData();
        } else {
            isVisible = false;
        }
    }

    private void getKeepListData() {
        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                workList = HttpClient.getKeepWorkList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(workList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapter = new MyWorkRecyclerAdapter(getActivity(), workList);
                        myWorkRecyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
}
