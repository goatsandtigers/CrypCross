package com.goatsandtigers.crypcross;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.GridLayout;

public class CrosswordView extends GridLayout {

    private Map<Point, CrosswordCellView> cells = new HashMap<Point, CrosswordCellView>();
    private ClueDesc selectedClue;
    private final Crossword crossword;
    private final CrosswordController controller;
    private Map<Integer, Point> clueStartPoints;
    private CrosswordCellView selectedCell;
    private Point selectedCellPoint;

    public CrosswordView(Context context, Crossword crossword, CrosswordController controller) {
        super(context);
        this.crossword = crossword;
        this.controller = controller;
        setColumnCount(Crossword.CROSSWORD_WIDTH);
        setRowCount(Crossword.CROSSWORD_HEIGHT);
        createCells(context);
    }

    private void createCells(Context context) {
        clueStartPoints = new HashMap<Integer, Point>();
        int number = 1;
        for (int i = 0; i < Crossword.CROSSWORD_WIDTH; i++) {
            for (int j = 0; j < Crossword.CROSSWORD_HEIGHT; j++) {
                CrosswordCellView tv = createCellView(context, i, j);
                if (crossword.isStartOfClue(i, j)) {
                    tv.showClueNumber(getContext(), number);
                    clueStartPoints.put(number, new Point(i, j));
                    number++;
                }
                addView(tv);
                cells.put(new Point(i, j), tv);
            }
        }
    }

    private CrosswordCellView createCellView(Context context, int x, int y) {
        char answer = crossword.getAnswer(x, y);
        CrosswordCellView tv = new CrosswordCellView(context, crossword, crossword.generateCellId(x, y));
        if (crossword.isBlackSquare(x, y)) {
            tv.setBlack();
        } else {
            // tv.setText("" + answer);
        }
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // resizeAllCells((right - left) / Crossword.CROSSWORD_WIDTH, (bottom - top) / Crossword.CROSSWORD_HEIGHT);
        int cellWidth = (right - left - getPaddingLeft() - getPaddingRight()) / Crossword.CROSSWORD_WIDTH;
        resizeAllCells(cellWidth, cellWidth);
    }

