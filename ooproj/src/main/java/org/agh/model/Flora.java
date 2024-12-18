package org.agh.model;

import java.util.HashMap;
import java.util.Vector;

abstract public class Flora {
    private final int perTurnAmount;
    private final int grassEnergy;
    private final Boundary boundary;
    private HashMap<Vector2d, WorldElement> plants;

    public Flora(int startingAmount, int perTurnAmount, int grassEnergy, Boundary boundary) {
        this.perTurnAmount = perTurnAmount;
        this.grassEnergy = grassEnergy;
        this.boundary = boundary;
        plants = new HashMap<>();
    }

    public abstract void generate(int amount);

    public void perTurnGenerate() {
        this.generate(perTurnAmount);
    };


    public int eat(Vector2d position) {

    }



}
