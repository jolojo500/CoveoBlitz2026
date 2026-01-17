package codes.blitz.game.bot;

import codes.blitz.game.generated.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;


public class Bot {

  private static int manhattanDistance(Position a, Position b) {
    return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y()); //TODO perhaps replace using pathfinding or wtv later
  }

  private static Position getNextPositionTowards(Position from, Position to) {
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

  private static int calculateBiomassLost(Position from, Position to, TeamInfo myTeam, TeamGameState gameMessage) {
    // Vérifier si la case de destination est déjà claimée par nous
    String owner = gameMessage.world().ownershipGrid()[to.y()][to.x()];

    // Si la case appartient déjà à notre équipe, pas de perte
    if (owner != null && owner.equals(myTeam.teamId())) {
      return 0;
    }

    // Sinon, on perd 1 biomasse (on laisse une trace)
    return 1;
  }



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

    List<Action> actions = new ArrayList<>();

    TeamInfo myTeam = gameMessage.world().teamInfos().get(gameMessage.yourTeamId());
    GameMap map = gameMessage.world().map();
    List<TeamInfo> blabla = getEnnemies(gameMessage);

    System.out.println("-------------------");
    System.out.println(blabla);
    System.out.println("------------------");

        int[][] nutrimentsGrid = gameMessage.world().map().nutrientGrid();
        tiles = new Tile[nutrimentsGrid[0].length][nutrimentsGrid.length];


        // CREATION DE LA MAP A UTILISER
        for (int i = 0; i < nutrimentsGrid[0].length; i++) {
            for (int j = 0; j < nutrimentsGrid[1].length; j++) {
                    //Tile t = new Tile(nutrimentsGrid[i][j],gameMessage.world().biomassGrid()[i][j],
                           // gameMessage.world().ownershipGrid()[i][j],false,new Position(i,j));
                  //tiles[i][j] = t;
            }
        }


    if (myTeam.spawners().isEmpty()) {
      actions.add(new SporeCreateSpawnerAction(myTeam.spores().getFirst().id()));
      System.out.println("coucou");
    } else if (myTeam.spores().isEmpty()) {
      actions.add(new SpawnerProduceSporeAction(myTeam.spawners().getFirst().id(), 20));
      System.out.println("coucou1");
    } else {
      /*Spore spore = myTeam.spores().getFirst();
      Path path = go_to(tiles, getTile(spore.position()), getTile(new Position(5, 5)), gameMessage.yourTeamId());
      Action act = new SporeMoveAction(spore.id(), path.path().poll().getPosition());
      System.out.println("my current pos : " + spore.position());
      System.out.println(act);
      actions.add(act);
    */

      /*actions.add(
          new SporeMoveToAction(
              myTeam.spores().getFirst().id(),
              new Position(
                  random.nextInt(gameMessage.world().map().width()),
                  random.nextInt(gameMessage.world().map().height()))));
    */
      List<Action> chargeActions = charge(blabla, myTeam, gameMessage);
      actions.addAll(chargeActions);

      // 2. Récupérer quelles spores ont déjà une action
      List<String> sporesWithActions = new ArrayList<>();
      for (Action action : actions) {
        if (action instanceof SporeMoveToAction) {
          sporesWithActions.add(((SporeMoveToAction) action).sporeId());
        }
      }

      // 3. SEULEMENT si on a PEU de nutrients (<100), merger les spores faibles
      if (myTeam.nutrients() < 100) {
        mergeWeakSpores(myTeam, gameMessage, actions, sporesWithActions);

        // Mettre à jour la liste après merge
        for (Action action : actions) {
          if (action instanceof SporeMoveToAction) {
            String id = ((SporeMoveToAction) action).sporeId();
            if (!sporesWithActions.contains(id)) {
              sporesWithActions.add(id);
            }
          }
        }
      }

      // 4. Les autres bougent random
      for (Spore spore : myTeam.spores()) {
        if (!sporesWithActions.contains(spore.id()) && spore.biomass() > 1) {
          actions.add(
                  new SporeMoveToAction(
                          spore.id(),
                          new Position(
                                  random.nextInt(gameMessage.world().map().width()),
                                  random.nextInt(gameMessage.world().map().height()))));
        }
      }
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

    boolean canAttack= false;
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
          canAttack = true;
        }
      }
    }



      if(!canAttack && !myTeam.spawners().isEmpty()){ //&& weakestEnemyBiomass!=Integer.MAX_VALUE){
        //moved here because lol why would I need it to be elsewhere
        int weakestEnemyBiomass = Integer.MAX_VALUE;
        for (TeamInfo ennemy : ennemies) {
          for (Spore enemySpore : ennemy.spores()) {
            if (enemySpore.biomass() < weakestEnemyBiomass) {
              weakestEnemyBiomass = enemySpore.biomass();
            }
          }
        }
        if(weakestEnemyBiomass != Integer.MAX_VALUE) {
          int availableNutrients = myTeam.nutrients();
          int targetBiomass = weakestEnemyBiomass + 5;//placeholder val

          if(availableNutrients > targetBiomass *2) { //another placeholder val
            targetBiomass = Math.min(availableNutrients/2, weakestEnemyBiomass + 15); //on utilise plus de nutrients sin on est riche type shit pour pas waste endgame
          }

          if(availableNutrients >= targetBiomass){
            actions.add(new SpawnerProduceSporeAction(
                    myTeam.spawners().getFirst().id(), //again placeholder, could be better byu distance search
                    targetBiomass
            ));
          }
        }
      }

    return actions;
  }

  private void mergeWeakSpores(TeamInfo myTeam, TeamGameState gameMessage,
                               List<Action> actions, List<String> sporesAttacking) {
    // Trouver la plus grosse spore ou une spore qui attaque
    Spore targetSpore = null;
    int maxBiomass = 0;

    // Prioriser les spores qui attaquent
    for (String attackingId : sporesAttacking) {
      for (Spore spore : myTeam.spores()) {
        if (spore.id().equals(attackingId) && spore.biomass() > maxBiomass) {
          maxBiomass = spore.biomass();
          targetSpore = spore;
        }
      }
    }

    // Sinon, prendre la plus grosse spore
    if (targetSpore == null) {
      for (Spore spore : myTeam.spores()) {
        if (spore.biomass() > maxBiomass) {
          maxBiomass = spore.biomass();
          targetSpore = spore;
        }
      }
    }

    if (targetSpore == null) return;

    // Les spores faibles (< 50% de la plus grosse) se dirigent vers elle
    int threshold = maxBiomass / 2;

    for (Spore weakSpore : myTeam.spores()) {
      // Skip si déjà en action ou si c'est la target
      if (sporesAttacking.contains(weakSpore.id()) ||
              weakSpore.id().equals(targetSpore.id()) ||
              weakSpore.biomass() <= 1) {
        continue;
      }

      // Si la spore est faible, elle rejoint la plus grosse
      if (weakSpore.biomass() < threshold) {
        Position nextPos = getNextPositionTowards(weakSpore.position(), targetSpore.position());
        actions.add(new SporeMoveToAction(weakSpore.id(), nextPos));
        System.out.println("Spore " + weakSpore.id() + " (biomass=" + weakSpore.biomass() +
                ") merging towards " + targetSpore.id() + " (biomass=" + targetSpore.biomass() + ")");
      }
    }
  }



}
