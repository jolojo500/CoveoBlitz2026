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


  private int manhattanDistance(Position a, Position b) {
    return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y()); //TODO perhaps replace using pathfinding or wtv later
  }

  private Position getNextPositionTowards(Position from, Position to) {
    // Se déplacer d'une case vers la cible (cardinale seulement)
    int dx = Integer.compare(to.x(), from.x()); //TODO perhaps replace using pathfinding or wtv later
    int dy = Integer.compare(to.y(), from.y());

    // Prioriser x ou y (tu peux ajuster la stratégie)
    if (dx != 0) {
      return new Position(from.x() + dx, from.y());
    } else if (dy != 0) {
      return new Position(from.x(), from.y() + dy);
    }

    return from; // Déjà sur la cible
  }

  private int calculateBiomassLost(Position from, Position to, TeamInfo myTeam, TeamGameState gameMessage) {
    // Vérifier si la case de destination est déjà claimée par nous
    String owner = gameMessage.world().ownershipGrid()[to.y()][to.x()];

    // Si la case appartient déjà à notre équipe, pas de perte
    if (owner != null && owner.equals(myTeam.teamId())) {
      return 0;
    }

    // Sinon, on perd 1 biomasse (on laisse une trace)
    return 1;
  }

  public List<Action> getActions(TeamGameState gameMessage) {
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

  public List<Action> charge(List<TeamInfo> ennemies, TeamInfo myTeam, TeamGameState gameMessage){
    //attaque spore, quelle spore est nearest et attackable post deplacemenet
    List<Action> actions = new ArrayList<>();
    //Position cible;  si je return, func name est charge tho so

    for (Spore mySpore : myTeam.spores()) {
      // Une spore avec 1 biomasse ne peut pas bouger
      if (mySpore.biomass() <= 1) {
        continue;
      }

      Spore nearestEnemy = null;
      int minDistance = Integer.MAX_VALUE;

      for (TeamInfo ennemy : ennemies) {
        for (Spore enemySpore : ennemy.spores()) {
          int distance = manhattanDistance(mySpore.position(), enemySpore.position());

          if (distance < minDistance) {
            minDistance = distance;
            nearestEnemy = enemySpore;
          }
        }
      }

      if(nearestEnemy != null){
        Position nextPos = getNextPositionTowards(mySpore.position(), nearestEnemy.position());
        int biomassLost = calculateBiomassLost(mySpore.position(), nextPos, myTeam,gameMessage);
        int myBiomassAfterMove= mySpore.biomass() - biomassLost;

        if(myBiomassAfterMove > nearestEnemy.biomass()){
          actions.add(new SporeMoveToAction(mySpore.id(), nextPos));
        }
      }
    }
    return actions;
  }




}
