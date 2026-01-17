package codes.blitz.game.generated;

import java.util.Objects;

public class Tile {
    int nutrients;
    int biomassValue;
    String SporeId;
    String controllingTeam;
    boolean spawner;
    Position position;

    public Tile (int nutrients, int biomassValue, String SporeId, String controllingTeam, boolean spawner, Position position) {
        this.nutrients = nutrients;
        this.biomassValue = biomassValue;
        this.SporeId = SporeId;
        this.controllingTeam = controllingTeam;
        this.spawner = spawner;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public String getSporeId() {
        return SporeId;
    }

    public void setSporeId(String sporeId) {
        SporeId = sporeId;
    }

    public int getNutrients() {
        return nutrients;
    }

    public int getBiomassValue() {
        return biomassValue;
    }

    public String getControllingTeam() {
        return controllingTeam;
    }

    public boolean isSpawner() {
        return spawner;
    }

    public void setSpawner(boolean spawner) {
        this.spawner = spawner;
    }
}


