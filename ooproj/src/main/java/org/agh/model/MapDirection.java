package org.agh.model;

import java.util.Map;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH,
    SOUTH_EAST,
    SOUTH_WEST,
    WEST,
    EAST;

    public String toString(){
        return switch(this){
            case NORTH -> "N";
            case NORTH_EAST -> "NE";
            case NORTH_WEST -> "NW";
            case SOUTH -> "S";
            case SOUTH_EAST -> "SE";
            case SOUTH_WEST -> "SW";
            case WEST -> "W";
            case EAST -> "E";
        };
    }

    public int toInt(){
        return switch(this){
            case NORTH -> 0;
            case NORTH_EAST -> 1;
            case EAST -> 2;
            case SOUTH_EAST -> 3;
            case SOUTH -> 4;
            case SOUTH_WEST -> 5;
            case WEST -> 6;
            case NORTH_WEST -> 7;
        };
    }

    public static MapDirection intToMapDirection(int num){
        return switch(num){
            case 0 -> NORTH;
            case 1 -> NORTH_EAST;
            case 2 -> EAST;
            case 3 -> SOUTH_EAST;
            case 4 -> SOUTH;
            case 5 -> SOUTH_WEST;
            case 6 -> WEST;
            case 7 -> NORTH_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + num);
        };
    }

    /**
     *
     * @param num
     * @return
     */
    public MapDirection rotate(int num){
        return intToMapDirection((num + this.toInt()) % 8);
    }

    public MapDirection opposite(){
        return rotate(4);
    }

    public Vector2d toUnitVector(){
        return switch(this){
            case NORTH -> new Vector2d(0,1);
            case NORTH_EAST -> new Vector2d(1,1);
            case NORTH_WEST -> new Vector2d(-1,1);
            case SOUTH -> new Vector2d(0,-1);
            case SOUTH_EAST -> new Vector2d(1,-1);
            case SOUTH_WEST -> new Vector2d(-1,-1);
            case WEST -> new Vector2d(-1,0);
            case EAST -> new Vector2d(1,0);
        };
    }
}
