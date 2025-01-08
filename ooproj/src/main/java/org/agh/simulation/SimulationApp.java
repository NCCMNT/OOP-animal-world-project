package org.agh.simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.agh.model.WorldMap;
import org.agh.utils.MapSettings;
import org.agh.utils.PlanterType;

import java.io.IOException;

public class SimulationApp extends Application {

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        Scene scene = new Scene(viewRoot);

        //handling CSS file
        String cssPath = getClass().getClassLoader().getResource("simulation.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        //setting scene and size for stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation app");
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1000);
//        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
//        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }

//    private Simulation exampleSim(){
//        MapSettings mapSettings = new MapSettings(10,10,17,2,3, PlanterType.EQUATOR, 7,10,
//                7,4, false, 1,3,8);
//        WorldMap worldMap = new WorldMap(mapSettings);
//        Simulation simulation = new Simulation(worldMap);
//        simulation.setSpeed(500);
//        return simulation;
//    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
            BorderPane viewRoot = loader.load();

            SimulationPresenter presenter = loader.getController();
//            Simulation simulation = exampleSim();
//            presenter.setWorldMap(simulation.getWorldMap());
//            presenter.setSimulation(simulation);

            configureStage(primaryStage, viewRoot);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> presenter.getSimulation().shutdown());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
