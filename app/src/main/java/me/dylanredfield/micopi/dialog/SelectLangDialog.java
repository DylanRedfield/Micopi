package me.dylanredfield.micopi.dialog;

import com.parse.ParseObject;

import me.dylanredfield.micopi.fragment.NewGameFragment;

public class SelectLangDialog extends AbstractListViewDialog {

    @Override
    public void setListeners() {
        ParseObject newGame = ((NewGameFragment) getTargetFragment()).getNewGame();

    }

    public static SelectLangDialog newInstance() {
        SelectLangDialog dialog = new SelectLangDialog();
        return dialog;
    }
}
