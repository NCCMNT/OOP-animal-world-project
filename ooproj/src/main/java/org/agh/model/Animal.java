package org.agh.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.round;

public class Animal extends WorldElement{
    private int energy;
    private MapDirection direction;
    private Vector2d position;
    private int age;
    private List<Integer> genom;
    private int activeGen;
    private static final Random random = new Random();

    private Animal(int energy, Vector2d position){
        this.energy = energy;
        this.position = position;
        this.age = 0;
        this.direction = MapDirection.intToMapDirection(random.nextInt(8));
    }
    // Animal when born in the beginning of simulation
    public Animal(int initEnergy, Vector2d position, int genomLen) {
        this(initEnergy, position);
        this.genom = new ArrayList<>(genomLen);
        for (int i = 0; i < genomLen; i++) {
            genom.add(random.nextInt(8));
        }
        this.activeGen = random.nextInt(genomLen);

    }
    // Animal when born from two parents
    public Animal(Animal parent1, Animal parent2, int energy) {
        this(energy, parent1.getPosition());
        int genomLen = parent1.genom.size();
        int sumEnergy = parent1.energy + parent2.energy;
        int gensFromParent1 = round(genomLen * parent1.energy / sumEnergy);
        int gensFromParent2 = genomLen - gensFromParent1;
        this.genom = new ArrayList<>(genomLen);

        if (random.nextBoolean()) {
            for (int i = 0; i < gensFromParent1; i++) {
                genom.add(parent1.genom.get(i));
            }
            for (int i = 0; i < gensFromParent2; i++) {
                genom.add(parent2.genom.get(i + gensFromParent1));
            }
        }
        else{
            for (int i = 0; i < gensFromParent2; i++) {
                genom.add(parent2.genom.get(i));
            }
            for (int i = 0; i < gensFromParent1; i++) {
                genom.add(parent1.genom.get(i + gensFromParent2));
            }
        }

        this.activeGen = random.nextInt(genomLen);

    }

    @Override
    public String toString() {
        return this.position.toString();
    }

    public int getEnergy() {
        return energy;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public int getAge() {
        return age;
    }

    public List<Integer> getGenom() {
        return genom;
    }

    public int getActiveGen() {
        return activeGen;
    }
}
