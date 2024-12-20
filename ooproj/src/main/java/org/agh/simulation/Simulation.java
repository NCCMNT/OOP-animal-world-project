package org.agh.simulation;

import org.agh.model.WorldMap;

public class Simulation implements Runnable {
    private WorldMap worldMap;

    public Simulation(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @Override
    public void run() {
        // TODO -> plan jest żeby symulacja po prostu wykonywała sekwencję eventów w każdej turze
        worldMap.moveAllAnimals();
        worldMap.growPlants();
        // ... etc, nwm czy w takiej kolejności
    }
}
