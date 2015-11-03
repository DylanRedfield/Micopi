package me.dylanredfield.micopi.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.parse.ParseUser;

import me.dylanredfield.micopi.util.Keys;
import me.dylanredfield.micopi.fragment.GameListFragment;
import me.dylanredfield.micopi.fragment.ProfileFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private static HomePagerAdapter mAdapter;
    public static HomePagerAdapter getAdapter(FragmentManager fm) {
        if (mAdapter == null) {
            mAdapter = new HomePagerAdapter(fm);
        }
        return mAdapter;
    }
    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new GameListFragment();
                break;
            case 1:
                fragment = new ProfileFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        if (ParseUser.getCurrentUser().getBoolean(Keys.IS_ANON_BOOL)) {
            return 1;
        } else {
            return 2;
        }
    }
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "<Games/>";
            case 1:
                return "<Profile/>";
        }
        return "test";
    }
}
