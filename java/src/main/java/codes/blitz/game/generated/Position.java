package codes.blitz.game.generated;

import java.util.Objects;

/**
 * A two-dimensional point on the map.
 *
 * @param x X coordinate of the position. 0 is the column on the left.
 * @param y Y coordinate of the position. 0 is the top row.
 */
public record Position(int x, int y) {
     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
