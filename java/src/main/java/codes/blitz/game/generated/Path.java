package codes.blitz.game.generated;

import java.util.ArrayDeque;
import java.util.Queue;

public class Path {
    private Queue<Tile> path;

    public Path() {
        this.path = new ArrayDeque<>();
    }

    public Queue<Tile> path() {
        return path;
    }
    public int get_cost(String teamName) {
        int cost = 0;
        for(Tile tile : path) {
            if(teamName != tile.getControllingTeam()) {
                cost += 1 + tile.getBiomassValue();
            }
        }
        return cost;
    }
}
