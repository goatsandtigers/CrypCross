package com.goatsandtigers.crypcross;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;

public class Crossword {

    public static final int CROSSWORD_WIDTH = 11;
    public static final int CROSSWORD_HEIGHT = 11;

    private static final char BLACK_CELL = '#';

    private char[][] solution = new char[CROSSWORD_WIDTH][CROSSWORD_HEIGHT];
    private List<ClueAndExplanation> acrossClues = new ArrayList<ClueAndExplanation>();
    private List<ClueAndExplanation> downClues = new ArrayList<ClueAndExplanation>();
    private final String filename;

    public Crossword(Context context, String filename) {
        this.filename = filename;
        loadFile(context, filename);
    }

    @SuppressLint("DefaultLocale")
    private void loadFile(Context context, String filename) {
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            for (int lineNumber = 0; lineNumber < CROSSWORD_HEIGHT; lineNumber++) {
                line = reader.readLine().toUpperCase();
                for (int i = 0; i < CROSSWORD_WIDTH; i++) {
                    solution[lineNumber][i] = line.charAt(i);
                }
            }
            // Blank line then "Across" line
            reader.readLine();
            reader.readLine();
            // Across clues
            for (;;) {
                String clue = reader.readLine();
                if (clue.trim().length() < 1) {
                    break;
                }
                String explanation = reader.readLine();
                acrossClues.add(new ClueAndExplanation(clue, explanation));
            }
            // Blank line then "Down" line
            reader.readLine();
            // Down clues
            for (;;) {
                String clue = reader.readLine();
                if (clue == null) {
                    break;
                }
                String explanation = reader.readLine();
                downClues.add(new ClueAndExplanation(clue, explanation));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char getAnswer(int x, int y) {
        return solution[x][y];
    }

    public char getAnswer(String id) {
        String split[] = id.split("#");
        int x = Integer.valueOf(split[1]);
        int y = Integer.valueOf(split[2]);
        return solution[x][y];
    }

    public String generateCellId(int x, int y) {
        return filename + "#" + x + "#" + y;
    }

    public boolean isBlackSquare(int x, int y) {
        return solution[x][y] == BLACK_CELL;
    }

    public boolean isStartOfClue(int x, int y) {
        if (isBlackSquare(x, y)) {
            return false;
        }
        if (y == 0 && !isBlackSquare(x, 1)) {
            return true;
        }
        if (x == 0 && !isBlackSquare(1, y)) {
            return true;
        }
        if (x > 0 && x < CROSSWORD_WIDTH - 1 && isBlackSquare(x - 1, y) && !isBlackSquare(x + 1, y)) {
            return true;
        }
        if (y > 0 && y < CROSSWORD_HEIGHT - 1 && isBlackSquare(x, y - 1) && !isBlackSquare(x, y + 1)) {
            return true;
        }
        return false;
    }

    public List<ClueDesc> getClueDescs() {
        List<ClueDesc> clueDescs = new ArrayList<ClueDesc>();
        for (int i = 0; i < CROSSWORD_WIDTH; i++) {
            for (int j = 0; j < CROSSWORD_HEIGHT; j++) {
                if (isBlackSquare(i, j)) {
                    continue;
                }
                // Horizontal clues
                if (i == 0 || isBlackSquare(i - 1, j)) {
                    int length;
                    for (length = 1; i + length < CROSSWORD_WIDTH; length++) {
                        if (isBlackSquare(i + length, j)) {
                            break;
                        }
                    }
                    if (length > 1) {
                        clueDescs.add(new ClueDesc(new Point(j, i), Direction.DOWN, length));
                    }
                }
                // Vertical clues
                if (j == 0 || isBlackSquare(i, j - 1)) {
                    int length;
                    for (length = 1; j + length < CROSSWORD_HEIGHT; length++) {
                        if (isBlackSquare(i, j + length)) {
                            break;
                        }
                    }
                    if (length > 1) {
                        clueDescs.add(new ClueDesc(new Point(j, i), Direction.RIGHT, length));
                    }
                }
            }
        }

        return Collections.unmodifiableList(clueDescs);
    }

    public List<ClueAndExplanation> getAcrossClues() {
        return acrossClues;
    }

    public List<ClueAndExplanation> getDownClues() {
        return downClues;
    }

    public List<ClueDesc> getCluesIntersectingPos(int x, int y) {
        List<ClueDesc> clues = new ArrayList<ClueDesc>();
        if (!isBlackSquare(y, x)) {
            for (ClueDesc clueDesc : getClueDescs()) {
                if (clueDesc.direction == Direction.RIGHT) {
                    if (clueDesc.startPos.y == y && clueDesc.startPos.x <= x && clueDesc.startPos.x + clueDesc.length >= x) {
                        clues.add(clueDesc);
                    }
                } else {
                    if (clueDesc.startPos.x == x && clueDesc.startPos.y <= y && clueDesc.startPos.y + clueDesc.length >= y) {
                        clues.add(clueDesc);
                    }
                }
            }
        }
        return clues;
    }

    public int getWidth() {
        return CROSSWORD_WIDTH;
    }

    public int getHeight() {
        return CROSSWORD_HEIGHT;
    }
}
