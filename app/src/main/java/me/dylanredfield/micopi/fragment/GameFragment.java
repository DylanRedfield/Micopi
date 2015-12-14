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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.dialog.InfoMidGameDialog;
import me.dylanredfield.micopi.ui.FailedEditText;
import me.dylanredfield.micopi.ui.LineNumberEditText;
import me.dylanredfield.micopi.util.Keys;

public class GameFragment extends Fragment {
    private View mView;
    private LineNumberEditText mEditText;
    private TextView mLineNumbers;
    private ParseObject mGame;
    private ParseObject mRound;
    private ParseObject mChallenge;
    private ParseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private InfoMidGameDialog mMidGameDialog;
    private ParseObject mSubmission;
    private boolean mSaved = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle state) {
        mView = inflater.inflate(R.layout.fragment_game, null, false);

        defaultValues();
        queryForGameInformation();
        return mView;
    }


    public void defaultValues() {
        setHasOptionsMenu(true);

        defaultViews();

        mCurrentUser = ParseUser.getCurrentUser();


        mGame = ParseObject.createWithoutData(Keys.KEY_GAME, getActivity().getIntent()
                .getStringExtra(Keys.EXTRA_GAME_OBJ_ID));


        // Created but can only be displayed when all information is queried
        mMidGameDialog = InfoMidGameDialog.newInstance();
        mMidGameDialog.setTargetFragment(this, 0);
    }

    public void defaultViews() {
        Typeface font = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mEditText = (LineNumberEditText) mView.findViewById(R.id.edit_text);
        mLineNumbers = (TextView) mView.findViewById(R.id.lines);
        mEditText.setTextView(mLineNumbers);

        mEditText.setTypeface(font);
        mLineNumbers.setTypeface(font);

        mEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditText.setSingleLine(false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");
    }

    // TODO due all asynchronously
    public void queryForGameInformation() {
        mProgressDialog.show();
        mGame.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    queryForCurrentRound();
                    mMidGameDialog.setGame(mGame);
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void queryForCurrentRound() {
        List<ParseObject> roundList = mGame.getList(Keys.ROUNDS_ARR);
        mRound = roundList.get(roundList.size() - 1);
        mRound.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    mMidGameDialog.setRound(mRound);
                    mChallenge = (ParseObject) mRound.get(Keys.CHALLENGE_POINT);

                    fetchChallenge();

                    queryForSubmission();


                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fetchChallenge() {
        mChallenge.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mMidGameDialog.setChallenge(mChallenge);
                } else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void queryForSubmission() {
        ParseQuery<ParseObject> submissionQuery = ParseQuery.getQuery(Keys.KEY_SUBMISSION);
        submissionQuery.whereEqualTo(Keys.GAME_ROUND_POINT, mRound);
        submissionQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    mSubmission = parseObject;
                } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    makeSubmission();

                }
            }
        });
    }

    public void makeSubmission() {
        mSubmission = ParseObject.create(Keys.KEY_SUBMISSION);
        mSubmission.put(Keys.GAME_ROUND_POINT, mRound);
        mSubmission.put(Keys.CAN_EDIT_BOOL, true);
        mSubmission.put(Keys.PLAYER_POINT, mCurrentUser);
        mSubmission.put(Keys.POWER_UPS_USED_NUM, 0);

        // TODO add endDate
        Calendar cal = Calendar.getInstance();
        //cal.set(Calendar.MIN)
        mSubmission.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
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
            case R.id.info:
                mMidGameDialog.show(getFragmentManager(), null);
                return true;
            case R.id.save:
                mSubmission.put(Keys.SUBMISSION_STR, mEditText.getText().toString());
                mSubmission.saveInBackground();
                return true;
            default:
                return false;
        }
    }
}
