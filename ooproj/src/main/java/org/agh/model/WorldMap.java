package org.agh.model;

import org.agh.utils.Fields;
import org.agh.utils.MapSettings;

import java.util.*;

public class WorldMap {
    private final MapSettings settings;
    private final int width;
    private final int height;

    private final int plantsPerTurn;
    private final int plantEnergy;
    private final int energeticFertilityThreshold;
    private final int energeticBreedingCost;

    private final Fields fields;
    private final Set<Vector2d> preferredFields;
    private final Set<Vector2d> regularFields;
    private final List<Vector2d> preferredFieldsAvailable;
    private final List<Vector2d> regularFieldsAvailable;
    private HashMap<Vector2d, WorldElement> plants = new HashMap<>();

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

        this.fields = (new Fields(this.width, this.height, 20));
        this.preferredFields = fields.getPreferredFields();
        this.regularFields = fields.getRegularFields();
        this.preferredFieldsAvailable = new ArrayList<>(preferredFields);
        this.regularFieldsAvailable = new ArrayList<>(regularFields);
    }

    public WorldMap(int width, int height, int plantsPerTurn, int plantEnergy, int energeticFertilityThreshold, int energeticBreedingCost, int minMutations, int maxMutations,
                    int startingAnimals, int startingEnergy, int genomLen, int startingGrass) {
        this(width, height, plantsPerTurn, plantEnergy, energeticFertilityThreshold, energeticBreedingCost, minMutations, maxMutations);
        initializeAnimals(startingAnimals, startingEnergy, genomLen);
        initializePlants(startingGrass);
        generateMutator(minMutations, maxMutations, genomLen);
    }

    public WorldMap(MapSettings mapSettings){
        this.settings = mapSettings;
        this.height = mapSettings.height();
        this.width = mapSettings.width();
        this.plantsPerTurn = mapSettings.plantsPerTurn();
        this.plantEnergy =  mapSettings.plantEnergy();
        this.energeticFertilityThreshold =  mapSettings.energeticFertilityThreshold();
        this.energeticBreedingCost = mapSettings.energeticBreedingCost();

        this.fields = (new Fields(this.width, this.height, 20));
        this.preferredFields = fields.getPreferredFields();
        this.regularFields = fields.getRegularFields();
        this.preferredFieldsAvailable = new ArrayList<>(preferredFields);
        this.regularFieldsAvailable = new ArrayList<>(regularFields);

        generateMutator(mapSettings.minMutations(), mapSettings.maxMutations(), mapSettings.genomLen());
        initializeAnimals(mapSettings.startingNumberOfAnimals(), mapSettings.startingEnergy(), mapSettings.genomLen());
        initializePlants(mapSettings.startingNumberOfPlants());
    }

    private void initializeAnimals(int amountOfAnimals, int startingEnergy, int genomLen) {
        animals = new ArrayList<>();
        for (int i = 0; i < amountOfAnimals; i++) {
            animals.add(new Animal(this, startingEnergy, new Vector2d(random.nextInt(width), random.nextInt(height)), genomLen));
        }
        animals.sort(Collections.reverseOrder());
    }

    private void initializePlants(int plantAmount) {
        int preferredFieldsCount = preferredFieldsAvailable.size();
        int regularFieldsCount = regularFieldsAvailable.size();

        for (int i = 0; i < plantAmount; i++) {
            double chance = random.nextDouble();
            //there is 80% chance that plant grows on a preferred field
            if (chance >= 0.2 && preferredFieldsCount > 0) {
                int ind = random.nextInt(preferredFieldsCount);
                Vector2d field = preferredFieldsAvailable.get(ind);
                plants.put(field, new Plant(field, plantEnergy));
                preferredFieldsAvailable.remove(ind);
                preferredFieldsCount--;
            }
            //there is 20% chance that plant grows on a regular field
            //if all preferred fields are already taken by plants, then for map to have exactly starting amount of plants
            //we have to distribute remaining plants onto regular field, hence second condition
            else if ( (chance < 0.2 && regularFieldsCount > 0) || (preferredFieldsCount == 0 && regularFieldsCount > 0)) {
                int ind = random.nextInt(regularFieldsCount);
                Vector2d field = regularFieldsAvailable.get(ind);
                plants.put(field, new Plant(field, plantEnergy));
                regularFieldsAvailable.remove(ind);
                regularFieldsCount--;
            }
        }
    }

    public void growPlants() {
        //TODO -> sprawdzić/zdecydować - gdy miejsca preferowane są zapełnione to dalej dokłanie ilość plantsPerTurn powinna się pojawić na mapie
        // czy tylko te faktycznie wylosowane za pomocą szansy 20% na polach zwykłych
        this.initializePlants(this.plantsPerTurn);
    }

    //TODO (co można zrobić next)
    // -> dodać mechanizm jedzenia plantów, jeśli roślina znika (jest zjedzona) z pola to powinna zwrócić swoją pozycję do puli FieldsAvailable
    // -> dodać mechanizm determinowania, które zwierzę zjada roślinę
    // -> dodać mechanizm rozmnażania zwierząt

    public void generateMutator(int minMutations, int maxMutations, int genomLen) {
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
        animals.sort(Collections.reverseOrder());
    }

    public void printAnimalInfo(){
        System.out.println("There are " + animals.size() + " animals in the world: ");
        for(Animal animal: animals) {
            System.out.println("    Energy=" + animal.getEnergy() + "|Position=" + animal.getPosition().toString() +
                    "|direction=" + animal.getDirection().toString() + "|genom=" + animal.getGenom().toString() ) ;
        }
    }

    //getters
    public HashMap<Vector2d, WorldElement> getPlants() { return plants; }
    public Fields getFields(){ return fields; }
    public List<Vector2d> getPreferredFieldsAvailable() { return preferredFieldsAvailable; }
    public List<Vector2d> getRegularFieldsAvailable() { return regularFieldsAvailable; }
}
