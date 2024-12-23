package org.agh.model;

import org.agh.utils.MapSettings;
import org.agh.utils.MapVisualizer;

import java.util.*;

public class WorldMap {
    private final MapSettings settings;
    private final int width;
    private final int height;

    private final int plantsPerTurn;
    private final int plantEnergy;
    private final int energeticFertilityThreshold;
    private final int energeticBreedingCost;

    private Planter planter;

    private List<Animal> animals;
    private Mutator mutator;
    private static final Random random = new Random();

    public WorldMap(int width, int height, int plantsPerTurn, int plantEnergy, int energeticFertilityThreshold, int energeticBreedingCost, int minMutations, int maxMutations) {
        if (energeticBreedingCost < energeticFertilityThreshold) throw new IllegalArgumentException("breedingCost < fertilityThreshold");
        if (minMutations > maxMutations) throw new IllegalArgumentException("minMutations > maxMutations");
        this.settings = null; // <- for implementation without map settings
        this.width = width;
        this.height = height;
        this.plantsPerTurn = plantsPerTurn;
        this.plantEnergy = plantEnergy;
        this.energeticFertilityThreshold = energeticFertilityThreshold;
        this.energeticBreedingCost = energeticBreedingCost;

        planter = new EquatorPlanter(width, height, plantEnergy);
    }

    public WorldMap(int width, int height, int plantsPerTurn, int plantEnergy, int energeticFertilityThreshold, int energeticBreedingCost, int minMutations, int maxMutations,
                    int startingAnimals, int startingEnergy, int genomLen, int startingPlants) {
        this(width, height, plantsPerTurn, plantEnergy, energeticFertilityThreshold, energeticBreedingCost, minMutations, maxMutations);
        initializeAnimals(startingAnimals, startingEnergy, genomLen);
        initializeMutator(minMutations, maxMutations, genomLen);
        planter.generatePlants(startingPlants);
    }

    public WorldMap(MapSettings mapSettings){
        this.settings = mapSettings;
        this.height = mapSettings.height();
        this.width = mapSettings.width();
        this.plantsPerTurn = mapSettings.plantsPerTurn();
        this.plantEnergy =  mapSettings.plantEnergy();
        this.energeticFertilityThreshold =  mapSettings.energeticFertilityThreshold();
        this.energeticBreedingCost = mapSettings.energeticBreedingCost();

        planter = new EquatorPlanter(width, height, plantEnergy, mapSettings.startingNumberOfPlants());

        initializeMutator(mapSettings.minMutations(), mapSettings.maxMutations(), mapSettings.genomLen());
        initializeAnimals(mapSettings.startingNumberOfAnimals(), mapSettings.startingEnergy(), mapSettings.genomLen());
    }

    private void initializeAnimals(int amountOfAnimals, int startingEnergy, int genomLen) {
        animals = new ArrayList<>();
        for (int i = 0; i < amountOfAnimals; i++) {
            animals.add(new Animal(this, startingEnergy, new Vector2d(random.nextInt(width), random.nextInt(height)), genomLen));
        }
        animals.sort(Animal::compareByPosition);
    }

    public void growPlants(){
        planter.generatePlants(plantsPerTurn);
    }

    //TODO (co można zrobić next)
    // -> dodać mechanizm jedzenia plantów, jeśli roślina znika (jest zjedzona) z pola to powinna zwrócić swoją pozycję do puli FieldsAvailable
    // -> dodać mechanizm determinowania, które zwierzę zjada roślinę
    // -> dodać mechanizm rozmnażania zwierząt

    public void initializeMutator(int minMutations, int maxMutations, int genomLen) {
        mutator = new Mutator(minMutations, maxMutations, genomLen);
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
        animals.sort(Animal::compareByPosition);
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
        System.out.println("There are " + animals.size() + " animals in the world: ");
        for(Animal animal: animals) {
            System.out.println("    Energy=" + animal.getEnergy() + "|Position=" + animal.getPosition().toString() +
                    "|direction=" + animal.getDirection().toString() + "|genom=" + animal.getGenom().toString() ) ;
        }
    }

    public boolean isPreferred(Vector2d position) {
        return planter.isPreferred( position );
    }


}
