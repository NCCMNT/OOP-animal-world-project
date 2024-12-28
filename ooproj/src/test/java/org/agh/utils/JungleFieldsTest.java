package org.agh.utils;

import org.agh.model.Vector2d;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JungleFieldsTest {

    private JungleFields jungleFields = new JungleFields(10, 10, 20);


    @Test
    void testPreferredFieldsDefinedCorrectly() {
        // Ensure preferred fields are correctly defined
        Set<Vector2d> preferredFields = jungleFields.getPreferredFields();
        Vector2d lowerLeftCorner = new Vector2d((10 - jungleFields.getSquareSide()) / 2, (10 - jungleFields.getSquareSide()) / 2);
        int squareSide = jungleFields.getSquareSide();

        for (int y = lowerLeftCorner.getY(); y < lowerLeftCorner.getY() + squareSide; y++) {
            for (int x = lowerLeftCorner.getX(); x < lowerLeftCorner.getX() + squareSide; x++) {
                assertTrue(preferredFields.contains(new Vector2d(x, y)), "Preferred field not found where expected.");
            }
        }
    }


    @Test
    void testIsPreferredField() {
        // Test the isPreferredField method
        Set<Vector2d> preferredFields = jungleFields.getPreferredFields();

        for (Vector2d field : preferredFields) {
            assertTrue(jungleFields.isPreferredField(field), "Field marked as regular but is preferred.");
        }

        Set<Vector2d> regularFields = jungleFields.getRegularFields();
        for (Vector2d field : regularFields) {
            assertFalse(jungleFields.isPreferredField(field), "Field marked as preferred but is regular.");
        }
    }

    @Test
    void edgeCases() {
        // Test the constructor with small dimensions
        JungleFields smallJungleFields = new JungleFields(3, 3, 100);
        assertEquals(0, smallJungleFields.getRegularFields().size(), "There are regular fields when all should be preferred");
        smallJungleFields = new JungleFields(3, 3, 0);
        assertEquals(0, smallJungleFields.getPreferredFields().size(), "There are preferred fields when all should be regular");
        smallJungleFields = new JungleFields(0, 0, 20);
        assertTrue( smallJungleFields.getPreferredFields().isEmpty(), "Preferred fields set is not empty.");
        assertTrue( smallJungleFields.getRegularFields().isEmpty(), "Regular fields set is not empty.");
    }

    @Test
    void testConstructorHandlesZeroDimensions() {
        // Test the constructor with zero dimensions

    }
}