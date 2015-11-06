package me.dylanredfield.micopi.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.dialog.AddFriendDialog;
import me.dylanredfield.micopi.fragment.FriendsFragment;
import me.dylanredfield.micopi.util.Keys;

public class SelectUserAdapter extends BaseAdapter {
    private AddFriendDialog mDialog;
    private List<ParseUser> mList;

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mDialog.getActivity().getLayoutInflater()
                    .inflate(R.layout.row_select_lang, null, false);
        }

        Typeface font = Typeface.createFromAsset(mDialog.getActivity().getResources().getAssets(),
                "source_code_pro_regular.ttf");
        TextView lineNum = (TextView) view.findViewById(R.id.line_num);
        TextView lang = (TextView) view.findViewById(R.id.lang);

        lineNum.setTypeface(font);
        lang.setTypeface(font);
        lineNum.setText("" + (i + 1));
        lang.setText(mList.get(i).getString(Keys.USERNAME_STR));

        return view;
    }

    public SelectUserAdapter(AddFriendDialog dialog) {
        mDialog = dialog;
        mList = mDialog.getSearchList();
    }
}
