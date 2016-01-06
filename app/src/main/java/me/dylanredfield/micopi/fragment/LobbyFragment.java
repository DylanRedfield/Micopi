package me.dylanredfield.micopi.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.menu.MenuView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.dylanredfield.micopi.activity.GameActivity;
import me.dylanredfield.micopi.activity.JudgeActivity;
import me.dylanredfield.micopi.dialog.AddPlayersToLobbyDialog;
import me.dylanredfield.micopi.dialog.EditLobbyDialog;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;
import me.dylanredfield.micopi.R;

public class LobbyFragment extends Fragment {
    private View mView;
    private TextView mLangText;
    private TextView mInvitedText;
    private TextView mAcceptedText;
    private TextView mDifficultyText;
    private Button mEditLobby;
    private Button mStartGame;
    private ParseUser mCurrentUser;
    private ParseObject mGame;
    private String mType;
    private Button mInviteFriends;
    private Fragment mFragment;
    private AddPlayersToLobbyDialog mAddPlayersDialog;
    private EditLobbyDialog mEditLobbyDialog;
    private Button mPlay;
    private ParseObject mCurrentRound;
    private boolean mIsLeader;
    private boolean mIsYourTurn;
    private Handler mTimeHandler;
    private Runnable mUpdateTime;
    private Typeface mFont;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedState) {
        mView = inflater.inflate(R.layout.fragment_lobby, null, false);

        setDefault();
        defaultValues();
        fetchGame();
        setListeners();


        return mView;

    }

    public void setDefault() {
        mCurrentUser = ParseUser.getCurrentUser();

        if (mFragment == null) {
            mFragment = this;
        }

        // Makes it easier to change things to all line numbers
        ArrayList<TextView> lineNumbers = new ArrayList<>();
/*      lineNumbers.add((TextView) mView.findViewById(R.id.line_id_1));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_2));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_3));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_4));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_5));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_6));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_7));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_8));*/

        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        for (TextView tv : lineNumbers) {
            tv.setTypeface(mFont);
        }

        TextView gameInfoLabel = (TextView) mView.findViewById(R.id.game_info_label);
        gameInfoLabel.setText("// Game Info");
        gameInfoLabel.setTypeface(mFont);

        TextView invitedLabel = (TextView) mView.findViewById(R.id.invited_label);
        invitedLabel.setText("// Invited");
        invitedLabel.setTypeface(mFont);

        ((TextView) mView.findViewById(R.id.public_label)).setTypeface(mFont);

        mDifficultyText = (TextView) mView.findViewById(R.id.difficulty);
        mDifficultyText.setTypeface(mFont);

        mLangText = (TextView) mView.findViewById(R.id.lang);
        mLangText.setTypeface(mFont);

        mInvitedText = (TextView) mView.findViewById(R.id.invited_arr);
        mInvitedText.setTypeface(mFont);

        mAcceptedText = (TextView) mView.findViewById(R.id.accpeted_arr);
        mAcceptedText.setTypeface(mFont);

        mEditLobby = (Button) mView.findViewById(R.id.edit_lobby);
        mEditLobby.setTypeface(mFont);

        mStartGame = (Button) mView.findViewById(R.id.start_game_leader);
        mStartGame.setTypeface(mFont);

        mInviteFriends = (Button) mView.findViewById(R.id.invite_friends);
        mInviteFriends.setTypeface(mFont);

        mAddPlayersDialog = AddPlayersToLobbyDialog.newInstance();
        mAddPlayersDialog.setTargetFragment(mFragment, 0);

        mEditLobbyDialog = EditLobbyDialog.newInstance();
        mEditLobbyDialog.setTargetFragment(mFragment, 0);

        mPlay = (Button) mView.findViewById(R.id.play);
        mPlay.setTypeface(mFont);
    }

    public void setListeners() {
        mEditLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditLobbyDialog.show(getFragmentManager(), null);
            }
        });

        mInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AddPlayersFrag", "Show");
                mAddPlayersDialog.show(getFragmentManager(), null);
            }
        });
    }

    public void setPlayersDialogList() {
        List<ParseObject> list = ParseUser.getCurrentUser().getList(Keys.FRIENDS_ARR);
        ParseObject.fetchAllInBackground(list, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<ParseObject> playersArr = mGame
                        .getList(Keys.PLAYERS_ARR);
                List<ParseObject> invitedArr = mGame
                        .getList(Keys.INVITED_PLAYERS_ARR);
                ArrayList<ParseObject> tempArr = new ArrayList<>();
                Log.d("SetPlayers", "list: " + list.toString());
                Log.d("SetPlayers", "players: " + playersArr.toString());
                Log.d("SetPlayers", "invited: " + invitedArr.toString());

                for (ParseObject p : list) {
                    boolean playersContains = false;
                    for (int i = 0; i < playersArr.size(); i++) {
                        if (!playersArr.get(i).getObjectId().equals(p.getObjectId())) {
                            if (!p.getObjectId().equals(ParseUser.getCurrentUser()
                                    .getObjectId())) {
                                Log.d("FriendsList", "players" + p.getObjectId());
                            } else {
                                playersContains = true;
                            }
                        } else {
                            playersContains = true;
                        }
                    }
                    boolean inviteContains = false;
                    for (int i = 0; i < invitedArr.size(); i++) {
                        if (!invitedArr.get(i).getObjectId().equals(p.getObjectId())) {
                            Log.d("FriendsList", p.getObjectId());
                        } else {
                            inviteContains = true;
                        }
                    }
                    if (!inviteContains && !playersContains) {
                        tempArr.add(p);
                    }
                }
                mAddPlayersDialog.setList(tempArr);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void fetchGame() {
        mGame = ParseObject.createWithoutData(Keys.KEY_GAME,
                getActivity().getIntent().getStringExtra(Keys.EXTRA_GAME_OBJ_ID));
        Log.d("mGame", "" + mGame.getNumber(Keys.DESIRED_NUM_PLAYERS));
        mGame.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    Log.d("IHateThisApp", "FUCK");
                    if (mGame.getBoolean(Keys.HAS_STARTED_BOOL)) {
                        queryForCurrentRound();
                    } else {
                        setViewsFromType(checkType());
                        setTextAfterFetch();
                        if (mType.equals("private_lobby") && !mIsLeader) {
                            setPlayersDialogList();
                        }
                    }

                    // TODO show button
                } else {
                    // TODO error
                }
            }
        });

    }

    public void queryForCurrentRound() {
        List<ParseObject> roundList = mGame.getList(Keys.ROUNDS_ARR);
        mCurrentRound = roundList.get(roundList.size() - 1);
        mCurrentRound.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    mCurrentRound = object;
                    setViewsFromType(checkType());
                    setTextAfterFetch();
                    if (mType.equals("private_lobby") && !mIsLeader) {
                        setPlayersDialogList();
                    }
                } else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public ParseObject getGame() {
        return mGame;
    }

    public void addPlayers(ArrayList<ParseObject> list) {
        mGame.addAll(Keys.INVITED_PLAYERS_ARR, list);
        mInvitedText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getResources().getColor(R.color.text_orange)) + ".invited = " +
                getColoredToString(Helpers.getStringArrayFromPoint
                        ((ArrayList<ParseObject>)
                                mGame.get(Keys.INVITED_PLAYERS_ARR))) + ";"));
        mGame.saveInBackground();
    }

    public String checkType() {
        String type = "";
        mIsLeader = ((ParseObject) mGame.get(Keys.INVITE_STARTER_POINT)).getObjectId()
                .equals(ParseUser.getCurrentUser().getObjectId());
        if (mGame.getBoolean(Keys.IS_OVER_BOOL)) {
            type = "over";
        } else if (mGame.getBoolean(Keys.HAS_STARTED_BOOL)) {
            boolean isNotDone = false;

            List<ParseObject> playersNotDoneArray = mCurrentRound.getList(Keys.PLAYERS_NOT_DONE_ARR);

            for (ParseObject p : playersNotDoneArray) {
                if (p.getObjectId().equals(mCurrentUser.getObjectId())) {
                    isNotDone = true;
                }
            }
            if (isNotDone) {
                boolean started = false;

                List<ParseObject> playersStarted = mCurrentRound.getList(Keys.PLAYERS_STARTED);

                for (ParseObject po : playersStarted) {
                    if (po.getObjectId().equals(mCurrentUser.getObjectId())) {
                        started = true;
                    }
                }

                if (started) {
                    type = "your_turn_player_started";
                } else {
                    type = "your_turn_player";
                }
            } else {
                type = "their_turn_player";
            }
            if (mCurrentRound.getParseUser(Keys.LEADER_POINT).getObjectId()
                    .equals(mCurrentUser.getObjectId())) {
                if (mCurrentRound.getBoolean(Keys.IS_READY_FOR_LEADER_BOOL)) {
                    type = "leader_your_turn";
                } else {
                    type = "leader_their_turn";
                    Log.d("GameObjectId", mCurrentRound.getObjectId());
                }
            }
        } else {
            if (mGame.getBoolean(Keys.IS_INVITE_BOOL)) {

                if (mGame.getParseObject(Keys.INVITE_STARTER_POINT).getObjectId()
                        .equals(mCurrentUser.getObjectId())) {
                    type = "private_lobby_start";
                } else {
                    type = "private_lobby_player";
                }
            } else {
                type = "public_lobby";
            }
        }

        Log.d("TypeOfLobby", type);

        mType = type;
        return type;
    }

    public void setViewsFromType(final String type) {
        if (type.equals(Keys.PRIVATE_LOBBY_STARTER)) {
            mView.findViewById(R.id.leader_panel).setVisibility(View.VISIBLE);
            if (checkIfCanStart()) {
                lobbyStarterCanStart();
            }
            mView.findViewById(R.id.invited_accepted_view).setVisibility(View.VISIBLE);
        } else if (type.equals(Keys.PRIVATE_LOBBY_PLAYER)) {
            mView.findViewById(R.id.lobby).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.invited_accepted_view).setVisibility(View.VISIBLE);
        } else if (type.equals(Keys.PUBLIC_LOBBY)) {
            mView.findViewById(R.id.invited_accepted_view).setVisibility(View.VISIBLE);
        } else if (type.equals("leader_your_turn")) {
            mView.findViewById(R.id.leader_started).setVisibility(View.VISIBLE);
            yourTurnLeader();
        } else if (type.equals("leader_their_turn")) {
            mView.findViewById(R.id.leader_started).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.leader_started).findViewById(R.id.select_winner)
                    .setVisibility(View.GONE);
            theirTurnLeader();
        } else if (type.equals("your_turn_player")) {
            mView.findViewById(R.id.your_turn_player).setVisibility(View.VISIBLE);
            yourTurnPlayer();
        } else if (type.equals("your_turn_player_started")) {
            mView.findViewById(R.id.your_turn_player).setVisibility(View.VISIBLE);
            yourTurnPlayerStarted();
        } else if (type.equals("their_turn_player")) {
            mView.findViewById(R.id.their_turn_player).setVisibility(View.VISIBLE);
            theirTurnPlayer();
        }

    }

    public void yourTurnLeader() {
        TextView timeRemaining = (TextView) mView.findViewById(R.id.time_remaining_leader);
        timeRemaining.setTypeface(mFont);
        updateTime(mCurrentRound.getDate(Keys.LEADER_END_DATE_DATE), "You have ",
                " remaining", timeRemaining);
        Button selectWinner = (Button) mView.findViewById(R.id.select_winner);
        selectWinner.setTypeface(mFont);
        selectWinner.setText("judgeSubmissions()");

        selectWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), JudgeActivity.class);
                i.putExtra(Keys.EXTRA_GAME_OBJ_ID, mCurrentRound.getObjectId());
                startActivity(i);
                getActivity().finish();
            }
        });

    }

    public void theirTurnLeader() {
        TextView timeRemaining = (TextView) mView.findViewById(R.id.time_remaining_leader);
        timeRemaining.setTypeface(mFont);
        updateTime(mCurrentRound.getDate(Keys.END_DATE_DATE), "",
                " until your turn to judge", timeRemaining);
    }

    public void yourTurnPlayer() {
        TextView timeRemaining = (TextView) mView.findViewById(R.id.time_remaining_player);
        timeRemaining.setTypeface(mFont);
        updateTime(mCurrentRound.getDate(Keys.END_DATE_DATE), "Your turn! Round ends in ",
                "", timeRemaining);

        setPlayListener();
    }

    public void yourTurnPlayerStarted() {
        Log.d("yourTurnPlayerStarted", "Triggered");
        final TextView timeRemaining = (TextView) mView.findViewById(R.id.time_remaining_player);
        timeRemaining.setTypeface(mFont);


        setPlayListener();
        ParseQuery<ParseObject> submissionQuery = ParseQuery.getQuery(Keys.KEY_SUBMISSION);
        submissionQuery.whereEqualTo(Keys.GAME_ROUND_POINT, mCurrentRound);

        submissionQuery.whereEqualTo(Keys.PLAYER_POINT, mCurrentUser);
        submissionQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.d("yourTurnPlayerStarted", "e == null");
                    updateTime(object.getDate(Keys.END_DATE_DATE), "Your turn! ",
                            " remaining.", timeRemaining);
                } else {

                    Log.d("yourTurnPlayerStarted", e.getMessage());
                }
            }
        });
    }

    public void theirTurnPlayer() {
        TextView timeRemaining = (TextView) mView.findViewById(R.id.time_remaining_their_player);
        timeRemaining.setTypeface(mFont);
        updateTime(mCurrentRound.getDate(Keys.END_DATE_DATE), "Waiting for other players.\n",
                " until judging begins", timeRemaining);
    }

    public boolean isTimeExpired(ParseObject submission) {
        Date endDate = submission.getDate(Keys.END_DATE_DATE);

        return endDate.getTime() <= new Date().getTime();

    }


    public void updateTime(final Date endDate, final String textStart,
                           final String textAfter, final TextView tv) {
        mTimeHandler = new Handler();
        tv.setTypeface(mFont);
        mUpdateTime = new Runnable() {
            @Override
            public void run() {
                String timeDifference =
                        getTimeDifference(endDate);
                Log.d("updateTime", "run");
                tv.setText(Html.fromHtml(textStart +
                        Helpers.getHtmlString(timeDifference, ""
                                + getResources().getColor(R.color.text_green)) + textAfter));

                if (getTimeDifferenceLong(endDate) > 0) {
                    Log.v("TimeDifference", "Time is Valid");
                    mTimeHandler.postDelayed(this, 1000);
                } else {
                    setViewsFromType(checkType());
                }
            }
        };
        mUpdateTime.run();
    }

    public String getTimeDifference(Date endDate) {
        //TODO make HH/MM/SS
        Interval interval = new Interval(new Date().getTime(),
                endDate.getTime());
        Period period = interval.toPeriod();
        return "" + period.getHours() + ":" + period.getMinutes() + ":" +
                period.getSeconds();
    }

    public int getTimeDifferenceLong(Date endDate) {
        return (int) (endDate.getTime() - new Date().getTime());
    }

    public void onStop() {
        super.onStop();
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacks(mUpdateTime);
        }
    }

    public boolean checkIfCanStart() {
        return mGame.getList(Keys.PLAYERS_ARR).size() >= 3;
    }

    public void lobbyStarterCanStart() {
        Button startGame = (Button) mView.findViewById(R.id.leader_panel)
                .findViewById(R.id.start_game_leader);
        startGame.setVisibility(View.VISIBLE);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("gameId", mGame.getObjectId());
                ParseCloud.callFunctionInBackground("createGameFromInvite", params,
                        new FunctionCallback<HashMap<String, Object>>() {
                            @Override
                            public void done(HashMap<String, Object>
                                                     stringObjectHashMap, ParseException e) {
                                ParseObject game = (ParseObject) stringObjectHashMap.get("game");
                                mGame = game;
                                // TODO updateUI
                            }
                        });
            }
        });
    }

    public void setPlayListener() {
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GameActivity.class);
                i.putExtra(Keys.EXTRA_GAME_OBJ_ID, mGame.getObjectId());

                startActivity(i);
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (mTimeHandler != null) {
            mUpdateTime.run();
        }
    }

    public void defaultValues() {
        mLangText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getResources().getColor(R.color.text_orange)) + ".lang = "
                + Helpers.getHtmlString("\" \"", "" +
                getResources().getColor(R.color.lang_pink)) + ";"));
        mDifficultyText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getResources().getColor(R.color.text_orange)) + ".difficulty = "
                + Helpers.getHtmlString("\" \"", "" +
                getResources().getColor(R.color.lang_pink)) + ";"));
        mInvitedText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getResources().getColor(R.color.text_orange)) + ".invited = " +
                "[];"));
        mAcceptedText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getResources().getColor(R.color.text_orange)) + ".accepted = " +
                "[];"));
    }

    public void setTextAfterFetch() {
        ParseObject lang = (ParseObject) mGame.get(Keys.LANGUAGE_POINT);
        mLangText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getResources().getColor(R.color.text_orange)) + ".lang = "
                + Helpers.getHtmlString("\"" +
                lang.getString(Keys.NAME_STR) + "\"", "" +
                getResources().getColor(R.color.lang_pink)) + ";"));
        ParseObject difficulty = (ParseObject) mGame
                .get(Keys.GAME_DIFFICULTY_POINT);
        difficulty.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                mDifficultyText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                        + getResources().getColor(R.color.text_orange))
                        + ".difficulty = " + Helpers.getHtmlString("\"" +
                        parseObject.getString(Keys.DIFFICULTY_STRING) + "\"", "" +
                        getResources().getColor(R.color.lang_pink)) + ";"));
            }
        });

        if (mGame.getBoolean(Keys.IS_INVITE_BOOL)) {
            mInvitedText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                    + getResources().getColor(R.color.text_orange)) + ".invited = " +
                    getColoredToString(Helpers.getStringArrayFromPoint
                            ((ArrayList<ParseObject>)
                                    mGame.get(Keys.INVITED_PLAYERS_ARR))) + ";"));
        } else {
            mInvitedText.setVisibility(View.GONE);
        }

        ArrayList<ParseObject> playersArr =
                (ArrayList<ParseObject>) mGame.get(Keys.PLAYERS_ARR);
        if (playersArr != null) {
            mAcceptedText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                    + getResources().getColor(R.color.text_orange)) + ".accepted = " +
                    getColoredToString(Helpers.getStringArrayFromPoint
                            (playersArr)) + ";"));
        } else {
            mAcceptedText.setText(Html.fromHtml(Helpers.getHtmlString("game", "" +
                    getResources().getColor(R.color.text_orange)) + ".accepted = ["
                    + Helpers.getHtmlString("null", "" + getResources()
                    .getColor(R.color.player_list_blue)) + "];"));
        }
    }


    private String getColoredToString(ArrayList<String> list) {
        String returnString = "[";
        for (int i = 0; i < list.size(); i++) {
            returnString += Helpers.getHtmlString(list.get(i), "" +
                    getResources().getColor(R.color.player_list_blue));

            if (i != list.size() - 1) {
                returnString += ",";
            } else {
                returnString += "]";
            }
        }
        return returnString;
    }

}
