package org.agh.model;

import java.lang.reflect.Array;
import java.util.Random;

public class Mutator {
    private final int minMutations;
    private final int maxMutations;
    private static final Random random = new Random();
    private int[] optionsArray;

    public Mutator(int minMutations, int maxMutations, int genomLen) {
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
        this.optionsArray = new int[genomLen];
        for (int i = 0; i < genomLen; i++) {
            optionsArray[i] = i;
        }
        if(maxMutations > genomLen){
            throw new IllegalArgumentException("maxMutations > genomLen");
        }
    }

    public void mutate(Animal animal) {
        int mutationAmount = random.nextInt(maxMutations+1-minMutations) + minMutations;
        int selfIndex;
        int foreignIndex;
        for (int i = 0; i < mutationAmount; i++) {
            selfIndex = random.nextInt(optionsArray.length - i);
            foreignIndex = optionsArray[selfIndex];
            animal.getGenom().set(foreignIndex, (animal.getGenom().get(foreignIndex) + random.nextInt(7) + 1) % 8);
            optionsArray[selfIndex] = optionsArray[optionsArray.length - i - 1];
            optionsArray[optionsArray.length - i - 1] = foreignIndex;
        }
    }
}
