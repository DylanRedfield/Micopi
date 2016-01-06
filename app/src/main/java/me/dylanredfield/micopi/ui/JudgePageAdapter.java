package me.dylanredfield.micopi.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import me.dylanredfield.micopi.activity.JudgeActivity;
import me.dylanredfield.micopi.fragment.JudgeCodeFragment;

public class JudgePageAdapter extends FragmentStatePagerAdapter {
    private List<ParseObject> mList;
    private JudgeActivity mActivity;

    public JudgePageAdapter(FragmentManager fm, JudgeActivity activity) {
        super(fm);
        mList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new JudgeCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("submissionObjectId", mList.get(position).getObjectId());
        bundle.putString("spot", "" + position);
//        bundle.putString("roundId", mActivity.getRoundObjectId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mList.size();
    }
    public void setList(List<ParseObject> list) {
        mList = list;
        notifyDataSetChanged();
    }

}
