package org.agh.model;

import org.agh.utils.MapSettings;
import org.agh.utils.PlanterType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    MapSettings mapSettings1 = new MapSettings(5, 5, 5, 3, 2, PlanterType.EQUATOR, 4,
            10, 1, 1, false, 0, 0, 2);

    MapSettings mapSettings1withAging = new MapSettings(5, 5, 5, 3, 2, PlanterType.EQUATOR, 4,
            10, 0, 1, true, 0, 0, 2);

    WorldMap worldMap1 = new WorldMap(mapSettings1);

    // MapSettings mapSettings2 = new MapSettings(50, 50, 100, 1, 20, PlanterType.EQUATOR, 0, 0, 0, 1, false, 10, 2, 2);
    // WorldMap worldMap2 = new WorldMap(mapSettings2);

    WorldMap worldMap1withAging = new WorldMap(mapSettings1withAging);

    @Test
    void testMoveAllAnimals() {
        worldMap1.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 10; i++) {
            worldMap1.moveAllAnimals();
            worldMap1.printAnimalInfo();
            System.out.println(worldMap1);
        }
        worldMap1.printAnimalInfo();
    }

    @Test
    void testFeedingAllAnimals() {
        worldMap1.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 10; i++) {
            worldMap1.moveAllAnimals();
            System.out.println("ANIMAL PRZED JEDZENIEM");
            worldMap1.printAnimalInfo();

            worldMap1.feedAllAnimals();
            System.out.println("ANIMAL PO JEDZENIU");
            worldMap1.printAnimalInfo();

            System.out.println(worldMap1);
            worldMap1.growPlants();
            System.out.println(worldMap1);
        }
        worldMap1.printAnimalInfo();
    }

    @Test
    void manualBreedingAllAnimals() {
        worldMap1.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 30; i++) {
            worldMap1.moveAllAnimals();
            System.out.println("PRZED ROZMNAŻANIEM");
            worldMap1.printAnimalInfo();

            worldMap1.breedAllAnimals();
            worldMap1.checkStateOfAllAnimals();
            System.out.println("PO ROZMNAŻANIU I USUNIĘCIU ZWIERZAKÓW Z ENERGIĄ 0");
            worldMap1.printAnimalInfo();

            worldMap1.growPlants();
            System.out.println(worldMap1);
        }
        worldMap1.printAnimalInfo();
    }

    @Test
    void manualWithAging() {
        for (int i = 0; i < 10; i++) {
            worldMap1withAging.checkStateOfAllAnimals();
        }
        worldMap1withAging.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 5; i++) {
            worldMap1withAging.moveAllAnimals();
            worldMap1withAging.checkStateOfAllAnimals();
            worldMap1withAging.printAnimalInfo();
            System.out.println(worldMap1withAging);
        }
        for (int i = 0; i < 100; i++) {
            worldMap1withAging.checkStateOfAllAnimals();
        }
        worldMap1withAging.printAnimalInfo();
        System.out.println(worldMap1);
        for (int i = 0; i < 5; i++) {
            worldMap1withAging.moveAllAnimals();
            worldMap1withAging.checkStateOfAllAnimals();
            worldMap1withAging.printAnimalInfo();
            System.out.println(worldMap1withAging);
        }
    }

    // Custom tests

    MapSettings customSettings = new MapSettings(3, 1, 3, 3, 2, PlanterType.EQUATOR, 0,
            0, 5, 2, false, 0, 0, 2);



    Vector2d place1 = new Vector2d(0, 0);
    Vector2d place2 = new Vector2d(0, 1);
    // Vector2d place3 = new Vector2d(0, 2);

    WorldMap customMap = null;
    Animal animal1 = null;
    Animal animal2 = null;
    Animal animal3 = null;
    Animal animal4 = null;
    Animal animal5 = null;
    Animal animal6 = null;
    Animal animal7 = null;

    private void setup() {
        customMap = new WorldMap(customSettings);
        animal1 = Animal.createCustomAnimal(place1, 7, MapDirection.NORTH, 5, List.of(1, 1), 0, 3, 11, customMap);
        animal2 = Animal.createCustomAnimal(place1, 6, MapDirection.NORTH, 5, List.of(2, 2), 0, 3, 12, customMap);
        animal3 = Animal.createCustomAnimal(place1, 5, MapDirection.NORTH, 5, List.of(3, 3), 0, 3, 13, customMap);
        animal4 = Animal.createCustomAnimal(place1, 5, MapDirection.NORTH, 4, List.of(4, 4), 0, 3, 14, customMap);
        animal5 = Animal.createCustomAnimal(place1, 5, MapDirection.NORTH, 5, List.of(5, 5), 0, 2, 15, customMap);
        animal6 = Animal.createCustomAnimal(place2, 5, MapDirection.NORTH, 4, List.of(6, 6), 0, 3, 16, customMap);
        animal7 = Animal.createCustomAnimal(place2, 5, MapDirection.NORTH, 5, List.of(7, 7), 0, 2, 17, customMap);
    }

    @Test
    void breedingInOneSpot(){
        setup();

        List<Animal> animalsBeforeBreeding = new ArrayList<>(List.of(animal1, animal2, animal3, animal4, animal5));
        customMap.setAnimals( animalsBeforeBreeding);
        customMap.breedAllAnimals();
        assertEquals(5, animal1.getEnergy());
        assertEquals(4, animal2.getEnergy());
        assertEquals(5, animal3.getEnergy());
        assertEquals(5, animal4.getEnergy());
        assertEquals(5, animal5.getEnergy());
        List<Animal> animalsAfterBreeding = customMap.getAnimals();
        assertEquals(6, animalsAfterBreeding.size());
        for (Animal animal : animalsAfterBreeding) {
            if(!animalsAfterBreeding.contains(animal)){
                assertEquals(4, animal.getEnergy());
                assertTrue(animal.getGenom().equals(new ArrayList<>(List.of(1,2))) || animal.getGenom().equals(new ArrayList<>(List.of(2,1))) );
            }
        }
        animalsBeforeBreeding = animalsAfterBreeding;
        customMap.breedAllAnimals();
        animalsAfterBreeding = customMap.getAnimals();
        assertEquals(3, animal1.getEnergy());
        assertEquals(4, animal2.getEnergy());
        assertEquals(3, animal3.getEnergy());
        assertEquals(5, animal4.getEnergy());
        assertEquals(5, animal5.getEnergy());
        assertEquals(7, animalsAfterBreeding.size());
        for (Animal animal : animalsAfterBreeding) {
            if(!animalsBeforeBreeding.contains(animal)){
                assertEquals(4, animal.getEnergy());
                assertTrue(animal.getGenom().equals(new ArrayList<>(List.of(1,3))) || animal.getGenom().equals(new ArrayList<>(List.of(3,1))) );
            }
        }
        animalsBeforeBreeding = animalsAfterBreeding;
        customMap.breedAllAnimals();
        animalsAfterBreeding = customMap.getAnimals();
        assertEquals(3, animal1.getEnergy());
        assertEquals(4, animal2.getEnergy());
        assertEquals(3, animal3.getEnergy());
        assertEquals(3, animal4.getEnergy());
        assertEquals(3, animal5.getEnergy());
        assertEquals(8, animalsAfterBreeding.size());
        for (Animal animal : animalsAfterBreeding) {
            if(!animalsBeforeBreeding.contains(animal)){
                assertEquals(4, animal.getEnergy());
                assertTrue(animal.getGenom().equals(new ArrayList<>(List.of(4,5))) || animal.getGenom().equals(new ArrayList<>(List.of(5,4))) );
            }
        }
        customMap.breedAllAnimals();
        animalsAfterBreeding = customMap.getAnimals();
        assertEquals(8, animalsAfterBreeding.size());
    }

    @Test
    void breedingInTwoSpots(){
        setup();

        List<Animal> animalsBeforeBreeding = new ArrayList<>(List.of(animal1, animal2, animal3, animal6, animal7));
        customMap.setAnimals( animalsBeforeBreeding);
        customMap.breedAllAnimals();
        assertEquals(5, animal1.getEnergy());
        assertEquals(4, animal2.getEnergy());
        assertEquals(5, animal3.getEnergy());
        assertEquals(3, animal6.getEnergy());
        assertEquals(3, animal7.getEnergy());
        List<Animal> animalsAfterBreeding = customMap.getAnimals();
        assertEquals(7, animalsAfterBreeding.size());
        for (Animal animal : animalsAfterBreeding) {
            if(!animalsAfterBreeding.contains(animal)){
                assertEquals(4, animal.getEnergy());
                assertTrue(animal.getGenom().equals(new ArrayList<>(List.of(1,2))) || animal.getGenom().equals(new ArrayList<>(List.of(2,1)))
                    || animal.getGenom().equals(new ArrayList<>(List.of(6,7))) || animal.getGenom().equals(new ArrayList<>(List.of(7,6))) );
            }
        }
    }

    @Test
    void descendants(){
        MapSettings customSettings = new MapSettings(3, 1, 3, 3, 2, PlanterType.EQUATOR, 0,
                0, 5, 3, false, 0, 0, 2);
        WorldMap customMap = new WorldMap(customSettings);
        animal1 = Animal.createCustomAnimal(place1, 10, MapDirection.NORTH, 5, List.of(1, 1), 0, 3, 11, customMap);
        animal2 = Animal.createCustomAnimal(place1, 7, MapDirection.NORTH, 5, List.of(2, 2), 0, 3, 12, customMap);
        animal3 = Animal.createCustomAnimal(place1, 7, MapDirection.NORTH, 5, List.of(3, 3), 0, 3, 13, customMap);
        customMap.setAnimals(new ArrayList<>(List.of(animal1, animal2, animal3)));
        customMap.breedAllAnimals();
        customMap.breedAllAnimals();
        customMap.breedAllAnimals();
        assertEquals(3, animal1.getDescendants());
        assertEquals(2, animal2.getDescendants());
        assertEquals(2, animal3.getDescendants());
    }

    @Test
    void eatingSmall(){
        setup();

        List<Animal> animalsBeforeEating = new ArrayList<>(List.of( animal3, animal5, animal6, animal7));
        customMap.setAnimals( animalsBeforeEating);
        customMap.feedAllAnimals();
        assertEquals(8, animal3.getEnergy());
        assertEquals(5, animal5.getEnergy());
        assertEquals(5, animal6.getEnergy());
        assertEquals(8, animal7.getEnergy());
        assertInstanceOf(Plant.class, customMap.elementAt(new Vector2d(0, 2)).orElse(null));
    }

    @Test
    void eatingBig(){
        MapSettings jungleSettings = new MapSettings(10, 10, 100, 3, 0, PlanterType.JUNGLE, 0, 0,
        0, 0,false, 0, 0, 1);
        Stack<Integer> winners = new Stack<>();
        List<Integer> genom = List.of(1);
        winners.push(0); winners.push(1); winners.push(2); winners.push(3);

        while(!winners.empty()) {
            WorldMap jungleMap = new WorldMap(jungleSettings);
            for (int x = 3; x < 7; x++) {
                for (int y = 3; y < 7; y++) {
                    if (winners.empty()) {
                        break;
                    }
                    if (jungleMap.elementAt(new Vector2d(x, y)).isPresent() && jungleMap.elementAt(new Vector2d(x, y)).get() instanceof BigPlant) {
                        Vector2d lowerLeft = jungleMap.elementAt(new Vector2d(x, y)).get().getPosition();
                        Animal contender1 = Animal.createCustomAnimal(lowerLeft, 10, MapDirection.NORTH, 0, genom, 0, 0, 0, jungleMap);
                        Animal contender2 = Animal.createCustomAnimal(lowerLeft.add(new Vector2d(1, 0)), 10, MapDirection.NORTH, 0, genom, 0, 0, 0, jungleMap);
                        Animal contender3 = Animal.createCustomAnimal(lowerLeft.add(new Vector2d(0, 1)), 10, MapDirection.NORTH, 0, genom, 0, 0, 0, jungleMap);
                        Animal contender4 = Animal.createCustomAnimal(lowerLeft.add(new Vector2d(1, 1)), 10, MapDirection.NORTH, 0, genom, 0, 0, 0, jungleMap);
                        List<Animal> contenders = List.of(contender1, contender2, contender3, contender4);
                        int winner = winners.pop();
                        contenders.get(winner).feed(1);
                        jungleMap.setAnimals(new ArrayList<>(contenders));
                        jungleMap.feedAllAnimals();
                        for (int i = 0; i < 4; i++) {
                            if (i == winner) {
                                assertEquals(20, contenders.get(i).getEnergy());
                            } else {
                                assertEquals(10, contenders.get(i).getEnergy());
                            }
                        }
                    }

                }

            }
        }
    }
}
