package org.agh.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.agh.utils.MapSettings;
import org.agh.utils.PlanterType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class StartingController implements Controller {

    @FXML
    public TextField PlantsPerTurnInput;
    @FXML
    public TextField StartingNumberOfPlantsInput;
    @FXML
    public TextField PlantEnergyInput;
    @FXML
    public TextField HeightInput;
    @FXML
    public TextField WidthInput;
    @FXML
    public TextField GenomLengthInput;
    @FXML
    public TextField StartingNumberOfAnimalsInput;
    @FXML
    public TextField StartingEnergyOfAnimalsInput;
    @FXML
    public TextField FertilityThresholdInput;
    @FXML
    public TextField EnergeticBreedingCostInput;
    @FXML
    public TextField MinNumberOfMutationsInput;
    @FXML
    public TextField MaxNumberOfMutationsInput;
    @FXML
    public CheckBox isAgingCheckBox;
    @FXML
    public TextField ConfigurationName;
    @FXML
    private ChoiceBox<String> MapVariant;


    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    public void initialize() {
        MapVariant.getItems().addAll("EQUATOR", "JUNGLE");
        MapVariant.setValue("EQUATOR"); // Default option

        System.out.println("New app opened");
    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private int getValidatedIntValue(String text, int defaultValue) {
        if (text == null || !text.matches("[1-9]\\d*")) {
            return defaultValue;
        }
        return Integer.parseInt(text);
    }

    public void OnStartButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
            root = loader.load();
            SimulationController simulationController = loader.getController();

            int height = getValidatedIntValue(HeightInput.getText(), 20);
            int width = getValidatedIntValue(WidthInput.getText(), 20);
            int startingNumberOfPlants = getValidatedIntValue(StartingNumberOfPlantsInput.getText(), 10);
            int plantEnergy = getValidatedIntValue(PlantEnergyInput.getText(), 2);
            int plantsPerTurn = getValidatedIntValue(PlantsPerTurnInput.getText(), 1);
            int startingNumberOfAnimals = getValidatedIntValue(StartingNumberOfAnimalsInput.getText(), 5);
            int startingEnergyOfAnimals = getValidatedIntValue(StartingEnergyOfAnimalsInput.getText(), 5);
            int fertility = getValidatedIntValue(FertilityThresholdInput.getText(), 5);
            int breedingCost = getValidatedIntValue(EnergeticBreedingCostInput.getText(), 2);
            int minMutations = getValidatedIntValue(MinNumberOfMutationsInput.getText(), 1);
            int maxMutations = getValidatedIntValue(MaxNumberOfMutationsInput.getText(), 3);
            int genomLength = getValidatedIntValue(GenomLengthInput.getText(), 5);

            MapSettings mapSettings = new MapSettings(height, width, startingNumberOfPlants, plantEnergy, plantsPerTurn, PlanterType.fromString(MapVariant.getValue()),
                    startingNumberOfAnimals, startingEnergyOfAnimals, fertility, breedingCost, isAgingCheckBox.isSelected(), minMutations, maxMutations, genomLength);

            simulationController.initialize(mapSettings);

            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);

            simulationController.setScene(scene);
            String cssPath = getClass().getClassLoader().getResource("simulation.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            stage.setScene(scene);

            stage.setTitle("Simulation");
            stage.setMinHeight(800);
            stage.setMinWidth(1400);

            stage.show();

            stage.setOnCloseRequest(event -> simulationController.getSimulation().shutdown());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OnConfigSaveClick(ActionEvent actionEvent) {
        int height = getValidatedIntValue(HeightInput.getText(), 20);
        int width = getValidatedIntValue(WidthInput.getText(), 20);
        int startingNumberOfPlants = getValidatedIntValue(StartingNumberOfPlantsInput.getText(), 10);
        int plantEnergy = getValidatedIntValue(PlantEnergyInput.getText(), 2);
        int plantsPerTurn = getValidatedIntValue(PlantsPerTurnInput.getText(), 1);
        int startingNumberOfAnimals = getValidatedIntValue(StartingNumberOfAnimalsInput.getText(), 5);
        int startingEnergyOfAnimals = getValidatedIntValue(StartingEnergyOfAnimalsInput.getText(), 5);
        int fertility = getValidatedIntValue(FertilityThresholdInput.getText(), 5);
        int breedingCost = getValidatedIntValue(EnergeticBreedingCostInput.getText(), 2);
        int minMutations = getValidatedIntValue(MinNumberOfMutationsInput.getText(), 1);
        int maxMutations = getValidatedIntValue(MaxNumberOfMutationsInput.getText(), 3);
        int genomLength = getValidatedIntValue(GenomLengthInput.getText(), 5);

        MapSettings mapSettings = new MapSettings(height, width, startingNumberOfPlants, plantEnergy, plantsPerTurn, PlanterType.fromString(MapVariant.getValue()),
                startingNumberOfAnimals, startingEnergyOfAnimals, fertility, breedingCost, isAgingCheckBox.isSelected(), minMutations, maxMutations, genomLength);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files", "*.config"));
        fileChooser.setInitialFileName(ConfigurationName.getText() +  ".config");

        String currentPath = System.getProperty("user.dir");
        String configurationsPath = currentPath + "/configurations";  // Path to the configurations folder
        File configurationsDir = new File(configurationsPath);

        if (!configurationsDir.exists()) {
            configurationsDir.mkdirs();  // Create the directory if it doesn't exist
        }

        fileChooser.setInitialDirectory(configurationsDir);

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // Save the configuration to the file
            try (FileOutputStream fileOut = new FileOutputStream(file);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(mapSettings);
                showAlert("Success", "Configuration saved successfully!", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                showAlert("Error", "Failed to save configuration.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void OnLoadConfigClick(ActionEvent actionEvent) {
    }
}
