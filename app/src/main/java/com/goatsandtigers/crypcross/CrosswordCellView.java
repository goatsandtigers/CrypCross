package com.goatsandtigers.crypcross;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CrosswordCellView extends LinearLayout {

    private static final GradientDrawable borderDrawableNoHighlight = buildCellBorderNoHighlightDrawable();
    private static final GradientDrawable borderDrawableLineHighlight = buildCellBorderWithHighlightDrawable();
    private static final GradientDrawable borderDrawableLineSelected = buildCellBorderWithSelectedDrawable();
    private static final String SAVED_STATE_KEY = "SAVED_STATE_KEY";
    private static final int REVEALED_LETTER_TEXT_COLOR = Color.BLUE;
    private static final String SAVED_USER_LETTER_KEY = "SAVED_USER_LETTER_KEY";
    private boolean black = false;
    private boolean selected = false;
    private boolean highlighted = false;
    private boolean revealed = false;
    private final String id;
    private final Crossword crossword;
    private TextView tv;

    public CrosswordCellView(Context context, Crossword crossword, String id) {
        super(context);
        this.crossword = crossword;
        this.id = id;
        setOrientation(HORIZONTAL);
        createTextView(context);
        setBackground(borderDrawableNoHighlight);
        initRevealedState();
    }

    public void showClueNumber(Context context, int number) {
        TextView clueTextView = new TextView(context) {
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                tv.setPadding(0, 0, w, 0);
            };
        };
        clueTextView.setText("" + number);
        clueTextView.setIncludeFontPadding(false);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        clueTextView.setLayoutParams(params);
        if (!CrypticCrosswordActivity.isTablet(getContext())) {
            clueTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        }
        addView(clueTextView, 0);
    }

    private void createTextView(Context context) {
        tv = new TextView(context);
        tv.setText(retrieveUserLetter());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, CrypticCrosswordActivity.isTablet(getContext()) ? 24 : 16);
        addView(tv);
        tv.setGravity(Gravity.CENTER);
        LayoutParams params = (LayoutParams) tv.getLayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        tv.setLayoutParams(params);
    }

    private static GradientDrawable buildCellBorderNoHighlightDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE); // Changes this drawbale to use a single color instead of a gradient
        gd.setStroke(1, Color.BLACK);
        return gd;
    }

    private static GradientDrawable buildCellBorderWithHighlightDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.YELLOW); // Changes this drawbale to use a single color instead of a gradient
        gd.setStroke(1, Color.BLACK);
        return gd;
    }

    private static GradientDrawable buildCellBorderWithSelectedDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.GREEN); // Changes this drawbale to use a single color instead of a gradient
        gd.setStroke(1, Color.BLACK);
        return gd;
    }

    public void unhighlight() {
        if (!black) {
            highlighted = false;
            setBackground(borderDrawableNoHighlight);
        }
    }

    public void highlight() {
        if (!black) {
            highlighted = true;
            setBackground(selected ? borderDrawableLineSelected : borderDrawableLineHighlight);
        }
    }

    public void setBlack() {
        black = true;
        setBackgroundColor(Color.BLACK);
    }

    public void setText(String text) {
        if (!revealed) {
            tv.setText(text);
            saveUserLetter(text);
        }
    }

    private void saveUserLetter(String userLetter) {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(getSavedUserLetterKey(), userLetter).commit();
    }

    private String retrieveUserLetter() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getSavedUserLetterKey(), "");
    }

    private String getSavedUserLetterKey() {
        return SAVED_USER_LETTER_KEY + id;
    }

    public void setWidth(int width) {
        android.view.ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        setLayoutParams(params);
    }

    public void setHeight(int height) {
        android.view.ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }

    public void select() {
        if (!black) {
            selected = true;
            setBackground(borderDrawableLineSelected);
        }
    }

    public void unselect() {
        if (!black) {
            selected = false;
            setBackground(highlighted ? borderDrawableLineHighlight : borderDrawableNoHighlight);
        }
    }

    public void revealLetter() {
        String answer = "" + crossword.getAnswer(id);
        if (!tv.getText().equals(answer)) {
            revealed = true;
            tv.setText(answer);
            tv.setTextColor(REVEALED_LETTER_TEXT_COLOR);
            saveRevealedState();
        }
    }

    private void saveRevealedState() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(getSavedStateKey(), true).commit();
    }

    private boolean retrieveRevealedState() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getSavedStateKey(), false);
    }

    private void initRevealedState() {
        revealed = retrieveRevealedState();
        if (revealed) {
            revealLetter();
        }
    }

    private String getSavedStateKey() {
        return SAVED_STATE_KEY + id;
    }

    public boolean containsCorrectAnswer() {
        if (black) {
            return true;
        } else {
            String correctAnswer = "" + crossword.getAnswer(id);
            return correctAnswer != null && correctAnswer.equals(tv.getText());
        }
    }

    public boolean isRevealed() {
        return revealed;
    }
}
