package me.dylanredfield.micopi;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LobbyFragment extends Fragment {
    private View mView;
    private TextView mLangText;
    private TextView mInvitedText;
    private TextView mAcceptedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedState) {
        mView = inflater.inflate(R.layout.fragment_lobby, null, false);

        setDefault();

        return mView;

    }

    public void setDefault() {
        ArrayList<TextView> lineNumbers = new ArrayList<>();

        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_1));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_2));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_3));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_4));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_5));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_6));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_7));
        lineNumbers.add((TextView) mView.findViewById(R.id.line_id_8));

        Typeface font = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        for (TextView tv : lineNumbers) {
            tv.setTypeface(font);
        }

        TextView gameInfoLabel = (TextView) mView.findViewById(R.id.game_info_label);
        gameInfoLabel.setText("// Game Info");
        gameInfoLabel.setTypeface(font);

        TextView invitedLabel = (TextView) mView.findViewById(R.id.invited_label);
        invitedLabel.setText("// Invited");
        invitedLabel.setTypeface(font);

        mLangText = (TextView) mView.findViewById(R.id.lang);
        mLangText.setTypeface(font);

        mInvitedText = (TextView) mView.findViewById(R.id.invited_arr);
        mInvitedText.setTypeface(font);

        mAcceptedText = (TextView) mView.findViewById(R.id.accpeted_arr);
        mAcceptedText.setTypeface(font);
    }
}
