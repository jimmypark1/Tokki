package com.Whowant.Tokki.UI.Fragment.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Search.FilterActivity;
import com.Whowant.Tokki.UI.Activity.StorageBox.StorageBoxBookListModifyActivity;
import com.Whowant.Tokki.UI.Popup.EditPopup;
import com.Whowant.Tokki.UI.Popup.MessagePopup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class StorageBoxFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private StorageBoxAdapter pagerAdapter;

    String readingOrder = "RECENTLY";
    String storageOrder = "TITLE";
    boolean isStorage = true;

    public static Fragment newInstance() {
        StorageBoxFragment fragment = new StorageBoxFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_storage_box, container, false);

        tabLayout = inflaterView.findViewById(R.id.tabLayout);
        viewPager = inflaterView.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);

        pagerAdapter = new StorageBoxAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showMenus(View v) {
        int currentPosition = viewPager.getCurrentItem();

        if (currentPosition == 0 || currentPosition == 1) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.getMenuInflater().inflate(R.menu.storage_box_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.align:
                            Intent intent = new Intent(getContext(), FilterActivity.class);
                            intent.putExtra("title", "??????");
                            intent.putExtra("type", FilterActivity.TYPE_STORAGE_BOX);
                            if (currentPosition == 0) {
                                intent.putExtra("order", readingOrder);
                            }
                            if (currentPosition == 1) {
                                intent.putExtra("order", storageOrder);
                                intent.putExtra("storage", isStorage);
                            }
                            startActivityForResult(intent, 101);
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        } else if (currentPosition == 2) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.getMenuInflater().inflate(R.menu.storage_box_book_list_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.modify:
                            startActivity(new Intent(getActivity(), StorageBoxBookListModifyActivity.class));
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    }

    public void showBugReport() {
        Intent intent = new Intent(getActivity(), MessagePopup.class);
        intent.putExtra("message", "????????? ??????????????????????");
        startActivityForResult(intent, 0);
    }

    public class StorageBoxAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private ArrayList<String> titles = new ArrayList<>();

        public StorageBoxAdapter(@NonNull FragmentManager fm) {                               // ????????? ????????? ?????? ????????? fragment ??? ??????. 1. ?????????, 2. ?????? ??????
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            fragments.add(new StorageBoxReadingFragment());
            fragments.add(new StorageBoxKeepFragment());
            fragments.add(new StorageBoxBookListFragment());

            titles.add("?????? ?????? ??????");
            titles.add("?????????");
            titles.add("????????????");
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            Intent intent = new Intent(getActivity(), EditPopup.class);
            intent.putExtra("type", EditPopup.TYPE_BUG_REPORT);
            startActivity(intent);
        } else if (requestCode == 101 && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                int currentPosition = viewPager.getCurrentItem();

                if (currentPosition == 0)
                    readingOrder = data.getStringExtra("order");
                if (currentPosition == 1)
                    storageOrder = data.getStringExtra("order");

                Fragment fragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + viewPager.getCurrentItem());

                if (fragment != null) {
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            ((StorageBoxReadingFragment) fragment).refreshData(readingOrder);
                            break;
                        case 1:
                            ((StorageBoxKeepFragment) fragment).refreshData(storageOrder);
                            break;
                    }
                }
            }
        }
    }
}
