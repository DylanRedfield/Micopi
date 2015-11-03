package me.dylanredfield.micopi.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import me.dylanredfield.micopi.ui.HomePagerAdapter;
import me.dylanredfield.micopi.R;

public class HomeActivity extends ActionBarActivity {
    HomePagerAdapter mAdapter;
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mAdapter = HomePagerAdapter.getAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);

    }

    @Override
    public void onBackPressed() {

    }
}
