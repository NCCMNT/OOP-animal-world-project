package org.agh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    Animal animal1 = new Animal(10, new Vector2d(0,0), 5);
    Animal animal2 = new Animal(5, new Vector2d(0,0), 5);

    @Test
    void animalsMakeNewAnimalTest(){
        Animal childAnimal = new Animal(animal1, animal2, 8);
        System.out.println(animal1.getGenom());
        System.out.println(animal2.getGenom());
        System.out.println(childAnimal.getGenom());
    }
}