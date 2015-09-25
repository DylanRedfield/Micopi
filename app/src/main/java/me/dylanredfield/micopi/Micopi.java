package me.dylanredfield.micopi;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

public class Micopi extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                Keys.APPLICATION_ID, Keys.CLIENT_KEY);
        ParseUser.enableAutomaticUser();
    }
}
