package codes.blitz.game.bot;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import codes.blitz.game.generated.*;

public class TilePathfinder {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Haut, Bas, Gauche, Droite

    public static Path findShortestPath(
            Tile[][] grid,
            Tile startTile,
            Tile endTile,
            String playerName
    ) {
        Path path = new Path();

        // Vérification des limites
        if (startTile == null || endTile == null ||
            startTile.getPosition().x() < 0 || startTile.getPosition().y() < 0 ||
            endTile.getPosition().x() < 0 || endTile.getPosition().y() < 0 ||
            startTile.getPosition().x() >= grid.length || startTile.getPosition().y() >= grid[0].length ||
            endTile.getPosition().x() >= grid.length || endTile.getPosition().y() >= grid[0].length) {
            return path;
        }

        // Liste ouverte (à explorer)
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
        // Liste fermée (déjà explorés)
        Set<Tile> closedList = new HashSet<>();

        // Ajout du nœud de départ
        openList.add(new Node(startTile, 0, manhattanDistance(startTile, endTile), null));

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            // Si on a atteint la destination
            if (current.tile.equals(endTile)) {
                Queue<Tile> tilePath = reconstructPath(current);
                path.path().addAll(tilePath);
                return path;
            }

            closedList.add(current.tile);

            // Exploration des voisins
            for (int[] dir : DIRECTIONS) {
                Position neighborPos = new Position(current.tile.getPosition().x() + dir[0], current.tile.getPosition().y() + dir[1]);

                // Vérification des limites
                if (neighborPos.x() < 0 || neighborPos.y() < 0 ||
                    neighborPos.x() >= grid.length || neighborPos.y() >= grid[0].length) {
                    continue;
                }

                Tile neighborTile = grid[neighborPos.x()][neighborPos.y()];

                // Vérification si déjà exploré
                if (closedList.contains(neighborTile)) {
                    continue;
                }

                // Calcul du coût du déplacement
                int movementCost = neighborTile.getControllingTeam().equals(playerName) ?
                    0 : neighborTile.getBiomassValue();

                // Calcul de g, h, f
                int tentativeG = current.g + movementCost;
                int h = manhattanDistance(neighborTile, endTile);
                int f = tentativeG + h;

                // Vérification si le voisin est déjà dans la liste ouverte
                boolean isInOpenList = openList.stream()
                        .anyMatch(node -> node.tile.equals(neighborTile) && node.g <= tentativeG);

                if (!isInOpenList) {
                    openList.add(new Node(neighborTile, tentativeG, h, current));
                }
            }
        }

        // Aucun chemin trouvé
        return path;
    }

    // Reconstruction du chemin sous forme de Queue<Tile>
    private static Queue<Tile> reconstructPath(Node node) {
        Queue<Tile> path = new ArrayDeque<>();
        while (node != null) {
            path.add(node.tile); // Ajoute au début pour inverser l'ordre
            node = node.parent;
        }

        Stack<Tile> stack = new Stack<>();
        
        // Step 1: Transfer all elements from queue to stack
        while (!path.isEmpty()) {
            stack.push(path.poll());
        }
        
        // Step 2: Transfer all elements from stack back to queue
        Queue<Tile> reversedQueue = new LinkedList<>();
        System.out.println("deleted : " + stack.pop().getPosition());
        while (!stack.isEmpty()) {
            Tile t = stack.pop();
            System.out.println(t.getPosition());
            reversedQueue.offer(t);
        }
        
        return reversedQueue;
    }

    // Distance de Manhattan
    private static int manhattanDistance(Tile a, Tile b) {
        return Math.abs(a.getPosition().x() - b.getPosition().x()) +
               Math.abs(a.getPosition().y() - b.getPosition().y());
    }

    // Classe interne pour les nœuds de l'algorithme A*
    private static class Node {
        Tile tile;
        int g; // coût depuis le départ
        int h; // estimation du coût restant
        int f; // f = g + h
        Node parent;

        public Node(Tile tile, int g, int h, Node parent) {
            this.tile = tile;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }
    }
}
