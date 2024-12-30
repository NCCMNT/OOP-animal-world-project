package org.agh.simulation;

import org.agh.model.WorldMap;

public class Simulation implements Runnable {
    private final WorldMap worldMap;
    private boolean running = true;
    private int turn = 0;
    private int speed = 500;

    public Simulation(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    //each turn execution is composed of few steps
    private void executeTurn(){
        System.out.println("Start of " + turn + " turn");
        //first we check if any animal has 0 energy, and remove these which has
        //this method also ages every animal by one time unit
        worldMap.checkStateOfAllAnimals();

        //then every animal moves to new plane determined by their active gen
        worldMap.moveAllAnimals();

        //then if animal stepped on a field where plant grows, it eats it
        //if there is two or more animals on the same field then the strongest eats
        worldMap.feedAllAnimals();

        //then all animals age, knowing that they survived another day
        worldMap.ageAllAnimals();

        //then fed animals can copulate in order to make new animals
        worldMap.breedAllAnimals();

        //lastly world blooms with new plants on available fields
        worldMap.growPlants();

        worldMap.printAnimalInfo();
        System.out.println(worldMap);
        turn++;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(speed);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            this.executeTurn();
        }
    }

    public void stop(){
        running = false;
    }

    public void resume(){
        running = true;
    }
}
