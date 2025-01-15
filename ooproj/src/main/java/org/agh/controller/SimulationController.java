package org.agh.controller;

import javafx.application.Platform;
import javafx.css.StyleClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.agh.model.*;
import org.agh.simulation.Simulation;
import org.agh.utils.MapSettings;
import org.agh.utils.MapStatistics;
import org.agh.utils.PlanterType;
import org.agh.utils.SimulationChangeListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

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
    private GridPane worldMapPane;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
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
    private boolean statTracking = false;
    private MapSettings mapSettings;
    private MapStatistics mapStatistics;
    private String simulationName;

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

        //binding start and stop button so that when one is enabled the other one is disabled
        startButton.disableProperty().bind(stopButton.disableProperty().not());
        stopButton.disableProperty().bind(simulation.stoppedProperty());

        //create simulation statistics file if needed
        if (statTracking) {

        }
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
                cell.setBorder(Border.EMPTY);

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

                            // Add image for Plant
                            ImageView plantImageView = new ImageView(new Image(getClass().getClassLoader().getResource("plant.png").toExternalForm()));
                            plantImageView.setFitWidth(cellSize);
                            plantImageView.setFitHeight(cellSize);
                            cell.getChildren().add(plantImageView);

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

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    private void addTurnStatisticsToTSV() {
        List<String> statisticsList = mapStatistics.getStatisticsStringList();

        String tsvFile = simulationName + ".tsv";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(tsvFile, true))) {
            String TSVline = String.join("\t", statisticsList);
            writer.write(TSVline);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateInfo(){
        //update statistics about whole simulation

        //stat: number of animals in the simulation
        AnimalCountInfoLabel.setText("Animals: " + worldMap.getNumOfAnimals());

        //stat: number of plants in the simulation
        PlantCountInfoLabel.setText("Plants: " + worldMap.getNumOfPlants());

        //stat: number of empty fields in the simulation
        EmptyFieldsCountInfoLabel.setText("Empty fields: " + worldMap.getNumOfEmptyFields());

        //stat: most popular genom amongst the animals
        MostPopularGenomInfoLabel.setText("Most popular genom: " + worldMap.getMostPopularGenom().toString());

        //stat: average animal energy
        AvgAnimalEnergyInfoLabel.setText("Average animal energy: " + worldMap.getAvgAnimalEnergy());

        //stat: average life span of animals that died
        AvgLifeSpanInfoLabel.setText("Average life span: " + worldMap.getAvgDeadAge());

        //stat: average children count for animals
        AvgChildrenCountInfoLabel.setText("Average number of children: " + worldMap.getAvgChildren());
        if (!cleared) displayAnimalInfo(LastViewedAnimal);

        //updating statistics record
        this.mapStatistics = new MapStatistics(simulation.getTurn(), worldMap.getNumOfAnimals(), worldMap.getNumOfPlants(), worldMap.getNumOfEmptyFields(),
                worldMap.getMostPopularGenom(), worldMap.getAvgAnimalEnergy(), worldMap.getAvgDeadAge(), worldMap.getAvgChildren());
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
            if (statTracking) addTurnStatisticsToTSV();
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
        if(energy >= 200) return 1.0;
        return (double)energy/200;
    }

    private ColorAdjust adjustFromEnergy(int energy) {
        ColorAdjust energyAdjust = new ColorAdjust();
        double energyNormalised = normalize(energy);
        energyAdjust.setBrightness(energyNormalised);
        energyAdjust.setContrast(energyNormalised);
        return energyAdjust;
    }
}
