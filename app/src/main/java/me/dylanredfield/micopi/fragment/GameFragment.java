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

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    private boolean mIsFirstView;
    private DateTime mEndDate;
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


        mGame = ParseObject.createWithoutData(Keys.KEY_GAME,
                getActivity().getIntent().getStringExtra(Keys.EXTRA_GAME_OBJ_ID));


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
        List<ParseUser> tempList = mRound.getList(Keys.PLAYERS_STARTED);

        mRound.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    mMidGameDialog.setRound(mRound);
                    mChallenge = (ParseObject) mRound.get(Keys.CHALLENGE_POINT);
                    mEndDate = new DateTime(mRound.getDate(Keys.END_DATE_DATE));
                    mMidGameDialog.setEndDate(mEndDate);

                    for (int i = 0; i < mRound.getList(Keys.PLAYERS_STARTED).size(); i++) {
                        if (((ParseObject) mRound.getList(Keys.PLAYERS_STARTED).get(i)).getObjectId()
                                .equals(mCurrentUser.getObjectId())) {
                            mIsFirstView = true;
                        }
                    }


                    fetchChallenge();

                    if (mIsFirstView) {
                        mRound.add(Keys.PLAYERS_STARTED, mCurrentUser);
                        mRound.saveInBackground();
                        makeSubmission();
                    } else {
                        queryForSubmission();
                    }


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
        submissionQuery.whereEqualTo(Keys.PLAYER_POINT, mCurrentUser);
        submissionQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    Log.d("queryForSub", mRound.getObjectId());
                    Log.d("queryForSub", mCurrentUser.getObjectId());
                    mSubmission = parseObject;
                } else {
                    makeSubmission();
                }
            }
        });
    }

    public DateTime getEndDate() {
        return mEndDate;
    }

    public void makeSubmission() {
        mSubmission = ParseObject.create(Keys.KEY_SUBMISSION);
        mSubmission.put(Keys.GAME_ROUND_POINT, mRound);
        mSubmission.put(Keys.CAN_EDIT_BOOL, true);
        mSubmission.put(Keys.PLAYER_POINT, mCurrentUser);
        mSubmission.put(Keys.POWER_UPS_USED_NUM, 0);
        mSubmission.put(Keys.SUBMISSION_STR, "");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);

        mSubmission.put(Keys.END_DATE_DATE, cal.getTime());
        mSubmission.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    makeSubmission();
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
            case R.id.submit:
                Log.d("Submit", "Click!");
                mSubmission.put(Keys.SUBMISSION_STR, mEditText.getText().toString());
                mSubmission.put(Keys.CAN_EDIT_BOOL, false);
                mSubmission.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("Submit", "saved");
                            HashMap<String, Object> params = new HashMap<>();
                            params.put("gameRoundId", mRound.getObjectId());
                            ParseCloud.callFunctionInBackground("handleSubmission", params, new FunctionCallback<HashMap<String, Object>>() {
                                @Override
                                public void done(HashMap<String, Object> stringObjectHashMap, ParseException e) {
                                    Log.d("Submit", "cloudCode");
                                    getActivity().finish();
                                    if (e != null) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            case R.id.save:
                mSubmission.put(Keys.SUBMISSION_STR, mEditText.getText().toString());
                mSubmission.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    }
                });

            default:
                return false;
        }
    }
}
