package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.SelectLangListAdapter;

public abstract class AbstractListViewDialog extends DialogFragment {
    private View mView;
    private List<ParseObject> mList;
    private SelectLangListAdapter mAdapter;
    private ListView mListView;
    private TextView mLabel;
    private Typeface mFont;
    private TextView mAdd;

    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_list_view, null);
        builder.setView(mView);

        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mListView = (ListView) mView.findViewById(R.id.list);
        mLabel = (TextView) mView.findViewById(R.id.label);
        mLabel.setTypeface(mFont);
        mAdd = (TextView) mView.findViewById(R.id.add);

        if (mList != null) {
            mAdapter = new SelectLangListAdapter(getActivity(), mList);
            mListView.setAdapter(mAdapter);
        }
        setDefaultValues();
        setListeners();


        Dialog dialog = builder.create();
        return dialog;
    }

    public void setList(List<ParseObject> list) {
        mList = list;

        if (mAdapter == null && getActivity() != null) {
            mAdapter = new SelectLangListAdapter(getActivity(), mList);
            mListView.setAdapter(mAdapter);
        }
    }

    public List<ParseObject> getList() {
        return mList;
    }

    public ListView getListView() {
        return mListView;
    }

    public TextView getLabel() {
        return mLabel;
    }

    public Typeface getFont() {
        return mFont;
    }

    public TextView getAdd() {
        return mAdd;
    }

    public void setColor() {
    }

    public abstract void setListeners();

    public abstract void setDefaultValues();
}
