package org.agh.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class WorldMap {
    private final int grassPerTurn;
    private final int grassEnergy;
    private final int width;
    private final int height;
    private HashMap<Vector2d, WorldElement> plants;
    private List<Animal> animals;
    private static final Random random = new Random();

    public WorldMap(int width, int height, int grassPerTurn, int grassEnergy) {
        this.width = width;
        this.height = height;
        this.grassPerTurn = grassPerTurn;
        this.grassEnergy = grassEnergy;
    }

    public WorldMap(int width, int height, int grassPerTurn, int grassEnergy,  int startingAnimals, int startingEnergy, int genomLen,  int startingGrass) {
        this(width, height, grassPerTurn, grassEnergy);
        generateAnimals(startingAnimals, startingEnergy, genomLen);
        generatePlants(startingGrass);
    }

    public void generateAnimals(int amountOfAnimals, int startingEnergy, int genomLen) {
        animals = new ArrayList<>();
        for (int i = 0; i < amountOfAnimals; i++) {
            animals.add(new Animal(this, startingEnergy, new Vector2d(random.nextInt(width), random.nextInt(height)), genomLen));
        }
    }

    public void generatePlants(int amountOfPlants) {
        plants = new HashMap<>();
    }

    public Vector2d whereToMove( Vector2d desiredPosition ) {
        if(desiredPosition.getY() >= height || desiredPosition.getY() < 0) {
            return null;
        }
        return new Vector2d((desiredPosition.getX()+width)%width, desiredPosition.getY());
    }

    public void moveAll(){
        for(Animal animal: animals) {
            animal.move();
        }
    }

    public void printAnimalInfo(){
        System.out.println("There are " + animals.size() + " animals in the world: ");
        for(Animal animal: animals) {
            System.out.println("    Energy=" + animal.getEnergy() + "|Position=" + animal.getPosition().toString() +
                    "|direction=" + animal.getDirection().toString() + "|genom=" + animal.getGenom().toString() ) ;
        }
    }

}
