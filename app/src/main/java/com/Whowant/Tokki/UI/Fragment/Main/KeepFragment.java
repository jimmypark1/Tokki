package com.Whowant.Tokki.UI.Fragment.Main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.Adapter.FlowerAdapter;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class KeepFragment extends Fragment {                                    // 2번 탭 보관함 화면
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private KeepAdapter pagerAdapter;

    private int nMode;
    private static final int MODE_KEEP = 0;
    private static final int MODE_READ = 1;

    public static Fragment newInstance() {
        KeepFragment fragment = new KeepFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.activity_keep, container, false);

        tabLayout = inflaterView.findViewById(R.id.tabLayout);
        viewPager = inflaterView.findViewById(R.id.viewPager);

        pagerAdapter = new KeepAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showMenus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("작품 정렬");

        if(viewPager.getCurrentItem() == 0) {
            builder.setItems(getResources().getStringArray(R.array.KEEP_MENU), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    KeepSubFragment fragment = (KeepSubFragment) pagerAdapter.getItem(0);
                    fragment.reOrder(pos);
                }
            });
        } else {
            builder.setItems(R.array.READ_MENU, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    KeepSubFragment fragment = (KeepSubFragment) pagerAdapter.getItem(1);
                    fragment.reOrder(pos);
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class KeepAdapter extends FragmentPagerAdapter {
        private String strWriterID;
        private KeepSubFragment keepSubFragment1;
        private KeepSubFragment keepSubFragment2;

        public KeepAdapter(@NonNull FragmentManager fm) {                               // 보관함 화면은 다시 두개의 fragment 로 나뉨. 1. 보관함, 2. 읽은 작품
            super(fm);

            keepSubFragment1 = new KeepSubFragment(MODE_KEEP);
            keepSubFragment2 = new KeepSubFragment(MODE_READ);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return keepSubFragment1;
            else
                return keepSubFragment2;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "보관함";
            else if(position == 1)
                return "읽은 작품";

            return "";
        }
    }
}
