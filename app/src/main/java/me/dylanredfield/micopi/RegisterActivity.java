package me.dylanredfield.micopi;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class RegisterActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager();
        setContentView(R.layout.activity_register);
    }

}
