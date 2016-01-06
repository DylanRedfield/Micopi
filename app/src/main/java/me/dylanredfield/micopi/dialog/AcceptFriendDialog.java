package me.dylanredfield.micopi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.FriendsFragment;
import me.dylanredfield.micopi.util.Keys;

public class AcceptFriendDialog extends DialogFragment {

    private View mView;
    private Typeface mFont;
    private ProgressDialog mProgressDialog;
    private TextView mLabel;
    private Button mAccept;
    private Button mDecline;
    private ParseUser mCurrentUser;
    private FriendsFragment mFragment;

    public static AcceptFriendDialog newInstance(Bundle extras) {
        AcceptFriendDialog dialog = new AcceptFriendDialog();
        dialog.setArguments(extras);
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
        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mCurrentUser = ParseUser.getCurrentUser();

        mLabel = (TextView) mView.findViewById(R.id.label);
        mAccept = (Button) mView.findViewById(R.id.accept);
        mDecline = (Button) mView.findViewById(R.id.decline);

        mLabel.setTypeface(mFont);
        mAccept.setTypeface(mFont);
        mDecline.setTypeface(mFont);

        mLabel.setText("//FriendRequest");

        mFragment = (FriendsFragment) getTargetFragment();
    }

    public void setListeners() {
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO fix
                mCurrentUser.add(Keys.FRIENDS_ARR,
                        ParseObject.createWithoutData(Keys.KEY_USER,
                                getArguments().getString(Keys.EXTRA_GAME_OBJ_ID)));
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("fromUserId", getArguments().getString(Keys.EXTRA_GAME_OBJ_ID));
                            ParseCloud.callFunctionInBackground("handleFriendRequest", params,
                                    new FunctionCallback<String>() {
                                        @Override
                                        public void done(String hashMap,
                                                         ParseException e) {
                                            // TODO cloud code error handling is shit
                                            dismiss();
                                            ParseObject.createWithoutData(Keys.KEY_FRIEND_REQUEST,
                                                    getArguments().getString("FriendRequest"))
                                                    .deleteInBackground(new DeleteCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                dismiss();
                                                                mFragment.queryParse();
                                                            } else {
                                                                Toast.makeText(mFragment.getActivity(),
                                                                        e.getMessage(),
                                                                        Toast.LENGTH_SHORT)
                                                                        .show();
                                                            }

                                                        }
                                                    });

                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}

