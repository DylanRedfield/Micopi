package me.dylanredfield.micopi.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.ui.LineNumberEditText;
import me.dylanredfield.micopi.ui.PrettifyHighlighter;
import me.dylanredfield.micopi.util.Keys;
import syntaxhighlight.SyntaxHighlighter;

public class JudgeCodeFragment extends Fragment {
    private View mView;
    private TextView mEditText;
    private ParseObject mSubmission;
    private ParseObject mGame;
    private String mSpot;
    private Button mSelect;
    private TextView mLabel;
    private Typeface mFont;
    private TextView mLines;
    private ParseObject mRound;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup viewGroup, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_judge_code, null, false);

        defaultValues();

        return mView;
    }

    public void defaultValues() {
        mFont = Typeface.createFromAsset(getResources().getAssets(),
                "source_code_pro_regular.ttf");

        mEditText = (TextView) mView.findViewById(R.id.edit_text);
        mEditText.setTypeface(mFont);

        mLines = (TextView) mView.findViewById(R.id.lines);
        mEditText.setTextColor(getResources().getColor(R.color.white));
        mLines.setTypeface(mFont);

        mSelect = (Button) mView.findViewById(R.id.select);
        mSelect.setTypeface(mFont);

        mLabel = (TextView) mView.findViewById(R.id.label);
        mLabel.setTypeface(mFont);
        mLines.setText("1");

        mSelect.setTag(false);

        final PrettifyHighlighter highlighter = new PrettifyHighlighter();

        mRound =

                mSubmission = ParseObject.createWithoutData(Keys.KEY_SUBMISSION,
                        getArguments().getString("submissionObjectId"));
        mSpot = getArguments().getString("spot");

        mLabel.setText("submissions[" + mSpot + "]");
        mSubmission.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                mEditText.setText(highlight(new SpannableStringBuilder(mEditText.getText())));
                Log.d("what the fuck", highlight(new SpannableStringBuilder(mEditText.getText())).toString());
                for (int i = 1; i < mEditText.getLineCount() + 1; i++) {
                    mLines.append("\n" + (i + 1));
                }
                Log.d("JudgeCode", "fetch");
                if (e == null) {
                    Log.d("JudgeCode", "e == null");
                }
            }
        });
        mRound.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                addListeners();
            }
        });
    }

    public void addListeners() {

        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Boolean) mSelect.getTag()) {
                    mRound.add(Keys.WINNERS_ARR, mSubmission.getParseObject(Keys.PLAYER_POINT));

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("gameId", mGame.getObjectId());
                    params.put("roundId", mRound.getObjectId());
                    ParseCloud.callFunctionInBackground("handleLeaderDone", params, new FunctionCallback<HashMap<String, Object>>() {
                        @Override
                        public void done(HashMap<String, Object> hashMap, ParseException e) {
                            getActivity().finish();
                        }
                    });
                } else {
                    mSelect.setTag(true);
                    mSelect.setText("confirm()");
                }
            }
        });
    }

    private Editable highlight(Editable e) {

        int errorLine = 0;
        final Pattern line = Pattern.compile(
                ".*\\n");
        final int COLOR_ERROR = 0x80ff0000;
        final Pattern numbers = Pattern.compile(
                "\\b(\\d*[.]?\\d+)\\b");

        final int COLOR_NUMBER = 0xff7ba212;
        final Pattern keywords = Pattern.compile(
                "\\b(attribute|const|uniform|varying|break|continue|" +
                        "do|for|while|if|else|in|out|inout|float|int|void|bool|true|false|" +
                        "lowp|mediump|highp|precision|invariant|discard|return|mat2|mat3|" +
                        "mat4|vec2|vec3|vec4|ivec2|ivec3|ivec4|bvec2|String|bvec3|bvec4|sampler2D|" +
                        "samplerCube|struct|gl_Vertex|gl_FragCoord|gl_FragColor)\\b");
        final Pattern builtins = Pattern.compile(
                "\\b(radians|degrees|sin|cos|tan|asin|acos|atan|pow|" +
                        "exp|log|exp2|log2|sqrt|inversesqrt|abs|sign|floor|ceil|fract|mod|" +
                        "min|max|clamp|mix|step|smoothstep|length|distance|dot|cross|" +
                        "normalize|faceforward|reflect|refract|matrixCompMult|lessThan|" +
                        "lessThanEqual|greaterThan|greaterThanEqual|equal|notEqual|any|all|" +
                        "not|dFdx|dFdy|fwidth|texture2D|texture2DProj|texture2DLod|" +
                        "texture2DProjLod|textureCube|textureCubeLod)\\b");
        final Pattern comments = Pattern.compile(
                "/\\*(?:.|[\\n\\r])*?\\*/|//.*");
        final int COLOR_KEYWORD = 0xff399ed7;
        final int COLOR_BUILTIN = 0xffd79e39;
        final int COLOR_COMMENT = 0xff808080;
        try {
            // don't use e.clearSpans() because it will remove
            // too much
            clearSpans(e);

            if (e.length() == 0)
                return e;

            if (errorLine > 0) {
                Matcher m = line.matcher(e);

                for (int n = errorLine;
                     n-- > 0 && m.find(); )
                    ;

                e.setSpan(
                        new BackgroundColorSpan(COLOR_ERROR),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            for (Matcher m = numbers.matcher(e);
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(COLOR_NUMBER),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (Matcher m = keywords.matcher(e);
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(COLOR_KEYWORD),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (Matcher m = builtins.matcher(e);
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(COLOR_BUILTIN),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (Matcher m = comments.matcher(e);
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(COLOR_COMMENT),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception ex) {
        }

        return e;

    }

    private void clearSpans(Editable e) {
        // remove foreground color spans
        {
            ForegroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    ForegroundColorSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }

        // remove background color spans
        {
            BackgroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    BackgroundColorSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }
    }
}
