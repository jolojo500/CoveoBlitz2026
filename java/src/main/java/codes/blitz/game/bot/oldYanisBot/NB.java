package codes.blitz.game.bot.oldYanisBot;

import codes.blitz.game.generated.*;
import codes.blitz.game.generated.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NB {
    //REFERENCE ONLY
//
//    public List<Action> getActions(TeamGameState gameMessage) {
//        List<Action> actions = new ArrayList<>();
//        List<Connection> connections = new ArrayList<>();
//        List<Colony> colonies = gameMessage.map().colonies();
//        int[][] gameMap = gameMessage.map().biomass();
//        int numberOfBiomassOnMap = 0;
//        System.out.println();
//        for (int i =0; i < gameMap.length; i++) {
//            for (int j =0; j < gameMap[i].length; j++) {
//                System.out.print(gameMap[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("SCORE : " + gameMessage.score());
//
//        for (int i = 0; i < colonies.size(); i++) {
//            for (int j = 0; j < colonies.size()-i; j++) {
//                if(i!=i+j) {
//                    connections.add(new Connection(colonies.get(i), colonies.get(i + j), gameMessage.map()));
//                }
//            }
//        }
//
//        // Pick a number of biomass to move this turn.
//        int biomassToPossiblyPlaceThisTurn = gameMessage.maximumNumberOfBiomassOnMap()-numberOfBiomassOnMap;
//        int biomassToPlaceThisTurn = Math.min(biomassToPossiblyPlaceThisTurn, gameMessage.maximumNumberOfBiomassPerTurn());
//        int biomassRemovableThisTurn = biomassToPlaceThisTurn-gameMessage.availableBiomass();
//        int biomassToRemove=0;
//        List<Removers> connectionsToRemove = new ArrayList<>();
//
//        for (int i = 0; i < connections.size(); i++) {
//            if (biomassToRemove<biomassRemovableThisTurn) {
//                int maxBio = 0;
//                if (connections.get(i).colony1.nutrients() > maxBio) {
//                    maxBio = connections.get(i).colony1.nutrients();
//                }
//                if (connections.get(i).colony2.nutrients() > maxBio) {
//                    maxBio = connections.get(i).colony2.nutrients();
//                }
//                for (int j=0; j<connections.get(i).allSteps.size() ; j++) {
//                    Position p =connections.get(i).allSteps.get(j);
//                    if (gameMap[p.x()][p.y()]>maxBio) {
//                        int transfer=0;
//                        if (biomassToRemove+(gameMap[p.x()][p.y()]-maxBio)>biomassRemovableThisTurn){
//                            transfer=biomassRemovableThisTurn-biomassToRemove;
//                        } else {
//                            biomassToRemove += gameMap[p.x()][p.y()]-maxBio;
//                            transfer=gameMap[p.x()][p.y()]-maxBio;
//                        }
//
//                        connectionsToRemove.add(new Removers(p,transfer));
//                    }
//                }
//            }
//        }
//        for (int i=0; i<connectionsToRemove.size(); i++) {
//            actions.add(new RemoveBiomassAction(connectionsToRemove.get(i).amount,connectionsToRemove.get(i).position));
//        }
//
//        List<Removers> connectionsToAdd = new ArrayList<>();
//        int biomassToPlace=0;
//        connections.sort(new Comparator<Connection>() {
//
//            @Override
//            public int compare(Connection o1, Connection o2) {
//                if (o1.allSteps.size()!=o2.allSteps.size()) {
//                    return o1.allSteps.size()-o2.allSteps.size();
//                } else {
//                    return Math.max(o1.colony1.nutrients(), o1.colony2.nutrients())-Math.max(o2.colony1.nutrients(),o2.colony2.nutrients());
//                }
//            }
//        });
//        for (int i = 0; i < connections.size(); i++) {
//            if (biomassToPlaceThisTurn>biomassToPlace) {
//                int maxBio = 0;
//                if (connections.get(i).colony1.nutrients() > maxBio) {
//                    maxBio = connections.get(i).colony1.nutrients();
//                }
//                if (connections.get(i).colony2.nutrients() > maxBio) {
//                    maxBio = connections.get(i).colony2.nutrients();
//                }
//                for (int j=0; j<connections.get(i).allSteps.size() ; j++) {
//                    Position p =connections.get(i).allSteps.get(j);
//                    if (gameMap[p.x()][p.y()]<maxBio) {
//                        int transfer=0;
//                        if (biomassToPlace+(maxBio-gameMap[p.x()][p.y()])>biomassToPlaceThisTurn){
//                            transfer=biomassToPlaceThisTurn-biomassToPlace;
//                        } else {
//                            biomassToPlace += maxBio-gameMap[p.x()][p.y()];
//                            transfer=maxBio-gameMap[p.x()][p.y()];
//                        }
//                        connectionsToAdd.add(new Removers(p,transfer));
//                    }
//                }
//            }
//        }
//
//        for (int i=0; i<connectionsToAdd.size(); i++) {
//            actions.add(new AddBiomassAction(connectionsToAdd.get(i).amount,connectionsToAdd.get(i).position));
//        }
//
//        // You can clearly do better than the random actions above. Have fun!!
//        return actions;
//    }
//
//    public class Connection {
//
//        Colony colony1;
//        Colony colony2;
//        GameMap map;
//        int xOfBestPath=0;
//        int connexionStrength=0;
//        int totalOfPath =0;
//        double valueOfPath = 0;
//        List<Position> weakSteps = new ArrayList<>();
//        List<Position> allSteps = new ArrayList<>();
//
//
//        public Connection(Colony colony1, Colony colony2, GameMap map) {
//            this.colony1 = colony1;
//            this.colony2 = colony2;
//            System.out.println("Colony 1 : "+colony1.position().toString());
//            System.out.println("Colony 2 : "+colony2.position().toString());
//            this.map = map;
//            findXOfBestPath();
//            totalOfPath = getTotalOfPath();
//            valueOfPath = Math.min(colony1.nutrients(), connexionStrength) * Math.min(colony2.nutrients(), connexionStrength);
//        }
//        public void findXOfBestPath() {
//            Position colony1Position = colony1.position();
//            Position colony2Position = colony2.position();
//            int diffx = colony2Position.x() - colony1Position.x();
//            int diffy = colony2Position.y() - colony1Position.y();
//            int currentWeight = 100000;
//            int bestWeight = -999999;
//
//            if (diffx ==0 ){
//                for (int i=1;i<Math.abs(diffy);i++) {
//                    if (currentWeight>map.biomass()[colony1Position.x()][((diffy>0)?1:-1)*i+colony1Position.y()]) // if new lower weight
//                        currentWeight = map.biomass()[colony1Position.x()][((diffy>0)?1:-1)*i+colony1Position.y()];
//                }
//                bestWeight = currentWeight;
//                xOfBestPath = colony1Position.x();
//            }
//            else if (diffy ==0 ){
//                for (int i=1;i<Math.abs(diffx);i++) {
//                    if (currentWeight>map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony1Position.y()]) // if new lower weight
//                        currentWeight = map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony1Position.y()];
//                }
//                bestWeight = currentWeight;
//                xOfBestPath = -1;
//            } else {
//                for (int i = 0; i < Math.abs(diffx); i++) { // line x
//                    for (int j = 0; j < Math.abs(diffy); j++) { // line y
//                        if (!(i==0&&j==0) && !(i==Math.abs(diffx)&&j==Math.abs(diffy))) { // if not on the colonies
//                            if (currentWeight>map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][((diffy>0)?1:-1)*j+colony1Position.y()]) // if new lower weight
//                                currentWeight = map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][((diffy>0)?1:-1)*j+colony1Position.y()];
//                        }
//                    }
//                    if (i > 1 && i<Math.abs(diffx)) { // rest of the X squares in the middle
//                        for (int j = 1; j < Math.abs(diffx); j++) {
//                            if (j > i) {
//                                if (currentWeight>map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony1Position.y()]){
//                                    currentWeight = map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony1Position.y()];
//                                }
//                            } else if (j < i){
//                                if (currentWeight>map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony2Position.y()]){
//                                    currentWeight = map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony2Position.y()];
//                                }
//                            }
//                        }
//                    }
//                    else if (i==0){ // first X line
//                        for (int j=0 ; j < Math.abs(diffx); j++) {
//                            if (currentWeight>map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony2Position.y()]) // if new lower weight
//                                currentWeight = map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony2Position.y()];
//                        }
//                    } else
//                    { //last X line
//                        for (int j=1 ; j <= Math.abs(diffx); j++) {
//                            if (currentWeight>map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony1Position.y()]) // if new lower weight
//                                currentWeight = map.biomass()[((diffx>0)?1:-1)*j+colony1Position.x()][colony1Position.y()];
//                        }
//                    }
//                    if (currentWeight > bestWeight || bestWeight == -999999) {
//                        bestWeight = currentWeight;
//                        xOfBestPath = i;
//                    }
//                    currentWeight =0;
//                }
//            }
//
//        connexionStrength=bestWeight;
//        }
//        public int getTotalOfPath() {
//            Position colony1Position = colony1.position();
//            Position colony2Position = colony2.position();
//            int diffx = colony2Position.x() - colony1Position.x();
//            int diffy = colony2Position.y() - colony1Position.y();
//            int currentTotalOfPath=0;
//            if (xOfBestPath==-1){
//                for (int i = 1; i <= Math.abs(diffx); i++) {
//                    if (connexionStrength ==map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony1Position.y()]){
//                        weakSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),colony1Position.y()));
//                    }
//                    allSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),colony1Position.y()));
//                    currentTotalOfPath+=map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony1Position.y()];
//                }
//            }
//            else if (diffx==0){
//                for (int i = 1; i <= Math.abs(diffy); i++) {
//                    if (connexionStrength ==map.biomass()[colony1Position.x()][((diffy>0)?1:-1)*i+colony1Position.y()]){
//                        weakSteps.add(new Position(colony1Position.x(),((diffy>0)?1:-1)*i+colony1Position.y()));
//                    }
//                    allSteps.add(new Position(colony1Position.x(),((diffy>0)?1:-1)*i+colony1Position.y()));
//                    currentTotalOfPath+=map.biomass()[colony1Position.x()][((diffy>0)?1:-1)*i+colony1Position.y()];
//                }
//            } else{
//                    for (int i = 0; i <= Math.abs(diffx); i++) {
//                        if (xOfBestPath > i) { // cases sur X a en ligne avec case de depart
//                            if (connexionStrength ==map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony1Position.y()]){
//                                weakSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),colony1Position.y()));
//                            }
//                            allSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),colony1Position.y()));
//                            currentTotalOfPath+=map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony1Position.y()];
//                        } else if (xOfBestPath < i){  // cases sur X a en ligne avec case de fin
//                            if (connexionStrength ==map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony2Position.y()]){
//                                weakSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),colony2Position.y()));
//                            }
//                            allSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),colony2Position.y()));
//                            currentTotalOfPath+=map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][colony2Position.y()];
//                        } else {
//                            for (int j = 0; j <= Math.abs(diffy); j++) {
//                                if (!(i==0&&j==0)&&!(i==Math.abs(diffx)&&j==Math.abs(diffy))){
//                                    if (connexionStrength ==map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][((diffy>0)?1:-1)*j+colony1Position.y()]){
//                                        weakSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),((diffy>0)?1:-1)*j+colony1Position.y()));
//                                    }
//                                    allSteps.add(new Position(((diffx>0)?1:-1)*i+colony1Position.x(),((diffy>0)?1:-1)*j+colony1Position.y()));
//                                    currentTotalOfPath+=map.biomass()[((diffx>0)?1:-1)*i+colony1Position.x()][((diffy>0)?1:-1)*j+colony1Position.y()];
//                                }
//                            }
//                        }
//                    }
//            }
//            for (int i=0; i<weakSteps.size(); i++){
//                if (weakSteps.get(i).x()== colony1Position.x()&&weakSteps.get(i).y()== colony1Position.y()){
//                    weakSteps.remove(i);
//                } else if (weakSteps.get(i).x()==colony2Position.x()&& weakSteps.get(i).y()== colony2Position.y()){
//                    weakSteps.remove(i);
//                }
//            }
//            for (int i=0; i<allSteps.size(); i++){
//                if (allSteps.get(i).x()== colony1Position.x()&&allSteps.get(i).y()== colony1Position.y()){
//                    allSteps.remove(i);
//                } else if (allSteps.get(i).x()==colony2Position.x()&& allSteps.get(i).y()== colony2Position.y()){
//                    allSteps.remove(i);
//                }
//            }
//
//            //debug
//            System.out.println("xline : "+ xOfBestPath);
//            System.out.println("allSteps: ");
//            for (int i =0; i<allSteps.size();i++) {
//                System.out.println(allSteps.get(i).toString());
//            }
//            return currentTotalOfPath;
//        }
//
//    }
//    public class Removers{
//        Position position;
//        int amount;
//        public Removers(Position position, int i){
//            this.position = position;
//            amount = i;
//        }
//    }
}

