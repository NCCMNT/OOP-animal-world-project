package org.agh.model;

import java.util.*;

import static java.lang.Math.*;

public class Animal extends WorldElement implements Comparable<Animal> {
    private int energy;
    private MapDirection direction;
    private int age;
    private List<Integer> genom;
    private int activeGen;
    private int childCount;
    private int animalId;
    private final WorldMap worldMap;
    private static final Random random = new Random();

    private Animal parent1;
    private Animal parent2;
    private int plantsEaten;
    private int descendants;
    private Optional<Integer> deathDate;

    private Boolean isRelatedToNewborn;

    private static final double AGING_CONSTANT = 1.0/30.0;
    private static final double AGING_SHIFT = log(5.0/4.0); // do not touch

    private Animal(WorldMap worldMap, int energy, Vector2d position){
        this.worldMap = worldMap;
        this.energy = energy;
        this.position = position;
        this.age = 0;
        this.childCount = 0;
        this.plantsEaten = 0;
        this.descendants = 0;
        this.isRelatedToNewborn = false;
        this.deathDate = Optional.empty();
        this.direction = MapDirection.intToMapDirection(random.nextInt(8));
    }
    // Animal when born in the beginning of simulation
    public Animal(WorldMap worldMap, int initEnergy, Vector2d position, int genomLen, int animalId) {
        this(worldMap, initEnergy, position);
        this.genom = new ArrayList<>(genomLen);
        for (int i = 0; i < genomLen; i++) {
            genom.add(random.nextInt(8));
        }
        this.activeGen = random.nextInt(genomLen);
        this.animalId = animalId;
        this.parent1 = null;
        this.parent2 = null;
    }
    // Animal when born from two parents
    public Animal(Animal parent1, Animal parent2, int energy, int animalId) {
        this(parent1.worldMap, energy, parent1.getPosition());
        parent1.childCount += 1; parent2.childCount += 1;
        int genomLen = parent1.genom.size();
        int sumEnergy = parent1.energy + parent2.energy;
        int gensFromParent1 = round(((float) (genomLen * parent1.energy) )/ sumEnergy); // Straszne susge, trzeba poczytać
        int gensFromParent2 = genomLen - gensFromParent1;
        this.genom = new ArrayList<>(genomLen);

        if (random.nextBoolean()) {
            for (int i = 0; i < gensFromParent1; i++) {
                genom.add(parent1.genom.get(i));
            }
            for (int i = 0; i < gensFromParent2; i++) {
                genom.add(parent2.genom.get(i + gensFromParent1));
            }
        }
        else{
            for (int i = 0; i < gensFromParent2; i++) {
                genom.add(parent2.genom.get(i));
            }
            for (int i = 0; i < gensFromParent1; i++) {
                genom.add(parent1.genom.get(i + gensFromParent2));
            }
        }

        this.animalId = animalId;
        this.activeGen = random.nextInt(genomLen);
        this.parent1 = parent1;
        this.parent2 = parent2;
        descendantNotified(parent1);
        descendantNotified(parent2);
        descendantFinished(parent1);
        descendantFinished(parent2);
    }

    public void move(){
        energy -= 1;
        if(worldMap.isAging() && skipOfOldAge()){ return; }
        direction = direction.rotate(genom.get(activeGen));
        activeGen = (activeGen + 1)%genom.size();
        Vector2d desiredPosition = position.add(this.direction.toUnitVector());
        Vector2d actualPosition = worldMap.whereToMove(desiredPosition);
        if(actualPosition == null){
            direction = direction.opposite();
        }
        else{
            position = actualPosition;
        }
    }

    private boolean skipOfOldAge(){
        double threshold = exp(-1.0*age*AGING_CONSTANT - AGING_SHIFT) + 0.2;
        return (random.nextDouble() > threshold);
    }

    public void feed(int energy){
        this.energy += energy;
        this.plantsEaten += 1;
    }

    public void loseEnergy(int energy){
        this.energy -= energy;
    }

    public void age(int timeUnit){
        this.age += timeUnit;
    }

    @Override
    public String toString() {
        return this.position.toString();
    }

    public String mapMarker(){return this.direction.toArrow();}

    public int getEnergy() {
        return energy;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public int getAge() {
        return age;
    }

    public List<Integer> getGenom() {
        return genom;
    }

    public int getActiveGen() {
        return activeGen;
    }

    public int getAnimalId() { return animalId; }

    public int getChildCount() {
        return childCount;
    }

    public int getDescendants() {
        return descendants;
    }

    public void setDeathDate(int deathDate) {
        this.deathDate = Optional.of(deathDate);
    }

    /**
     * Comparator used for sorting the animals in List, it sorts first by position then by all other factors.
     * After sorting using this animals will be grouped by position and inside a position those with the priority to
     * eat or procreate will be first.
     * @param other animal to compare to
     * @return int value determining their order
     */
    public int compareByPosition(Animal other){
        if(position.getY() - other.position.getY() != 0){
            return position.getY() - other.position.getY();
        }
        if(position.getX() - other.position.getX() != 0){
            return position.getX() - other.position.getX();
        }
        return -1*compareTo(other);
    }

    @Override
    public int compareTo(Animal other) {
        if(energy - other.energy != 0){return energy - other.energy;}
        if(age - other.age != 0){return age - other.age;}
        if (childCount - other.childCount != 0){return childCount - other.childCount;}
        return 0;
    }

    // For testing purposes
    public static Animal createCustomAnimal(Vector2d position, int energy, MapDirection direction, int age, List<Integer> genom,
                                             int activeGen, int childCount, int animalId, final WorldMap worldMap){
        Animal newAnimal = new Animal(worldMap, energy, position);
        newAnimal.direction = direction;
        newAnimal.age = age;
        newAnimal.genom = genom;
        newAnimal.activeGen = activeGen;
        newAnimal.animalId = animalId;
        newAnimal.childCount = childCount;
        return newAnimal;
    }

    private static void descendantNotified(Animal animal){
        if(animal.isRelatedToNewborn) return;
        animal.isRelatedToNewborn = true;
        animal.descendants += 1;
        if(animal.parent1 != null) descendantNotified(animal.parent1);
        if(animal.parent2 != null) descendantNotified(animal.parent2);
    }
    private static void descendantFinished(Animal animal){
        if(!animal.isRelatedToNewborn) return;
        animal.isRelatedToNewborn = false;
        if(animal.parent1 != null) descendantFinished(animal.parent1);
        if(animal.parent2 != null) descendantFinished(animal.parent2);
    }

    private String genomHighlight(){
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < genom.size(); i++) {
            if(i == activeGen) result.append("<");
            result.append(genom.get(i));
            if(i == activeGen) result.append(">");
            if(i != genom.size()-1) result.append(", ");
        }
        result.append("]");
        return result.toString();
    }

    public String infoUI(){
        return( "Status: " + deathDate.map(integer -> "Dead").orElse("Alive") + "\n"
                + "Genom: " + genomHighlight() + "\n"
                + "Active gen: " + activeGen + "\n"
                + "Energy: " + energy + "\n"
                + "Plants eaten: " + plantsEaten + "\n"
                + "Children number: " + childCount + "\n"
                + "Descendants number: " + descendants + "\n"
                + "Age: " + age + "\n"
                + "Death date: " + deathDate.map(Objects::toString).orElse("NaN")
        );
    }

}
