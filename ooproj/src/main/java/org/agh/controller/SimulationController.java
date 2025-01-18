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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.Glow;
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
    public Label TurnLabel;
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
    private boolean statTracking = false;
    private MapSettings mapSettings;
    private MapStatistics mapStatistics;
    private String simulationName;
    private String statisticsDir  = System.getProperty("user.dir") + "/statistics";

    private boolean isHighlightedFields = false;
    private boolean isHighlightedGenom = false;

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

    public void setMapSettings(MapSettings mapSettings) {
        this.mapSettings = mapSettings;
    }

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

        //binding highlight buttons so they are only accessible when the simulation is stopped
        preferButton.disableProperty().bind(simulation.stoppedProperty().not());
        genButton.disableProperty().bind(simulation.stoppedProperty().not());

        //create simulation statistics TSV file if needed
        if (statTracking) {
            //list of headings for TSV file
            List<String> headings = List.of("Turn", "Number of animals", "Number of plants", "Number of empty fields",
                    "Most popular genom", "Average animal energy", "Average life span", "Average children count");

            String filePath = statisticsDir + "/" + simulationName + ".tsv";
            File file = new File(filePath);
            File dir = new File(statisticsDir);

            if (!file.exists()) {
                dir.mkdirs();  // Create the directory if it doesn't exist
            }

            //checking if file with that name exists and deleting it if it does exist
            if (!file.exists()) {
                if (file.delete()) System.out.println("Existing file " + filePath + " deleted");
            }

            //creating new file
            try {
                if(file.createNewFile()) System.out.println("Created new file " + filePath);;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            //appending headings to TSV file
            appendFileLine(headings, filePath);
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

        //creates grid of given dimensions
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Vector2d position = new Vector2d(col, row);
                WorldElement element = upperLayer.get(position);

                //each element on the grid is implemented as Pane
                //with given cell size of 20
                Pane cell = new Pane();
                cell.setPrefSize(cellSize, cellSize);
                cell.setBorder(Border.EMPTY);

                if (element != null) {
                    //if there is world element on a given position
                    //then it is needed to add on a cell adequate behaviour
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
                    //add created Pane to a grid pane
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

    public void enableStatTracking(){
        this.statTracking = true;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    /**
     * Gets a List of Strings that is to be put in a TSV file, each element is this
     * list will be separated from another with tabulation so that each element will
     * be separate element in TSV file.
     * @param TSVLine Elements to be but separately into TSV file
     * @param filePath Path to the TSV file
     */
    private void appendFileLine(List<String> TSVLine, String filePath){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String TSVline = String.join("\t", TSVLine);
            writer.write(TSVline);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTurnStatisticsToTSV() {
        List<String> statisticsList = mapStatistics.getStatisticsStringList();
        String tsvFile = statisticsDir + "/" + simulationName + ".tsv";
        appendFileLine(statisticsList, tsvFile);
    }

    public void onPreferredClicked(ActionEvent actionEvent) {
        //handling button that highlights preferred fields on a map
        if (!isHighlightedFields) { //if highlight is off and user clicks the button
            System.out.println("Preferred fields highlight on");
            //set highlight bool on true and handle button CSS classes
            isHighlightedFields = true;
            preferButton.getStyleClass().remove("button");
            preferButton.getStyleClass().add("button-clicked");
            //highlight fields on a map
            highlightPreferredFields();
        }
        else { //if highlight is on and user clicks the button
            System.out.println("Preferred fields highlight off");
            //set highlight bool on false and handle button CSS classes
            isHighlightedFields = false;
            preferButton.getStyleClass().remove("button-clicked");
            preferButton.getStyleClass().add("button");
            //bring back map without highlights
            drawMap();
            //leave genom highlight if it was on
            if(isHighlightedGenom) highlightGenom();
        }
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
        //handling button that highlights most popular genom on a map
        if (!isHighlightedGenom) { //if highlight is off and user clicks the button
            System.out.println("Genom highlight on");
            //set highlight bool on true and handle button CSS classes
            isHighlightedGenom = true;
            genButton.getStyleClass().remove("button");
            genButton.getStyleClass().add("button-clicked");
            //highlight an animal with the most popular genom
            highlightGenom();
        }
        else { //if highlight is on and user clicks the button
            System.out.println("Genom highlight off");
            //set highlight bool on false and handle button CSS classes
            isHighlightedGenom = false;
            genButton.getStyleClass().remove("button-clicked");
            genButton.getStyleClass().add("button");
            //bring back map without highlights
            drawMap();
            //leave fields highlight if it was on
            if(isHighlightedFields) highlightPreferredFields();
        }
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

    public void updateInfo(){
        //update statistics about whole simulation

        //update turn number info
        TurnLabel.setText("Turn: " + simulation.getTurn());

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

    private void drawMap() {
        clearGrid();
        makeGrid();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            drawMap();
            updateInfo();
            if (statTracking) addTurnStatisticsToTSV();
        });
    }

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        //handle highlight buttons behaviour
        //clear highlight bool values, clear CSS classes and bring back original button class
        isHighlightedFields = false;
        preferButton.getStyleClass().clear();
        preferButton.getStyleClass().add("button");

        isHighlightedGenom = false;
        genButton.getStyleClass().clear();
        genButton.getStyleClass().add("button");

        //start simulation
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
                AnimalInfoBox.setVisible(false); //hide the VBox
                AnimalInfoBox.setManaged(false); //remove it from the layout entirely
                AnimalInfoButton.setText("Show Animal Info");
            } else {
                AnimalInfoBox.setVisible(true); //show the VBox
                AnimalInfoBox.setManaged(true); //add it back to the layout
                AnimalInfoButton.setText("Hide Animal Info");
            }
            isAnimalInfoVisible = !isAnimalInfoVisible; //toggle the state
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