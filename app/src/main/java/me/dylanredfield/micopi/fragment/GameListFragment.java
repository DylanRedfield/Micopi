package me.dylanredfield.micopi.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import me.dylanredfield.micopi.activity.LobbyActivity;
import me.dylanredfield.micopi.activity.RegisterActivity;
import me.dylanredfield.micopi.activity.SignInActivity;
import me.dylanredfield.micopi.dialog.AcceptInviteDialog;
import me.dylanredfield.micopi.dialog.NewGameDialog;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;
import me.dylanredfield.micopi.listener.LineNumberSetHeightListner;
import me.dylanredfield.micopi.R;


public class GameListFragment extends Fragment {
    private ListView mGameListView;
    private GameListAdapter mListAdapter;
    private View mView;
    private ArrayList<ParseObject> mQueryList;
    private ActionButton mActionButton;
    private ParseUser mCurrentUser;
    private Typeface mFont;
    private Fragment mFragment;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_game_list, container, false);
        setHasOptionsMenu(true);
        Log.d("GameList", "OnCreateView");
        getDefaultValues();
        setListeners();
        setEmptyList();
        queryGameList();

        return mView;
    }

    public void getDefaultValues() {
        mCurrentUser = ParseUser.getCurrentUser();

        if (mFragment == null) {
            mFragment = this;
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setCancelable(false);

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

                NewGameDialog dialog = new NewGameDialog(getActivity());
                dialog.setTargetFragment(mFragment, 0);
                dialog.show(getFragmentManager(), null);
            }
        });

        mGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getTag() != null && view.getTag().equals("invite")) {
                    AcceptInviteDialog dialog = AcceptInviteDialog.newInstance();
                    dialog.setGame((ParseObject) mListAdapter.getItem(i));
                    dialog.show(getFragmentManager(), null);

                } else {
                    Intent intent = new Intent(getActivity(), LobbyActivity.class);
                    intent.putExtra(Keys.EXTRA_GAME_OBJ_ID,
                            ((ParseObject) mListAdapter.getItem(i)).getObjectId());
                    startActivityForResult(intent, Keys.GAME_LIST_REQUEST_CODE);
                }
            }
        });
    }


    public void queryGameList() {
        ParseQuery yourTurnQuery = ParseQuery.getQuery("GameRound");

        //TODO handle users names
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

        ParseQuery gameQuery6 = ParseQuery.getQuery("Game");
        gameQuery6.whereEqualTo("hasStarted", false);
        gameQuery6.whereEqualTo("players", mCurrentUser);

        ParseQuery gameQuery7 = ParseQuery.getQuery("Game");
        gameQuery7.whereEqualTo("hasStarted", true);
        gameQuery7.whereEqualTo("players", mCurrentUser);
        ArrayList<ParseQuery<ParseObject>> params = new ArrayList<>();
        params.add(gameQuery1);
        params.add(gameQuery2);
        params.add(gameQuery3);
        params.add(gameQuery4);
        params.add(gameQuery5);
        params.add(gameQuery6);
        params.add(gameQuery7);

        ParseQuery<ParseObject> gamesQuery = ParseQuery.or(params);
        gamesQuery.orderByDescending("updatedAt");
        gamesQuery.include("rounds");
        gamesQuery.include("rounds.playersNotDone");
        gamesQuery.include("rounds.playersDone");
        gamesQuery.include("rounds.challenge");
        gamesQuery.include("rounds.leader");
        gamesQuery.include("rounds.winner");
        gamesQuery.include(Keys.PLAYERS_ARR);
        gamesQuery.include(Keys.INVITED_PLAYERS_ARR);
        gamesQuery.include("Language");
        //mProgressDialog.show();
        gamesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    for (ParseObject p : list) {
                        mQueryList.add(p);
                    }
                    Log.d("QueryList", mQueryList.toString());

                    // Must sort so Invites, YoutTurn, and TheirTurn are together
                    sortQuery();
                    mGameListView.setAdapter(mListAdapter);
                } else {
                    Helpers.showDialog("Whoops", e.getMessage(), getActivity());
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
            } else if (p.getBoolean(Keys.IS_INVITE_BOOL)) {
                List<String> stringList = new ArrayList<>();
                for (Object po : p.getList(Keys.PLAYERS_ARR)) {
                    stringList.add(((ParseObject) po).getObjectId());
                }

                boolean isLobby = false;
                for (String s : stringList) {
                    if (s.equals(mCurrentUser.getObjectId()) ||
                            ((ParseObject) p.get(Keys.INVITE_STARTER_POINT)).getObjectId()
                                    .equals(mCurrentUser.getObjectId())) {
                        isLobby = true;
                    }
                }
                if (!isLobby) {
                    // Is invite
                    invitesList.add(p);
                } else {
                    lobbyList.add(p);
                }

            } else {
                // is Lobby
                lobbyList.add(p);
            }
        }
        Log.d("SortLog", invitesList.toString() + "\n" +
                yourTurnList.toString() + "\n" +
                theirTurnList.toString() + "\n" +
                lobbyList.toString() + "\n" +
                mQueryList.toString());
        mListAdapter.setList(invitesList, yourTurnList, theirTurnList, lobbyList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem register = menu.findItem(R.id.register);
        MenuItem signIn = menu.findItem(R.id.sign_in);

        if (mCurrentUser.getBoolean(Keys.IS_ANON_BOOL)) {
            register.setVisible(true);
            signIn.setVisible(true);
        } else {
            register.setVisible(false);
            signIn.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.register:
                Intent i = new Intent(getContext(), RegisterActivity.class);
                startActivity(i);
                //getActivity().finish();
                return true;
            case R.id.sign_in:
                Intent fuckVariableNamesAmIRight = new Intent(getContext(), SignInActivity.class);
                startActivity(fuckVariableNamesAmIRight);
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AdapterNotify", "Trigger2");
        if (mListAdapter != null && requestCode == Keys.GAME_LIST_REQUEST_CODE
                && resultCode == Keys.CREATED_GAME_RESULT_CODE && data != null) {
            mQueryList.add(ParseObject.createWithoutData(Keys.KEY_GAME,
                    data.getStringExtra(Keys.EXTRA_GAME_OBJ_ID)));
            sortQuery();
            mListAdapter.notifyDataSetChanged();
            Log.d("AdapterNotify", "Success2");
        } else if (requestCode == Keys.GAME_LIST_REQUEST_CODE
                && resultCode == Keys.REFRESH_LIST_RESULT_CODE && data != null) {
            for (ParseObject p : mQueryList) {
                if (p.getObjectId().equals(data.getStringExtra(Keys.EXTRA_GAME_OBJ_ID))) {
                    mQueryList.remove(p);
                    break;
                }
            }
            sortQuery();
            mListAdapter.notifyDataSetChanged();
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
            Log.d("Position", "" + position);
            mSeparator = (TextView) convertView.findViewById(R.id.separator);
            mLine1 = (TextView) convertView.findViewById(R.id.line_1);
            mLine2 = (TextView) convertView.findViewById(R.id.line_2);


            LinearLayout separatorLayout = (LinearLayout) convertView.findViewById(R.id.lay_1);

            mSeparator.setTypeface(mFont);
            mLine1.setTypeface(mFont);
            mLine2.setTypeface(mFont);
            if (mInvitesList.size() > 0 && position == 0) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("// Invites");
            } else if (mYourTurnList.size() > 0 && position == mInvitesList.size()) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("// Your Turn");
            } else if (mTheirTurnList.size() > 0 && position ==
                    mInvitesList.size() + mYourTurnList.size()) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("// Their Turn");
            } else if (mLobbyList.size() > 0 && position == mInvitesList.size() +
                    mYourTurnList.size() + mTheirTurnList.size()) {
                mSeparator.setVisibility(View.VISIBLE);
                mSeparator.setText("// Lobbies");
            } else {
                separatorLayout.setVisibility(View.GONE);
            }

            TextView num1 = (TextView) convertView.findViewById(R.id.num_1);
            TextView num2 = (TextView) convertView.findViewById(R.id.num_2);
            TextView num3 = (TextView) convertView.findViewById(R.id.num_3);
            TextView num4 = (TextView) convertView.findViewById(R.id.num_4);

            num1.setTypeface(mFont);
            num2.setTypeface(mFont);
            num3.setTypeface(mFont);
            num4.setTypeface(mFont);

            if (separatorLayout.getVisibility() == View.VISIBLE) {
                num1.setText(" " + (1 + position * 4));
                num2.setText(" " + (2 + position * 4));
                num3.setText(" " + (3 + position * 4));
                num4.setText(" " + (4 + position * 4));
            } else {
                num2.setText(" " + (1 + position * 4));
                num3.setText(" " + (2 + position * 4));
                num4.setText(" " + (3 + position * 4));
            }

            if (Integer.parseInt(num1.getText().toString().trim()) > 9) {
                num1.setText(num1.getText().toString().substring(1));
            }
            if (Integer.parseInt(num2.getText().toString().trim()) > 9) {
                num2.setText(num2.getText().toString().substring(1));
            }
            if (Integer.parseInt(num3.getText().toString().trim()) > 9) {
                num3.setText(num3.getText().toString().substring(1));
            }
            if (Integer.parseInt(num4.getText().toString().trim()) > 9) {
                num4.setText(num4.getText().toString().substring(1));
            }
            // 1 is "object" text
            // 2 is playerList
            // 3 is language
            if (mInvitesList.size() > 0 && position < mInvitesList.size()) {
                // Invites
                mString1 = "invite";
                mColor1 = "" + getResources().getColor(R.color.text_green);

                mString2 = getPlayersString(position, true);
                mColor2 = "" + getResources().getColor(R.color.player_list_blue);
                convertView.setTag("invite");

            } else if (mYourTurnList.size() > 0 && position < mInvitesList.size()
                    + mYourTurnList.size()) {
                mString1 = "game";
                mColor1 = "" + getResources().getColor(R.color.text_orange);

                mString2 = getPlayersString(position, false);
                mColor2 = "" + getResources().getColor(R.color.player_list_blue);

            } else if (mTheirTurnList.size() > 0 && position < mInvitesList.size()
                    + mYourTurnList.size() + mTheirTurnList.size()) {
                mString1 = "game";
                mColor1 = "" + getResources().getColor(R.color.text_orange);

                mString2 = getPlayersString(position, false);
                mColor2 = "" + getResources().getColor(R.color.player_list_blue);
            } else if (mLobbyList.size() > 0 && position < mInvitesList.size() +
                    mYourTurnList.size() + mTheirTurnList.size() + mLobbyList.size()) {
                mString1 = "lobby";
                mColor1 = "" + getResources().getColor(R.color.text_orange);

                mString2 = getPlayersString(position, false);
                mColor2 = "" + getResources().getColor(R.color.player_list_blue);
            } else {
                //mSeparator.setVisibility(View.GONE);
            }
            mString3 = ((ParseObject) mFullList.get(position).get(Keys.LANGUAGE_POINT))
                    .getString(Keys.NAME_STR);
            mColor3 = "" + getResources().getColor(R.color.lang_pink);
            String uppercase = mString1.substring(0, 1).toUpperCase() + mString1.substring(1);

            String line1Text = Helpers.getHtmlString(mString1, mColor1) + " = " + uppercase + "(" +
                    Helpers.getHtmlString(mString2, mColor2) + ");";
            String line2Text = Helpers.getHtmlString(mString1, mColor1) + ".lang = " +
                    Helpers.getHtmlString("\"" + mString3 + "\"", mColor3) + ";";
            mLine1.setText(Html.fromHtml(line1Text));
            mLine2.setText(Html.fromHtml(line2Text));


            // Sets the height of the linenumber background to match the layout it resides in to
            // ensure the background is flush even on a multiline input
            convertView.findViewById(R.id.lay_1).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num1,
                            (LinearLayout) convertView.findViewById(R.id.lay_1)));
            convertView.findViewById(R.id.lay_2).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num2,
                            (LinearLayout) convertView.findViewById(R.id.lay_2)));
            convertView.findViewById(R.id.lay_3).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num3,
                            (LinearLayout) convertView.findViewById(R.id.lay_3)));
            convertView.findViewById(R.id.lay_4).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num4,
                            (LinearLayout) convertView.findViewById(R.id.lay_4)));
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
            mFullList.clear();

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

        public String getPlayersString(int position, boolean isInvite) {
            mString2 = "";

            List<ParseUser> list;
            if (isInvite) {
                list = mFullList.get(position).getList(Keys.INVITED_PLAYERS_ARR);
            } else {
                list = mFullList.get(position).getList(Keys.PLAYERS_ARR);
            }

            for (ParseUser p : list) {
                mString2 += p.getString(Keys.USERNAME_STR) + ", ";
            }
            mString2 = mString2.substring(0, mString2.length() - 2);
            Log.d("playerStringTest", mString2);
            return mString2;
        }

    }


}
