package org.agh.simulation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.agh.model.WorldMap;
import org.agh.utils.SimulationChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulation implements Runnable {
    private final WorldMap worldMap;
    private int turn = 0;
    private int speed = 500;

    private BooleanProperty stopped = new SimpleBooleanProperty(true);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final List<SimulationChangeListener> observers;

    public Simulation(WorldMap worldMap) {
        this.worldMap = worldMap;
        observers = new ArrayList<SimulationChangeListener>();
    }

    public void start() {
        //stop();
        executorService.submit(this);
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

        notifyObservers(String.valueOf(turn));
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void run() {
        stopped.set(false);
        while(!stopped.get()){
            try {
                Thread.sleep(speed);
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            this.executeTurn();
        }
    }

    public boolean isStopped() {
        return stopped.get();
    }

    public BooleanProperty stoppedProperty() {
        return stopped;
    }

    public void stop() {
        this.stopped.set(true);
    }

    public void shutdown() {
        stop();
        executorService.shutdown();
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public void addObserver(SimulationChangeListener observer) {
        observers.add(observer);
    }
    public void notifyObservers(String message) {
        for (SimulationChangeListener observer : observers) {
            observer.mapChanged(this.worldMap, message);
        }
    }

}
