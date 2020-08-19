package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.TutorialPagerAdapter;

public class TutorialActivity extends AppCompatActivity {
    ViewPager viewPager;
    TutorialPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = findViewById(R.id.tutorialViewPager);
        adapter = new TutorialPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    public void setCurrent(int nIndex) {
        viewPager.setCurrentItem(nIndex);
    }
}