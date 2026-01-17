package codes.blitz.game.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import codes.blitz.game.generated.*;

class Node {
    Position position;
    int g; // coût depuis le départ
    int h; // estimation du coût restant
    int f; // f = g + h
    Node parent;

    public Node(Position position, int g, int h, Node parent) {
        this.position = position;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.parent = parent;
    }
}

public class Pathfinder {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

     public static List<Position> findShortestPath(
            int[][] biomas_grid,
            String[][] ownership_grid,
            Position start,
            Position end,
            String playerName
    ) {        
        // Vérification des limites
        if (start.x() < 0 || start.y() < 0 || end.x() < 0 || end.y() < 0 ||
            start.x() >= biomas_grid.length || start.y() >= biomas_grid[0].length ||
            end.x() >= biomas_grid.length || end.y() >= biomas_grid[0].length) {
            return Collections.emptyList();
        }

        // Liste ouverte (à explorer)
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
        // Liste fermée (déjà explorés)
        Set<Position> closedList = new HashSet<>();

        // Ajout du nœud de départ
        openList.add(new Node(start, 0, manhattanDistance(start, end), null));

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            // Si on a atteint la destination
            if (current.position.equals(end)) {
                return reconstructPath(current);
            }

            closedList.add(current.position);

            // Exploration des voisins
            for (int[] dir : DIRECTIONS) {
                Position neighborPos = new Position(current.position.x() + dir[0], current.position.y() + dir[1]);

                // Vérification des limites
                if (neighborPos.x() < 0 || neighborPos.y() < 0 ||
                    neighborPos.x() >= biomas_grid.length || neighborPos.y() >= biomas_grid[0].length ||
                    closedList.contains(neighborPos)) {
                    continue;
                }

                // Calcul du coût du déplacement
                int movementCost = ownership_grid[neighborPos.x()][neighborPos.y()].equals(playerName) ?
                    0 : biomas_grid[neighborPos.x()][neighborPos.y()];

                // Calcul de g, h, f
                int tentativeG = current.g + movementCost;
                int h = manhattanDistance(neighborPos, end);
                int f = tentativeG + h;

                // Vérification si le voisin est déjà dans la liste ouverte
                boolean isInOpenList = openList.stream()
                        .anyMatch(node -> node.position.equals(neighborPos) && node.g <= tentativeG);

                if (!isInOpenList) {
                    openList.add(new Node(neighborPos, tentativeG, h, current));
                }
            }
        }

        // Aucun chemin trouvé
        return Collections.emptyList();
    }

    // Reconstruction du chemin
    private static List<Position> reconstructPath(Node node) {
        List<Position> path = new ArrayList<>();
        while (node != null) {
            path.add(node.position);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    // Distance de Manhattan
    private static int manhattanDistance(Position a, Position b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
