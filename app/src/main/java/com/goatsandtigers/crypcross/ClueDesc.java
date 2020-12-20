package com.goatsandtigers.crypcross;

import android.graphics.Point;

public class ClueDesc {

    public final Point startPos;
    public final Direction direction;
    public final int length;

    public ClueDesc(Point startPos, Direction direction, int length) {
        super();
        this.startPos = startPos;
        this.direction = direction;
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClueDesc)) {
            return false;
        }
        ClueDesc other = (ClueDesc) o;
        return other.startPos.x == startPos.x && other.startPos.y == startPos.y && other.direction == direction && other.length == length;
    }
}
