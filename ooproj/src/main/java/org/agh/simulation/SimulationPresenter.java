package org.agh.simulation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.agh.World;
import org.agh.model.*;
import org.agh.utils.MapSettings;
import org.agh.utils.PlanterType;
import org.agh.utils.SimulationChangeListener;

import java.util.HashMap;
import java.util.Optional;

public class SimulationPresenter implements SimulationChangeListener {
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
    public Button chartsButton;
    @FXML
    private GridPane worldMapPane;
    @FXML
    private Label moveDescription;
    @FXML
    private TextField movesField;
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
    private boolean isSidePanelVisible = true;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        simulation.addObserver(this);
    }

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @FXML
    public void initialize() {
        MapSettings mapSettings = new MapSettings(40,40,17,5,3, PlanterType.EQUATOR, 15,10,
                7,4, false, 1,3,8);
        WorldMap worldMap = new WorldMap(mapSettings);
        this.worldMap = worldMap;

        Simulation simulation = new Simulation(worldMap);
        setSimulation(simulation);
        simulation.setSpeed(500);

        startButton.disableProperty().bind(stopButton.disableProperty().not());
        stopButton.disableProperty().bind(simulation.stoppedProperty());

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
                "Status: " + animal.getStatus() + "\n"
                + "Age: " + animal.getAge() + "\n"
                + "Genom: " + "\n" + animal.getGenom() + "\n"
                + "Active gen: " + animal.getActiveGen() + "\n"
                + "Energy: " + animal.getEnergy() + "\n"
                + "Children: " + animal.getChildCount() + "\n"
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

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Vector2d position = new Vector2d(col, row);
                Optional<WorldElement> element = this.worldMap.elementAt(position);

                Pane cell = new Pane();
                cell.setPrefSize(cellSize, cellSize);

                if (element.isPresent()) {
                    if (element.get() instanceof Animal){
                        cell.getStyleClass().add("animal");
                        cell.setOnMouseClicked(event -> displayAnimalInfo((Animal) element.get()));
                    }
                    else if (element.get() instanceof BigPlant){
                        cell.getStyleClass().add("big-plant");
                        cell.setOnMouseClicked(event -> {infoLabel.setText("Big Plant"); clearAnimalInfo(); cleared = true;});
                    }
                    else if (element.get() instanceof Plant){
                        cell.getStyleClass().add("plant");
                        cell.setOnMouseClicked(event -> {infoLabel.setText("Plant"); clearAnimalInfo(); cleared = true;});
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

    public void updateInfo(){
        AnimalCountInfoLabel.setText("Animals: " + worldMap.getNumOfAnimals());
        PlantCountInfoLabel.setText("Plants: " + worldMap.getNumOfPlants());
        EmptyFieldsCountInfoLabel.setText("Empty fields: " + worldMap.getNumOfEmptyFields());
        MostPopularGenomInfoLabel.setText("Most popular genom: " + worldMap.getMostPopularGenom().toString());
        AvgAnimalEnergyInfoLabel.setText("Average animal energy: " + worldMap.getAvgAnimalEnergy());
        //TODO
        AvgLifeSpanInfoLabel.setText("Average life span: ");
        AvgChildrenCountInfoLabel.setText("Average number of children: ");
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
            moveDescription.setText(message);
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
        if (isSidePanelVisible) {
            // Remove VBox from the BorderPane (leave the space open)
            borderPane.setLeft(null);
            chartsButton.setText("Show Info Panel");
        } else {
            // Add VBox back to the left side of BorderPane
            borderPane.setLeft(sidePanel);
            chartsButton.setText("Hide Info Panel");
        }
        isSidePanelVisible = !isSidePanelVisible; // Toggle visibility state
    }
}
