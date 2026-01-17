package codes.blitz.game.bot;

import codes.blitz.game.generated.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BetterBot {
    Random random = new Random();
    // MAP A UTILISER
    Tile [][] tiles;
    List<Tile> freeNutrimentsTiles= new ArrayList<>();

    public BetterBot() {
        System.out.println("Initializing bot");
    }

    /*
     * Here is where the magic happens, for now the moves are not very good. I bet you can do better ;)
     */
    public List<Action> getActions(TeamGameState gameMessage) {
        List<Action> actions = new ArrayList<>();
        int[][] nutrimentsGrid = gameMessage.world().map().nutrientGrid();
        tiles = new Tile[nutrimentsGrid[0].length][nutrimentsGrid.length];


        // CREATION DE LA MAP A UTILISER
        for (int i = 0; i < nutrimentsGrid[0].length; i++) {
            for (int j = 0; j < nutrimentsGrid[1].length; j++) {
                if (nutrimentsGrid[i][j] != 0 ) {
                    Tile t =new Tile(nutrimentsGrid[i][j],gameMessage.world().biomassGrid()[i][j],
                            gameMessage.world().ownershipGrid()[i][j],false,new Position(i,j));
                    if (t.getNutrients()>0 && (t.getControllingTeam().equals("") || t.getControllingTeam().equals("NEUTRAL"))) {
                        freeNutrimentsTiles.add(t);
                    }
                    tiles[i][j] = t;
                }
            }
        }

        List<Spawner> spawners = gameMessage.world().spawners();
        for (Spawner spawner : spawners) {
            tiles[spawner.position().x()][spawner.position().y()].setSpawner(true);
        }
        // FIN DE CREATION DE LA MAP A UTILISER

        //-/ ////////////// continuer apres cette ligne


        //-/ ///////////////////////////////////////////////////
        TeamInfo myTeam = gameMessage.world().teamInfos().get(gameMessage.yourTeamId());
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

        // -/////////////////////////////////////////////////////////// continuer avant cette ligne
        return actions;
    }
}
