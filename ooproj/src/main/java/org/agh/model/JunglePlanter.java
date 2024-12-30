package org.agh.model;

import org.agh.utils.Fields;
import org.agh.utils.JungleFields;

import java.util.*;

public class JunglePlanter implements Planter{

    private final static int BIGENERGYMULTIPLIER = 3;

    private final HashMap<Vector2d, Plant> plants = new HashMap<>();

    //private Fields fields;
    private final Set<Vector2d> preferredFields;
    private final Set<Vector2d> regularFields;
    private final List<Vector2d> fieldsAvailable;
    private final int plantEnergy;

    private static final Random random = new Random();

    public JunglePlanter(int width, int height, int plantEnergy) {
        Fields fields = (new JungleFields(width, height, 20));
        this.plantEnergy = plantEnergy;
        this.preferredFields = fields.getPreferredFields();
        this.regularFields = fields.getRegularFields();
        this.fieldsAvailable = new ArrayList<>(preferredFields);
        this.fieldsAvailable.addAll(regularFields);
    }
    public JunglePlanter(int width, int height, int plantEnergy, int startingPlants) {
        this(width, height, plantEnergy);
        generatePlants(startingPlants);
    }

    @Override
    public void generatePlants(int plantAmount) {
        for (int i = 0; i < plantAmount; i++) {
            if(fieldsAvailable.isEmpty()) break;
            int selectedIndex = random.nextInt(fieldsAvailable.size());
            Vector2d selectedPosition = fieldsAvailable.get(selectedIndex);
            if(!tryBigPlacing(selectedPosition)) {
                plants.put(selectedPosition, new Plant(selectedPosition, plantEnergy));
                fieldsAvailable.set(selectedIndex, fieldsAvailable.getLast());
                fieldsAvailable.removeLast();
            }

        }
    }

    private boolean tryBigPlacing(Vector2d lowerLeft){
        List<Vector2d> neighbours = List.of(lowerLeft.add(new Vector2d(0,1)),
                                            lowerLeft.add(new Vector2d(1,0)),
                                            lowerLeft.add(new Vector2d(1,1)));
        if(     isPreferred(lowerLeft) &&
                isPreferred(neighbours.getLast()) &&
                !plants.containsKey(neighbours.getFirst()) &&
                !plants.containsKey(neighbours.getLast()) &&
                !plants.containsKey(neighbours.get(1))
        ) {
            BigPlant bigPlant = new BigPlant(lowerLeft, BIGENERGYMULTIPLIER*plantEnergy);
            plants.put(lowerLeft, bigPlant);
            for(Vector2d position: neighbours){
                plants.put(position, bigPlant);
            }
            fieldsAvailable.removeAll(neighbours);
            fieldsAvailable.remove(lowerLeft);
            return true;
        }
        return false;
    }

    public Plant removePlant(Vector2d position) {
        if (this.plants.containsKey(position)) {
            Plant plant = this.plants.get(position);

            if(plant instanceof BigPlant) {
                List<Vector2d> positions = List.of(plant.position,
                        plant.position.add(new Vector2d(0,1)),
                        plant.position.add(new Vector2d(1,0)),
                        plant.position.add(new Vector2d(1,1)));
                for(Vector2d position2: positions){
                    this.addToFieldsAvailable(position2);
                    plants.remove(position2);
                }
            }
            else {
                this.addToFieldsAvailable(position);
                this.plants.remove(position);
            }
            return plant;
        }
        return null;
    }

    //handling regular fields availability
    private void addToFieldsAvailable(Vector2d position) {
        fieldsAvailable.add(position);
    }

    private void removeFromFieldsAvailable(Vector2d position) {
        fieldsAvailable.remove(position);
    }


    @Override
    public boolean isPreferred(Vector2d position) {
        return preferredFields.contains(position);
    }

    @Override
    public Plant plantAt(Vector2d position) {
        return plants.get(position);
    }

//    public Fields getFields() {
//        return fields;
//    }

    public HashMap<Vector2d, Plant> getPlants() {
        return plants;
    }

    public Set<Vector2d> getPreferredFields() {
        return preferredFields;
    }

    public Set<Vector2d> getRegularFields() {
        return regularFields;
    }
}
