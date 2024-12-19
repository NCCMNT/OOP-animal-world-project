package org.agh.utils;

import org.agh.model.Vector2d;

import java.util.HashSet;
import java.util.Set;

public class Fields {
    private final int width;
    private final int height;
    private final Set<Vector2d> preferredFields;
    private final Set<Vector2d> regularFields;
    private final int equatorStart;
    private final int equatorEnd;

    public Fields(int width, int height, int percentage) {
        this.width = width;
        this.height = height;
        this.preferredFields = new HashSet<>();
        this.regularFields = new HashSet<>();
        this.equatorStart = (int)Math.ceil( ( (double)(100 - percentage)/200) * this.height);
        this.equatorEnd = equatorStart + (int)Math.ceil( ((double) percentage /100) * this.height);
        this.definePreferredFields();
    }

    private void definePreferredFields() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y >= equatorStart && y < equatorEnd) preferredFields.add(new Vector2d(x, y));
                else regularFields.add(new Vector2d(x, y));
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

    public int getEquatorStart() { return equatorStart; }
    public int getEquatorEnd() { return equatorEnd; }
}
