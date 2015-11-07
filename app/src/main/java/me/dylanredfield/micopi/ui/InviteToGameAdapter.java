package me.dylanredfield.micopi.ui;

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
import me.dylanredfield.micopi.dialog.InviteToGameDialog;
import me.dylanredfield.micopi.util.Keys;

public class InviteToGameAdapter extends BaseAdapter {
    private InviteToGameDialog mDialog;
    private List<ParseObject> mList;

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

        //TODO include first name when available
        lang.setText(mList.get(i).getString(Keys.USERNAME_STR));

        return view;
    }

    public void setList() {
        mList = mDialog.getFriendsList();
        notifyDataSetChanged();
    }

    public InviteToGameAdapter(InviteToGameDialog dialog) {
        mDialog = dialog;
        mList = mDialog.getFriendsList();
    }
}
