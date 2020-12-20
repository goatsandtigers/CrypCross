package com.goatsandtigers.crypcross;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KeyboardView extends LinearLayout {

    private static final GradientDrawable borderDrawableNoHighlight = buildCellBorderNoHighlightDrawable();
    private static final int VERTICAL_PADDING = 0;
    private static final long MIN_TIME_BETWEEN_SAME_LETTER_TOUCHES = 250;
    public static final String BACKSPACE = "<-";

    private final CrosswordController controller;
    private LinearLayout topRow;
    private LinearLayout middleRow;
    private LinearLayout bottomRow;

    private class LastTouchInfo {
        String letter;
        long timeOfTouchMillis;

        public LastTouchInfo(String letter, long timeOfTouchMillis) {
            this.letter = letter;
            this.timeOfTouchMillis = timeOfTouchMillis;
        }
    }

    private LastTouchInfo lastTouchInfo;

    public KeyboardView(Context context, CrosswordController controller) {
        super(context);
        this.controller = controller;
        setBackgroundColor(Color.BLACK);
        setOrientation(VERTICAL);
        addView(createTopRow());
        addView(createMiddleRow());
        addView(createBottomRow());
    }

    private LinearLayout createTopRow() {
        topRow = new LinearLayout(getContext());
        topRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        topRow.addView(createLetterView("Q"));
        topRow.addView(createLetterView("W"));
        topRow.addView(createLetterView("E"));
        topRow.addView(createLetterView("R"));
        topRow.addView(createLetterView("T"));
        topRow.addView(createLetterView("Y"));
        topRow.addView(createLetterView("U"));
        topRow.addView(createLetterView("I"));
        topRow.addView(createLetterView("O"));
        topRow.addView(createLetterView("P"));
        return topRow;
    }

    private LinearLayout createMiddleRow() {
        middleRow = new LinearLayout(getContext());
        middleRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        middleRow.addView(createLetterView("A"));
        middleRow.addView(createLetterView("S"));
        middleRow.addView(createLetterView("D"));
        middleRow.addView(createLetterView("F"));
        middleRow.addView(createLetterView("G"));
        middleRow.addView(createLetterView("H"));
        middleRow.addView(createLetterView("J"));
        middleRow.addView(createLetterView("K"));
        middleRow.addView(createLetterView("L"));
        return middleRow;
    }

    private LinearLayout createBottomRow() {
        bottomRow = new LinearLayout(getContext());
        bottomRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        bottomRow.addView(createLetterView("Z"));
        bottomRow.addView(createLetterView("X"));
        bottomRow.addView(createLetterView("C"));
        bottomRow.addView(createLetterView("V"));
        bottomRow.addView(createLetterView("B"));
        bottomRow.addView(createLetterView("N"));
        bottomRow.addView(createLetterView("M"));
        bottomRow.addView(createLetterView(BACKSPACE));
        return bottomRow;
    }

    private TextView createLetterView(final String letter) {
        TextView tv = new TextView(getContext());
        tv.setText(letter);
        tv.setTextSize(16);
        tv.setBackground(borderDrawableNoHighlight);
        tv.setGravity(Gravity.CENTER);
        tv.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                long timeOfTouchMillis = System.currentTimeMillis();
                if (lastTouchInfo == null || lastTouchInfo.letter != letter
                        || timeOfTouchMillis - lastTouchInfo.timeOfTouchMillis > MIN_TIME_BETWEEN_SAME_LETTER_TOUCHES) {
                    controller.onLetterPressed(letter);
                    lastTouchInfo = new LastTouchInfo(letter, timeOfTouchMillis);
                    return true;
                } else {
                    return false;
                }
            }
        });
        return tv;
    }

    private static GradientDrawable buildCellBorderNoHighlightDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE); // Changes this drawbale to use a single color instead of a gradient
        gd.setStroke(1, Color.BLACK);
        return gd;
    }

    /**
     * onSizeChanged does not get called on Nexus 5 so this event must be used to perform the same resizing funtionality
     * as when size changes.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        handleSizeChanged(r - l, b - t);
    }

    /**
     * This event does not fire on Nexus 5 so it must call the reusable method handleSizeChanged() which can also be
     * called from the onLayout event which does get fired on Nexus 5.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        handleSizeChanged(w, h);
    }

    private void handleSizeChanged(int w, int h) {
        final int textViewWidth = Math.max(w / 11, 50);
        final int textViewHeight = Math.max((h - (4 * VERTICAL_PADDING)) / 3, 40);
        for (int i = 0; i < topRow.getChildCount(); i++) {
            TextView childView = (TextView) topRow.getChildAt(i);
            childView.setWidth(textViewWidth);
            childView.setHeight(textViewHeight);
        }
        for (int i = 0; i < middleRow.getChildCount(); i++) {
            TextView childView = (TextView) middleRow.getChildAt(i);
            childView.setWidth(textViewWidth);
            childView.setHeight(textViewHeight);
        }
        for (int i = 0; i < bottomRow.getChildCount(); i++) {
            TextView childView = (TextView) bottomRow.getChildAt(i);
            childView.setWidth(textViewWidth);
            childView.setHeight(textViewHeight);
        }
        topRow.setPadding(textViewWidth / 2, VERTICAL_PADDING, textViewWidth / 2, VERTICAL_PADDING);
        middleRow.setPadding(textViewWidth, VERTICAL_PADDING, textViewWidth, VERTICAL_PADDING);
        bottomRow.setPadding(textViewWidth, VERTICAL_PADDING, textViewWidth, VERTICAL_PADDING);
    }
}
