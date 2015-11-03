package me.dylanredfield.micopi.fragment;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.ui.HomePagerAdapter;
import me.dylanredfield.micopi.R;


public class SignInActivityFragment extends Fragment {
    private View mView;
    private EditText mUsername;
    private EditText mPassword;
    private Button mEnter;
    private Typeface mFont;

    public SignInActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        defaultValues();
        setListeners();

        return mView;
    }

    public void defaultValues() {
        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mUsername = (EditText) mView.findViewById(R.id.username);
        mPassword = (EditText) mView.findViewById(R.id.password);
        mEnter = (Button) mView.findViewById(R.id.enter);

        mUsername.setTypeface(mFont);
        mPassword.setTypeface(mFont);
        mEnter.setTypeface(mFont);

        mUsername.setHint("// enterUsername(\"Required\")");
        mPassword.setHint("// enterPassword(\"Required\")");
        mEnter.setText("signIn()");
    }

    public void setListeners() {
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logInInBackground(
                        mUsername.getText().toString().trim(),
                        mPassword.getText().toString().trim(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null) {
                                    getActivity().finish();
                                    HomePagerAdapter.getAdapter(
                                            getActivity().getSupportFragmentManager())
                                            .notifyDataSetChanged();
                                } else {
                                    Helpers.showDialog("Whoops", e.getMessage(), getActivity());
                                }

                            }
                        }
                );

            }
        });
    }
}
