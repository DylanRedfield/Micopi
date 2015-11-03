package me.dylanredfield.micopi.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import me.dylanredfield.micopi.R;

public class FriendsFragment extends Fragment {
    private View mView;
    private ListView mListView;
    private Typeface mFont;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedState) {
        mView = inflater.inflate(R.layout.fragment_friends, null, false);

        setDefaultValues();
        return mView;
    }

    public void setDefaultValues() {
        mFont = Typeface.createFromAsset(getActivity().getAssets(), "source_code_pro_regular.ttf");
        mListView = (ListView) mView.findViewById(R.id.friends_list);

        TextView emptyList = (TextView) mView.findViewById(R.id.empty_list);
        emptyList.setTypeface(mFont);
        mListView.setEmptyView(emptyList);
    }
}
