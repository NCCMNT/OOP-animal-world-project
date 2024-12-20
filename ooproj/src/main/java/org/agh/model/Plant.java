package org.agh.model;

public class Plant extends WorldElement{
    private final int energy;

    public Plant(int x, int y, int energy){
        this.position = new Vector2d(x, y);
        this.energy = energy;
    }
    public Plant(Vector2d position, int energy){
        this.position = position;
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "*";
    }

    public int getEnergy() {
        return energy;
    }
}
