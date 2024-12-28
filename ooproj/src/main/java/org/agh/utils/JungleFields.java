package org.agh.utils;

import org.agh.model.Vector2d;

import java.util.HashSet;
import java.util.Set;

import java.lang.Math;

public class JungleFields implements Fields {
    private final int width;
    private final int height;
    private final Set<Vector2d> preferredFields;
    private final Set<Vector2d> regularFields;
    private final Vector2d lowerLeftCorner;
    private final int squareSide;

    public JungleFields(int width, int height, int percentage) {
        this.width = width;
        this.height = height;
        this.preferredFields = new HashSet<>();
        this.regularFields = new HashSet<>();
        // Assigning a jungle square side that will cover at least 1/5 of all the fields, but it cannot exceed width
        // of height for obvious reasons
        squareSide = Math.min((int) Math.ceil(Math.sqrt(width * height / 5.0)), Math.min(width, height));
        lowerLeftCorner = new Vector2d((width - squareSide) / 2, (height - squareSide) / 2);
        this.definePreferredFields();
    }

    private void definePreferredFields() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x >= lowerLeftCorner.getX() && x < lowerLeftCorner.getX() + squareSide
                        && y >= lowerLeftCorner.getY() && y < lowerLeftCorner.getY() + squareSide) {
                    preferredFields.add(new Vector2d(x, y));
                } else regularFields.add(new Vector2d(x, y));
            }
        }
    }

    public boolean isPreferredField(Vector2d position) {
        return preferredFields.contains(position);
    }

    public Set<Vector2d> getPreferredFields() {
        return preferredFields;
    }

    public Set<Vector2d> getRegularFields() {
        return regularFields;
    }

}