package codes.blitz.game.bot;

import codes.blitz.game.generated.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Bot {
  Random random = new Random();
      Tile [][] tiles;

       public Tile getTile(Position pos) {
        return tiles[pos.x()][pos.y()];
    }

  public Bot() {
    System.out.println("Initializing your super mega duper bot");
  }

  /*
   * Here is where the magic happens, for now the moves are not very good. I bet you can do better ;)
   */
  public List<Action> getActions(TeamGameState gameMessage) {

    int[][] nutri = gameMessage.world().map().nutrientGrid();
    for (int i = 0; i < nutri[0].length; i++) {
      for (int j = 0; j < nutri[1].length; j++) {
        System.out.print(gameMessage.world().ownershipGrid()[i][j]);
      }
      System.out.println();
    }
    System.out.println(    );

    List<Action> actions = new ArrayList<>();

    TeamInfo myTeam = gameMessage.world().teamInfos().get(gameMessage.yourTeamId());
    GameMap map = gameMessage.world().map();

        int[][] nutrimentsGrid = gameMessage.world().map().nutrientGrid();
        tiles = new Tile[nutrimentsGrid[0].length][nutrimentsGrid.length];


        // CREATION DE LA MAP A UTILISER
        for (int i = 0; i < nutrimentsGrid[0].length; i++) {
            for (int j = 0; j < nutrimentsGrid[1].length; j++) {
                    Tile t = new Tile(nutrimentsGrid[i][j],gameMessage.world().biomassGrid()[i][j],
                            gameMessage.world().ownershipGrid()[i][j],false,new Position(i,j));
                  tiles[i][j] = t;
            }
        }
    

    if (myTeam.spawners().isEmpty()) {
      actions.add(new SporeCreateSpawnerAction(myTeam.spores().getFirst().id()));
      System.out.println("coucou");
    } else if (myTeam.spores().isEmpty()) {
      actions.add(new SpawnerProduceSporeAction(myTeam.spawners().getFirst().id(), 20));
      System.out.println("coucou1");
    } else {
      Spore spore = myTeam.spores().getFirst();
      Path path = go_to(tiles, getTile(spore.position()), getTile(new Position(5, 5)), gameMessage.yourTeamId());
      Action act = new SporeMoveAction(spore.id(), path.path().poll().getPosition());
      System.out.println("my current pos : " + spore.position());
      System.out.println(act);
      actions.add(act);
    }


    // You can clearly do better than the random actions above. Have fun!!
    return actions;
  }

  /**
   * Find the shortest path, taking count of the cost of each step
  */
  private Path go_to(Tile[][] map, Tile current_pos, Tile dest_pos, String TeamId) {

    return TilePathfinder.findShortestPath(
            map,
            current_pos,
            dest_pos,
            TeamId
    );

  }
}
