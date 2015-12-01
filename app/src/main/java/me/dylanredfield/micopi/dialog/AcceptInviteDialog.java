package me.dylanredfield.micopi.dialog;


import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.GameListFragment;
import me.dylanredfield.micopi.util.Keys;

public class AcceptInviteDialog extends DialogFragment {
    private View mView;
    private TextView mLabel;
    private Button mAccept;
    private Button mDecline;
    private GameListFragment mTargetFragment;
    private ParseObject mGame;

    public static AcceptInviteDialog newInstance() {
        AcceptInviteDialog dialog = new AcceptInviteDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_accept, null);
        builder.setView(mView);

        setDefaultValues();
        setListeners();
        Dialog dialog = builder.create();

        return dialog;
    }

    public void setDefaultValues() {
        mTargetFragment = (GameListFragment) getTargetFragment();
        Typeface font = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");
        mLabel = (TextView) mView.findViewById(R.id.label);
        mLabel.setTypeface(font);
        mLabel.setText("//GameInvite");

        mAccept = (Button) mView.findViewById(R.id.accept);
        mAccept.setTypeface(font);

        mDecline = (Button) mView.findViewById(R.id.decline);
        mDecline.setTypeface(font);

    }

    public void setListeners() {
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("inviteId", mGame.getObjectId());
                ParseCloud.callFunctionInBackground("handleInviteAccept", params,
                        new FunctionCallback<HashMap<String, Object>>() {
                            @Override
                            public void done(HashMap<String, Object> stringObjectHashMap,
                                             ParseException e) {
                                handleInviteAcceptResult(stringObjectHashMap);


                            }
                        });
            }
        });
        mDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Invite", "Decline Click");
                List<ParseObject> invitedList = mGame.getList(Keys.INVITED_PLAYERS_ARR);
                int spot = 0;

                for (int i = 0; i < invitedList.size(); i++) {
                    if (invitedList.get(i).getObjectId().equals(ParseUser.getCurrentUser()
                            .getObjectId())) {
                        spot = i;
                    }
                }
                invitedList.remove(spot);
                mGame.put(Keys.INVITED_PLAYERS_ARR, invitedList);
                mGame.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            dismiss();
                        } else {
                            Log.d("Invite", "Decline Click");
                        }
                        // TODO error check

                    }
                });
            }
        });
    }

    public void handleInviteAcceptResult(HashMap<String, Object> returnValue) {
        if (returnValue != null) {
            if (returnValue.get("message").equals("Game Started")) {
                // TODO send to game
            } else if (returnValue.get("message").equals("Not Enough Players")) {
                // TODO add game to gamelist
            } else if (returnValue.get("message").equals("Waiting On Leader")) {
                // TODO add to gamelist
            } else if (returnValue.get("message").equals("Waiting on Invites")) {
                // TODO add to gamelist
            }
        }
    }

    public void setGame(ParseObject game) {
        mGame = game;
    }
}