    private void resizeAllCells(int width, int height) {
        for (CrosswordCellView view : cells.values()) {
            view.setWidth(width);
            view.setHeight(height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int cellWidth = getWidth() / Crossword.CROSSWORD_WIDTH;
        int cellHeight = getHeight() / Crossword.CROSSWORD_HEIGHT;
        int touchedCellX = (int) (event.getX() / cellWidth);
        int touchedCellY = (int) (event.getY() / cellHeight);
        List<ClueDesc> clues = crossword.getCluesIntersectingPos(touchedCellX, touchedCellY);
        if (clues.isEmpty()) {
            selectedClue = null;
            unhighlightAllSquares();
        } else if (clues.size() == 1) {
            selectedClue = clues.get(0);
            highlightClue(selectedClue);
            int clueNumber = getClueWithStartPos(selectedClue.startPos);
            controller.onClueSelected(clueNumber, selectedClue.direction);
            controller.scrollToClue(clueNumber, selectedClue.direction);
        } else {
            if (selectedClue != null && selectedClue.equals(clues.get(0))) {
                selectedClue = clues.get(1);
            } else {
                selectedClue = clues.get(0);
            }
            highlightClue(selectedClue);
            int clueNumber = getClueWithStartPos(selectedClue.startPos);
            controller.onClueSelected(clueNumber, selectedClue.direction);
            controller.scrollToClue(clueNumber, selectedClue.direction);
        }
        selectLetter(touchedCellY, touchedCellX);
        return super.onTouchEvent(event);
    }

    private void selectLetter(int touchedCellX, int touchedCellY) {
        selectedCellPoint = new Point(touchedCellX, touchedCellY);
        if (selectedCell != null) {
            selectedCell.unselect();
        }
        selectedCell = cells.get(selectedCellPoint);
        selectedCell.select();
    }

    public void unhighlightAllSquares() {
        for (CrosswordCellView view : cells.values()) {
            view.unhighlight();
        }
        controller.hideKeyboard();
    }

    private void highlightClue(ClueDesc clueToHighlight) {
        unhighlightAllSquares();
        for (int i = 0; i < clueToHighlight.length; i++) {
            int x = clueToHighlight.startPos.x + (i * clueToHighlight.direction.dx);
            int y = clueToHighlight.startPos.y + (i * clueToHighlight.direction.dy);
            // TODO investigate why x and y need to be the wrong way round.
            CrosswordCellView view = cells.get(new Point(y, x));
            view.highlight();
        }
    }

    private int getClueWithStartPos(Point startPos) {
        for (Integer key : clueStartPoints.keySet()) {
            Point value = clueStartPoints.get(key);
            // TODO investigate why x and y need to be the wrong way round.
            if (startPos.x == value.y && startPos.y == value.x) {
                return key;
            }
        }
        return 1;
    }

    public void highlightClue(int clueNumber, Direction direction) {
        Point cluePos = clueStartPoints.get(clueNumber);
        if (cluePos == null) {
            return;
        }
        List<ClueDesc> clues = crossword.getCluesIntersectingPos(cluePos.y, cluePos.x);
        selectedClue = null;
        if (clues.size() == 1) {
            selectedClue = clues.get(0); // TODO do we need this?
        } else {
            for (ClueDesc clue : clues) {
                if (clue.direction == direction) {
                    selectedClue = clue;
                    break;
                }
            }
        }
        if (selectedClue != null) {
            highlightClue(selectedClue);
        }
        selectLetter(cluePos.x, cluePos.y);
    }

    public boolean isAnySquareHighlighted() {
        return selectedCell != null;
    }

    public void onLetterPressed(String letter) {
        if (KeyboardView.BACKSPACE.equals(letter)) {
            selectedCell.setText("");
            selectPreviousCellForWord();
        } else {
            selectedCell.setText(letter);
            selectNextCellForWord();
        }
    }

    private void selectNextCellForWord() {
        if (selectedClue.direction == Direction.RIGHT) {
            if (selectedCellPoint.y < crossword.getWidth() - 1 && !crossword.isBlackSquare(selectedCellPoint.x, selectedCellPoint.y + 1)) {
                selectLetter(selectedCellPoint.x, selectedCellPoint.y + 1);
            }
        } else {
            if (selectedCellPoint.x < crossword.getHeight() - 1 && !crossword.isBlackSquare(selectedCellPoint.x + 1, selectedCellPoint.y)) {
                selectLetter(selectedCellPoint.x + 1, selectedCellPoint.y);
            }
        }
    }

    private void selectPreviousCellForWord() {
        if (selectedClue.direction == Direction.RIGHT) {
            if (selectedCellPoint.y > 0 && !crossword.isBlackSquare(selectedCellPoint.x, selectedCellPoint.y - 1)) {
                selectLetter(selectedCellPoint.x, selectedCellPoint.y - 1);
            }
        } else {
            if (selectedCellPoint.x > 0 && !crossword.isBlackSquare(selectedCellPoint.x - 1, selectedCellPoint.y)) {
                selectLetter(selectedCellPoint.x - 1, selectedCellPoint.y);
            }
        }
    }

    public void revealSelectedLetter() {
        if (selectedCell == null) {
            String msg = "No cell selected. Please select a crossword cell before choosing \"Reveal Letter\".";
            new AlertDialog.Builder(getContext()).setTitle(R.string.app_name).setMessage(msg).setPositiveButton(android.R.string.yes, null)
                    .setIcon(R.drawable.ic_launcher).show();
        } else {
            selectedCell.revealLetter();
        }
    }

    public void revealSelectedWord() {
        for (CrosswordCellView cellInWord : listCellsForClue(selectedClue)) {
            cellInWord.revealLetter();
        }
    }

    private List<CrosswordCellView> listCellsForClue(ClueDesc clue) {
        if (clue == null) {
            return Collections.emptyList();
        } else {
            List<CrosswordCellView> cellsForClue = new ArrayList<CrosswordCellView>();
            for (int i = 0; i < clue.length; i++) {
                int x = clue.startPos.x + (i * clue.direction.dx);
                int y = clue.startPos.y + (i * clue.direction.dy);
                // TODO investigate why x and y have to be the wrong way around.
                Point cellPoint = new Point(y, x);
                CrosswordCellView cell = cells.get(cellPoint);
                cellsForClue.add(cell);
            }
            return cellsForClue;
        }
    }

    public boolean isSolved() {
        for (CrosswordCellView cell : cells.values()) {
            if (!cell.containsCorrectAnswer()) {
                return false;
            }
        }
        return true;
    }

    public int getNumRevealedLetters() {
        int numRevealedLetters = 0;
        for (CrosswordCellView cell : cells.values()) {
            if (cell.isRevealed()) {
                numRevealedLetters++;
            }
        }
        return numRevealedLetters;
    }
}
