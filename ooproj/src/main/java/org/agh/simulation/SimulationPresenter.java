package org.agh.simulation;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.agh.utils.SimulationChangeListener;

import java.util.List;

public class SimulationPresenter implements SimulationChangeListener {

    Simulation simulation;

    @FXML
    private GridPane worldMapPane;
    @FXML
    private Label moveDescription;
    @FXML
    private TextField movesField;
    @FXML
    private Button startButton;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    private void clearGrid(GridPane mapGrid) {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void drawMap(){
        clearGrid(worldMapPane);
//        Boundary mapBounds = worldMap.getCurrentBounds();
//        int rowCount = mapBounds.max().getY() - mapBounds.min().getY() + 1;
//        int columnCount = mapBounds.max().getX() - mapBounds.min().getX() + 1;
//        int rowHeight = (360/rowCount);
//        int columnWidth = (360/columnCount);
//        for (int i = 0; i < rowCount; i++) {
//            worldMapPane.getRowConstraints().add(new RowConstraints(rowHeight));
//        }
//        for (int i = 0; i < columnCount; i++) {
//            worldMapPane.getColumnConstraints().add(new ColumnConstraints(columnWidth));
//        }
//        for (WorldElement element:worldMap.getElements().values()) {
//            worldMapPane.add(new Label(element.toString()),element.getPosition().getX() - mapBounds.min().getX(), mapBounds.max().getY() - element.getPosition().getY() );
//        }
    }

    @Override
    public void simulationChanged(String message) {
        Platform.runLater(() -> {
            moveDescription.setText(message);
            drawMap();
        });
    }

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        Thread simulationThread = new Thread(simulation);
        simulationThread.start();
    }
}
