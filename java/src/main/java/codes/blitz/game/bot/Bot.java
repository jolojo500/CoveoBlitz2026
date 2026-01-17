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
    int[][] nutri = gameMessage.world().map().nutrientGrid();
    for (int i = 0; i < nutri[0].length; i++) {
      for (int j = 0; j < nutri[1].length; j++) {
        System.out.print(nutri[i][j]);
      }
      System.out.println();
    }

    List<Action> actions = new ArrayList<>();

    TeamInfo myTeam = gameMessage.world().teamInfos().get(gameMessage.yourTeamId());
    List<TeamInfo> blabla = getEnnemies(gameMessage);

    System.out.println("-------------------");
    System.out.println(blabla);
    System.out.println("------------------");
    if (myTeam.spawners().isEmpty()) {
      actions.add(new SporeCreateSpawnerAction(myTeam.spores().getFirst().id()));
    } else if (myTeam.spores().isEmpty()) {
      actions.add(new SpawnerProduceSporeAction(myTeam.spawners().getFirst().id(), 20));
    } else {
      actions.add(
          new SporeMoveToAction(
              myTeam.spores().getFirst().id(),
              new Position(
                  random.nextInt(gameMessage.world().map().width()),
                  random.nextInt(gameMessage.world().map().height()))));
    }

    // You can clearly do better than the random actions above. Have fun!!
    return actions;
  }



  public List<TeamInfo> getEnnemies(TeamGameState gameMessage){
    List<TeamInfo> enemies = new ArrayList<>();

    for (String teamId : gameMessage.teamIds()) {
      if (!teamId.equals(gameMessage.yourTeamId())) {
        TeamInfo enemyTeam = gameMessage.world().teamInfos().get(teamId);

        if (enemyTeam != null &&
                !enemyTeam.spores().isEmpty() &&
                enemyTeam.isAlive()) {
          enemies.add(enemyTeam);
        }

      }
    }

    return enemies;
  }



}
