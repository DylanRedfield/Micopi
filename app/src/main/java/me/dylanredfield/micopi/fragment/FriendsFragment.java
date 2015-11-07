package me.dylanredfield.micopi.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.dialog.AcceptFriendDialog;
import me.dylanredfield.micopi.dialog.AddFriendDialog;
import me.dylanredfield.micopi.listener.LineNumberSetHeightListner;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;

public class FriendsFragment extends Fragment {
    private View mView;
    private ListView mListView;
    private Typeface mFont;
    private List<ParseObject> mFriendsList;
    private List<ParseObject> mInviteList;
    private List<ParseObject> mFullList;
    private ParseUser mCurrentUser;
    private FriendsAdapter mAdapter;
    private Fragment mFragment;
    private ActionButton mAddFriend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedState) {
        mView = inflater.inflate(R.layout.fragment_friends, null, false);

        setDefaultValues();

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        queryParse();
        setListeners();
    }

    public void setDefaultValues() {
        mFont = Typeface.createFromAsset(getActivity().getAssets(), "source_code_pro_regular.ttf");
        mListView = (ListView) mView.findViewById(R.id.friends_list);

        TextView emptyList = (TextView) mView.findViewById(R.id.empty_list);
        emptyList.setTypeface(mFont);
        mListView.setEmptyView(emptyList);

        mAddFriend = (ActionButton) mView.findViewById(R.id.add_friend);

        mFullList = new ArrayList<>();
        mFriendsList = new ArrayList<>();
        mCurrentUser = ParseUser.getCurrentUser();

        if (mFragment == null) {
            mFragment = this;
        }
    }

    public void queryParse() {
        // TODO speed this query up by doing async
        mFriendsList = mCurrentUser.getList(Keys.FRIENDS_ARR);
        mCurrentUser.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                for (int i = 0; i < mFriendsList.size(); i++) {
                    if (i == mFriendsList.size() - 1) {
                        mFriendsList.get(i).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                Log.d("fetchInBackground", "true");
                                inviteQuery();

                            }
                        });
                    } else {
                        mFriendsList.get(i).fetchIfNeededInBackground();
                    }
                }

                if (mFriendsList.size() == 0) {
                    inviteQuery();
                }
            }
        });


    }

    public void inviteQuery() {
        ParseQuery<ParseObject> inviteQuery = ParseQuery.getQuery(Keys.KEY_FRIEND_REQUEST);
        inviteQuery.whereEqualTo(Keys.TO_USER_POINT, mCurrentUser);
        inviteQuery.include(Keys.FROM_USER_POINT);
        inviteQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list2, ParseException e) {
                if (e == null) {
                    mFullList.addAll(list2);
                    mFullList.addAll(mFriendsList);
                    mAdapter = new FriendsAdapter(mFragment);
                    mListView.setAdapter(mAdapter);

                } else {
                    Log.d("FuckTest", e.getMessage());
                }
            }
        });
    }

    public void setListeners() {
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFriendDialog dialog = AddFriendDialog.newInstance(mFriendsList);
                dialog.show(getFragmentManager(), null);
                dialog.setTargetFragment(mFragment, Keys.FRAGMENT_REQUEST_CODE);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mFullList.get(i).getClassName().equals(Keys.KEY_FRIEND_REQUEST)) {
                    AcceptFriendDialog dialog = new AcceptFriendDialog(getActivity());
                    Bundle extras = new Bundle();
                    extras.putString(Keys.EXTRA_GAME_OBJ_ID,
                            ((ParseObject) mFullList.get(i)
                                    .get(Keys.FROM_USER_POINT)).getObjectId());
                    dialog.setArguments(extras);
                    dialog.show(getFragmentManager(), null);
                }
            }
        });
    }

    public List<ParseObject> getFriendsList() {
        return mFriendsList;
    }

    public List<ParseObject> getInviteList() {
        return mInviteList;
    }

    public Typeface getFont() {
        return mFont;
    }

    public List<ParseObject> getFullList() {
        Log.d("FuckTest", "full list: " + mFullList.toString());
        return mFullList;
    }

    public static class FriendsAdapter extends BaseAdapter {
        private FriendsFragment mFragment;
        private List<ParseObject> mList;
        private Typeface mFont;

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mFragment.getActivity().getLayoutInflater().inflate(R.layout.row_friend
                        , null, false);
            }
            LinearLayout separator = (LinearLayout) view.findViewById(R.id.lay_1);
            LinearLayout line2LayoutOut = (LinearLayout) view.findViewById(R.id.lay_3);
            TextView separatorText = (TextView) view.findViewById(R.id.separator);

            TextView num1 = (TextView) view.findViewById(R.id.num_1);
            TextView num2 = (TextView) view.findViewById(R.id.num_2);
            TextView num3 = (TextView) view.findViewById(R.id.num_3);
            TextView num4 = (TextView) view.findViewById(R.id.num_4);

            TextView line1 = (TextView) view.findViewById(R.id.line_1);
            TextView line2 = (TextView) view.findViewById(R.id.line_2);

            separatorText.setTypeface(mFont);
            num1.setTypeface(mFont);
            num2.setTypeface(mFont);
            num3.setTypeface(mFont);
            num4.setTypeface(mFont);
            line1.setTypeface(mFont);


            if (position == 0) {
                separator.setVisibility(View.VISIBLE);

                mList.get(position).toString();

                if (mList.get(position).getClassName().equals(Keys.KEY_FRIEND_REQUEST)) {
                    separatorText.setText("// Friend Requests");
                } else if (mList.get(position).getClassName().equals(Keys.KEY_USER)) {
                    separatorText.setText("// Friends");
                }
            } else if (position > 0 && !mList.get(position - 1).getClassName()
                    .equals(mList.get(position).getClassName())) {
                separator.setVisibility(View.VISIBLE);
                separatorText.setText("// Friends");
            }


            if (separator.getVisibility() == View.VISIBLE) {
                num1.setText(" " + (1 + position * 4));
                num2.setText(" " + (2 + position * 4));
                num3.setText(" " + (3 + position * 4));
                num4.setText(" " + (4 + position * 4));
            } else {
                num2.setText(" " + (1 + position * 4));
                num3.setText(" " + (2 + position * 4));
                num4.setText(" " + (3 + position * 4));
            }


            if (mList.get(position).getClassName().equals(Keys.KEY_FRIEND_REQUEST)) {

                // friends.add("name");
                line1.setText(Html.fromHtml(
                        Helpers.getHtmlString("friends",
                                "" + mFragment.getActivity().getResources()
                                        .getColor(R.color.text_orange)) + ".add("
                                + Helpers.getHtmlString("\"" + ((ParseUser) mList.get(position)
                                .get(Keys.FROM_USER_POINT)).getUsername() + "\"", ""
                                + mFragment.getActivity()
                                .getResources().getColor(R.color.text_green)) + ");"));
                line2LayoutOut.setVisibility(View.GONE);
            } else if (mList.get(position).getClassName().equals(Keys.KEY_USER)) {
                // friend = friend("name");
                line2LayoutOut.setVisibility(View.GONE);
                line1.setText(Html.fromHtml(
                        Helpers.getHtmlString("friend", ""
                                + mFragment.getActivity().getResources()
                                .getColor(R.color.text_green))
                                + ".name = " + Helpers.getHtmlString(mList.get(position)
                                .getString(Keys.USERNAME_STR), "" + mFragment.getActivity()
                                .getResources().getColor(R.color.text_blue)) + ";"));
            }
            // Sets the height of the linenumber background to match the layout it resides in to
            // ensure the background is flush even on a multiline input
            view.findViewById(R.id.lay_1).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num1,
                            (LinearLayout) view.findViewById(R.id.lay_1)));
            view.findViewById(R.id.lay_2).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num2,
                            (LinearLayout) view.findViewById(R.id.lay_2)));
            view.findViewById(R.id.lay_3).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num3,
                            (LinearLayout) view.findViewById(R.id.lay_3)));
            view.findViewById(R.id.lay_4).getViewTreeObserver()
                    .addOnPreDrawListener(new LineNumberSetHeightListner(num4,
                            (LinearLayout) view.findViewById(R.id.lay_4)));
            return view;
        }

        public FriendsAdapter(Fragment fragment) {
            mFragment = (FriendsFragment) fragment;
            mList = mFragment.getFullList();

            mFont = mFragment.getFont();
        }
    }
}
