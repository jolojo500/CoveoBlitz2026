package codes.blitz.game.bot;

import codes.blitz.game.generated.*;

import java.util.*;

public class BetterBot {
    Random random = new Random();
    // MAP A UTILISER
    Tile [][] tiles;
    List<Tile> freeNutrimentsTiles= new ArrayList<>();
    List<Tile> ourPositionTiles= new ArrayList<>();

    List<Queue<Tile>>  conqueringPaths = new ArrayList<>();

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

                    Tile t = new Tile(nutrimentsGrid[i][j], gameMessage.world().biomassGrid()[i][j], "",
                            gameMessage.world().ownershipGrid()[i][j], false, new Position(i, j));
                    if (t.getNutrients() > 0 && (t.getControllingTeam().equals("") || t.getControllingTeam().equals("NEUTRAL"))) {
                        freeNutrimentsTiles.add(t);
                    }
                    if (t.getControllingTeam().equals(gameMessage.yourTeamId()) && t.getBiomassValue() > 1) {
                        ourPositionTiles.add(t);
                    }
                    tiles[i][j] = t;

            }
        }

        List<Spawner> spawners = gameMessage.world().spawners();
        for (Spawner spawner : spawners) {
            tiles[spawner.position().x()][spawner.position().y()].setSpawner(true);
        }

        List<Spore> spores = gameMessage.world().spores();
        for (Spore spore : spores) {
            tiles[spore.position().x()][spore.position().y()].setSporeId(spore.id());
        }
        // FIN DE CREATION DE LA MAP A UTILISER

        if (freeNutrimentsTiles.size()>0) {
            // FINDER MODE
            Queue<Tile> t = getClosestNutriTile(gameMessage.yourTeamId());
            actions.addAll(moveAllBiomassTo(t, gameMessage.yourTeamId()));
        } else {
           // ATTACK MODE


        }


        // -/////////////////////////////////////////////////////////// continuer avant cette ligne
        return actions;
    }

    public Queue<Tile> getClosestNutriTile(String teamId) {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < ourPositionTiles.size(); i++) {
            for (int j = i + 1; j < freeNutrimentsTiles.size(); j++) {
                paths.add(TilePathfinder.findShortestPath(tiles, ourPositionTiles.get(i),freeNutrimentsTiles.get(j),teamId));
            }
        }
        paths.sort(new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o2.get_cost(teamId)-o1.get_cost(teamId);
            }
        });
        Queue<Tile> theChosenOne = paths.get(0).path();

        return theChosenOne ;
    }
    public Action goToBorder(Tile start, Tile end, String teamId) {
        Path pa = TilePathfinder.findShortestPath(tiles, start, end, teamId);
        Tile t = pa.path().remove();
        Tile tPlusUn = pa.path().peek();
        Position p=new Position(1,0);
        if (t.getPosition().x()+1==tPlusUn.getPosition().x()) {
            p = new Position(1,0);
        }
        else if (t.getPosition().x()-1==tPlusUn.getPosition().x()) {
            p = new Position(-1,0);
        } else if (t.getPosition().y()+1==tPlusUn.getPosition().y()) {
            p = new Position(0,1);
        } else if (t.getPosition().y()-1==tPlusUn.getPosition().y()) {
            p = new Position(0,-1);
        }
        return  new SporeMoveAction(t.getSporeId(),p);
    }

    public void findBorders(TeamGameState gameMessaage){

    }
    public List<Action> moveAllBiomassTo(Queue<Tile> tile, String teamId) {
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < ourPositionTiles.size(); i++) {
            System.out.println("POSITION NUM " +i + " : "+ourPositionTiles.get(i).getPosition());
            for (int j = 0 ; j < tile.size(); j++) {
                Tile t = tile.poll();
                Tile tPlusUn = tile.peek();
                Position p=new Position(0,0);
                if (t.getPosition().x()+1<tiles.length){
                    if (t.getPosition().x()+1==tPlusUn.getPosition().x()) {
                        p = new Position(1,0);
                    }
                }
                if (t.getPosition().x()>0){
                    if (t.getPosition().x()-1==tPlusUn.getPosition().x()) {
                     p = new Position(-1,0);
                    }
                }
                if (t.getPosition().y()+1<tiles.length){
                    if (t.getPosition().y()+1==tPlusUn.getPosition().y()) {
                        p = new Position(0,1);

                    }
                }
                if (t.getPosition().y()>0){
                    if (t.getPosition().y()-1==tPlusUn.getPosition().y()) {
                        p = new Position(0,-1);
                    }
                }
                actions.add(new SporeMoveAction(t.getSporeId(),p));
            }
        }
        return actions;
    }
}
