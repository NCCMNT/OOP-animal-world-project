package org.agh;

import org.agh.model.Animal;
import org.agh.model.Vector2d;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class AnimalTest {
    Animal animal1 = new Animal(null, 10, new Vector2d(0,0), 5);
    Animal animal2 = new Animal(null, 5, new Vector2d(0,0), 5);

    @Test
    void animalsMakeNewAnimalTest(){
        Animal childAnimal = new Animal(animal1, animal2, 8);
        System.out.println(animal1.getGenom());
        System.out.println(animal2.getGenom());
        System.out.println(childAnimal.getGenom());
    }

    @Test
    void sortAnimals(){
        List<Animal> animals = new java.util.ArrayList<>(List.of(animal1, animal2, new Animal(animal1, animal2, 8),
                new Animal(null, 6, new Vector2d(1, 1), 5),
                new Animal(null, 7, new Vector2d(1, 1), 5),
                new Animal(null, 5, new Vector2d(1, 1), 5),
                animal1
        ));

        System.out.println("Unsorted:");
        for (Animal animal : animals) {
            System.out.print(animal.getEnergy() + animal.getPosition().toString() + "; ");
        }
        animals.sort(Collections.reverseOrder());
        System.out.println("\nSorted:");
        for (Animal animal : animals) {
            System.out.print(animal.getEnergy() + animal.getPosition().toString()+ "; ");
        }
    }
}