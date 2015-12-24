package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Calendar;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.GameFragment;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class InfoMidGameDialog extends DialogFragment {
    private TextView mTitle;
    private TextView mDescription;
    private GameFragment mFragment;
    private ParseObject mGame;
    private ParseObject mChallenge;
    private ParseObject mRound;
    private DateTime mEndDate;
    private View mView;
    private TextView mCountDown;

    @Override

    public Dialog onCreateDialog(Bundle savedState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_info_mid_game, null);
        builder.setView(mView);

        defaultValues();

        Dialog dialog = builder.create();
        return dialog;
    }

    public void defaultValues() {
        mFragment = (GameFragment) getTargetFragment();

        mTitle = (TextView) mView.findViewById(R.id.title);
        mDescription = (TextView) mView.findViewById(R.id.challenge_text);
        mCountDown = (TextView) mView.findViewById(R.id.time_remaining);

        Typeface font = Typeface.createFromAsset(getResources()
                .getAssets(), "source_code_pro_regular.ttf");

        mTitle.setTypeface(font);
        mDescription.setTypeface(font);
        mCountDown.setTypeface(font);

        Log.d("EndDate", "Before: " + mEndDate.toString());
        Log.d("EndDate", "After: " + mEndDate.toString());

        Period period = new Period(DateTime.now(), mEndDate);

        mCountDown.setText(period.getHours() + " Hours " + period.getMinutes() + " Minutes");

        if (mChallenge != null) {
            mTitle.setText("//" + mChallenge.getString(Keys.NAME_STR));
            mDescription.setText(Html.fromHtml(Helpers.getHtmlString("TODO ",
                    "" + getResources().getColor(R.color.text_green)) +
                    mChallenge.getString(Keys.DESCRIPTION_STR)));
        }
    }

    public void setGame(ParseObject game) {
        mGame = game;
    }

    public void setRound(ParseObject round) {
        mRound = round;
    }

    public void setEndDate(DateTime date) {
        mEndDate = date;
    }

    public void setChallenge(ParseObject challenge) {
        mChallenge = challenge;

        if (mTitle != null) {
            mTitle.setText("//" + mChallenge.getString(Keys.NAME_STR));
            mDescription.setText(Html.fromHtml(Helpers.getHtmlString("TODO ",
                    "" + getResources().getColor(R.color.text_green)) +
                    mChallenge.getString(Keys.DESCRIPTION_STR)));
        }
    }

    public static InfoMidGameDialog newInstance() {
        InfoMidGameDialog dialog = new InfoMidGameDialog();
        return dialog;
    }

}
