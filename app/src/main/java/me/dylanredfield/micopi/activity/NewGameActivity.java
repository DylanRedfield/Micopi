package me.dylanredfield.micopi.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.dylanredfield.micopi.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewGameActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
    }

}
