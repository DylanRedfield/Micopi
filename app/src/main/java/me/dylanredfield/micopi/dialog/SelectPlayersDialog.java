package me.dylanredfield.micopi.dialog;

/**
 * Created by dylan_000 on 11/7/2015.
 */
public class SelectPlayersDialog extends AbstractListViewDialog {

    @Override
    public void setListeners() {

    }

    public static SelectLangDialog newInstance() {
        SelectLangDialog dialog = new SelectLangDialog();
        return dialog;
    }
}
