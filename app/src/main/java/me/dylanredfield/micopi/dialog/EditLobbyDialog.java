package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.LobbyFragment;
import me.dylanredfield.micopi.util.Keys;

public class EditLobbyDialog extends DialogFragment {
    private View mView;
    private TextView mLabel;
    private Button mInviteMoreFriends;
    private Button mRemovePlayers;
    private Button mDeleteLobby;
    private LobbyFragment mTargerFragment;
    private AddPlayersToLobbyDialog mPlayersAddDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_edit_lobby, null);
        builder.setView(mView);

        Typeface font = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");
        mTargerFragment = (LobbyFragment) getTargetFragment();
        mPlayersAddDialog = AddPlayersToLobbyDialog.newInstance();
        mPlayersAddDialog.setTargetFragment(mTargerFragment, 0);

        mLabel = (TextView) mView.findViewById(R.id.label);
        mLabel.setTypeface(font);

        mInviteMoreFriends = (Button) mView.findViewById(R.id.invite_more_friends);
        mInviteMoreFriends.setTypeface(font);

        mRemovePlayers = (Button) mView.findViewById(R.id.remove_players);
        mRemovePlayers.setTypeface(font);

        mDeleteLobby = (Button) mView.findViewById(R.id.delete_lobby);
        mDeleteLobby.setTypeface(font);
        mDeleteLobby.getBackground().setColorFilter(getResources()
                .getColor(R.color.fab_material_red_500), PorterDuff.Mode.LIGHTEN);
        mInviteMoreFriends.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.
                OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRemovePlayers.setWidth(mInviteMoreFriends.getWidth());
                mDeleteLobby.setWidth(mInviteMoreFriends.getWidth());
            }
        });
        setPlayersDialogList();
        setListeners();
        Dialog dialog = builder.create();
        return dialog;
    }

    public static EditLobbyDialog newInstance() {
        EditLobbyDialog dialog = new EditLobbyDialog();
        return dialog;
    }

    public void setPlayersDialogList() {
        setPlayersDialogListHelper();

    }

    public void setPlayersDialogListHelper() {
        List<ParseObject> list = ParseUser.getCurrentUser().getList(Keys.FRIENDS_ARR);
        ParseObject.fetchAllInBackground(list, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<ParseObject> playersArr = mTargerFragment.getGame()
                        .getList(Keys.PLAYERS_ARR);
                List<ParseObject> invitedArr = mTargerFragment.getGame()
                        .getList(Keys.INVITED_PLAYERS_ARR);
                ArrayList<ParseObject> tempArr = new ArrayList<>();
                Log.d("SetPlayers", "list: " + list.toString());
                Log.d("SetPlayers", "players: " + playersArr.toString());
                Log.d("SetPlayers", "invited: " + invitedArr.toString());

                for (ParseObject p : list) {
                    boolean playersContains = false;
                    for (int i = 0; i < playersArr.size(); i++) {
                        if (!playersArr.get(i).getObjectId().equals(p.getObjectId())) {
                            if (!p.getObjectId().equals(ParseUser.getCurrentUser()
                                    .getObjectId())) {
                                Log.d("FriendsList", "players" + p.getObjectId());
                            } else {
                                playersContains = true;
                            }
                        } else {
                            playersContains = true;
                        }
                    }
                    boolean inviteContains = false;
                    for (int i = 0; i < invitedArr.size(); i++) {
                        if (!invitedArr.get(i).getObjectId().equals(p.getObjectId())) {
                            Log.d("FriendsList", p.getObjectId());
                        } else {
                            inviteContains = true;
                        }
                    }
                    if (!inviteContains && !playersContains) {
                        tempArr.add(p);
                    }
                }
                mPlayersAddDialog.setList(tempArr);
            }
        });
    }


    public void setListeners() {
        mInviteMoreFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayersAddDialog.show(getFragmentManager(), null);
            }
        });
        mDeleteLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String objectId = mTargerFragment.getGame().getObjectId();
                mTargerFragment.getGame().deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent data = new Intent();
                        data.putExtra(Keys.EXTRA_GAME_OBJ_ID, objectId);
                        getActivity().setResult(Keys.REFRESH_LIST_RESULT_CODE,
                                data);
                        getActivity().finish();
                        dismiss();
                        // TODO error handle
                    }
                });
            }
        });
    }

}
