package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.DialogListAdapter;

public class InfoMidGameDialog extends DialogFragment {
    private View mView;
    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_list_view, null);
        builder.setView(mView);

        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");


        Dialog dialog = builder.create();
        return dialog;
    }


}
