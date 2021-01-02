package com.goatsandtigers.crypcross;

import android.view.Menu;
import android.view.MenuItem;

public interface CrosswordController {

    void onClueSelected(int clueNumber, Direction direction);

    void scrollToClue(int clueNumber, Direction direction);

    void hideKeyboard();

    void onLetterPressed(String letter);

    boolean onCreateOptionsMenu(Menu menu);

    boolean onOptionsItemSelected(MenuItem item);
}
