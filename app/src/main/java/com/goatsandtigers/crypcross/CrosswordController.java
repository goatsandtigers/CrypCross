package com.goatsandtigers.crypcross;

public interface CrosswordController {

    void onClueSelected(int clueNumber, Direction direction);

    void scrollToClue(int clueNumber, Direction direction);

    void hideKeyboard();

    void onLetterPressed(String letter);
}
