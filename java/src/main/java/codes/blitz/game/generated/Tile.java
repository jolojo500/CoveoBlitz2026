package codes.blitz.game.generated;

import java.util.Objects;

public class Tile {
    int nutrients;
    int biomass;
    String controllingTeam;
    boolean spawner;
    Position position;

    public Tile (int nutrients, int biomass, String controllingTeam, boolean spawner, Position position) {
        this.nutrients = nutrients;
        this.biomass = biomass;
        this.controllingTeam = controllingTeam;
        this.spawner = spawner;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public int getNutrients() {
        return nutrients;
    }

    public int getBiomass() {
        return biomass;
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

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return this.position.x() == tile.position.x() && this.position.y() == tile.position.y();
    }

    @Override
    public int hashCode() {
        return Objects.hash(position.x(), position.y());
    }
}

