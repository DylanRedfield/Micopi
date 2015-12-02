package me.dylanredfield.micopi.fragment;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.FailedEditText;
import me.dylanredfield.micopi.ui.LineNumberEditText;
import me.dylanredfield.micopi.util.Keys;

public class GameFragment extends Fragment {
    private View mView;
    private LineNumberEditText mEditText;
    private TextView mLineNumbers;
    private ParseObject mGame;
    private ParseObject mRound;
    private ParseUser mCurrentUser;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle state) {
        mView = inflater.inflate(R.layout.fragment_game, null, false);

        defaultValues();
        return mView;
    }

    public void defaultValues() {
        setHasOptionsMenu(true);
        mEditText = (LineNumberEditText) mView.findViewById(R.id.edit_text);
        mLineNumbers = (TextView) mView.findViewById(R.id.lines);
        mEditText.setTextView(mLineNumbers);

        Typeface font = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");
        mEditText.setTypeface(font);
        mLineNumbers.setTypeface(font);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");

        mGame = ParseObject.createWithoutData(Keys.KEY_GAME, getActivity().getIntent()
                .getStringExtra(Keys.EXTRA_GAME_OBJ_ID));
        mProgressDialog.show();
        mEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditText.setSingleLine(false);
        mGame.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                queryForCurrentRound();
            }
        });
    }

    public void queryForCurrentRound() {
        List<ParseObject> roundList = mGame.getList(Keys.ROUNDS_ARR);
        mRound = roundList.get(roundList.size() - 1);
        mRound.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mProgressDialog.dismiss();
                Log.d("CurrentRound", "" + mRound.getInt(Keys.ROUND_NUM));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:

            default:
                return false;
        }
    }
}
