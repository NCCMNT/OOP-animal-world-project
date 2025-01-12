package org.agh.simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.agh.controller.SimulationController;
import org.agh.controller.StartingController;

import java.io.IOException;

public class SimulationApp extends Application {

    private Scene configureStage(Stage primaryStage, BorderPane viewRoot) {
        Scene scene = new Scene(viewRoot);

        //handling CSS file
        String cssPath = getClass().getClassLoader().getResource("startpage.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        //setting scene and size for stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation configuration");
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1400);

        return scene;
    }


    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("startpage.fxml"));
            BorderPane root = loader.load();

            Scene scene = configureStage(primaryStage, root);
            primaryStage.show();

            StartingController startingController= loader.getController();
            startingController.setStage(primaryStage);
            startingController.setScene(scene);


//            primaryStage.setOnCloseRequest(event -> presenter.getSimulation().shutdown());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
