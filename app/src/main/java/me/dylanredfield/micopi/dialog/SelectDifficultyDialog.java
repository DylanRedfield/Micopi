package me.dylanredfield.micopi.dialog;

import android.text.Html;
import android.view.View;
import android.widget.AdapterView;

import com.parse.ParseObject;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.NewGameFragment;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class SelectDifficultyDialog extends AbstractListViewDialog {
    private ParseObject mNewGame;

    @Override
    public void setListeners() {
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mNewGame.put(Keys.GAME_DIFFICULTY_POINT, getList().get(i));
                ((NewGameFragment) getTargetFragment()).getSelectDifficulty()
                        .setText(Html.fromHtml(
                                Helpers.getHtmlString("game", "" + getResources()
                                        .getColor(R.color.text_orange)) + ".difficulty = " +
                                        Helpers.getHtmlString("\"" +
                                                getList().get(i).getString(Keys.DIFFICULTY_STRING)
                                                + "\"", ""
                                                + getResources().getColor(R.color.lang_pink))
                                        + ";"));
                dismiss();
            }
        });
    }

    @Override
    public void setDefaultValues() {
        mNewGame = ((NewGameFragment) getTargetFragment()).getNewGame();
        getLabel().setText("//Difficulty");
    }

    public static SelectDifficultyDialog newInstance() {
        SelectDifficultyDialog dialog = new SelectDifficultyDialog();
        return dialog;
    }
}
