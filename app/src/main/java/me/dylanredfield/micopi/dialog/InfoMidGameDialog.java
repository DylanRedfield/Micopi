package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
<<<<<<< HEAD
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

=======
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
>>>>>>> 70bbad87695be47d0d20cad3249eefa3f317225a
    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
<<<<<<< HEAD
        mView = inflater.inflate(R.layout.dialog_info_mid_game, null);
        builder.setView(mView);

=======
        mView = inflater.inflate(R.layout.dialog_list_view, null);
        builder.setView(mView);

        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");


>>>>>>> 70bbad87695be47d0d20cad3249eefa3f317225a
        Dialog dialog = builder.create();
        return dialog;
    }

<<<<<<< HEAD
    public static InfoMidGameDialog newInstance() {
        InfoMidGameDialog dialog = new InfoMidGameDialog();
        return dialog;
    }
=======

>>>>>>> 70bbad87695be47d0d20cad3249eefa3f317225a
}
