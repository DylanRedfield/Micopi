package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseObject;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.GameFragment;

public class InfoMidGameDialog extends DialogFragment {
    private View mView;
    private TextView mTitle;
    private TextView mMessage;
    private GameFragment mFragment;
    private ParseObject mGame;
    private ParseObject mChallenge;

    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_info_mid_game, null);
        builder.setView(mView);

        Dialog dialog = builder.create();
        return dialog;
    }

    public static InfoMidGameDialog newInstance() {
        InfoMidGameDialog dialog = new InfoMidGameDialog();
        return dialog;
    }
}
