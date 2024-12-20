package org.agh.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EquatorPlanterTest {

    EquatorPlanter planter1 = new EquatorPlanter(5, 5, 2, 5);
    EquatorPlanter planter2 = new EquatorPlanter(50, 50, 2, 100);

    @Test
    void testPlantInitialization() {
        //tests for planter1
        assertEquals(2, planter1.getFields().getEquatorStart());
        assertEquals(3, planter1.getFields().getEquatorEnd());

        //initial number of plants must always equal size of hashmap with plants positions
        assertEquals(5, planter1.getPlants().size());

        //tests for planter2
        assertEquals(100, planter2.getPlants().size());
    }

    @Test
    void testPlantGrowth() {
        //tests for planter1
        assertEquals(5, planter1.getPlants().size());
        System.out.println(planter1.getPlants());

        planter1.generatePlants(2);

        assertEquals(7, planter1.getPlants().size());
        System.out.println(planter1.getPlants());

        planter1.generatePlants(2);
        assertEquals(9, planter1.getPlants().size());

        //tests for planter2
        assertEquals(100, planter2.getPlants().size());
        planter2.generatePlants(20);
        assertEquals(120, planter2.getPlants().size());

        //all planter2 should be filled with plants
        for (int i = 0; i < 1000; i++) {
            planter2.generatePlants(20);
        }
        assertEquals(2500, planter2.getPlants().size());
        //both availability lists should be empty
        assertEquals(List.of(), planter2.getRegularFieldsAvailable());
        assertEquals(List.of(), planter2.getPreferredFieldsAvailable());

    }

    @Test
    void testPreferredFields(){
        List<Vector2d> preferred = planter1.getPreferredFieldsAvailable();
        List<Vector2d> regular = planter1.getRegularFieldsAvailable();

        for (Vector2d field : preferred) {
            assertTrue(planter1.getFields().isPreferredField(field));
        }

        for (Vector2d field : regular) {
            assertFalse(planter1.getFields().isPreferredField(field));
        }
    }
}