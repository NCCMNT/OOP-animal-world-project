package org.agh.model;

public class BigPlant extends Plant {
    private final Vector2d upperRight;

    public BigPlant(Vector2d lowerLeft, int energy) {
        super(lowerLeft, energy);
        upperRight = lowerLeft.add(new Vector2d(1,1));
    }



    @Override
    public String mapMarker() {
        return "#";
    }
}
