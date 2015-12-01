package me.dylanredfield.micopi.ui;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;


public class LineNumberEditText extends EditText implements View.OnKeyListener {
    private int mPreviousLineNum = 1;
    private int mTabCount = 0;
    private TextView mLines;
    private String mPreviousText;
    private LineNumberListener mListener = new LineNumberListener();
    private Handler mHandler = new Handler();
    private Runnable mUpdate = new Runnable() {
        @Override
        public void run() {

            int pos = getSelectionStart();
            setText(Html.fromHtml(mHighlighter.highlight("java", getText().toString())));
            setSelection(pos);
            mHandler.postDelayed(mUpdate, 1000);
        }
    };
    PrettifyHighlighter mHighlighter;

    public LineNumberEditText(Context context) {
        super(context);
        Log.d("Constructor", "word");
        setMaxLines(Integer.MAX_VALUE);
        mHighlighter = new PrettifyHighlighter();
        setOnKeyListener(this);
        addTextChangedListener(mListener);
        mHandler.postDelayed(mUpdate, 1000);
    }

    public LineNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("Constructor", "word");
        setOnKeyListener(this);
        addTextChangedListener(mListener);
        mHighlighter = new PrettifyHighlighter();
        mHandler.postDelayed(mUpdate, 1000);
    }

    public LineNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("Constructor", "word");
        setOnKeyListener(this);
        addTextChangedListener(mListener);
        mHighlighter = new PrettifyHighlighter();
        mHandler.postDelayed(mUpdate, 1000);
    }

    String prev = "";

    public boolean onKey(View v, int i, KeyEvent keyEvent) {
        Log.d("Tab", "Key Up");
        if (i == KeyEvent.KEYCODE_DEL && prev.length() > 0) {
            int pos = getSelectionStart();
            char c = prev.charAt(pos);

            Log.d("Tab", "" + c);
            if (c == '{') {
                mTabCount--;
                Log.d("Tab", "Remove tab: " + c);
            } else if (c == '}') {
                mTabCount++;
                Log.d("Tab", "Add tab" + c);
            }
        }
        prev = getText().toString();
        return false;
    }


    public void setTextView(TextView tv) {
        mLines = tv;
        mLines.setText("1");
    }

    public void onTextDelete() {
        Log.d("Tab", "K");

    }

    class LineNumberListener implements TextWatcher {

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            Log.d("Tab2", "Before: " + charSequence.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //removeTextChangedListener(mListener);
            Log.d("Prettify", "" + s + start + " " + before + " " + count);
            //addTextChangedListener(mListener);
            mPreviousText = getText().toString();
            /*if (s.length() > 0) {
                if (s.charAt(s.length() - 1) == '{') {
                    mTabCount++;
                    Log.d("Tab", "Add tab");
                } else if (s.charAt(s.length() - 1) == '}') {
                    mTabCount--;
                    Log.d("Tab", "Removend he drank  tab");
                } else {
                    Log.d("Tab", s.toString());
                }
            }*/

            int lines = getLineCount();
            if (mPreviousLineNum < lines) {
                mPreviousLineNum = lines;
                mLines.append("\n" + lines);

                //int position = getSelectionStart();
                //setText(Html.fromHtml(mHighlighter.highlight("java", getText().toString()) + "\n\n\n"));
                //setSelection(position);
                /*for (int i = 0; i < mTabCount; i++) {
                    append("\t");
                }*/
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("Tab", "After: " + s.toString());
        }
    }
}
