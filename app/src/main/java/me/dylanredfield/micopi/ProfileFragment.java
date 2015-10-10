package me.dylanredfield.micopi;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private View mView;
    private TextView mCurrentPlayer;
    private TextView mName;
    private TextView mEmail;
    private TextView mSetName;
    private TextView mSetPassword;
    private TextView mSetEmail;
    private TextView mLogOut;
    private Typeface mFont;
    private String mUsernameString;
    private String mEmailString;
    private ParseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private ParseInstallation mInstalation;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        mCurrentUser = ParseUser.getCurrentUser();

        getDefaultViews();
        changeFonts();
        setText();
        setListeners();
        return mView;
    }

    public void getDefaultViews() {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        mCurrentPlayer = (TextView) mView.findViewById(R.id.current_player);
        mName = (TextView) mView.findViewById(R.id.name);
        mEmail = (TextView) mView.findViewById(R.id.email);

        mSetName = (TextView) mView.findViewById(R.id.set_name);
        mSetPassword = (TextView) mView.findViewById(R.id.set_password);
        mLogOut = (TextView) mView.findViewById(R.id.logout);
        mSetEmail = (TextView) mView.findViewById(R.id.set_email);

        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mProgressDialog = new ProgressDialog(getActivity());
    }

    public void changeFonts() {
        ((TextView) mView.findViewById(R.id.your_account)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.manage_account)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_1)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_2)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_3)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_4)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_5)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_6)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_7)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_8)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_9)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_10)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_11)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_12)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_13)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_14)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_15)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_new)).setTypeface(mFont);
        ((TextView) mView.findViewById(R.id.line_id_new_2)).setTypeface(mFont);

        mName.setTypeface(mFont);
        mCurrentPlayer.setTypeface(mFont);
        mEmail.setTypeface(mFont);
        mSetName.setTypeface(mFont);
        mSetEmail.setTypeface(mFont);
        mSetPassword.setTypeface(mFont);
        mLogOut.setTypeface(mFont);
    }

    public void setText() {
        mUsernameString = mCurrentUser.getUsername();
        mEmailString = mCurrentUser.getString(Keys.EMAIL_STR);

        if (mCurrentUser.getEmail() != null && mCurrentUser.getEmail().equals("")) {
            mEmailString = "null";
        } else {
            mEmailString = "\"" + mEmailString + "\"";
        }
        mCurrentPlayer.setText(Html.fromHtml(getHtmlString("player", "" +
                getResources().getColor(R.color.text_blue)) + " = "
                + getHtmlString("getPlayer()",
                "" + getResources().getColor(R.color.text_orange)) + ";"));

        mName.setText(Html.fromHtml(getHtmlString("player", "" + getResources()
                .getColor(R.color.text_blue)) + ".name = "
                + getHtmlString("\"" + mUsernameString + "\"", ""
                + getResources().getColor(R.color.text_green)) + ";"));

        mEmail.setText(Html.fromHtml(getHtmlString("player", "" + getResources()
                .getColor(R.color.text_blue)) + ".email = "
                + getHtmlString(mEmailString, ""
                + getResources().getColor(R.color.text_green)) + ";"));
    }

    public void setListeners() {
        mSetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDataDialogFragment dialog = new EditDataDialogFragment();
                dialog.setEditing(Keys.USERNAME_STR);
                dialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });

        mSetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDataDialogFragment dialog = new EditDataDialogFragment();
                dialog.setEditing(Keys.EMAIL_STR);
                dialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInstalation = ParseInstallation.getCurrentInstallation();
                mInstalation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        logOut();
                    }
                });
            }
        });
    }


    public void logOut() {
        mInstalation = ParseInstallation.getCurrentInstallation();

        defaultUserData();
    }

    public void defaultUserData() {
        mProgressDialog.show();
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(final ParseUser parseUser, ParseException e) {
                if (e == null) {
                    parseUser.put(Keys.USERNAME_STR, "guest_" + parseUser.getObjectId());
                    parseUser.put(Keys.FRIENDS_ARR, new ArrayList());
                    parseUser.put(Keys.IS_ANON_BOOL, true);
                    parseUser.put(Keys.NUMBER_OF_COMPILES, 0);
                    parseUser.put(Keys.GAMES_WON_NUM, 0);
                    parseUser.put(Keys.ROUNDS_WON_NUM, 0);
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (e == null) {
                                    HomePagerAdapter.getAdapter(getActivity()
                                            .getSupportFragmentManager())
                                            .notifyDataSetChanged();
                                    mProgressDialog.dismiss();
                                }
                            } else {
                                mProgressDialog.dismiss();
                                Helpers.showDialog(
                                        "Whoops",
                                        e.getMessage(),
                                        getActivity());
                            }
                        }
                    });
                } else {
                    mProgressDialog.dismiss();
                    Helpers.showDialog("Whoops", e.getMessage(), getActivity());
                }
            }
        });


    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHtmlString(String input, String hexColor) {
        return "<font color = '" + hexColor + "'>" + input + "</font>";
    }

    public class EditDataDialogFragment extends DialogFragment {
        private String mEditing;
        private View mView;

        public EditDataDialogFragment() {

        }

        public void setEditing(String editing) {
            mEditing = editing;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            mView = inflater.inflate(R.layout.dialog_edit_data, null);
            builder.setView(mView);

            TextView label = (TextView) mView.findViewById(R.id.label);
            final EditText editText = (EditText) mView.findViewById(R.id.edit_text);
            Button enter = (Button) mView.findViewById(R.id.enter);

            label.setTypeface(mFont);
            editText.setTypeface(mFont);
            enter.setTypeface(mFont);

            if (mEditing.equals(Keys.USERNAME_STR)) {
                enter.setText("setName()");
                label.setText("Change Username");
                editText.setHint("username");
            } else if (mEditing.equals(Keys.EMAIL_STR)) {
                enter.setText("setEmail()");
                label.setText("Change Email");
                editText.setHint("email");
            }


            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCurrentUser = ParseUser.getCurrentUser();
                    mCurrentUser.put(mEditing, editText.getText().toString().trim());

                    mProgressDialog.show();
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                setText();
                                getDialog().dismiss();
                            } else {
                                Helpers.showDialog("Whoops", e.getMessage(), mActivity);
                            }
                        }
                    });
                }
            });


            Dialog dialog = builder.create();
            return dialog;
        }
    }
}

