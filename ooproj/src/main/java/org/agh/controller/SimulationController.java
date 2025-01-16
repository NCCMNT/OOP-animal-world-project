package org.agh.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.agh.model.*;
import org.agh.simulation.Simulation;
import org.agh.utils.MapSettings;
import org.agh.utils.SimulationChangeListener;

import java.util.HashMap;
import java.util.List;

public class SimulationController implements SimulationChangeListener, Controller {
    @FXML
    public Label AnimalCountInfoLabel;
    @FXML
    public Label PlantCountInfoLabel;
    @FXML
    public Label EmptyFieldsCountInfoLabel;
    @FXML
    public Label MostPopularGenomInfoLabel;
    @FXML
    public Label AvgAnimalEnergyInfoLabel;
    @FXML
    public Label AvgLifeSpanInfoLabel;
    @FXML
    public Label AvgChildrenCountInfoLabel;
    @FXML
    public Label AnimalInfoLabel;
    @FXML
    public BorderPane borderPane;
    @FXML
    public Button AnimalInfoButton;
    @FXML
    public VBox AnimalInfoBox;
    @FXML
    public VBox AllInfoBox;
    @FXML
    private GridPane worldMapPane;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button preferButton;
    @FXML
    private Button genButton;
    @FXML
    private Button clearHighlightButton;
    @FXML
    private Label infoLabel;
    @FXML
    private VBox sidePanel;

    private Simulation simulation;
    private WorldMap worldMap;
    private HashMap<Vector2d, Pane> cellPanes = new HashMap<>();
    private Animal LastViewedAnimal;
    private boolean cleared = true;
    private boolean isAnimalInfoVisible = true;
    private MapSettings mapSettings;

