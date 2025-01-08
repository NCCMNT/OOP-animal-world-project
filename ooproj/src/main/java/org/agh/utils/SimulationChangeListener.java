package org.agh.utils;

import org.agh.model.WorldMap;

public interface SimulationChangeListener {
    void mapChanged(WorldMap worldMap, String message);
}
