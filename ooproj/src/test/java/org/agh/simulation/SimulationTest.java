package org.agh.simulation;

import org.agh.model.WorldMap;
import org.agh.utils.MapSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    MapSettings mapSettings = new MapSettings(10,10,17,2,3,7,10,
            7,4, false, 1,3,8);

    WorldMap worldMap = new WorldMap(mapSettings);

    @Test
    void testSimulation() {
        Simulation simulation = new Simulation(worldMap);
        Thread thread = new Thread(simulation);
        thread.start();
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        simulation.stop();
    }
}