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
import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;

public class ClassificationConfig extends Stage implements Dialog{
    private static ClassificationConfig dialog;
    private Label configMessage = new Label();
    public int maxIterations2 = -20;
    public int updateIterations2 = -20;
    private Button saveConfig2;
    public boolean continRun2;
    private TextField maxIterationsField2 = new TextField();
    private TextField updateIntervalsField2 = new TextField();
    private CheckBox continRunBox2 = new CheckBox();
    private ClassificationConfig storeConfigClassification;
    private ClassificationConfig() {

    }
    private ClassificationConfig(int maxIterations2, int updateIterations2, boolean continRun2) {
        this.maxIterations2 = maxIterations2;
        this.updateIterations2 = updateIterations2;
        this.continRun2 = continRun2;
    }
    public static ClassificationConfig getDialog() {
        if (dialog == null)
            dialog = new ClassificationConfig();
        return dialog;
    }
    @Override
    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);

        PropertyManager manager     = PropertyManager.getManager();
        VBox messagePane2 = new VBox();
        Text maxIterations2 = new Text(manager.getPropertyValue(AppPropertyTypes.MAXIMUM_ITERATIONS.name()));
        HBox maxIterationsContainer2 = new HBox(maxIterations2, maxIterationsField2);
        Text updateInterval2 = new Text(manager.getPropertyValue(AppPropertyTypes.UPDATE_INTERVALS.name()));
        HBox updateIntervalsContainer2 = new HBox(updateInterval2, updateIntervalsField2);
        Text continRun2 = new Text(manager.getPropertyValue(AppPropertyTypes.CONTINUOUS_RUN.name()));
        HBox continRunContainer2 = new HBox(continRun2, continRunBox2);
        saveConfig2 = new Button(manager.getPropertyValue(AppPropertyTypes.SAVE_CONFIG.name()));
        setSaveConfigClassificationActions();
        messagePane2.getChildren().addAll(maxIterationsContainer2, updateIntervalsContainer2, continRunContainer2, saveConfig2);
        messagePane2.setAlignment(Pos.CENTER);
        messagePane2.setPadding(new Insets(10, 20, 20, 20));
        messagePane2.setSpacing(10);
        storeConfigClassification = new ClassificationConfig(-20,-20,false);
        this.setScene(new Scene(messagePane2));
    }
    @Override
    public void show(String dialogTitle, String message) {
        setTitle(dialogTitle);           // set the title of the dialog
        setConfigMessage(message); // set the main error message
        showAndWait();              // open the dialog and wait for the user to click the close button
    }
    private void setConfigMessage(String message) { configMessage.setText(message); }
    public static int runConfigValueMaxClassif(int configValue) {
        if(configValue <= 0) {
            return 0;
        }
        else {
            return configValue;
        }
    }
    public static int runConfigValueUpdateClassif(int configValue) {
        if(configValue <= 0) {
            return 1;
        }
        else {
            return configValue;
        }
    }
    private void setSaveConfigClassificationActions() {
        saveConfig2.setOnMouseClicked(event -> {
            String max = maxIterationsField2.getText();
            this.maxIterations2 = -20;
            try {
                int check = Integer.parseInt(max);
                this.maxIterations2 = runConfigValueMaxClassif(check);
            }
            catch (NumberFormatException num1) {
                this.maxIterations2 = 0;
            }
            String update = updateIntervalsField2.getText();
            updateIterations2 = -20;
            try {
                int check = Integer.parseInt(update);
                this.updateIterations2 = runConfigValueUpdateClassif(check);
            }
            catch (NumberFormatException num2) {
                this.updateIterations2 = 0;
            }
            this.continRun2 = continRunBox2.isSelected();
            setStoreConfigClassification(maxIterations2, updateIterations2, continRun2);
            dialog.close();
        });
    }
    public void setStoreConfigClassification(int maxIterations2, int updateIterations2, boolean continRun2) {
        this.storeConfigClassification.maxIterations2 = maxIterations2;
        this.storeConfigClassification.updateIterations2 = updateIterations2;
        this.storeConfigClassification.continRun2 = continRun2;
    }
    public void clearFields() {
        this.maxIterationsField2.clear();
        this.updateIntervalsField2.clear();
        this.continRunBox2.setSelected(false);
    }
    public ClassificationConfig getStoreConfigClassification() {
        return this.storeConfigClassification;
    }
    public void setFields(int maxIterations2, int updateIterations2, boolean continRun2) {
        Integer max = maxIterations2;
        this.maxIterationsField2.setText(max.toString());
        Integer update = updateIterations2;
        this.updateIntervalsField2.setText(update.toString());
        this.continRunBox2.setSelected(continRun2);
    }

}
