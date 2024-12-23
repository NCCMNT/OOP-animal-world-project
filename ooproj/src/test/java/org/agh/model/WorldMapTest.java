package org.agh.model;

import org.agh.utils.MapSettings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    WorldMap worldMap1 = new WorldMap(5, 5, 2, 3,
            0, 1, 0, 0, 4, 10, 2, 5);

    MapSettings mapSettings2 = new MapSettings(50, 50, 100, 1, 20,
            0, 0, 0, 1, 10, 2, 2);
    WorldMap worldMap2 = new WorldMap(mapSettings2);

    @Test
    void testMoveAllAnimals() {
        worldMap1.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 10; i++) {
            worldMap1.moveAllAnimals();
            worldMap1.printAnimalInfo();
            System.out.println(worldMap1);
        }
        worldMap1.printAnimalInfo();
    }

    @Test
    void testFeedingAllAnimals() {
        worldMap1.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 10; i++) {
            worldMap1.moveAllAnimals();
            System.out.println("ANIMAL PRZED JEDZENIEM");
            worldMap1.printAnimalInfo();

            worldMap1.feedAllAnimals();
            System.out.println("ANIMAL PO JEDZENIU");
            worldMap1.printAnimalInfo();

            System.out.println(worldMap1);
            worldMap1.growPlants();
            System.out.println(worldMap1);
        }
        worldMap1.printAnimalInfo();
    }

    @Test
    void testBreedingAllAnimals(){
        worldMap1.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 10; i++) {
            worldMap1.moveAllAnimals();
            System.out.println("PRZED ROZMNAŻANIEM");
            worldMap1.printAnimalInfo();

            worldMap1.breedAllAnimals();
            worldMap1.checkStateOfAllAnimals();
            System.out.println("PO ROZMNAŻANIU I USUNIĘCIU ZWIERZAKÓW Z ENERGIĄ 0");
            worldMap1.printAnimalInfo();

            worldMap1.growPlants();
            System.out.println(worldMap1);
        }
        worldMap1.printAnimalInfo();
    }
}