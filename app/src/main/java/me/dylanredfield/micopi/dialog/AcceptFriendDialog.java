package me.dylanredfield.micopi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.util.Keys;

public class AcceptFriendDialog extends DialogFragment {

    private View mView;
    private Typeface mFont;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private TextView mLabel;
    private Button mAccept;
    private Button mDecline;
    private Bundle mExtras;

    // TODO fix
    public AcceptFriendDialog(Activity activity) {
        mActivity = activity;
    }


    public void setArguments(Bundle extras) {
        mExtras = extras;
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

        mLabel = (TextView) mView.findViewById(R.id.label);
        mAccept = (Button) mView.findViewById(R.id.accept);
        mDecline = (Button) mView.findViewById(R.id.decline);

        mLabel.setTypeface(mFont);
        mAccept.setTypeface(mFont);
        mDecline.setTypeface(mFont);

        mLabel.setText("//FriendRequest");
    }

    public void setListeners() {
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO fix
                ParseUser.getCurrentUser().add(Keys.FRIENDS_ARR, ParseObject.createWithoutData(Keys.KEY_USER,
                        mExtras.getString(Keys.EXTRA_GAME_OBJ_ID)));
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("fromUserId", mExtras.getString(Keys.EXTRA_GAME_OBJ_ID));
                        ParseCloud.callFunctionInBackground("handleFriendRequest", params,
                                new FunctionCallback<String>() {
                                    @Override
                                    public void done(String hashMap,
                                                     ParseException e) {
                                        if (e == null) {
                                            dismiss();

                                        } else {
                                        }

                                    }
                                });
                    }
                });

            }
        });
    }
}

