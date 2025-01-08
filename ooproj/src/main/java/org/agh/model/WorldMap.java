package org.agh.model;

import org.agh.utils.MapSettings;
import org.agh.utils.MapVisualizer;
import org.agh.utils.PlanterType;

import java.util.*;

public class WorldMap {
    private final MapSettings settings;
    private final int width;
    private final int height;

    private final int plantsPerTurn;
    private final int energeticFertilityThreshold;
    private final int energeticBreedingCost;

    private Planter planter;

    private List<Animal> animals;
    private int animalId = 0;
    private Mutator mutator;
    private final boolean isAging;
    private static final Random random = new Random();

// Replaced by Mapsetting constructor.
//
//    public WorldMap(int width, int height, int plantsPerTurn, int plantEnergy, int energeticFertilityThreshold, int energeticBreedingCost, int minMutations, int maxMutations, boolean isAging) {
//        if (energeticBreedingCost < energeticFertilityThreshold) throw new IllegalArgumentException("breedingCost < fertilityThreshold");
//        if (minMutations > maxMutations) throw new IllegalArgumentException("minMutations > maxMutations");
//        this.settings = null; // <- for implementation without map settings
//        this.width = width;
//        this.height = height;
//        this.plantsPerTurn = plantsPerTurn;
//        this.energeticFertilityThreshold = energeticFertilityThreshold;
//        this.energeticBreedingCost = energeticBreedingCost;
//        this.isAging = isAging;
//
//        planter = new EquatorPlanter(width, height, plantEnergy);
//    }
//
//    public WorldMap(int width, int height, int plantsPerTurn, int plantEnergy, int energeticFertilityThreshold, int energeticBreedingCost, int minMutations, int maxMutations,
//                    int startingAnimals, int startingEnergy, int genomLen, int startingPlants, boolean isAging) {
//        this(width, height, plantsPerTurn, plantEnergy, energeticFertilityThreshold, energeticBreedingCost, minMutations, maxMutations, isAging);
//        initializeAnimals(startingAnimals, startingEnergy, genomLen);
//        initializeMutator(minMutations, maxMutations, genomLen);
//        planter.generatePlants(startingPlants);
//    }

    public WorldMap(MapSettings mapSettings){
        this.settings = mapSettings;
        this.height = mapSettings.height();
        this.width = mapSettings.width();
        this.plantsPerTurn = mapSettings.plantsPerTurn();
        this.energeticFertilityThreshold =  mapSettings.energeticFertilityThreshold();
        this.energeticBreedingCost = mapSettings.energeticBreedingCost();
        this.isAging = mapSettings.isAging();

        initializePlanter(this.width, this.height, mapSettings.plantEnergy(), mapSettings.startingNumberOfPlants(), mapSettings.planterType());
        initializeMutator(mapSettings.minMutations(), mapSettings.maxMutations(), mapSettings.genomLen());
        initializeAnimals(mapSettings.startingNumberOfAnimals(), mapSettings.startingEnergy(), mapSettings.genomLen());
    }

    private void initializeAnimals(int amountOfAnimals, int startingEnergy, int genomLen) {
        animals = new ArrayList<>();
        for (int i = 0; i < amountOfAnimals; i++) {
            animals.add(new Animal(this, startingEnergy, new Vector2d(random.nextInt(width), random.nextInt(height)), genomLen, animalId));
            animalId++;
        }

        //printing info for logs
        System.out.println("Animals initialized");
    }

    private void initializeMutator(int minMutations, int maxMutations, int genomLen) {
        mutator = new Mutator(minMutations, maxMutations, genomLen);

        //printing info for logs
        System.out.println("Mutator initialized");
    }

    private void initializePlanter(int width, int height, int plantEnergy, int startingPlants, PlanterType planterType) {
        switch (planterType){
            case EQUATOR -> planter = new EquatorPlanter(width, height, plantEnergy, startingPlants);
            case JUNGLE -> planter = new JunglePlanter(width, height, plantEnergy, startingPlants);
        }
        //printing info for logs
        System.out.println("Planter initialized, type = " + planterType.name());
    }

    public void checkStateOfAllAnimals() {
        int count = 0;
        for (int i = animals.size() - 1; i >= 0; i--) {
            //if animal has 0 energy - it dies
            if (animals.get(i).getEnergy() == 0) {
                animals.remove(i);
                count++;
            }
            //if animal lives then ages by one time unit
            else{
                animals.get(i).age(1);
            }
        }
        //printing info for logs
        System.out.println("Removed " + count + " dead animals from map");
    }