    private Scene scene;
    private Stage stage;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        simulation.addObserver(this);
    }

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
    }
    public void setMapSettings(MapSettings mapSettings) { this.mapSettings = mapSettings; }

    @FXML
    public void initialize(MapSettings mapSettings) {
        if(mapSettings == null) {
            throw new IllegalStateException("MapSettings not set");
        }
        setMapSettings(mapSettings);
        WorldMap worldMap = new WorldMap(mapSettings);
        this.worldMap = worldMap;

        Simulation simulation = new Simulation(worldMap);
        setSimulation(simulation);
        simulation.setSpeed(500);

        startButton.disableProperty().bind(stopButton.disableProperty().not());
        stopButton.disableProperty().bind(simulation.stoppedProperty());
        preferButton.disableProperty().bind(simulation.stoppedProperty().not());
        genButton.disableProperty().bind(simulation.stoppedProperty().not());
        clearHighlightButton.disableProperty().bind(simulation.stoppedProperty().not());

        sidePanel = (VBox) borderPane.getLeft();
    }

    private void clearGrid() {
        worldMapPane.getChildren().retainAll(worldMapPane.getChildren().getFirst()); // hack to retain visible grid lines
        worldMapPane.getColumnConstraints().clear();
        worldMapPane.getRowConstraints().clear();
    }

    private void displayAnimalInfo(Animal animal) {
        cleared = false;
        LastViewedAnimal = animal;
        infoLabel.setText("Animal");
        AnimalInfoLabel.setText(
                animal.infoUI()
        );
    }

    private void clearAnimalInfo() {
        AnimalInfoLabel.setText("");
    }

    private void makeGrid() {
        int rows = worldMap.getHeight();
        int cols = worldMap.getWidth();
        int cellSize = 20;

        // Clear existing constraints
        worldMapPane.getRowConstraints().clear();
        worldMapPane.getColumnConstraints().clear();

        HashMap<Vector2d, WorldElement> upperLayer = this.worldMap.upperLayer();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Vector2d position = new Vector2d(col, row);
                WorldElement element = upperLayer.get(position);

                Pane cell = new Pane();
                cell.setPrefSize(cellSize, cellSize);

                if (element != null) {
                    switch (element) {
                        case Animal animal -> {
                            cell.getStyleClass().add("animal");
                            cell.setOnMouseClicked(event -> displayAnimalInfo((Animal) element));

                            cell.setEffect(adjustFromEnergy(animal.getEnergy()));
                        }
                        case BigPlant ignored -> {
                            cell.getStyleClass().add("big-plant");
                            cell.setOnMouseClicked(event -> {
                                infoLabel.setText("Big Plant");
                                clearAnimalInfo();
                                cleared = true;
                            });
                        }
                        case Plant ignored -> {
                            cell.getStyleClass().add("plant");
                            cell.setOnMouseClicked(event -> {
                                infoLabel.setText("Plant");
                                clearAnimalInfo();
                                cleared = true;
                            });
                        }
                        default -> {
                        }
                    }
                    worldMapPane.add(cell, col, row);
                }
                else {
                    cell.getStyleClass().add("empty-field");
                    cell.setOnMouseClicked(event -> {infoLabel.setText("Empty Field"); clearAnimalInfo(); cleared = true;});
                    worldMapPane.add(cell, col, row);
                }
                cellPanes.put(position, cell);
            }
        }
        worldMapPane.setGridLinesVisible(true);
    }

    public void onPreferredClicked(ActionEvent actionEvent) {
        System.out.println("Wszystko ok");
        highlightPreferredFields();
    }

    private void highlightPreferredFields() {
        int rows = worldMap.getHeight();
        int cols = worldMap.getWidth();
        Effect effect = new Glow(0.4);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if(worldMap.isPreferred(new Vector2d(col, row))) {
                    cellPanes.get(new Vector2d(col, row)).setEffect(effect);
                }
            }
        }

    }

    public void onGenomClicked(ActionEvent actionEvent) {
        highlightGenom();
    }

    private void highlightGenom() {
        int rows = worldMap.getHeight();
        int cols = worldMap.getWidth();
        Effect effect = new Bloom();
        HashMap<Vector2d, WorldElement> upperLayer = worldMap.upperLayer();
        List<Integer> genom = worldMap.getMostPopularGenom();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Vector2d position = new Vector2d(col, row);
                WorldElement element = upperLayer.get(position);
                if(element instanceof Animal && ((Animal) element).getGenom().equals(genom)) {
                    cellPanes.get(new Vector2d(col, row)).setEffect(effect);
                }
            }
        }
    }

    public void onClearHighlightClicked(ActionEvent actionEvent) {
        drawMap();
    }

    public void updateInfo(){
        AnimalCountInfoLabel.setText("Animals: " + worldMap.getNumOfAnimals());
        PlantCountInfoLabel.setText("Plants: " + worldMap.getNumOfPlants());
        EmptyFieldsCountInfoLabel.setText("Empty fields: " + worldMap.getNumOfEmptyFields());
        MostPopularGenomInfoLabel.setText("Most popular genom: " + worldMap.getMostPopularGenom().toString());
        AvgAnimalEnergyInfoLabel.setText("Average animal energy: " + worldMap.getAvgAnimalEnergy());
        AvgLifeSpanInfoLabel.setText("Average life span: " + worldMap.getAvgDeadAge());
        AvgChildrenCountInfoLabel.setText("Average number of children: " + worldMap.getAvgChildren());
        if (!cleared) displayAnimalInfo(LastViewedAnimal);
    }

    public void drawMap() {
        clearGrid();
        makeGrid();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
//        setWorldMap(worldMap);
        Platform.runLater(() -> {
            drawMap();
            updateInfo();
        });
    }

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        simulation.start();
    }
    public void onSimulationStopClicked(ActionEvent actionEvent) {
        simulation.stop();
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void onChartDisplayClicked(ActionEvent actionEvent) {
        if (AnimalInfoBox != null) {
            if (isAnimalInfoVisible) {
                AnimalInfoBox.setVisible(false); // Hide the VBox
                AnimalInfoBox.setManaged(false); // Remove it from the layout entirely
                AnimalInfoButton.setText("Show Animal Info");
            } else {
                AnimalInfoBox.setVisible(true); // Show the VBox
                AnimalInfoBox.setManaged(true); // Add it back to the layout
                AnimalInfoButton.setText("Hide Animal Info");
            }
            isAnimalInfoVisible = !isAnimalInfoVisible; // Toggle the state
        }
    }

    private Double normalize (int energy){
        // In order to use ColorAdjust energy value must be normalized
        if(energy >= 200) return 1.0;
        return (double)energy/200;
    }

    private ColorAdjust adjustFromEnergy(int energy) {
        // Creating ColorAdjust effect to show amount of energy of each animal
        ColorAdjust energyAdjust = new ColorAdjust();
        double energyNormalised = normalize(energy);
        energyAdjust.setBrightness(energyNormalised);
        energyAdjust.setSaturation(energyNormalised);
        return energyAdjust;
    }


}
