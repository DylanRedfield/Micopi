package me.dylanredfield.micopi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameListFragment extends Fragment {
    private ListView mGameListView;
    private GameListAdapter mListAdapter;
    private View mView;
    private ArrayList<ParseObject> mQueryList;
    private ActionButton mActionButton;
    private ParseUser mCurrentUser;
    private Typeface mFont;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_game_list, container, false);
        setHasOptionsMenu(true);
        getDefaultValues();
        setListeners();
        setEmptyList();
        queryGameList();

        return mView;
    }

    public void getDefaultValues() {


        mCurrentUser = ParseUser.getCurrentUser();

        mGameListView = (ListView) mView.findViewById(R.id.game_list);

        mQueryList = new ArrayList<>();

        mListAdapter = new GameListAdapter();
        mActionButton = (ActionButton) mView.findViewById(R.id.action_button);

        mFont = Typeface.createFromAsset(getResources().getAssets()
                , "source_code_pro_regular.ttf");
    }

    public void setListeners() {
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                // send is map with "languageId"

                // returns error if no lobbies open and creates one

                // if joined lobby and is not 4th person will get a success string that says "wait"
                // Show alert

                // If is fourth person will create game
                // returns back objectId of Game

                HashMap<String, String> params = new HashMap<>();

                Log.d("userName", mCurrentUser.getUsername());
                // TODO change to user inputed lang objectID, using Java's to test
                params.put("languageId", "7kNdgNs2QP");
                ParseCloud.callFunctionInBackground(Keys.SEARCH_FOR_LOBBY_CLOUD, params,
                        new FunctionCallback<String>() {
                            @Override
                            public void done(String o, ParseException e) {
                                if (e == null) {

                                    if (o.equals("wait")) {

                                        Log.d("CloudCall", "wait");
                                        createDialog("Lobby Found!", "The Game will start will " +
                                                "begin when enough players have joined").show();
                                    } else {
                                        // TODO sends user to Game Screen with ObjectId extra
                                        Log.d("CloudCall", "found");
                                    }
                                } else {
                                    Log.d("CloudCall",  e.getMessage());
                                    // Lobby has been created
                                    createDialog("Lobby Found!", "The Game will start will " +
                                            "begin when enough players have joined").show();
                                }
                            }
                        });

*/
            }
        });
    }

    public void queryGameList() {
        ParseQuery yourTurnQuery = ParseQuery.getQuery("GameRound");
        //TODO handle users
        yourTurnQuery.whereEqualTo("playerNotDone", mCurrentUser);

        ParseQuery<ParseObject> notYourTurnQuery = ParseQuery.getQuery("GameRound");
        notYourTurnQuery.whereEqualTo("playersDone", mCurrentUser);

        ParseQuery<ParseObject> leaderQuery = ParseQuery.getQuery("GameRound");
        leaderQuery.whereEqualTo("leader", mCurrentUser);
        leaderQuery.whereDoesNotExist("playersNotDone");

        ParseQuery<ParseObject> gameQuery1 = ParseQuery.getQuery("Game");
        gameQuery1.whereMatchesQuery("rounds", yourTurnQuery);

        ParseQuery<ParseObject> gameQuery2 = ParseQuery.getQuery("Game");
        gameQuery2.whereMatchesQuery("rounds", notYourTurnQuery);

        ParseQuery<ParseObject> gameQuery3 = ParseQuery.getQuery("Game");
        gameQuery3.whereMatchesQuery("rounds", leaderQuery);

        ParseQuery gameQuery4 = ParseQuery.getQuery("Game");
        gameQuery4.whereEqualTo("hasStarted", false);
        gameQuery4.whereEqualTo("invitedPlayers", mCurrentUser);

        ParseQuery gameQuery5 = ParseQuery.getQuery("Game");
        gameQuery5.whereEqualTo("hasStarted", false);
        gameQuery5.whereEqualTo("inviteStarter", mCurrentUser);

        ArrayList<ParseQuery<ParseObject>> params = new ArrayList<>();
        params.add(gameQuery1);
        params.add(gameQuery2);
        params.add(gameQuery3);
        params.add(gameQuery4);
        params.add(gameQuery5);

        ParseQuery<ParseObject> gamesQuery = ParseQuery.or(params);
        gamesQuery.orderByDescending("updatedAt");
        gamesQuery.include("rounds");
        gamesQuery.include("rounds.playersNotDone");
        gamesQuery.include("rounds.playersDone");
        gamesQuery.include("rounds.challenge");
        gamesQuery.include("rounds.leader");
        gamesQuery.include("rounds.winner");
        gamesQuery.include("Language");
        gamesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject p : list) {
                        mQueryList.add(p);
                    }

                    // Must sort so Invites, YoutTurn, and TheirTurn are together
                    sortQuery();
                    mGameListView.setAdapter(mListAdapter);
                } else {
                    Log.d("gameQueryError", e.getMessage());
                    Log.d("gameQueryError", e.getLocalizedMessage());
                }
            }
        });
    }

    public void setEmptyList() {
        mGameListView.setEmptyView(mView.findViewById(R.id.empty_list));
        String text = "games = " + Helpers.getHtmlString("null", ""
                + getResources().getColor(R.color.login_red));
        ((TextView) mView.findViewById(R.id.games_null)).setText(Html.fromHtml(text));
        ((TextView) mView.findViewById(R.id.line_1))
                .setText(Html.fromHtml("/* No games running right now."));
        ((TextView) mView.findViewById(R.id.line_2))
                .setText(Html.fromHtml("Start a new one! */"));

        ((TextView) mView.findViewById(R.id.games_null)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_1)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_2)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.id_1)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.id_2)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.id_3)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.id_4)).setTypeface(mFont);
    }

    public void sortQuery() {
        ArrayList<ParseObject> invitesList = new ArrayList<>();
        ArrayList<ParseObject> yourTurnList = new ArrayList<>();
        ArrayList<ParseObject> theirTurnList = new ArrayList<>();
        ArrayList<ParseObject> lobbyList = new ArrayList<>();
        // if Game started is game in progress
        // if hasnt started, if public its lobby
        // if hasnt started, is private, and is not in players array its invite
        // else private lobby
        for (ParseObject p : mQueryList) {

            // Checks if yourTurn list or theirTurn List
            if (p.getBoolean(Keys.HAS_STARTED_BOOL)) {
                List<ParseObject> rounds = p.getList(Keys.ROUNDS_ARR);
                ParseObject currentRound = rounds.get(rounds.size() - 1);
                List<ParseUser> playersNotDoneList =
                        currentRound.getList(Keys.PLAYERS_NOT_DONE_ARR);
                for (int i = 0; i < playersNotDoneList.size(); i++) {
                    if (playersNotDoneList.get(i).getObjectId()
                            .equals(mCurrentUser.getObjectId())) {
                        yourTurnList.add(p);
                    } else if (i == playersNotDoneList.size() - 1) {
                        theirTurnList.add(p);
                    }
                }
            } else {
                // Checks if invite or lobby
                if (p.getBoolean(Keys.IS_PUBLIC_ARR)) {
                    lobbyList.add(p);
                } else {
                    List<ParseUser> playersList = p.getList(Keys.PLAYERS_ARR);

                    for (int i = 0; i < playersList.size(); i++) {
                        if (playersList.get(i).getObjectId()
                                .equals(mCurrentUser.getObjectId())) {
                            invitesList.add(p);
                        } else if (i == playersList.size() - 1) {
                            lobbyList.add(p);
                        }
                    }
                }
            }
        }
        mListAdapter.setList(invitesList, yourTurnList, theirTurnList, lobbyList);
    }

    public AlertDialog createDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_in:
                Intent i = new Intent(getContext(), RegisterActivity.class);
                startActivity(i);
                return true;
            default:
                return false;
        }
    }
    public class GameListAdapter extends BaseAdapter {
        private TextView mSeparator;
        private TextView mLine1;
        private TextView mLine2;
        private ArrayList<ParseObject> mInvitesList;
        private ArrayList<ParseObject> mYourTurnList;
        private ArrayList<ParseObject> mTheirTurnList;
        private ArrayList<ParseObject> mLobbyList;
        private ArrayList<ParseObject> mFullList = new ArrayList<>();
        private String mString1 = "";
        private String mString2 = "";
        private String mString3 = "";
        private String mColor1;
        private String mColor2;
        private String mColor3;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater(null).inflate(R.layout.row_game, null);
            }
            mSeparator = (TextView) convertView.findViewById(R.id.separator);
            mLine1 = (TextView) convertView.findViewById(R.id.line_1);
            mLine2 = (TextView) convertView.findViewById(R.id.line_2);
            mSeparator.setTypeface(mFont);
            mLine1.setTypeface(mFont);
            mLine2.setTypeface(mFont);
            if (mInvitesList.size() > 0 && position == 0) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("Invites");
            } else if (mYourTurnList.size() > 0 && position == mInvitesList.size()) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("Your Turn");
            } else if (mTheirTurnList.size() > 0 && position ==
                    mInvitesList.size() + mYourTurnList.size()) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("Their Turn");
            } else if (mLobbyList.size() > 0 && position == mInvitesList.size() +
                    mYourTurnList.size() + mTheirTurnList.size()) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("Lobbies");
            } else {
                mSeparator.setVisibility(View.GONE);
            }

            if (mInvitesList.size() > 0 && position < mInvitesList.size()) {
                // Invites
                mString1 = "invite";
                mColor1 = "" + getResources().getColor(R.color.text_green);

                List<ParseUser> invitedPlayers = mFullList.get(position)
                        .getList(Keys.INVITED_PLAYERS_ARR);

                for (ParseUser p : invitedPlayers) {
                    mString2 += p.getUsername() + ", ";
                }
                mString2 = mString2.substring(0, mString2.length() - 1);
                mColor2 = "" + getResources().getColor(R.color.text_blue);

                mString3 = ((ParseObject) mFullList.get(position).get(Keys.LANGUAGE_POINT))
                        .getString(Keys.NAME_STR);
                mColor3 = "" + getResources().getColor(R.color.login_red);

            } else if (mYourTurnList.size() > 0 && position < mInvitesList.size()
                    + mYourTurnList.size()) {
            } else if (mTheirTurnList.size() > 0 && position < mInvitesList.size()
                    + mYourTurnList.size() + mTheirTurnList.size()) {
            } else if (mLobbyList.size() > 0 && position < mInvitesList.size() +
                    mYourTurnList.size() + mTheirTurnList.size() + mLobbyList.size()) {
            } else {
                mSeparator.setVisibility(View.GONE);
            }

            String uppercase = mString1.substring(0, 1).toUpperCase() + mString1.substring(1);
            String line1Text = "<font color= '#" + mColor1 + "'> " + mString1 + "</font> " +
                    "= new " + uppercase + "(<font color = '#" + mColor2 + "" +
                    "'> " + mString2 + "</font>);";

            String line2Text = "<font color= '#" + mColor1 + "'> " + mString1 +
                    "</font>.lang = <font color = '#" + mColor3 + "'>\"" + mString3 + "\"</font>;";
            mLine1.setText(Html.fromHtml(line1Text));
            mLine2.setText(Html.fromHtml(line2Text));

            return convertView;
        }

        @Override
        public int getCount() {
            return mFullList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFullList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setList(ArrayList<ParseObject> invitesList
                , ArrayList<ParseObject> yourTurnList
                , ArrayList<ParseObject> theirTurnList
                , ArrayList<ParseObject> lobbyList) {

            mInvitesList = invitesList;
            mYourTurnList = yourTurnList;
            mTheirTurnList = theirTurnList;
            mLobbyList = lobbyList;

            for (ParseObject p : mInvitesList) {
                mFullList.add(p);
            }
            for (ParseObject p : mYourTurnList) {
                mFullList.add(p);
            }
            for (ParseObject p : mTheirTurnList) {
                mFullList.add(p);
            }
            for (ParseObject p : mLobbyList) {
                mFullList.add(p);
            }
        }


    }
}
