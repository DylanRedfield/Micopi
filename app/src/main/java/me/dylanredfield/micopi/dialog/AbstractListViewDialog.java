package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.parse.ParseObject;

import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.SelectLangListAdapter;

public abstract class AbstractListViewDialog extends DialogFragment {
    private View mView;
    private List<ParseObject> mList;
    private SelectLangListAdapter mAdapter;
    private ListView mListView;

    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_list_view, null);
        builder.setView(mView);

        mListView = (ListView) mView.findViewById(R.id.list);

        if (mList != null) {
            mAdapter = new SelectLangListAdapter(getActivity(), mList);
            mListView.setAdapter(mAdapter);
        }


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

    public abstract void setListeners();
}
