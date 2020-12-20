package com.goatsandtigers.crypcross;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CrosswordCluesLayout extends LinearLayout {

    private static final int ACROSS_CLUE_START_ID = 10000;
    private static final int DOWN_CLUE_START_ID = 20000;
    private final CrosswordController controller;
    private List<TextView> headerViews;
    private List<TextView> clueViews;

    public CrosswordCluesLayout(Context context, Crossword crossword, CrosswordController controller) {
        super(context);
        this.controller = controller;
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        buildClueViews(context, crossword);
    }

    private void buildClueViews(Context context, Crossword crossword) {
        headerViews = new ArrayList<TextView>();
        clueViews = new ArrayList<TextView>();
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(VERTICAL);
        ll.addView(buildHeader(context, "Across"));
        for (ClueAndExplanation clueDesc : crossword.getAcrossClues()) {
            ll.addView(buildClueView(context, clueDesc, Direction.RIGHT));
        }
        ll.addView(buildHeader(context, "\nDown"));
        for (ClueAndExplanation clueDesc : crossword.getDownClues()) {
            ll.addView(buildClueView(context, clueDesc, Direction.DOWN));
        }
        ScrollView scroll = new ScrollView(context);
        scroll.addView(ll);
        addView(scroll);
    }

    private TextView buildHeader(Context context, String text) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.getClueHeaderFontSize(getContext()));
        headerViews.add(tv);
        return tv;
    }

    private TextView buildClueView(Context context, final ClueAndExplanation clueDesc, final Direction direction) {
        TextView tv = new TextView(context);
        final int clueNumber = Integer.valueOf(clueDesc.clue.split("\\.")[0]);
        tv.setId(generateIdForClueView(clueNumber, direction));
        tv.setText(clueDesc.clue);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.getClueBodyFontSize(getContext()));
        tv.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                controller.onClueSelected(clueNumber, direction);
                return false;
            }
        });
        clueViews.add(tv);
        return tv;
    }

    private int generateIdForClueView(int clueNumber, Direction direction) {
        if (direction == Direction.RIGHT) {
            return ACROSS_CLUE_START_ID + clueNumber;
        } else {
            return DOWN_CLUE_START_ID + clueNumber;
        }
    }

    public void scrollToClue(int clueNumber, Direction direction) {
        if (!(getParent() instanceof ScrollView)) {
            return;
        }
        try {
            int id = generateIdForClueView(clueNumber, direction);
            View targetView = findViewById(id);
            ScrollView scroll = (ScrollView) getParent();
            scroll.scrollTo(0, (int) targetView.getY());
        } catch (Exception ignore) {
        }
    }

    public void updateFontSize() {
        float headerFontSize = FontSize.getClueBodyFontSize(getContext());
        for (TextView header : headerViews) {
            header.setTextSize(TypedValue.COMPLEX_UNIT_SP, headerFontSize);
        }
        float bodyFontSize = FontSize.getClueBodyFontSize(getContext());
        for (TextView body : clueViews) {
            body.setTextSize(TypedValue.COMPLEX_UNIT_SP, bodyFontSize);
        }

    }
}