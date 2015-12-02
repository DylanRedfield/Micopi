package me.dylanredfield.micopi.dialog;

import android.view.View;

import me.dylanredfield.micopi.fragment.LobbyFragment;

public class AddPlayersToLobbyDialog extends SelectPlayersDialog {

    @Override
    public void setAddListener() {
        getAdd().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LobbyFragment) getTargetFragment()).addPlayers(getPlayersList());
            }
        });
    }

    public static AddPlayersToLobbyDialog newInstance() {
        AddPlayersToLobbyDialog dialog = new AddPlayersToLobbyDialog();
        return dialog;
    }
}
