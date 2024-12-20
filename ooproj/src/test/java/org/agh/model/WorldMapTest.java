package org.agh.model;

import org.agh.utils.MapSettings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    WorldMap worldMap1 = new WorldMap(5, 5, 2, 0, 0, 0, 0, 0, 1, 10, 2, 5);

    MapSettings mapSettings2 = new MapSettings(50, 50, 100, 0, 20,
            0, 0, 0, 1, 10, 2, 2);
    WorldMap worldMap2 = new WorldMap(mapSettings2);

    @Test
    void testMoveAllAnimals() {
        for (int i = 0; i < 10; i++) {
            worldMap1.printAnimalInfo();
            worldMap1.moveAllAnimals();
            System.out.println(worldMap1);
        }
        worldMap1.printAnimalInfo();
    }
}