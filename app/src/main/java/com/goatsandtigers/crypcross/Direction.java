package com.goatsandtigers.crypcross;
public enum Direction {

    RIGHT(1, 0), DOWN(0, 1);

    public final int dx, dy;

    private Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
