package me.dylanredfield.micopi.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.SelectLangListAdapter;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class NewGameDialog extends DialogFragment {

    private View mView;
    private Typeface mFont;
    private Button mFindGame;
    private Button mInviteFriends;
    private TextView mLabel;
    private ParseUser mCurrentUser;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;

    //TODO fix
    public NewGameDialog(Activity activity) {
        mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_new_game, null);
        builder.setView(mView);
        setDefault();

        Dialog dialog = builder.create();

        return dialog;
    }

    public void setDefault() {
        mCurrentUser = ParseUser.getCurrentUser();

        mFindGame = (Button) mView.findViewById(R.id.find_game);
        mInviteFriends = (Button) mView.findViewById(R.id.invite_friends);

        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mLabel = (TextView) mView.findViewById(R.id.label);
        setListeners();

        mLabel.setTypeface(mFont);
        mFindGame.setTypeface(mFont);
        mInviteFriends.setTypeface(mFont);
        mLabel.setText("new Game()");

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Loading...");
        mInviteFriends.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.
                OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mFindGame.setWidth(mInviteFriends.getWidth());
            }
        });
    }

    public void setListeners() {
        mFindGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.findViewById(R.id.default_layout).setVisibility(View.GONE);
                mView.findViewById(R.id.select_lang_layout).setVisibility(View.VISIBLE);

                queryLanguageTable();
            }
        });
    }

    public void queryLanguageTable() {
        ParseQuery<ParseObject> langQuery = ParseQuery.getQuery(Keys.KEY_LANGUAGE);
        langQuery.orderByAscending(Keys.NAME_STR);
        mProgressDialog.show();
        langQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    ListView listView = (ListView) mView.findViewById(R.id.select_lang_list);
                    SelectLangListAdapter listAdapter = new SelectLangListAdapter(getActivity(),
                            list);
                    listView.setAdapter(listAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView,
                                                View view, int i, long l) {
                            findGame(list.get(i).getObjectId());

                        }
                    });
                } else {
                    Helpers.showDialog("Whoops", e.getMessage(), mActivity);
                }
            }
        });
    }

    public void findGame(final String langId) {
        HashMap<String, String> params = new HashMap<>();

        Log.d("userName", mCurrentUser.getUsername());
        // TODO change to user inputed lang objectID, using Java's to test
        params.put("languageId", langId);
        mProgressDialog.show();
        ParseCloud.callFunctionInBackground(Keys.SEARCH_FOR_LOBBY_CLOUD, params,
                new FunctionCallback<HashMap<String, Object>>() {

                    @Override
                    public void done(HashMap<String, Object> stringObjectHashMap, ParseException e) {
                        String message = "";
                        if (stringObjectHashMap != null) {
                            message = (String) stringObjectHashMap.get("message");
                        }
                        if (message.equals("Joined Lobby")) {
                            Helpers.showDialog("Lobby Found!", "The Game will " +
                                    "start will begin when enough players have " +
                                    "joined", mActivity);
                            dismiss();
                        } else if (message.equals("Game Started")) {
                            // TODO game found and started
                            // Send to game screen with objectId in the "game" key
                        } else if (e != null && e.getMessage().equals("None Open")) {
                            createGame(langId);
                        } else if (e != null) {
                            Helpers.showDialog("Whoops", e.getMessage(), mActivity);
                        }
                    }
                });

        ParseCloud.callFunctionInBackground(Keys.SEARCH_FOR_LOBBY_CLOUD, params,
                new FunctionCallback<String>() {
                    @Override
                    public void done(String o, ParseException e) {
                        if (e == null) {
                            if (o.equals("wait")) {
                                Log.d("CloudCall", "wait");
                                Helpers.showDialog("Lobby Found!", "The Game will " +
                                        "start will begin when enough players have " +
                                        "joined", mActivity);
                                dismiss();
                            } else {
                                // TODO sends user to Game Screen with ObjectId extra
                                Log.d("CloudCall", "found");
                                dismiss();
                            }
                        } else {
                            // if e No Lobbys Open
                            // TODO correct error handling
                            //createGame();
                            Log.d("SearchForLobby Error: ", e.getMessage());
                            if (e.getMessage().equals("No Lobbys Open")) {

                                createGame(langId);
                                // Lobby has been created

                            } else {
                                Helpers.showDialog("Whoops", e.getMessage(), getActivity());
                                dismiss();
                                mProgressDialog.dismiss();
                            }
                        }
                    }
                });
    }

    public void createGame(String langId) {
        ParseObject game = new ParseObject(Keys.KEY_GAME);
        game.put(Keys.IS_PUBLIC_BOOL, true);
        game.put(Keys.HAS_STARTED_BOOL, false);
        game.put(Keys.DESIRED_NUM_PLAYERS, 4);
        ParseObject lang = ParseObject.createWithoutData(Keys.KEY_LANGUAGE, langId);
        game.put(Keys.LANGUAGE_POINT, lang);
        // invitedPlayersArr = new Array with current user with first slut
        game.add(Keys.PLAYERS_ARR, mCurrentUser);
        game.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    dismiss();
                    Helpers.showDialog("Lobby Found!", "The Game will start " +
                            "will begin when enough players have joined"
                            , mActivity);
                } else {
                    Helpers.showDialog("Whoops", e.getMessage(), getActivity());
                }
            }
        });
    }
}

