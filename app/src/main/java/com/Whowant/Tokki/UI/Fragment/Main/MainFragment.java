package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.NoticeActivity;
import com.Whowant.Tokki.UI.Activity.Login.InputRecommendCodeActivity;
import com.Whowant.Tokki.UI.Activity.Main.SearchActivity;
import com.Whowant.Tokki.UI.Adapter.MainCardListAdapter;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MainCardVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MainFragment extends Fragment {                                                            // 1번 탭 메인 페이지. RecyclerView 구조로 안에 다른 RecyclerView 로 이루어져 있음
    private ArrayList<MainCardVO> mainCardList;                                                         // MainCardVO 가 한 줄 단위의 row 를 이룬다
    private ArrayList<MainCardVO> recommendCardList;
    private RecyclerView mainRecyclerView;
    private SwipeRefreshLayout refreshLayout;
    private MainCardListAdapter adapter;

    private boolean bVisible = false;

    public int nType = 0;
    public static Fragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public void Clear()
    {
      //  mainRecyclerView.smoothScrollToPosition(0);
        mainCardList.clear();
        recommendCardList.clear();

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainCardList = new ArrayList<>();
        recommendCardList = new ArrayList<>();
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inflaterView = inflater.inflate(R.layout.main_fragment, container, false);
        mainRecyclerView = inflaterView.findViewById(R.id.mainRecyclerView);
        refreshLayout = inflaterView.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                getMainData();
            }
        });

        mainRecyclerView.setHasFixedSize(true);
        adapter = new MainCardListAdapter(getActivity(), mainCardList,nType);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mainRecyclerView.setAdapter(adapter);

//        Intent intent = new Intent(getActivity(), EpisodeCommentActivity.class);
//        intent.putExtra("WORK_ID", 1172078772);
//        startActivity(intent);

//        startActivity(new Intent(getActivity(), InputRecommendCodeActivity.class));

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bVisible) {
            getMainData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            bVisible = true;
            getMainData();
            FirebaseAnalytics.getInstance(getContext()).setCurrentScreen(getActivity(), "MainFragment", null);
        } else {
            bVisible = false;
        }
    }

    public void getMainData() {
        if(mainCardList == null)
            return;

        CommonUtils.showProgressDialog(getActivity(), "최근 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");
        mainCardList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mainCardList = HttpClient.getAllRankingList(new OkHttpClient(), nType);
                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                recommendCardList = HttpClient.getRecommendList(new OkHttpClient(), pref.getString("USER_ID", "Guest"),nType);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(mainCardList == null || mainCardList.size() == 0) {
                            CommonUtils.hideProgressDialog();

                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();


                            return;
                        } else {
                            if (recommendCardList.size() < 1) {
                                MainCardVO vo = new MainCardVO();
                                vo.setViewType(3);

                                WorkVO emptyVO = new WorkVO();
                                ArrayList<WorkVO> emptyList = new ArrayList<>();
                                emptyList.add(emptyVO);
                                emptyList.add(emptyVO);
                                emptyList.add(emptyVO);
                                emptyList.add(emptyVO);
                                emptyList.add(emptyVO);
                                vo.setAllItemInCard(emptyList);
                                recommendCardList.clear();
                                recommendCardList.add(vo);

                                if(mainCardList.size() < 7)
                                {
                                    /*
                                    if(nType == 0)
                                    {
                                        mainCardList.addAll(1, recommendCardList);

                                    }
                                    else
                                    {
                                        mainCardList.addAll(0, recommendCardList);

                                    }

                                     */
                                    mainCardList.addAll(1, recommendCardList);
                                    getRecommendData();
/*
                                    if(getActivity() == null || mainCardList == null || mainCardList.size() == 0)
                                        return;

                                     if(mainCardList.size() > 0)
                                    {
                                        mainCardList.remove(1);

                                    }
                                    if(recommendCardList.size() > 0)
                                    {
                                        if(mainCardList.size() > 0)
                                            mainCardList.addAll(1, recommendCardList);

                                    }
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(recommendCardList == null) {
                                                Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            adapter.setData(mainCardList);
//                        adapter.notifyDataSetChanged();
                                        }
                                    });

 */

                                }

                               CommonUtils.hideProgressDialog();

                            } else {
                                mainCardList.addAll(1, recommendCardList);
                         //       recommendCardList.clear();

                            //    getRecommendData();
                                CommonUtils.hideProgressDialog();

                            }
                            adapter.nType = nType;

                            adapter.setData(mainCardList);
                        }




//                        mainRecyclerView.setHasFixedSize(true);
//                        adapter = new MainCardListAdapter(getActivity(), mainCardList);
//                        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//                        mainRecyclerView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();

//                        adapter.setData(mainCardList);
//                        getRecommendData();
                    }
                });
            }
        }).start();
    }

    private void getRecommendData() {

        recommendCardList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(getActivity() == null || mainCardList == null || mainCardList.size() == 0)
                    return;

                SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
                recommendCardList = HttpClient.getRecommendList(new OkHttpClient(), pref.getString("USER_ID", "Guest"),nType);
                if(mainCardList.size() > 0)
                {
                    mainCardList.remove(1);

                }
                if(recommendCardList.size() > 0)
                {
                    if(mainCardList.size() > 0)
                    mainCardList.addAll(1, recommendCardList);

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(recommendCardList == null) {
                            Toast.makeText(getActivity(), "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter.setData(mainCardList);
//                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}
