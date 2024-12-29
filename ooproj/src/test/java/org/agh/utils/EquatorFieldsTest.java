package org.agh.utils;

import org.agh.model.Vector2d;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EquatorFieldsTest {

    private final EquatorFields equatorFields = new EquatorFields(10, 10, 20);

    @Test
    void testEquatorBoundsCalculation() {
        // Verify the start and end of the equator are calculated correctly
        assertEquals(4, equatorFields.getEquatorStart(), "Incorrect equator start position.");
        assertEquals(6, equatorFields.getEquatorEnd(), "Incorrect equator end position.");
    }

    @Test
    void testPreferredFieldsDefinedCorrectly() {
        // Ensure preferred fields (within equator bounds) are correctly defined
        Set<Vector2d> preferredFields = equatorFields.getPreferredFields();

        for (int y = 4; y < 6; y++) {
            for (int x = 0; x < 10; x++) {
                assertTrue(preferredFields.contains(new Vector2d(x, y)), "Preferred field not found where expected.");
            }
        }
    }

    @Test
    void testRegularFieldsDefinedCorrectly() {
        // Ensure regular fields (outside equator bounds) are correctly defined
        Set<Vector2d> regularFields = equatorFields.getRegularFields();
        Set<Vector2d> preferredFields = equatorFields.getPreferredFields();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Vector2d position = new Vector2d(x, y);
                if (y < 4 || y >= 6) {
                    assertTrue(regularFields.contains(position), "Regular field not found where expected.");
                } else {
                    assertFalse(regularFields.contains(position), "Field marked as regular but is preferred.");
                }
            }
        }
    }

    @Test
    void testIsPreferredField() {
        // Test the isPreferredField method
        Set<Vector2d> preferredFields = equatorFields.getPreferredFields();

        for (Vector2d field : preferredFields) {
            assertTrue(equatorFields.isPreferredField(field), "Field marked as regular but is preferred.");
        }

        Set<Vector2d> regularFields = equatorFields.getRegularFields();
        for (Vector2d field : regularFields) {
            assertFalse(equatorFields.isPreferredField(field), "Field marked as preferred but is regular.");
        }
    }

    @Test
    void allOrNoneRegularFields() {
        EquatorFields equatorFields = new EquatorFields(10, 10, 100);
        assertEquals(0, equatorFields.getRegularFields().size(), "There should be no regular fields.");
        assertEquals(100, equatorFields.getPreferredFields().size(), "There should be only preferred fields.");
        assertEquals(0, equatorFields.getEquatorStart());
        assertEquals(10, equatorFields.getEquatorEnd());
        equatorFields = new EquatorFields(10, 10, 0);
        assertEquals(100, equatorFields.getRegularFields().size(), "There should be only regular fields.");
        assertEquals(0, equatorFields.getPreferredFields().size(), "There should be no preferred fields.");
    }

    @Test
    void constructorHandlesZeroDimensions() {
        // Test the constructor with zero dimensions
        EquatorFields zeroEquatorFields = new EquatorFields(0, 0, 50);
        assertTrue(zeroEquatorFields.getPreferredFields().isEmpty(), "Preferred fields set is not empty.");
        assertTrue(zeroEquatorFields.getRegularFields().isEmpty(), "Regular fields set is not empty.");
    }
}
