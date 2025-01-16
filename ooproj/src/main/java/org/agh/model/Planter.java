package org.agh.model;

import java.util.HashMap;

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

    /**
     * @return hashmap of with plants as values and their position - Vector2d as keys
     */
    public HashMap<Vector2d,Plant> getPlants();

    /**
     * @param position
     * @return Plant that was removed from Planter (plants hashmap)
     */
    public Plant removePlant(Vector2d position);
}
