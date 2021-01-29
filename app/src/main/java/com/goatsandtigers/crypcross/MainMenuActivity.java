package com.goatsandtigers.crypcross;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildRootView());
    }

    private View buildRootView() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setBackgroundResource(R.drawable.news_bg);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(createHeaderView());
        mainLayout.addView(createPuzzlePackView(1, 6));
        mainLayout.addView(createThemedPuzzlePackView());
        mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        scroll.addView(mainLayout);
        return scroll;
    }

    private TextView createHeaderView() {
        TextView tv = new TextView(this);
        tv.setText("Touch any puzzle below to display it. Scroll down to see all puzzles.");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        tv.setPadding(10, 10, 10, 10);
        return tv;
    }

    private LinearLayout createThemedPuzzlePackView() {
        LinearLayout puzzlePackLayout = new LinearLayout(this);
        puzzlePackLayout.setOrientation(LinearLayout.VERTICAL);
        puzzlePackLayout.setPadding(10, 10, 10, 10);
        puzzlePackLayout.addView(buildPuzzlePackHeader("Themed Puzzles"));
        puzzlePackLayout.addView(buildPuzzleOption("themed_chemistry1.txt", "Chemistry Puzzle 1"));
        puzzlePackLayout.addView(buildTextView("This puzzle was originally compiled for a Chemistry department's quiz.", 18));
        puzzlePackLayout.addView(buildPuzzleOption("themed_christmas1.txt", "Christmas Puzzle 1"));
        puzzlePackLayout.addView(buildPuzzleOption("themed_christmas2.txt", "Christmas Puzzle 2"));
        puzzlePackLayout.addView(buildPuzzleOption("themed_christmas3.txt", "Christmas Puzzle 3"));
        puzzlePackLayout.addView(buildPuzzleOption("themed_classics1.txt", "Classic Literature Puzzle 1"));
        puzzlePackLayout.addView(buildTextView("Hint: all answers are hidden in quotes from classic literature.", 18));
        return puzzlePackLayout;
    }

    private TextView buildTextView(String text, int fontSize) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        return tv;
    }

    private TextView buildPuzzlePackHeader(String headerText) {
        TextView tv = new TextView(this);
        tv.setText(headerText);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        return tv;
    }

    private LinearLayout createPuzzlePackView(int packNumber, int numPuzzles) {
        LinearLayout puzzlePackLayout = new LinearLayout(this);
        puzzlePackLayout.setOrientation(LinearLayout.VERTICAL);
        puzzlePackLayout.setPadding(10, 10, 10, 10);
        puzzlePackLayout.addView(buildPuzzlePackHeader(packNumber));
        for (int i = 1; i <= numPuzzles; i++) {
            puzzlePackLayout.addView(buildPuzzleOption(packNumber, i));
        }
        return puzzlePackLayout;
    }

    private TextView buildPuzzlePackHeader(int packNumber) {
        TextView tv = new TextView(this);
        tv.setText("Puzzle Pack " + packNumber);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        return tv;
    }

    private View buildPuzzleOption(final String filename, String puzzleTitle) {
        TextView tv = new TextView(this) {
            @Override
            public boolean onTouchEvent(android.view.MotionEvent event) {
                PreferenceManager.getDefaultSharedPreferences(MainMenuActivity.this).edit()
                        .putString(CrypticCrosswordActivity.KEY_PUZZLE_FILENAME, filename).commit();
                startActivity(new Intent(getBaseContext(), CrypticCrosswordActivity.class));
                return true;
            };
        };
        tv.setPadding(0, 4, 0, 4);
        tv.setText(puzzleTitle);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        if (CrypticCrosswordActivity.isPuzzleWithFilenameSolved(this, filename)) {
            LinearLayout ll = new LinearLayout(this);
            ll.addView(tv);
            ll.addView(buildTickView());
            return ll;
        } else {
            return tv;
        }
    }

    private View buildPuzzleOption(final int packNumber, final int puzzleNumber) {
        final String filename = packNumber + "_" + puzzleNumber + ".txt";
        TextView tv = new TextView(this) {
            @Override
            public boolean onTouchEvent(android.view.MotionEvent event) {
                PreferenceManager.getDefaultSharedPreferences(MainMenuActivity.this).edit()
                        .putString(CrypticCrosswordActivity.KEY_PUZZLE_FILENAME, filename).commit();
                startActivity(new Intent(getBaseContext(), CrypticCrosswordActivity.class));
                return true;
            };
        };
        tv.setPadding(0, 4, 0, 4);
        tv.setText("Puzzle " + puzzleNumber);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        if (CrypticCrosswordActivity.isPuzzleWithFilenameSolved(this, filename)) {
            LinearLayout ll = new LinearLayout(this);
            ll.addView(tv);
            ll.addView(buildTickView());
            return ll;
        } else {
            return tv;
        }
    }

    private ImageView buildTickView() {
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.tick);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        iv.setLayoutParams(layoutParams);
        iv.setPadding(8, 0, 0, 0);
        return iv;
    }
}
