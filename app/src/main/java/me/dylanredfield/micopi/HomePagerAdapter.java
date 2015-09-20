package me.dylanredfield.micopi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomePagerAdapter extends FragmentPagerAdapter {
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
        return 2;
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
