package me.dylanredfield.micopi.listener;

/**
 * Created by dylan_000 on 9/27/2015.
 */

import android.text.Layout;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LineNumberSetHeightListner implements ViewTreeObserver.OnPreDrawListener {
    private TextView mLineNumber;
    private LinearLayout mLayout;

    public LineNumberSetHeightListner(TextView textView, LinearLayout layout) {
        mLineNumber = textView;
        mLayout = layout;
    }

    @Override
    public boolean onPreDraw() {
        ViewTreeObserver observer = mLineNumber.getViewTreeObserver();
        if (observer.isAlive()) {
            mLineNumber.setHeight(mLayout.getHeight());
        }
        observer.removeOnPreDrawListener(this);
        return true;
    }
}