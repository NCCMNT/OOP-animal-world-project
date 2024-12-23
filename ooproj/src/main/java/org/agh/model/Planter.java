package org.agh.model;

public interface Planter {
    /**
     * Generates specified amount of plants
     * @param plantAmount
     */
    public void generatePlants(int plantAmount);


    /**
     * Informs whether the position is favourised by planter for map drawing purposes
     * @param position
     * @return boolean
     */
    public boolean isPreferred(Vector2d position);

    /**
     * @param position
     * @return plant growing at specified position or Null if none grow there.
     */
    public Plant plantAt(Vector2d position);
}
