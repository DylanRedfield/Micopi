package me.dylanredfield.micopi.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import me.dylanredfield.micopi.R;

public class RegisterActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager();
        setContentView(R.layout.activity_register);
    }

}
