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
) implements Serializable {}
