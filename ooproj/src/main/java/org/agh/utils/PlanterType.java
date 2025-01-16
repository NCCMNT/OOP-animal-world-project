package org.agh.utils;

public enum PlanterType {
    EQUATOR,
    JUNGLE;

    /**
     * Parses given String into PlanterType, currently only EQUATOR, JUNGLE are available
     * @param string
     * @return PlanterType
     */
    public static PlanterType fromString(String string){
        return switch (string){
            case "JUNGLE" -> JUNGLE;
            case "EQUATOR" -> EQUATOR;
            default -> throw new IllegalArgumentException("Invalid Planter Type");
        };
    }
}
