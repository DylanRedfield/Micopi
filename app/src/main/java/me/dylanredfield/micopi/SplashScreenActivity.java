package me.dylanredfield.micopi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends Activity {
    private Typeface mFont;
    private TextView mLogo;
    private TextView mLogoText;
    private SharedPreferences mPref;
    private ParseUser mCurrentUser;
    private ParseInstallation mInstallation;
    private boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        defaultThings();

        installationLogic();
    }

    public void defaultThings() {
        mPref = getSharedPreferences(Keys.PREF_STR, Activity.MODE_PRIVATE);
        isFirstTime = mPref.getBoolean(Keys.IS_FIRST_TIME_STR, true);

        mFont = Typeface.createFromAsset(getAssets(), "source_code_pro_regular.ttf");
        mLogo = (TextView) findViewById(R.id.logo);
        mLogoText = (TextView) findViewById(R.id.logo_text);

        mLogo.setTypeface(mFont);
        mLogoText.setTypeface(mFont);

        mLogoText.setText(Html.fromHtml("Code " + Helpers.getHtmlString("Against",
                "" + getResources().getColor(R.color.login_red))
                + " " + Helpers.getHtmlString("Humanity", ""
                + getResources().getColor(R.color.text_blue))));
    }

    public void installationLogic() {
        mCurrentUser = ParseUser.getCurrentUser();

        mInstallation = ParseInstallation.getCurrentInstallation();
        mInstallation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (isFirstTime) {
                        defaultUserData();
                    } else {
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        if (isOnline()) {
                            startActivity(i);
                            finish();
                        } else {
                            //TODO error
                        }
                    }
                } else {
                    // TODO error
                }
            }
        });
    }

    public void defaultUserData() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(final ParseUser parseUser, ParseException e) {
                if (e == null) {
                    //parseUser.put(Keys.USERNAME_STR, "guest_" + parseUser.getObjectId());
                    parseUser.put(Keys.FRIENDS_ARR, new ArrayList());
                    parseUser.put(Keys.IS_ANON_BOOL, true);
                    parseUser.put(Keys.NUMBER_OF_COMPILES, 0);
                    parseUser.put(Keys.GAMES_WON_NUM, 0);
                    parseUser.put(Keys.ROUNDS_WON_NUM, 0);
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            mInstallation.put(Keys.KEY_USER, parseUser);
                            mInstallation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Log.d("testUserData", "" + parseUser.getUsername());
                                    Log.d("testUserData", "" + parseUser.getObjectId());
                                    mPref.edit().putBoolean(Keys.IS_FIRST_TIME_STR, false).apply();
                                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);

                                    if (isOnline()) {
                                        startActivity(i);
                                        finish();
                                    } else {
                                        // TODO error
                                    }
                                }
                            });

                        }
                    });
                } else {
                    Log.d("AnonError", e.getMessage());
                }
            }
        });


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
