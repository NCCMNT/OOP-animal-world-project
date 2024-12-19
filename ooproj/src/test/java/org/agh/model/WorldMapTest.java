package org.agh.model;

import org.agh.utils.MapSettings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    WorldMap worldMap1 = new WorldMap(5, 5, 0, 0, 0, 0, 0, 0, 1, 10, 2, 20);
    @Test
    void test() {
        for (int i = 0; i < 10; i++) {
            worldMap1.printAnimalInfo();
            worldMap1.moveAllAnimals();
        }
        worldMap1.printAnimalInfo();
    }

    @Test
    void testPlantInitialization() {
        //tests for worldMap1
        assertEquals(2, worldMap1.getFields().getEquatorStart());
        assertEquals(3, worldMap1.getFields().getEquatorEnd());

        //initial number of plants must always equal size of hashmap with plants positions
        assertEquals(20, worldMap1.getPlants().size());

        //tests for worldMap2
        MapSettings mapSettings2 = new MapSettings(50, 50, 100, 0, 0,
                0, 0, 0, 1, 10, 2, 2);
        WorldMap worldMap2 = new WorldMap(mapSettings2);
        assertEquals(100, worldMap2.getPlants().size());

    }

}