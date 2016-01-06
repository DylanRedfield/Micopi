package me.dylanredfield.micopi.fragment;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.dialog.SelectDifficultyDialog;
import me.dylanredfield.micopi.dialog.SelectLangDialog;
import me.dylanredfield.micopi.dialog.SelectPlayersDialog;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class NewGameFragment extends Fragment {
    private View mView;
    private Typeface mFont;
    private TextView mSelectLang;
    private ParseObject mNewGame;
    private List<ParseObject> mFriendsList;
    private SelectLangDialog mLangDialog;
    private Button mStartGame;
    private LinearLayout mLayout2;
    private LinearLayout mLayout3;
    private TextView mSelectPlayersLabel;
    private TextView mSelectPlayers;
    private SelectPlayersDialog mPlayersDialog;
    private TextView mSelectDifficulty;
    private SelectDifficultyDialog mDifficultyDialog;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstance) {
        mView = inflater.inflate(R.layout.fragment_new_game, viewGroup, false);

        setDefaultValues();
        setListeners();
        queryParse();

        return mView;
    }

    public void setDefaultValues() {
        mFont = Typeface.createFromAsset(getActivity().getAssets(), "source_code_pro_regular.ttf");
        setLineNumbers();

        TextView newGameTV = (TextView) mView.findViewById(R.id.new_game);
        TextView selectLangLabel = (TextView) mView.findViewById(R.id.select_lang_label);
        TextView difficultyLabel = (TextView) mView.findViewById(R.id.select_difficult_label);
        mSelectPlayersLabel = (TextView) mView.findViewById(R.id.select_players_label);
        mSelectLang = (TextView) mView.findViewById(R.id.lang);
        mSelectPlayers = (TextView) mView.findViewById(R.id.select_players);
        mLayout2 = (LinearLayout) mView.findViewById(R.id.layout_2);
        mLayout3 = (LinearLayout) mView.findViewById(R.id.layout_3);
        mStartGame = (Button) mView.findViewById(R.id.start_game_leader);
        mSelectDifficulty = (TextView) mView.findViewById(R.id.select_difficulty);

        newGameTV.setTypeface(mFont);
        selectLangLabel.setTypeface(mFont);
        difficultyLabel.setTypeface(mFont);
        mSelectLang.setTypeface(mFont);
        mSelectPlayersLabel.setTypeface(mFont);
        mSelectPlayers.setTypeface(mFont);
        mStartGame.setTypeface(mFont);
        mSelectDifficulty.setTypeface(mFont);

        newGameTV.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getActivity().getResources()
                .getColor(R.color.text_orange)) + " = new Game();"));

        mNewGame = ParseObject.create(Keys.KEY_GAME);
        mNewGame.put(Keys.DESIRED_NUM_PLAYERS, 1);
        mNewGame.put(Keys.HAS_STARTED_BOOL, false);
        mNewGame.put(Keys.INVITE_STARTER_POINT, ParseUser.getCurrentUser());
        mNewGame.put(Keys.IS_OVER_BOOL, false);
        mNewGame.put(Keys.IS_PUBLIC_BOOL, false);
        mNewGame.put(Keys.IS_INVITE_BOOL, true);
        mNewGame.put(Keys.NUM_PLAYERS_NUM, 1);
        mNewGame.add(Keys.PLAYERS_ARR, ParseUser.getCurrentUser());


        mLangDialog = SelectLangDialog.newInstance();
        mLangDialog.setTargetFragment(this, 0);
        mPlayersDialog = SelectPlayersDialog.newInstance();
        mPlayersDialog.setTargetFragment(this, 0);
        mDifficultyDialog = SelectDifficultyDialog.newInstance();
        mDifficultyDialog.setTargetFragment(this, 0);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        setDefaultText();
    }

    public void setLineNumbers() {
        ArrayList<TextView> lineNums = new ArrayList();
        lineNums.add((TextView) mView.findViewById(R.id.line_id_1));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_2));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_3));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_4));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_5));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_6));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_7));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_8));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_9));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_10));
        lineNums.add((TextView) mView.findViewById(R.id.line_id_11));

        for (TextView tv : lineNums) {
            tv.setTypeface(mFont);
        }
    }

    public void setDefaultText() {
        mSelectLang.setText(
                Html.fromHtml(
                        Helpers.getHtmlString("game",
                                "" + getResources().getColor(R.color.text_orange)) + ".lang = "
                                + Helpers.getHtmlString("\"Select\"", ""
                                        + getResources().getColor(R.color.lang_pink)
                        ) + ";"
                )
        );

        mSelectPlayers.setText(
                Html.fromHtml(
                        Helpers.getHtmlString("game", ""
                                + getResources().getColor(R.color.text_orange))
                                + ".players = ["
                                + Helpers.getHtmlString("Select", ""
                                        + getResources().getColor(R.color.text_blue)
                        ) + "];"
                )
        );

        mSelectDifficulty.setText(
                Html.fromHtml(
                        Helpers.getHtmlString("game", ""
                                + getResources().getColor(R.color.text_orange))
                                + ".difficulty = "
                                + Helpers.getHtmlString("\"Select\"", ""
                                        + getResources().getColor(R.color.lang_pink)
                        ) + ";"
                )
        );
    }

    public void setListeners() {
        mSelectLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLangDialog.show(getFragmentManager(), null);
            }
        });
        mLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayersDialog.show(getFragmentManager(), null);
            }
        });
        mLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDifficultyDialog.show(getFragmentManager(), null);
            }
        });
        mStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                mNewGame.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mProgressDialog.dismiss();
                        if (e == null) {
                            Intent data = new Intent();
                            data.putExtra(Keys.EXTRA_GAME_OBJ_ID, mNewGame.getObjectId());
                            getActivity().setResult(Keys.CREATED_GAME_RESULT_CODE,
                                    data);
                            getActivity().finish();
                        } else {
                            Helpers.showDialog("Whoops", e.getMessage(), getActivity());
                        }
                    }
                });
            }
        });
    }

    public void queryParse() {
        // TODO implement static query work
        ParseQuery<ParseObject> langQuery = new ParseQuery<>(Keys.KEY_LANGUAGE);
        langQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                mLangDialog.setList(list);
            }
        });
        mFriendsList = ParseUser.getCurrentUser().getList(Keys.FRIENDS_ARR);
        for (int i = 0; i < mFriendsList.size(); i++) {
            if (i == mFriendsList.size() - 1) {
                mFriendsList.get(i).fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        Log.d("FetchIfNeeded", "word");
                        mPlayersDialog.setList(mFriendsList);
                    }
                });
            } else {
                mFriendsList.get(i).fetchIfNeededInBackground();
            }
        }

        ParseQuery<ParseObject> difficultyQuery = ParseQuery.getQuery(Keys.KEY_GAME_DIFFICULTY);
        difficultyQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                mDifficultyDialog.setList(list);
            }
        });
    }

    public ParseObject getNewGame() {
        return mNewGame;
    }

    public TextView getSelectLang() {
        return mSelectLang;
    }

    public LinearLayout getLayout2() {
        return mLayout2;
    }

    public LinearLayout getLayout3() {
        return mLayout3;
    }

    public TextView getSelectPlayers() {
        return mSelectPlayers;
    }

    public TextView getSelectDifficulty() {
        return mSelectDifficulty;
    }

}
