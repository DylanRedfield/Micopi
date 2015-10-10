package me.dylanredfield.micopi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class RegisterFragmentBasic extends Fragment {
    private View mView;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private EditText mPasswordConfirmEdit;
    private EditText mEmailEdit;
    private Button mEnter;
    private Typeface mFont;
    private ProgressDialog mProgressDialog;
    private ParseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register_basic, container, false);

        setDefaultValues();
        setListeners();
        return mView;
    }

    public void setDefaultValues() {
        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mUsernameEdit = (EditText) mView.findViewById(R.id.username);
        mPasswordEdit = (EditText) mView.findViewById(R.id.password);
        mPasswordConfirmEdit = (EditText) mView.findViewById(R.id.password_confirm);
        mEmailEdit = (EditText) mView.findViewById(R.id.email);
        mEnter = (Button) mView.findViewById(R.id.enter);

        mUsernameEdit.setHint("// setUsername(\"Required\")");
        mPasswordEdit.setHint("// setPassword(\"Required\")");
        mPasswordConfirmEdit.setHint("// confirmPassword(\"Required\")");
        mEmailEdit.setHint("// setEmail()");
        mEnter.setText("createUser()");

        mUsernameEdit.setTypeface(mFont);
        mPasswordEdit.setTypeface(mFont);
        mPasswordConfirmEdit.setTypeface(mFont);
        mEmailEdit.setTypeface(mFont);
        mEnter.setTypeface(mFont);

        mUsernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i != 3) {
                    Log.d("TESTTEST", "FALSE -" + i1);
                } else {

                    Log.d("TESTTEST", "TRUE -" + i2);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mUser = ParseUser.getCurrentUser();
    }

    public void setListeners() {
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordsMatch()) {
                    if (isUsernameValid()) {
                        if (isEmailValid()) {
                            if (hasAllRequired()) {
                                register();
                            } else {
                                Helpers.showDialog("Whoops", "Please enter required Inputs"
                                        , getActivity());
                            }
                        } else {
                            Helpers.showDialog("Whoops", "Invalid Email", getActivity());
                        }
                    } else {
                        Helpers.showDialog("Whoops!", "Username must be no more than "
                                + Keys.USERNAME_LENGTH, getActivity());
                    }
                } else {
                    Helpers.showDialog("Whoops!", "Passwords do not match", getActivity());
                }
            }
        });
    }

    public boolean passwordsMatch() {
        return mPasswordEdit.getText().toString()
                .equals(mPasswordConfirmEdit.getText().toString());
    }

    public boolean isUsernameValid() {
        //Ensures username is 16 characters or less
        return mUsernameEdit.length() <= Keys.USERNAME_LENGTH;
    }

    public boolean isEmailValid() {
        return mEmailEdit.length() <= Keys.EMAIL_LENGTH;
    }

    public boolean hasAllRequired() {
        return mUsernameEdit.length() > 0 && mPasswordEdit.length() > 0
                && mPasswordConfirmEdit.length() > 0;
    }

    public void register() {
        mUser.setUsername(mUsernameEdit.getText().toString().trim());
        mUser.setPassword(mPasswordEdit.getText().toString().trim());
        mUser.put(Keys.IS_ANON_BOOL, false);
        if (mEmailEdit.length() > 0) {
            mUser.put(Keys.EMAIL_STR, mEmailEdit.getText().toString().trim());
        }

        mProgressDialog.show();
        mUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    HomePagerAdapter.getAdapter(getActivity().getSupportFragmentManager())
                            .notifyDataSetChanged();

                    getActivity().finish();
                } else {
                    Helpers.showDialog("Whoops", e.getMessage(), getActivity());
                }
            }
        });
    }
}
