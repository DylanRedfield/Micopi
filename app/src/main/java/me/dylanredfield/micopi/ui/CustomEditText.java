package me.dylanredfield.micopi.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends EditText {
    public CustomEditText(Context context) {
        super(context);
    }
    public CustomEditText (Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        // set your input filter here
    }
}
