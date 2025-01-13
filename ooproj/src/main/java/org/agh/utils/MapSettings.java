package org.agh.utils;

import java.io.Serializable;

public record MapSettings(
        int height,
        int width,
        int startingNumberOfPlants,
        int plantEnergy,
        int plantsPerTurn,
        PlanterType planterType,
        int startingNumberOfAnimals,
        int startingEnergy,
        int energeticFertilityThreshold,
        int energeticBreedingCost,
        boolean isAging,
        int minMutations,
        int maxMutations,
        int genomLen
) implements Serializable {
    public void validate() throws MapSettingsException{
        StringBuilder stringBuilder = new StringBuilder("Map settings validation failed due to:\n");

        if(height < 1)
            stringBuilder.append("Height must be greater than 0\n");
        if(width < 1)
            stringBuilder.append("Width must be greater than 0\n");
        if(startingNumberOfPlants < 0)
            stringBuilder.append("Starting number must be greater than 0\n");
        if(plantEnergy < 0)
            stringBuilder.append("Plant energy must be greater than 0\n");
        if(plantsPerTurn < 0)
            stringBuilder.append("Plants per turn must be greater than 0\n");
        if(energeticFertilityThreshold < 0)
            stringBuilder.append("Energy fertility threshold must be greater than 0\n");
        if(energeticBreedingCost < 0)
            stringBuilder.append("Energy breeding cost must be greater than 0\n");
        if(energeticBreedingCost > energeticFertilityThreshold)
            stringBuilder.append("Energy fertility threshold must be higher or equal to energy breeding cost\n");
        if(minMutations < 0)
            stringBuilder.append("Min mutations must be greater than 0\n");
        if(maxMutations < minMutations)
            stringBuilder.append("Max mutations must be greater than or equal to min mutations\n");
        if(maxMutations > genomLen)
            stringBuilder.append("Max mutations cannot be greater than genomLen\n");
        if(genomLen <= 0)
            stringBuilder.append("GenomLen must be greater than 0\n");

        if(!stringBuilder.isEmpty()){
            throw new MapSettingsException(stringBuilder.toString());
        }
    }
}


