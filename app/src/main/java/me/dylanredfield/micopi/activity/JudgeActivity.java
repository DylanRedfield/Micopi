package me.dylanredfield.micopi.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.JudgePageAdapter;
import me.dylanredfield.micopi.util.Keys;

public class JudgeActivity extends AppCompatActivity {
    private List<ParseObject> mSubmissionList;
    private ParseObject mRound;
    private ViewPager mPager;
    private JudgePageAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge);

        mRound = ParseObject.createWithoutData(Keys.KEY_GAME_ROUND,
                getIntent().getStringExtra(Keys.EXTRA_GAME_OBJ_ID));
        Log.d("mRound", mRound.getObjectId());

        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new JudgePageAdapter(getSupportFragmentManager(), this);

        mPager.setAdapter(mAdapter);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Keys.KEY_SUBMISSION);
        query.whereEqualTo(Keys.GAME_ROUND_POINT, mRound);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.d("SubmissionQuery", "" + list.size());
                    mSubmissionList = list;
                    mAdapter.setList(mSubmissionList);
                }
            }
        });
    }
    public String getRoundObjectId() {
        return mRound.getObjectId();
    }
}

