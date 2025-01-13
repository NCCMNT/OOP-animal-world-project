package org.agh.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.agh.utils.MapSettings;
import org.agh.utils.MapSettingsException;
import org.agh.utils.PlanterType;

import java.io.*;
import java.util.Map;

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
        // if there is no user input or input is different from positive integer, then default value will be returned
        if (text == null || !text.matches("[1-9]\\d*")) {
            return defaultValue;
        }
        return Integer.parseInt(text);
    }

    public void OnStartButtonClick(ActionEvent actionEvent) {
        try {
            // getting map settings input with adjustments -> if user inputs wrong values then
            // default values will be put into map settings
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

            // storing input data into MapSettings record
            MapSettings mapSettings = new MapSettings(height, width, startingNumberOfPlants, plantEnergy, plantsPerTurn, PlanterType.fromString(MapVariant.getValue()),
                    startingNumberOfAnimals, startingEnergyOfAnimals, fertility, breedingCost, isAgingCheckBox.isSelected(), minMutations, maxMutations, genomLength);

            // Validating all inputed values
            try {
                mapSettings.validate();
            } catch (MapSettingsException exception) {
                System.out.println(exception.getMessage());
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
            root = loader.load();
            SimulationController simulationController = loader.getController();

            // initialize new simulation controller with given settings
            simulationController.initialize(mapSettings);

            // scene config
            Scene simulationScene = new Scene(root);
            simulationController.setScene(simulationScene);
            String cssPath = getClass().getClassLoader().getResource("simulation.css").toExternalForm();
            simulationScene.getStylesheets().add(cssPath);

            // stage config
            Stage simulationStage = new Stage();
            simulationStage.setScene(simulationScene);
            simulationStage.setTitle("Simulation");
            simulationStage.setMinHeight(800);
//            simulationStage.setMinWidth(1000);

            // handling closing window of simulation
            simulationStage.setOnCloseRequest(event -> simulationController.getSimulation().shutdown());

            // show new window of simulation
            simulationStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OnConfigSaveClick(ActionEvent actionEvent) {

        if (ConfigurationName.getText().isEmpty()){
            showAlert("Error", "Configuration name is required", Alert.AlertType.ERROR);
            return;
        }

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
        fileChooser.setInitialFileName(ConfigurationName.getText() + ".config");

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
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files", "*.config"));

        String currentPath = System.getProperty("user.dir");
        String configurationsPath = currentPath + "/configurations";  // Path to the configurations folder
        File configurationsDir = new File(configurationsPath);

        fileChooser.setInitialDirectory(configurationsDir);

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (FileInputStream fileIn = new FileInputStream(file); ObjectInputStream in = new ObjectInputStream(fileIn)){

                MapSettings mapSettings = (MapSettings) in.readObject();
                HeightInput.setText(String.valueOf(mapSettings.height()));
                WidthInput.setText(String.valueOf(mapSettings.width()));
                StartingNumberOfPlantsInput.setText(String.valueOf(mapSettings.startingNumberOfPlants()));
                PlantEnergyInput.setText(String.valueOf(mapSettings.plantEnergy()));
                PlantsPerTurnInput.setText(String.valueOf(mapSettings.plantsPerTurn()));
                StartingNumberOfAnimalsInput.setText(String.valueOf(mapSettings.startingNumberOfAnimals()));
                StartingEnergyOfAnimalsInput.setText(String.valueOf(mapSettings.startingEnergy()));
                FertilityThresholdInput.setText(String.valueOf(mapSettings.energeticFertilityThreshold()));
                EnergeticBreedingCostInput.setText(String.valueOf(mapSettings.energeticBreedingCost()));
                MinNumberOfMutationsInput.setText(String.valueOf(mapSettings.minMutations()));
                MaxNumberOfMutationsInput.setText(String.valueOf(mapSettings.maxMutations()));
                GenomLengthInput.setText(String.valueOf(mapSettings.genomLen()));
                isAgingCheckBox.setSelected(mapSettings.isAging());
                MapVariant.setValue(mapSettings.planterType().toString());

                ConfigurationName.clear();

            }
            catch (IOException | ClassNotFoundException e) {
                showAlert("Error", "Failed to load configuration.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
}
