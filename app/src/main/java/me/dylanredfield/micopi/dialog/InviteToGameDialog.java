package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.InviteToGameAdapter;
import me.dylanredfield.micopi.util.Keys;

public class InviteToGameDialog extends DialogFragment {
    private View mView;
    private TextView mAdd;
    private TextView mLabel;
    private TextView mDone;
    private TextView mNumSelectedTextView;
    private ListView mListView;
    private InviteToGameAdapter mAdapter;
    private List<ParseObject> mFriendsList;
    private ParseUser mCurrentUser;
    private InviteToGameDialog mDialog;

    public static InviteToGameDialog newInstance() {
        InviteToGameDialog dialog = new InviteToGameDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_invite_to_game, null);
        if (mDialog == null) {
            mDialog = this;
        }
        builder.setView(mView);
        setDefaultValues();

        queryParse();

        Dialog dialog = builder.create();
        return dialog;

    }

    public void setDefaultValues() {
        mAdd = (TextView) mView.findViewById(R.id.add);
        mLabel = (TextView) mView.findViewById(R.id.label_players);
        mDone = (TextView) mView.findViewById(R.id.done);
        mNumSelectedTextView = (TextView) mView.findViewById(R.id.selected_friends);
        mListView = (ListView) mView.findViewById(R.id.select_user_list);

        mCurrentUser = ParseUser.getCurrentUser();
    }

    public void queryParse() {
        mFriendsList = mCurrentUser.getList(Keys.FRIENDS_ARR);
        for (int i = 0; i < mFriendsList.size(); i++) {
            if (i == mFriendsList.size() - 1) {
                mFriendsList.get(i).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        mAdapter = new InviteToGameAdapter(mDialog);
                        mListView.setAdapter(mAdapter);
                    }
                });
            } else {
                mFriendsList.get(i).fetchIfNeededInBackground();
            }
        }
    }

    public void setListeners() {
        mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO intent to newgame gamefragment
            }
        });
    }
    public List<ParseObject> getFriendsList() {
        return mFriendsList;
    }
}
