package me.dylanredfield.micopi.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import me.dylanredfield.micopi.activity.GameActivity;
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
/*        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_1));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_2));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_3));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_4));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_5));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_6));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_7));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_8));*/

        Typeface font = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        for (TextView tv : lineNumbers) {
            tv.setTypeface(font);
        }

        TextView gameInfoLabel = (TextView) mView.findViewById(R.id.game_info_label);
        gameInfoLabel.setText("// Game Info");
        gameInfoLabel.setTypeface(font);

        TextView invitedLabel = (TextView) mView.findViewById(R.id.invited_label);
        invitedLabel.setText("// Invited");
        invitedLabel.setTypeface(font);

        ((TextView) mView.findViewById(R.id.public_label)).setTypeface(font);

        mDifficultyText = (TextView) mView.findViewById(R.id.difficulty);
        mDifficultyText.setTypeface(font);

        mLangText = (TextView) mView.findViewById(R.id.lang);
        mLangText.setTypeface(font);

        mInvitedText = (TextView) mView.findViewById(R.id.invited_arr);
        mInvitedText.setTypeface(font);

        mAcceptedText = (TextView) mView.findViewById(R.id.accpeted_arr);
        mAcceptedText.setTypeface(font);

        mEditLobby = (Button) mView.findViewById(R.id.edit_lobby);
        mEditLobby.setTypeface(font);

        mStartGame = (Button) mView.findViewById(R.id.start_game_leader);
        mStartGame.setTypeface(font);

        mInviteFriends = (Button) mView.findViewById(R.id.invite_friends);
        mInviteFriends.setTypeface(font);

        mAddPlayersDialog = AddPlayersToLobbyDialog.newInstance();
        mAddPlayersDialog.setTargetFragment(mFragment, 0);

        mEditLobbyDialog = EditLobbyDialog.newInstance();
        mEditLobbyDialog.setTargetFragment(mFragment, 0);

        mPlay = (Button) mView.findViewById(R.id.play);
        mPlay.setTypeface(font);
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
        ParseQuery<ParseObject> currentRoundQuery = ParseQuery.getQuery(Keys.KEY_GAME_ROUND);
        currentRoundQuery.whereEqualTo(Keys.GAME_POINT, mGame);
        currentRoundQuery.include(Keys.LEADER_POINT);
        currentRoundQuery.include(Keys.PLAYERS_DONE_ARR);
        currentRoundQuery.include(Keys.PLAYERS_NOT_DONE_ARR);
        currentRoundQuery.include(Keys.LEADER_POINT);
        currentRoundQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    mCurrentRound = parseObject;
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
            boolean isContained = false;
            List<ParseObject> playersDoneArray = mCurrentRound.getList(Keys.PLAYERS_DONE_ARR);
            for (ParseObject p : playersDoneArray) {
                if (p.getObjectId().equals(mCurrentUser.getObjectId())) {
                    isContained = true;
                }
            }
            if (mGame.getBoolean(Keys.IS_INVITE_BOOL)) {
                if (!isContained) {
                    type = "private_started_your_turn_player";
                } else {
                    type = "private_started_their_turn_player";
                }
            } else {
                if (!isContained) {
                    type = "public_started_your_turn_player";
                } else {
                    type = "public_started_their_turn_player";
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

    public void setViewsFromType(String type) {
        if (type.equals(Keys.PRIVATE_LOBBY_STARTER)) {
            mView.findViewById(R.id.leader_panel).setVisibility(View.VISIBLE);
            checkIfCanStart();
            mView.findViewById(R.id.invited_accepted_view).setVisibility(View.VISIBLE);
        } else if (type.equals(Keys.PRIVATE_LOBBY_PLAYER)) {
            mView.findViewById(R.id.lobby).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.invited_accepted_view).setVisibility(View.VISIBLE);
        } else if (type.equals(Keys.PUBLIC_LOBBY)) {
            mView.findViewById(R.id.invited_accepted_view).setVisibility(View.VISIBLE);
        } else if (type.contains("your_turn")) {
            mView.findViewById(R.id.your_turn).setVisibility(View.VISIBLE);
            setPlayListener();
        }

    }

    public void checkIfCanStart() {
        if (mGame.getList(Keys.PLAYERS_ARR).size() >= 3) {
            Button startGame = (Button) mView.findViewById(R.id.leader_panel)
                    .findViewById(R.id.start_game_leader);
            startGame.setVisibility(View.VISIBLE);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> params = new HashMap<String, Object>();
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
