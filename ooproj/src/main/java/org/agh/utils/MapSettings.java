package org.agh.utils;

public record MapSettings(
        int height,
        int width,
        int startingNumberOfPlants,
        int plantEnergy,
        int plantsPerTurn,
        //uzupełnić <- wariant wzrostu roślin
        int startingNumberOfAnimals,
        int startingEnergy,
        int energeticFertilityThreshold,
        int energeticBreedingCost,
        //uzupełnić <- wariant starzenia się animali
        int minMutations,
        int maxMutations,
        int genomLen
) {}
