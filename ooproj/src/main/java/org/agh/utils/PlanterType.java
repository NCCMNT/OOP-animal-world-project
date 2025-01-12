package org.agh.utils;

import org.agh.model.MapDirection;

public enum PlanterType {
    EQUATOR,
    JUNGLE;

    public static PlanterType fromString(String string){
        return switch (string){
            case "JUNGLE" -> JUNGLE;
            case "EQUATOR" -> EQUATOR;
            default -> throw new IllegalArgumentException("Invalid Planter Type");
        };
    }
}
