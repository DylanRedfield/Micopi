package me.dylanredfield.micopi.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.dylanredfield.micopi.ui.FailedEditText;
import me.dylanredfield.micopi.R;

public class RegisterFragment extends Fragment {
    private View mView;
    private FailedEditText mEditText;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register, container, false);
        defaultValues();
        return mView;
    }
    public void defaultValues() {
        mEditText = (FailedEditText) mView.findViewById(R.id.edit_text);
        final String current = "Device:~ CaH$ register";
        mEditText.setText(current);
        Selection.setSelection(mEditText.getText(), mEditText.getText().length());
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= current.length() &&
                        !s.toString().substring(0, current.length()).equals(current)) {
                    mEditText.setText("Device:~ CaH$ register");
                    Selection.setSelection(mEditText.getText(), mEditText.getText().length());

                }

            }
        });
    }
}
