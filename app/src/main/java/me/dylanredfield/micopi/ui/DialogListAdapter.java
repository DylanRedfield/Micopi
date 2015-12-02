package me.dylanredfield.micopi.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import me.dylanredfield.micopi.util.Keys;
import me.dylanredfield.micopi.R;

public class DialogListAdapter extends BaseAdapter {
    private List<ParseObject> mLangList;
    private Activity mActivity;

    public DialogListAdapter(Activity activity, List<ParseObject> list) {
        mActivity = activity;
        mLangList = list;
    }


    @Override
    public int getCount() {
        return mLangList.size();
    }

    @Override
    public Object getItem(int i) {
        return mLangList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    // TODO edit class name
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = mActivity.getLayoutInflater().inflate(R.layout.row_select_lang, null, false);
        }

        Typeface font = Typeface.createFromAsset(mActivity.getAssets(),
                "source_code_pro_regular.ttf");
        TextView lineNum = (TextView) view.findViewById(R.id.line_num);
        TextView bulk = (TextView) view.findViewById(R.id.lang);

        lineNum.setTypeface(font);
        bulk.setTypeface(font);
        lineNum.setText("" + (i + 1) );

        if (mLangList.get(i).getClassName().equals(Keys.KEY_LANGUAGE)) {
            bulk.setText(mLangList.get(i).getString(Keys.NAME_STR));
        } else if (mLangList.get(i).getClassName().equals(Keys.KEY_GAME_DIFFICULTY)) {
            bulk.setText(mLangList.get(i).getString(Keys.DIFFICULTY_STRING));
        } else if (mLangList.get(i).getClassName().equals(Keys.KEY_USER)) {
            bulk.setText(mLangList.get(i).getString(Keys.USERNAME_STR));
            bulk.setTextColor(mActivity.getResources().getColor(R.color.text_blue));
        }

        return view;
    }
}
