package org.agh.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JunglePlanterTest {

    JunglePlanter junglePlanter = new JunglePlanter(10,10,2);

    @Test
    void generatePlants() {
        int lastPlantAmount = 0;
        for (int i = 0; i < 100; i++) {
            junglePlanter.generatePlants(1);
            // System.out.println(junglePlanter.getPlants());
            assertTrue( junglePlanter.getPlants().size()==lastPlantAmount + 1
                                || junglePlanter.getPlants().size()== lastPlantAmount + 4
                                || junglePlanter.getPlants().size() == lastPlantAmount);
            lastPlantAmount = junglePlanter.getPlants().size();
        }
    }

    @Test
    void manualTestRemovePlants(){
        junglePlanter.generatePlants(10);
        System.out.println(junglePlanter.getPlants());
        for (int i = 0; i < 10; i++) {
            Plant removedPlant = null;
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    removedPlant = junglePlanter.removePlant(new Vector2d(x,y));
                    if (removedPlant != null) {break;}
                }
                if (removedPlant != null) {break;}
            }
            System.out.println(junglePlanter.getPlants());
        }
    }

}