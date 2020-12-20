package com.goatsandtigers.crypcross;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.TextView;

public class ScoreBoardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildGrid(orderData(parseCsv(loadData()))));
    }

    private List<ScoreBoardRow> orderData(List<ScoreBoardRow> parseCsv) {
        Collections.sort(parseCsv, new Comparator<ScoreBoardRow>() {

            @Override
            public int compare(ScoreBoardRow lhs, ScoreBoardRow rhs) {
                if (lhs.revealedLetters < rhs.revealedLetters) {
                    return -1;
                } else if (lhs.revealedLetters == rhs.revealedLetters) {
                    if (lhs.timeInSeconds < rhs.timeInSeconds) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return 1;
                }
            }
        });
        return parseCsv;
    }

    private GridLayout buildGrid(List<ScoreBoardRow> data) {
        GridLayout grid = new GridLayout(this) {
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                int fifthOfWidth = w / 5;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.getLayoutParams().width = fifthOfWidth;
                }
            }
        };
        grid.setPadding(10, 0, 0, 0);
        grid.setColumnCount(5);
        boolean isTablet = CrypticCrosswordActivity.isTablet(this);
        grid.addView(buildHeaderTextView(isTablet ? "Position " : "Pos. "));
        grid.addView(buildHeaderTextView("Name "));
        grid.addView(buildHeaderTextView(isTablet ? "Revealed Letters " : "Revealed \nLetters"));
        grid.addView(buildHeaderTextView("Time "));
        grid.addView(buildHeaderTextView("Country"));
        int pos = 1;
        for (ScoreBoardRow row : data) {
            grid.addView(buildTextView("" + pos + ". "));
            grid.addView(buildTextView(row.name + " "));
            grid.addView(buildTextView("" + row.revealedLetters + " "));
            grid.addView(buildTextView(formatTimeInSeconds(row.timeInSeconds) + " "));
            String countryEmoji = CountryCodeToEmojiConverter.getEmojiForCountryCode(row.country);
            grid.addView(buildTextView(countryEmoji));
            pos++;
        }
        return grid;
    }

    private String formatTimeInSeconds(int timeInSeconds) {
        int seconds = timeInSeconds % 60;
        int minutes = (timeInSeconds / 60) % 60;
        int hours = timeInSeconds / 3600;
        if (hours > 0) {
            return "" + hours + "h " + minutes + "m " + seconds + "s";
        } else {
            return "" + minutes + "m " + seconds + "s";
        }

    }

    private TextView buildHeaderTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTypeface(null, Typeface.BOLD);
        if (CrypticCrosswordActivity.isTablet(this)) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        }
        return tv;
    }

    private TextView buildTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        return tv;
    }

    private String loadData() {
        return getIntent().getStringExtra(CrypticCrosswordActivity.KEY_SCOREBOARD_DATA);
    }

    private class ScoreBoardRow {
        String name;
        int timeInSeconds;
        int revealedLetters;
        String country;
        String platform;

        public ScoreBoardRow(String name, int timeInSeconds, int revealedLetters, String country, String platform) {
            this.name = name;
            this.timeInSeconds = timeInSeconds;
            this.revealedLetters = revealedLetters;
            this.country = country;
            this.platform = platform;
        }

    }

    private List<ScoreBoardRow> parseCsv(String csv) {
        List<ScoreBoardRow> scoreBoardRows = new ArrayList<ScoreBoardRow>();
        String split[] = csv.split(",");
        for (int i = 0; i < split.length; i += 5) {
            String name = split[i];
            int timeInSeconds;
            try {
                timeInSeconds = Integer.valueOf(split[i + 1]);
            } catch (Exception e) {
                timeInSeconds = 0;
            }
            int revealedLetters;
            try {
                revealedLetters = Integer.valueOf(split[i + 2]);
            } catch (Exception e) {
                revealedLetters = 0;
            }
            String country = split[i + 3];
            String platform = split[i + 4];
            scoreBoardRows.add(new ScoreBoardRow(name, timeInSeconds, revealedLetters, country, platform));
        }
        return scoreBoardRows;
    }
}
