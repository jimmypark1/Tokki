package com.Whowant.Tokki.UI.Fragment.MyPage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Market.MarketContentsFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketMainActivity;
import com.Whowant.Tokki.UI.Adapter.MyPageCarrotInfoAdapter;
import com.Whowant.Tokki.UI.Adapter.MyPageCarrotMainAdapter;
import com.google.android.material.tabs.TabLayout;

import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageCarrotMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageCarrotMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MyPageCarrotMainAdapter pagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public MyPageCarrotMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageCarrotMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageCarrotMainFragment newInstance(String param1, String param2) {
        MyPageCarrotMainFragment fragment = new MyPageCarrotMainFragment();
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

        View v = inflater.inflate(R.layout.fragment_my_page_carrot_main, container, false);


        tabLayout = v.findViewById(R.id.tabLayout);
        viewPager = v.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("누적 사용 당근"));
        tabLayout.addTab(tabLayout.newTab().setText("총 획득 당근"));


        pagerAdapter = new MyPageCarrotMainAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //tabLayout.setupWithViewPager(viewPager, true);

        //pagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if(pos == 0)
                {
                    MyPageCarrotInfoUseFragment fragment =  (MyPageCarrotInfoUseFragment)pagerAdapter.getItem(pos);

                    viewPager.setCurrentItem(pos);
                 //   scrollCenter();
                }
                else
                {
                    MyPageCarrotInfoGetFragment fragment =  (MyPageCarrotInfoGetFragment)pagerAdapter.getItem(pos);

                    viewPager.setCurrentItem(pos);

                }


            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });
        /*



         */
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

    }
}