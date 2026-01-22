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

  private Spawner findClosestSpawner(TeamInfo myTeam, Position targetPos){
    Spawner closest = null;
    int minDist = Integer.MAX_VALUE;

    for (Spawner spawner : myTeam.spawners()) {
      int dist = manhattanDistance(spawner.position(), targetPos);
      if (dist < minDist) {
        minDist = dist;
        closest = spawner;
      }
    }

    return closest;
  }
  private Position findBestNutrientTile(Position from, TeamInfo myTeam, TeamGameState gameMessage) {
    Position bestPos = null;
    double bestScore = -999999;

    int[][] nutrientGrid = gameMessage.world().map().nutrientGrid();
    String[][] ownershipGrid = gameMessage.world().ownershipGrid();

    for (int y = 0; y < nutrientGrid.length; y++) {
      for (int x = 0; x < nutrientGrid[y].length; x++) {
        String owner = ownershipGrid[y][x];

        // Skip si déjà à nous
        if (owner != null && owner.equals(myTeam.teamId())) {
          continue;
        }

        int nutrients = nutrientGrid[y][x];

        // Accepter n'importe quelle tile PAS à nous (même avec 0 nutrients si besoin)
        int distance = manhattanDistance(from, new Position(x, y));

        // Score: prioriser les nutrients mais accepter toute tile non-owned
        double score = (double) (nutrients + 1) / (distance + 1);

        if (score > bestScore) {
          bestScore = score;
          bestPos = new Position(x, y);
        }
      }
    }

    return bestPos;
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
      //#0. lol
      List<Action> spawnerActions = manageSpawners(myTeam,gameMessage,blabla);
      actions.addAll(spawnerActions);

      List<Action> chargeActions = charge(tiles, blabla, myTeam, gameMessage);
      actions.addAll(chargeActions);

      // 2. Récupérer quelles spores ont déjà une action
      List<String> sporesWithActions = new ArrayList<>();
      for (Action action : actions) {
        if (action instanceof SporeMoveToAction || action instanceof  SporeCreateSpawnerAction) {
          if(action instanceof SporeMoveToAction){ //
            sporesWithActions.add(((SporeMoveToAction) action).sporeId());
          } else if (action instanceof SporeCreateSpawnerAction){
            sporesWithActions.add(((SporeCreateSpawnerAction) action).sporeId());
          }
        }
      }

      // 3. SEULEMENT si on a PEU de nutrients (<100), merger les spores faibles
      if (myTeam.nutrients() < 20) {
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

      // 4. Les autres bougent random
      for (Spore spore : myTeam.spores()) {
        if (!sporesWithActions.contains(spore.id()) && spore.biomass() > 1) {

          // Trouver la tile avec le plus de nutrients PAS à nous
          Position bestPos = null;
          int maxNutrients = -1;

          int[][] nutrientGrid = gameMessage.world().map().nutrientGrid();
          String[][] ownershipGrid = gameMessage.world().ownershipGrid();

          for (int y = 0; y < nutrientGrid.length; y++) {
            for (int x = 0; x < nutrientGrid[y].length; x++) {
              // Skip si c'est à nous
              String owner = ownershipGrid[y][x];
              if (owner != null && owner.equals(myTeam.teamId())) {
                continue;
              }

              if (nutrientGrid[y][x] > maxNutrients) {
                maxNutrients = nutrientGrid[y][x];
                bestPos = new Position(x, y);
              }
            }
          }

          if (bestPos != null) {
            Position nextPos = getNextPositionTowards(spore.position(), bestPos);
            actions.add(new SporeMoveToAction(spore.id(), nextPos));
          } else {
            // Fallback random si tout est à nous
            actions.add(
                    new SporeMoveToAction(
                            spore.id(),
                            new Position(
                                    random.nextInt(gameMessage.world().map().width()),
                                    random.nextInt(gameMessage.world().map().height()))));
          }
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


  public List<Action> charge(Tile[][]map, List<TeamInfo> ennemies, TeamInfo myTeam, TeamGameState gameMessage){
    List<Action> actions = new ArrayList<>();
    boolean canAttack = false;

    for (Spore mySpore : myTeam.spores()) {
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
        Position nextPos = null;
        int myBiomassAfterMove = mySpore.biomass() - 10;

        try {
          if (myBiomassAfterMove > nearestEnemy.biomass()) {
            Path path = TilePathfinder.findShortestPath(map, getTile(mySpore.position()), getTile(nearestEnemy.position()), myTeam.teamId());
            if (path != null && path.path() != null && !path.path().isEmpty()) {
              path.path().poll();

              if (!path.path().isEmpty()) {
                Tile nextTile = path.path().poll();
                if (nextTile != null) {
                  nextPos = nextTile.getPosition();
                }
              }
            }
          }
        }catch (Exception e){
          System.out.println("Pathfinding failed, using simple movement");
          nextPos = null;
        }

        if (nextPos == null) {
          nextPos = getNextPositionTowards(mySpore.position(), nearestEnemy.position());
          int biomassLost = calculateBiomassLost(mySpore.position(), nextPos, myTeam, gameMessage);
          myBiomassAfterMove = mySpore.biomass() - biomassLost;
        }

        if(myBiomassAfterMove > nearestEnemy.biomass()){
          actions.add(new SporeMoveToAction(mySpore.id(), nextPos));
          canAttack = true;
        }
      }
    }

    if(!canAttack && !myTeam.spawners().isEmpty()){
      int weakestEnemyBiomass = Integer.MAX_VALUE;
      Spore weakestEnemy = null;

      for (TeamInfo ennemy : ennemies) {
        for (Spore enemySpore : ennemy.spores()) {
          if (enemySpore.biomass() < weakestEnemyBiomass) {
            weakestEnemyBiomass = enemySpore.biomass();
            weakestEnemy = enemySpore;
          }
        }
      }

      if(weakestEnemyBiomass != Integer.MAX_VALUE && weakestEnemy != null) {
        int availableNutrients = myTeam.nutrients();

        // MONSTER MODE: Si >= 1300 nutrients, spawn des monstres de 300!
        if (availableNutrients >= 1300) {
          int monsterBiomass = 300;

          for (Spawner spawner : myTeam.spawners()) {
            if (availableNutrients >= monsterBiomass) {
              actions.add(new SpawnerProduceSporeAction(spawner.id(), monsterBiomass));
              availableNutrients -= monsterBiomass;
              System.out.println("🔥 MONSTER SPAWN! biomass=" + monsterBiomass + " from spawner " + spawner.id());
            }
          }
        }
        // Logique normale
        else {
          int minNutrientsToSpawn = 250;

          if (availableNutrients >= minNutrientsToSpawn || gameMessage.tick() >= 220) {
            int targetBiomass = weakestEnemyBiomass + 5;

            if(availableNutrients > targetBiomass * 2) {
              targetBiomass = Math.min(availableNutrients / 2, weakestEnemyBiomass + 15);
            }

            if(availableNutrients >= targetBiomass && targetBiomass > 0){
              Spawner bestSpawner = findClosestSpawner(myTeam, weakestEnemy.position());

              if(bestSpawner != null){
                actions.add(new SpawnerProduceSporeAction(bestSpawner.id(), targetBiomass));
              } else {
                actions.add(new SpawnerProduceSporeAction(myTeam.spawners().getFirst().id(), targetBiomass));
              }
              System.out.println("Spawning spore: biomass=" + targetBiomass + ", nutrients left=" + (availableNutrients - targetBiomass));
            }
          } else {
            System.out.println("SAVING nutrients! Current=" + availableNutrients + ", need " + minNutrientsToSpawn + " to spawn");
          }
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

  /*
  public List<Action> manageSpawners(TeamInfo myTeam, TeamGameState gameMessage, List<TeamInfo> ennemies) {
    List<Action> actions = new ArrayList<>();

    // Compter notre territoire
    int territorySize = countTerritory(myTeam, gameMessage);
    int currentSpawnerCount = myTeam.spawners().size();

    // Calculer le coût du prochain spawner (séquence exponentielle: 0, 1, 3, 7, 15, 31...)
    int nextSpawnerCost = (int) Math.pow(2, currentSpawnerCount + 1) - 1;

    System.out.println("Territory: " + territorySize + ", Spawners: " + currentSpawnerCount +
            ", Next cost: " + nextSpawnerCost + ", Nutrients: " + myTeam.nutrients());

    // Conditions pour créer un nouveau spawner:
    // 1. On a assez de territoire (50+ tiles par spawner)
    // 2. On a assez de nutrients pour le créer + garder une réserve
    // 3. On a une spore assez grosse pour se sacrifier
    // 4. On est pas déjà en train d'économiser pour un gros spawn

    boolean hasEnoughTerritory = territorySize >= (currentSpawnerCount + 1) * 50;
    boolean hasEnoughNutrients = myTeam.nutrients() >= 500; // Reserve pour continuer à spawner
    boolean shouldCreateSpawner = hasEnoughTerritory && hasEnoughNutrients;

    if (shouldCreateSpawner && !myTeam.spores().isEmpty()) {
      // Trouver la meilleure spore pour devenir un spawner
      Spore bestSpore = findBestSporeForSpawner(myTeam, gameMessage, ennemies, nextSpawnerCost);

      if (bestSpore != null) {
        actions.add(new SporeCreateSpawnerAction(bestSpore.id()));
        System.out.println("Creating spawner with spore " + bestSpore.id() +
                " (biomass=" + bestSpore.biomass() + ") at cost " + nextSpawnerCost);
      }
    } else if (!shouldCreateSpawner) {
      System.out.println("Not creating spawner: territory=" + hasEnoughTerritory +
              ", nutrients=" + hasEnoughNutrients);
    }

    return actions;
  }

  private Spore findBestSporeForSpawner(TeamInfo myTeam, TeamGameState gameMessage,
                                        List<TeamInfo> ennemies, int spawnerCost) {
    Spore bestSpore = null;
    double bestScore = -1;

    for (Spore spore : myTeam.spores()) {
      // Skip les spores trop faibles (< 10 biomass)
      if (spore.biomass() < 10) continue;

      // Calculer un score basé sur:
      // - Position stratégique (centre de notre territoire)
      // - Distance aux ennemis (pas trop proche)
      // - Valeur nutritive de la zone

      double score = scoreSporeForSpawner(spore, myTeam, gameMessage, ennemies);

      if (score > bestScore) {
        bestScore = score;
        bestSpore = spore;
      }
    }

    return bestSpore;
  }

  private double scoreSporeForSpawner(Spore spore, TeamInfo myTeam,
                                      TeamGameState gameMessage, List<TeamInfo> ennemies) {
    double score = 0;

    // 1. Préférer les spores sur notre territoire (sécurité)
    String owner = gameMessage.world().ownershipGrid()[spore.position().y()][spore.position().x()];
    if (myTeam.teamId().equals(owner)) {
      score += 50;
    }

    // 2. Préférer les zones avec des nutrients
    int nutrients = gameMessage.world().map().nutrientGrid()[spore.position().y()][spore.position().x()];
    score += nutrients * 2;

    // 3. Pénaliser si trop proche des ennemis (< 10 tiles)
    int minEnemyDistance = Integer.MAX_VALUE;
    for (TeamInfo enemy : ennemies) {
      for (Spore enemySpore : enemy.spores()) {
        int dist = manhattanDistance(spore.position(), enemySpore.position());
        minEnemyDistance = Math.min(minEnemyDistance, dist);
      }
    }

    if (minEnemyDistance < 10) {
      score -= 100; // Trop proche, danger!
    } else {
      score += Math.min(minEnemyDistance, 20); // Bonus pour distance safe
    }

    // 4. Pénaliser les spores très grosses (on veut les garder pour attaquer)
    if (spore.biomass() > 50) {
      score -= spore.biomass(); // Plus grosse = moins bon candidat
    }

    return score;
  }
*/

  public List<Action> manageSpawners(TeamInfo myTeam, TeamGameState gameMessage, List<TeamInfo> ennemies) {
    List<Action> actions = new ArrayList<>();

    // On veut maximum 3-4 spawners dans la game
    int maxSpawners = 3;
    int currentSpawners = myTeam.spawners().size();

    if (currentSpawners >= maxSpawners) {
      System.out.println("Max spawners reached (" + currentSpawners + ")");
      return actions; // Déjà assez de spawners
    }

    // Créer un nouveau spawner seulement si:
    // - On a beaucoup de nutrients (500+)
    // - On a du territoire (100+ tiles)
    // - Pas trop tôt dans la game (tick > 50)

    int territory = countTerritory(myTeam, gameMessage);
    boolean shouldCreate = myTeam.nutrients() >= 100 &&
            territory >= 10 &&
            gameMessage.tick() > 180;

    if (shouldCreate) {
      // Trouver une spore pas trop importante pour la sacrifier
      Spore sacrificeSpore = null;

      for (Spore spore : myTeam.spores()) {
        // Chercher une spore moyenne (10-30 biomass)
        // Pas trop grosse (on veut la garder pour attaquer)
        // Pas trop petite (elle doit pouvoir bouger)
        if (spore.biomass() >= 10 && spore.biomass() <= 30) {
          // Vérifier qu'elle est en sécurité (sur notre territoire)
          String owner = gameMessage.world().ownershipGrid()[spore.position().y()][spore.position().x()];

          if (myTeam.teamId().equals(owner)) {
            sacrificeSpore = spore;
            break; // On a trouvé une bonne candidate
          }
        }
      }

      if (sacrificeSpore != null) {
        actions.add(new SporeCreateSpawnerAction(sacrificeSpore.id()));
        System.out.println("Creating spawner #" + (currentSpawners + 1) +
                " with spore " + sacrificeSpore.id());
      } else {
        System.out.println("No suitable spore found for spawner");
      }
    } else {
      System.out.println("Not creating spawner: nutrients=" + myTeam.nutrients() +
              ", territory=" + territory + ", tick=" + gameMessage.tick());
    }

    return actions;
  }
  private int countTerritory(TeamInfo myTeam, TeamGameState gameMessage) {
    int count = 0;
    String[][] ownership = gameMessage.world().ownershipGrid();

    for (int y = 0; y < ownership.length; y++) {
      for (int x = 0; x < ownership[y].length; x++) {
        if (myTeam.teamId().equals(ownership[y][x])) {
          count++;
        }
      }
    }

    return count; //possession
  }



}