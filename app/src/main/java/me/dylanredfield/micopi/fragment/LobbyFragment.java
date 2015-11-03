package me.dylanredfield.micopi.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;

import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;
import me.dylanredfield.micopi.R;

public class LobbyFragment extends Fragment {
    private View mView;
    private TextView mLangText;
    private TextView mInvitedText;
    private TextView mAcceptedText;
    private ParseObject mGame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedState) {
        mView = inflater.inflate(R.layout.fragment_lobby, null, false);

        setDefault();

        return mView;

    }

    public void setDefault() {
        ArrayList<TextView> lineNumbers = new ArrayList<>();

        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_1));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_2));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_3));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_4));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_5));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_6));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_7));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_8));

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

        mLangText = (TextView) mView.findViewById(R.id.lang);
        mLangText.setTypeface(font);

        mInvitedText = (TextView) mView.findViewById(R.id.invited_arr);
        mInvitedText.setTypeface(font);

        mAcceptedText = (TextView) mView.findViewById(R.id.accpeted_arr);
        mAcceptedText.setTypeface(font);

        getGame();
    }

    @SuppressWarnings("unchecked")
    private void getGame() {
        mGame = ParseObject.createWithoutData(Keys.KEY_GAME,
                getActivity().getIntent().getStringExtra(Keys.EXTRA_GAME_OBJ_ID));
        Log.d("mGame", "" + mGame.getNumber(Keys.DESIRED_NUM_PLAYERS));
        mGame.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseObject lang = (ParseObject) parseObject.get(Keys.LANGUAGE_POINT);
                    mLangText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                            + getResources().getColor(R.color.text_orange)) + ".lang = "
                            + Helpers.getHtmlString("\"" +
                            lang.getString(Keys.NAME_STR) + "\"", "" +
                            getResources().getColor(R.color.lang_pink)) + ";"));

                    mInvitedText.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                            + getResources().getColor(R.color.text_orange)) + ".invited = " +
                            getColoredToString(Helpers.getStringArrayFromPoint
                                    ((ArrayList<ParseObject>)
                                            mGame.get(Keys.INVITED_PLAYERS_ARR))) + ";"));

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
                } else {
                    Helpers.showDialog("Whoops", e.getMessage(), getActivity());
                }
            }
        });

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
