package org.agh.model;

import org.agh.utils.EquatorFields;

import java.util.*;

public class EquatorPlanter implements Planter{

    private HashMap<Vector2d, Plant> plants = new HashMap<>();

    private final Set<Vector2d> preferredFields;
    private final Set<Vector2d> regularFields;
    private final List<Vector2d> preferredFieldsAvailable;
    private final List<Vector2d> regularFieldsAvailable;
    private final int plantEnergy;

    private static final Random random = new Random();

    public EquatorPlanter(int width, int height, int plantEnergy) {
        EquatorFields fields = (new EquatorFields(width, height, 20));
        this.plantEnergy = plantEnergy;
        this.preferredFields = fields.getPreferredFields();
        this.regularFields = fields.getRegularFields();
        this.preferredFieldsAvailable = new ArrayList<>(preferredFields);
        this.regularFieldsAvailable = new ArrayList<>(regularFields);
    }
    public EquatorPlanter(int width, int height, int plantEnergy, int startingPlants) {
        this(width, height, plantEnergy);
        generatePlants(startingPlants);
    }

    @Override
    public void generatePlants(int plantAmount) {
        int preferredFieldsCount = preferredFieldsAvailable.size();
        int regularFieldsCount = regularFieldsAvailable.size();

        for (int i = 0; i < plantAmount; i++) {
            double chance = random.nextDouble();
            //there is 80% chance that plant grows on a preferred field, or if the regular fields are full and the
            // preferred are not the plant must grow on the preferred field.
            if ((chance >= 0.2 && preferredFieldsCount > 0) || (regularFieldsCount == 0 && preferredFieldsCount > 0)) {
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

    public Plant removePlant(Vector2d position) {
        if (this.plants.containsKey(position)) {
            Plant plant = this.plants.get(position);

            if (isPreferred(position)) {
                this.addToPreferredFieldsAvailable(position);
            }
            else {
                this.addToRegularFieldsAvailable(position);
            }

            this.plants.remove(position);
            return plant;
        }
        return null;
    }

    //handling regular fields availability
    private void addToRegularFieldsAvailable(Vector2d position) {
        regularFieldsAvailable.add(position);
    }

    private void removeFromRegularFieldsAvailable(Vector2d position) {
        regularFieldsAvailable.remove(position);
    }

    //handling preferred fields availability
    private void addToPreferredFieldsAvailable(Vector2d position) {
        preferredFieldsAvailable.add(position);
    }

    private void removeFromPreferredFieldsAvailable(Vector2d position) {
        preferredFieldsAvailable.remove(position);
    }

    @Override
    public boolean isPreferred(Vector2d position) {
        return preferredFields.contains(position);
    }

    @Override
    public Plant plantAt(Vector2d position) {
        return plants.get(position);
    }

    //getters
    public HashMap<Vector2d, Plant> getPlants() {
        return plants;
    }

    public Set<Vector2d> getPreferredFields() {
        return preferredFields;
    }

    public Set<Vector2d> getRegularFields() {
        return regularFields;
    }

    public List<Vector2d> getPreferredFieldsAvailable() {
        return preferredFieldsAvailable;
    }

    public List<Vector2d> getRegularFieldsAvailable() {
        return regularFieldsAvailable;
    }
}
