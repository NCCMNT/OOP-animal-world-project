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

    @Test
    void testPlantInitialization() {
        //tests for worldMap1
        assertEquals(2, worldMap1.getFields().getEquatorStart());
        assertEquals(3, worldMap1.getFields().getEquatorEnd());

        //initial number of plants must always equal size of hashmap with plants positions
        assertEquals(5, worldMap1.getPlants().size());

        //tests for worldMap2
        assertEquals(100, worldMap2.getPlants().size());
    }

    @Test
    void testPlantGrowth() {
        //tests for worldMap1
        assertEquals(5, worldMap1.getPlants().size());
        System.out.println(worldMap1.getPlants());

        worldMap1.growPlants();

        assertEquals(7, worldMap1.getPlants().size());
        System.out.println(worldMap1.getPlants());

        worldMap1.growPlants();
        assertEquals(9, worldMap1.getPlants().size());

        //tests for worldMap2
        assertEquals(100, worldMap2.getPlants().size());
        worldMap2.growPlants();
        assertEquals(120, worldMap2.getPlants().size());

        //all worldMap2 should be filled with plants
        for (int i = 0; i < 1000; i++) {
            worldMap2.growPlants();
        }
        assertEquals(2500, worldMap2.getPlants().size());
        //both availability lists should be empty
        assertEquals(List.of(), worldMap2.getRegularFieldsAvailable());
        assertEquals(List.of(),worldMap2.getPreferredFieldsAvailable());

    }

    @Test
    void testPreferredFields(){
        List<Vector2d> preferred = worldMap1.getPreferredFieldsAvailable();
        List<Vector2d> regular = worldMap1.getRegularFieldsAvailable();

        for (Vector2d field : preferred) {
            assertTrue(worldMap1.getFields().isPreferredField(field));
        }

        for (Vector2d field : regular) {
            assertFalse(worldMap1.getFields().isPreferredField(field));
        }
    }

}