package org.agh.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    @Test
    void test() {
        WorldMap worldMap = new WorldMap(5, 5, 0, 0, 0, 0, 1, 10, 2, 0);
        for (int i = 0; i < 10; i++) {
            worldMap.printAnimalInfo();
            worldMap.moveAll();
        }
        worldMap.printAnimalInfo();
    }

}