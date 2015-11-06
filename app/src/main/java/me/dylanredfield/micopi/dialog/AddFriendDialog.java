package me.dylanredfield.micopi.dialog;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.FriendsFragment;
import me.dylanredfield.micopi.ui.SelectUserAdapter;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class AddFriendDialog extends DialogFragment {
    private View mView;
    private TextView mLabel;
    private Button mUsername;
    private Button mFacebook;
    private Button mMessage;
    private Typeface mFont;
    private FriendsFragment mFragment;
    private Bundle mBundle;
    private List<ParseUser> mSearchList;

    public static AddFriendDialog newInstance(List<ParseObject> friendsList) {
        AddFriendDialog dialog = new AddFriendDialog();

        ArrayList<String> stringFriendsList = new ArrayList<>();
        for (ParseObject o : friendsList) {
            stringFriendsList.add(o.getObjectId());
        }

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putStringArrayList(Keys.EXTRA_FRIENDS_LIST, stringFriendsList);
        dialog.setArguments(args);

        return dialog;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_add_friend, null);
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
        mUsername = (Button) mView.findViewById(R.id.username);
        mFacebook = (Button) mView.findViewById(R.id.facebook);
        mMessage = (Button) mView.findViewById(R.id.message);

        mLabel.setTypeface(mFont);
        mUsername.setTypeface(mFont);
        mFacebook.setTypeface(mFont);
        mMessage.setTypeface(mFont);

        mLabel.setText("//NewFriend");

        mFragment = (FriendsFragment) getTargetFragment();

        mUsername.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.
                OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFacebook.setWidth(mUsername.getWidth());
                mMessage.setWidth(mUsername.getWidth());
            }
        });
    }

    public void setListeners() {
        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUsername();

            }
        });
    }

    public void loadUsername() {
        mView.findViewById(R.id.default_layout).setVisibility(View.GONE);
        mView.findViewById(R.id.username_search_layout).setVisibility(View.VISIBLE);

        TextView newLabel = (TextView) mView.findViewById(R.id.label_select_lang);
        newLabel.setTypeface(mFont);

        final ListView listView = (ListView) mView.findViewById(R.id.select_user_list);
        ImageButton button = (ImageButton) mView.findViewById(R.id.search);
        final EditText editText = (EditText) mView.findViewById(R.id.enter_username);
        final AddFriendDialog dialog = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseUser> query = new ParseQuery<>(Keys.KEY_USER);
                query.whereContains(Keys.USERNAME_STR, editText.getText().toString()
                        .trim().toLowerCase());
                /*query.whereNotEqualTo(Keys.OBJECT_ID_STR,
                        getArguments().getStringArrayList(Keys.EXTRA_FRIENDS_LIST));*/
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        mSearchList = list;
                        if (list != null) {
                            SelectUserAdapter adapter = new SelectUserAdapter(dialog);
                            listView.setAdapter(adapter);
                        } else if (e != null) {

                        } else {
                            Helpers.showDialog("Whoops", "" + e.getCode(), getActivity());
                        }
                    }
                });
            }
        });


    }

    public List<ParseUser> getSearchList() {
        return mSearchList;
    }
}
