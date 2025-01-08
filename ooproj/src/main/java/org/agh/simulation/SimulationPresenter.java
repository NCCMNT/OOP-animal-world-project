package org.agh.simulation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.agh.World;
import org.agh.model.Vector2d;
import org.agh.model.WorldElement;
import org.agh.model.WorldMap;
import org.agh.utils.MapSettings;
import org.agh.utils.PlanterType;
import org.agh.utils.SimulationChangeListener;

import java.util.Optional;

public class SimulationPresenter implements SimulationChangeListener {
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

    private int cellSize = 20;
    private Simulation simulation;
    private WorldMap worldMap;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation; simulation.addObserver(this);
        stopButton.disableProperty().bind(simulation.stoppedProperty());
    }

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @FXML
    public void initialize() {
        startButton.disableProperty().bind(stopButton.disableProperty().not());
    }

    private void clearGrid(GridPane mapGrid) {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst()); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    private void setGrid(GridPane mapGrid) {
        int rows = worldMap.getWidth();
        int cols = worldMap.getHeight();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Optional<WorldElement> element = this.worldMap.elementAt(new Vector2d(col,row));
                if (element.isPresent()) {
                    Label label = new Label(element.toString());
                    mapGrid.add(label, col, row);
                }
                else {
                    Label label = new Label(" ");
                    mapGrid.add(label, col, row);
                }
            }
        }
        mapGrid.setGridLinesVisible(true);
    }

    public void drawMap(){
        clearGrid(worldMapPane);
        setGrid(worldMapPane);
    }

    @Override
    public void simulationChanged(String message) {
        Platform.runLater(() -> {
            moveDescription.setText(message);
            drawMap();
        });
    }

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        simulation.start();
    }
    public void onSimulationStopClicked(ActionEvent actionEvent) {
        simulation.stop();
    }
}
