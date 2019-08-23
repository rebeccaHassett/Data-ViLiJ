package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;

public class ClusteringConfig extends Stage implements Dialog{
    private static ClusteringConfig dialog;
    private Label configMessage = new Label();
    public int numLabels = -20;
    public int maxIterations = -20;
    public int updateIterations = -20;
    private Button saveConfig;
    public boolean continRun;
    private TextField maxIterationsField = new TextField();
    private TextField updateIntervalsField = new TextField();
    private TextField numLabelsField = new TextField();
    private CheckBox continRunBox = new CheckBox();
    private ClusteringConfig storeConfig;
    private ClusteringConfig() {

    }
    public ClusteringConfig(int maxIterations, int updateIterations, int numLabels, boolean continRun) {
        this.maxIterations = maxIterations;
        this.updateIterations = updateIterations;
        this.numLabels = numLabels;
        this.continRun = continRun;
    }
    public static ClusteringConfig getDialog() {
        if (dialog == null)
            dialog = new ClusteringConfig();
        return dialog;
    }
    @Override
    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);

        VBox messagePane = new VBox();
        Text maxIterations = new Text("Maximum Iterations:");
        HBox maxIterationsContainer = new HBox(maxIterations, maxIterationsField);
        Text updateInterval = new Text("Update Interval:");
        HBox updateIntervalsContainer = new HBox(updateInterval, updateIntervalsField);
        Text numLabels = new Text("Number Labels:");
        HBox numLabelsContainer = new HBox(numLabels, numLabelsField);
        Text continRun = new Text("Continuous Run?");
        HBox continRunContainer = new HBox(continRun, continRunBox);
        saveConfig = new Button("Save Run Settings");
        setSaveConfigActions();
        messagePane.getChildren().addAll(maxIterationsContainer, updateIntervalsContainer, numLabelsContainer, continRunContainer, saveConfig);
        messagePane.setAlignment(Pos.CENTER);
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);
        storeConfig = new ClusteringConfig(-20,-20,-20,false);
        this.setScene(new Scene(messagePane));
    }
    @Override
    public void show(String dialogTitle, String message) {
        setTitle(dialogTitle);           // set the title of the dialog
        setConfigMessage(message); // set the main error message
        showAndWait();              // open the dialog and wait for the user to click the close button
    }
    private void setConfigMessage(String message) { configMessage.setText(message); }
    public static int runConfigValueMax(int configValue) {
        if(configValue <= 0) {
            return 0;
        }
        else {
            return configValue;
        }
    }
    public static int runConfigValueUpdateAndLabels(int configValue) {
        if(configValue <= 0) {
            return 1;
        }
        else {
            return configValue;
        }
    }
    private void setSaveConfigActions() {
        saveConfig.setOnMouseClicked(event -> {
            String max = maxIterationsField.getText();
            this.maxIterations = -20;
            try {
                int check = Integer.parseInt(max);
                this.maxIterations = runConfigValueMax(check);
            }
            catch (NumberFormatException num1) {
                this.maxIterations = 1;
            }
            String update = updateIntervalsField.getText();
            updateIterations = -20;
            try {
                int check = Integer.parseInt(update);
                this.updateIterations = runConfigValueUpdateAndLabels(check);
            }
            catch (NumberFormatException num2) {
                this.updateIterations = 0;
            }
            String labels = numLabelsField.getText();
            this.numLabels = -20;
            try {
                int check = Integer.parseInt(labels);
                this.numLabels = runConfigValueUpdateAndLabels(check);
            }
            catch (NumberFormatException num3) {
                this.numLabels = 0;
            }
            this.continRun = continRunBox.isSelected();
            setStoreConfig(maxIterations, updateIterations, numLabels, continRun);
            dialog.close();
        });
    }
    public void setStoreConfig(int maxIterations, int updateIterations, int numLabels, boolean continRun) {
        this.storeConfig.maxIterations = maxIterations;
        this.storeConfig.numLabels = numLabels;
        this.storeConfig.updateIterations = updateIterations;
        this.storeConfig.continRun = continRun;
    }
    public void clearFields() {
        this.maxIterationsField.clear();
        this.updateIntervalsField.clear();
        this.numLabelsField.clear();
        this.continRunBox.setSelected(false);
    }
    public ClusteringConfig getStoreConfig() {
        return this.storeConfig;
    }
    public void setFields(int maxIterations, int updateIterations, int numLabels, boolean continRun) {
        Integer max = maxIterations;
        this.maxIterationsField.setText(max.toString());
        Integer update = updateIterations;
        this.updateIntervalsField.setText(update.toString());
        Integer labels = numLabels;
        this.numLabelsField.setText(labels.toString());
        this.continRunBox.setSelected(continRun);
    }

}
