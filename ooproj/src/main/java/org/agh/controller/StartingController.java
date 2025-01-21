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
    public CheckBox statTrackCheckBox;
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

    private int getValidatedIntValue(String text, String promptValue) throws NumberFormatException {
        // if there is no user input or input is different from positive integer, then default value will be returned
        if (text.isEmpty()) {
            return Integer.parseInt(promptValue);
        }
        return Integer.parseInt(text);
    }

    private MapSettings getMapSettings() {
        MapSettings mapSettings = null;

        try {
            int height = getValidatedIntValue(HeightInput.getText(), HeightInput.getPromptText());
            int width = getValidatedIntValue(WidthInput.getText(), WidthInput.getPromptText());
            int startingNumberOfPlants = getValidatedIntValue(StartingNumberOfPlantsInput.getText(), StartingNumberOfPlantsInput.getPromptText());
            int plantEnergy = getValidatedIntValue(PlantEnergyInput.getText(), PlantEnergyInput.getPromptText());
            int plantsPerTurn = getValidatedIntValue(PlantsPerTurnInput.getText(), PlantsPerTurnInput.getPromptText());
            int startingNumberOfAnimals = getValidatedIntValue(StartingNumberOfAnimalsInput.getText(), StartingNumberOfAnimalsInput.getPromptText());
            int startingEnergyOfAnimals = getValidatedIntValue(StartingEnergyOfAnimalsInput.getText(), StartingEnergyOfAnimalsInput.getPromptText());
            int fertility = getValidatedIntValue(FertilityThresholdInput.getText(), FertilityThresholdInput.getPromptText());
            int breedingCost = getValidatedIntValue(EnergeticBreedingCostInput.getText(), EnergeticBreedingCostInput.getPromptText());
            int minMutations = getValidatedIntValue(MinNumberOfMutationsInput.getText(), MinNumberOfMutationsInput.getPromptText());
            int maxMutations = getValidatedIntValue(MaxNumberOfMutationsInput.getText(), MaxNumberOfMutationsInput.getPromptText());
            int genomLength = getValidatedIntValue(GenomLengthInput.getText(), GenomLengthInput.getPromptText());

            // storing input data into MapSettings record
            mapSettings = new MapSettings(height, width, startingNumberOfPlants, plantEnergy, plantsPerTurn, PlanterType.fromString(MapVariant.getValue()),
                    startingNumberOfAnimals, startingEnergyOfAnimals, fertility, breedingCost, isAgingCheckBox.isSelected(), minMutations, maxMutations, genomLength);
        }catch (NumberFormatException e) {
            showAlert("Parsing error", "Could not parse inputted value to int", Alert.AlertType.ERROR);
            return null;
        }

        // Validating all input values
        try {
            mapSettings.validate();
        } catch (MapSettingsException exception) {
            showAlert("Map Setting Invalid",exception.getMessage(), Alert.AlertType.WARNING);
            return null;
        }

        return mapSettings;
    }

    public void OnStartButtonClick(ActionEvent actionEvent) {
        try {
            // getting map settings input with adjustments -> if user inputs wrong values then
            // default values will be put into map settings

            MapSettings mapSettings = getMapSettings();
            if(mapSettings == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
            root = loader.load();
            SimulationController simulationController = loader.getController();

            //handling statistics tracking option
            if(statTrackCheckBox.isSelected()) {
                if (ConfigurationName.getText().isEmpty()){
                    showAlert("Warning", "Configuration name is required for statistics tracking", Alert.AlertType.WARNING);
                    return;
                }
                simulationController.enableStatTracking();
                simulationController.setSimulationName(ConfigurationName.getText());
            }

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
            simulationStage.setMinHeight(850);
            simulationStage.setMinWidth(1800);

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

        MapSettings mapSettings = getMapSettings();
        if(mapSettings == null) return;

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
