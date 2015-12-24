package me.dylanredfield.micopi.util;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

import net.danlew.android.joda.JodaTimeAndroid;

import me.dylanredfield.micopi.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Micopi extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
        Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                Keys.APPLICATION_ID, Keys.CLIENT_KEY);
        ParseUser.enableAutomaticUser();
    }
}
