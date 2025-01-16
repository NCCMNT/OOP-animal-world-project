package org.agh.utils;

import java.util.List;

public record MapStatistics (
        int turn,
        int numberOfAnimals,
        int numberOfPlants,
        int numberOfEmptyFields,
        List<Integer> mostPopularGenom,
        String avgAnimalEnergy,
        String avgLifeSpan,
        String avgChildrenCount
) {
    /**
     *
     * @return List of Strings with every map statistics
     */
    public List<String> getStatisticsStringList() {
        return List.of(
                String.valueOf(turn),
                String.valueOf(numberOfAnimals),
                String.valueOf(numberOfPlants),
                String.valueOf(numberOfEmptyFields),
                String.valueOf(mostPopularGenom),
                avgAnimalEnergy,
                avgLifeSpan,
                avgChildrenCount
        );
    }
}
