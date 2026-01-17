package codes.blitz.game.bot;

import codes.blitz.game.generated.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot {
  Random random = new Random();

  public Bot() {
    System.out.println("Initializing your super mega duper bot");
  }

  /*
   * Here is where the magic happens, for now the moves are not very good. I bet you can do better ;)
   */
  public List<Action> getActions(TeamGameState gameMessage) {
    List<Action> actions = new ArrayList<>();

    TeamInfo myTeam = gameMessage.world().teamInfos().get(gameMessage.yourTeamId());
    GameMap map = gameMessage.world().map();
    

    if (myTeam.spawners().isEmpty()) {
      actions.add(new SporeCreateSpawnerAction(myTeam.spores().getFirst().id()));
    } else if (myTeam.spores().isEmpty()) {
      actions.add(new SpawnerProduceSporeAction(myTeam.spawners().getFirst().id(), 20));
    } else {
      actions.add(go_to(gameMessage.world().biomassGrid(), gameMessage.world().ownershipGrid(), myTeam.spores().getFirst(), new Position(5, 5), gameMessage.yourTeamId()));
    }

    // You can clearly do better than the random actions above. Have fun!!
    return actions;
  }

  /**
   * Find the shortest path, taking count of the cost of each step
  */
  private Action go_to(int[][] biomas_grid, String[][] ownership_grid, Spore spore, Position dest_pos, String TeamId) {
    Position current_pos = spore.position();

    List<Position> path = Pathfinder.findShortestPath(
            biomas_grid,
            ownership_grid,
            current_pos,
            dest_pos,
            TeamId
    );

    System.out.println(path.get(1));

    return new SporeMoveToAction(spore.id(), dest_pos);    
  }
}
