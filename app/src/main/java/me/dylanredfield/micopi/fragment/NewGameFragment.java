package me.dylanredfield.micopi.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.dialog.SelectLangDialog;
import me.dylanredfield.micopi.ui.InviteToGameAdapter;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class NewGameFragment extends Fragment {
    private View mView;
    private Typeface mFont;
    private TextView mSelectLang;
    private ParseObject mNewGame;
    private List<ParseObject> mFriendsList;
    private SelectLangDialog mLangDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstance) {
        mView = inflater.inflate(R.layout.fragment_new_game, viewGroup, false);

        setDefaultValues();
        queryParse();

        return mView;
    }

    public void setDefaultValues() {
        mFont = Typeface.createFromAsset(getActivity().getAssets(), "source_code_pro_regular.ttf");
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

        TextView newGameTV = (TextView) mView.findViewById(R.id.new_game);
        newGameTV.setTypeface(mFont);
        newGameTV.setText(Html.fromHtml(Helpers.getHtmlString("game", ""
                + getActivity().getResources()
                .getColor(R.color.text_orange)) + " = new Game();"));


        TextView selectLangLabel = (TextView) mView.findViewById(R.id.select_lang_label);
        selectLangLabel.setTypeface(mFont);

        mSelectLang = (TextView) mView.findViewById(R.id.lang);
        mSelectLang.setTypeface(mFont);
        mSelectLang.setText(Html.fromHtml(Helpers.getHtmlString("game",
                "" + getResources().getColor(R.color.text_orange)) + ".lang = "
                + Helpers.getHtmlString("\"Select\"", ""
                + getResources().getColor(R.color.lang_pink)) + ";"));
        mLangDialog = SelectLangDialog.newInstance();
        mSelectLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLangDialog.show(getFragmentManager(), null);
            }
        });
    }
    public void queryParse() {
        ParseQuery<ParseObject> langQuery = new ParseQuery<ParseObject>(Keys.KEY_LANGUAGE);
        langQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                mLangDialog.setList(list);
            }
        });
        mFriendsList = ParseUser.getCurrentUser().getList(Keys.FRIENDS_ARR);
        for (int i = 0; i < mFriendsList.size(); i++) {
            if (i == mFriendsList.size() - 1) {
                mFriendsList.get(i).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                    }
                });
            } else {
                mFriendsList.get(i).fetchIfNeededInBackground();
            }
        }
    }

    public ParseObject getNewGame() {
        return mNewGame;
    }

}
