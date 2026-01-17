package codes.blitz.game.bot;

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int[] direction = {0, 0};

    private Direction(int i, int j) {
        direction[0] = i;
        direction[1] = j;
    }

    public int[] getDirection() {
        return direction;
    }
}
