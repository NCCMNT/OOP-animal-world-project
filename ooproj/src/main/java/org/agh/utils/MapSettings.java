package org.agh.utils;

import org.agh.model.Plant;

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
        if(height < 1)
            throw new MapSettingsException("Height must be greater than 0");
        if(width < 1)
            throw new MapSettingsException("Width must be greater than 0");
        if(startingNumberOfPlants < 0)
            throw new MapSettingsException("Starting number must be greater than 0");
        if(plantEnergy < 0)
            throw new MapSettingsException("Plant energy must be greater than 0");
        if(plantsPerTurn < 0)
            throw new MapSettingsException("Plants per turn must be greater than 0");
        if(energeticFertilityThreshold < 0)
            throw new MapSettingsException("Energy fertility threshold must be greater than 0");
        if(energeticBreedingCost < 0)
            throw new MapSettingsException("Energy breeding cost must be greater than 0");
        if(energeticBreedingCost > energeticFertilityThreshold)
            throw new MapSettingsException("Energy fertility threshold must be higher or equal to energy breeding cost");
        if(minMutations < 0)
            throw new MapSettingsException("Min mutations must be greater than 0");
        if(maxMutations < minMutations)
            throw new MapSettingsException("Max mutations must be greater than or equal to min mutations");
        if(maxMutations > genomLen)
            throw new MapSettingsException("Max mutations cannot be greater than genomLen");
        if(genomLen <= 0)
            throw new MapSettingsException("GenomLen must be greater than 0");
    }
}


