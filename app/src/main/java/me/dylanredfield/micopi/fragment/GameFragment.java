package me.dylanredfield.micopi.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.LineNumberEditText;
import me.dylanredfield.micopi.ui.ShaderEditor;

public class GameFragment extends Fragment {
    private View mView;
    private LineNumberEditText mEditText;
    private TextView mLineNumbers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle state) {
        mView = inflater.inflate(R.layout.fragment_game, null, false);

        mEditText = (LineNumberEditText) mView.findViewById(R.id.edit_text);
        mLineNumbers = (TextView) mView.findViewById(R.id.lines);
        //mEditText.setTextView(mLineNumbers);

        Typeface font = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");
        mEditText.setTypeface(font);
        mLineNumbers.setTypeface(font);
        return mView;
    }

}