    public void breedAllAnimals(){
        //wariant dwóch najsilniejszych na danym polu
        animals.sort(Animal::compareByPosition);

        int i = 0;
        while (i < animals.size() - 1){
            Animal potentialParent1 = animals.get(i);
            Animal potentialParent2 = animals.get(i+1);
            if (potentialParent1.getPosition().equals(potentialParent2.getPosition()) &&
                potentialParent1.getEnergy() >= energeticFertilityThreshold && potentialParent2.getEnergy() >= energeticFertilityThreshold) {

                //printing info for logs
                System.out.println("Animals " + potentialParent1.getAnimalId() + " and " + potentialParent2.getAnimalId() + " mated");

                breedAnimals(potentialParent1, potentialParent2);
            }
            // We move until we get to another position, because current position is already occupied by the first 2
            // strongest animals.
            while( i < animals.size() - 1 && animals.get(i).getPosition().equals(animals.get(i+1).getPosition())){
                i++;
            }
            i++;
        }
    }

    private void breedAnimals(Animal animal1, Animal animal2) {
        animal1.loseEnergy(energeticBreedingCost);
        animal2.loseEnergy(energeticBreedingCost);
        Animal child = new Animal(animal1, animal2, 2*energeticBreedingCost, animalId);
        animalId++;
        mutator.mutate(child);
        animals.add(child);
        updateDescendants();

        //printing info for logs
        System.out.println("Animal " + child.getAnimalId() + " was born");
    }

    public void feedAllAnimals(){
        //list of animals must be sorted by energy
        animals.sort(Comparator.reverseOrder());
        //we feed strongest first due to how list is sorted
        for (Animal animal : animals) {
            feedAnimal(animal);
        }

        //printing info for logs
        System.out.println("All animals were fed");
    }

    private void feedAnimal(Animal animal){
        Vector2d position = animal.getPosition();
        Plant plant = planter.removePlant(position);
        if (plant != null) {
            animal.feed(plant.getEnergy());
        }
    }

    public void growPlants(){
        planter.generatePlants(plantsPerTurn);

        //printing info for logs
        System.out.println("World blooms with new plants");
    }

    public Vector2d whereToMove( Vector2d desiredPosition ) {
        if(desiredPosition.getY() >= height || desiredPosition.getY() < 0) {
            return null;
        }
        return new Vector2d((desiredPosition.getX()+width)%width, desiredPosition.getY());
    }

    public void moveAllAnimals(){
        for(Animal animal: animals) {
            animal.move();
        }

        //printing info for logs
        System.out.println("All animals were moved");
    }

    public void ageAllAnimals(){
        for (Animal animal : animals) {
            animal.age(1);
        }
    }

    public boolean isAging() {
        return isAging;
    }

    public void updateDescendants(){
        for (Animal animal : animals) {
            animal.updateDescendant();
        }
    }

    // Visual helpers

    public Optional<WorldElement> elementAt(Vector2d position) {
        for (Animal animal : animals) {
            if(animal.getPosition().equals(position)) {
                return Optional.of(animal);
            }
        }
        return Optional.ofNullable(planter.plantAt(position));
    }

    public String toString(){
        MapVisualizer visualizer = new MapVisualizer(this);
        return  visualizer.draw(new Vector2d(width - 1, height - 1));
    }

    public void printAnimalInfo(){
        animals.sort(Animal::compareByPosition);
        System.out.println("There are " + animals.size() + " animals in the world: ");
        for(Animal animal: animals) {
            System.out.println("Animal: " + animal.getAnimalId() + " | energy: " + animal.getEnergy() + " | position: " + animal.getPosition().toString() +
                    " | direction: " + animal.getDirection().toString() + " | genom: " + animal.getGenom().toString() +
                    " | active gen: " + animal.getActiveGen() + " | age: " + animal.getAge() + " | desc: " + animal.getDescendants());
        }
    }

    public boolean isPreferred(Vector2d position) {
        return planter.isPreferred( position );
    }

    // For Testing Purposes

    void setAnimals (List<Animal> animals) {
        this.animals = animals;
        System.out.println("Animals were set, if that's not a test contact your local software engineer");
    }
    List<Animal> getAnimals ( ) {
        return animals;
    }
}
