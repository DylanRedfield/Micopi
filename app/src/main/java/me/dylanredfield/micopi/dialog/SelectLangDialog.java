package me.dylanredfield.micopi.dialog;

import android.text.Html;
import android.view.View;
import android.widget.AdapterView;

import com.parse.ParseObject;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.NewGameFragment;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class SelectLangDialog extends AbstractListViewDialog {

    @Override
    public void setListeners() {
        final ParseObject newGame = ((NewGameFragment) getTargetFragment()).getNewGame();

        getLabel().setText("//Language");

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                newGame.put(Keys.LANGUAGE_POINT, getList().get(i));
                ((NewGameFragment) getTargetFragment()).getSelectLang()
                        .setText(Html.fromHtml(Helpers.getHtmlString("game",
                                "" + getResources().getColor(R.color.text_orange)) + ".lang = "
                                + Helpers.getHtmlString("\""
                                + getList().get(i).getString(Keys.NAME_STR)
                                + "\"", "" + getResources().getColor(R.color.lang_pink)) + ";"));
                ((NewGameFragment) getTargetFragment()).getLayout2().setVisibility(View.VISIBLE);
                dismiss();
            }
        });
    }

    public static SelectLangDialog newInstance() {
        SelectLangDialog dialog = new SelectLangDialog();
        return dialog;
    }
}
