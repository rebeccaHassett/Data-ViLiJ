package actions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;

public class AlgorithmExitDialog extends Stage implements Dialog {
    private Button returnAlgo;
    private Button terminateAlgo;
    private static AlgorithmExitDialog dialog;
    private Label algoMessage = new Label();
    private AlgorithmExitDialog() {

    }
    public static AlgorithmExitDialog getDialog() {
        if(dialog == null) {
            dialog = new AlgorithmExitDialog();
        }
        return dialog;
    }
    @Override
    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);

        PropertyManager manager = PropertyManager.getManager();
        VBox messagePane2 = new VBox();

        terminateAlgo = new Button(manager.getPropertyValue(AppPropertyTypes.YES.name()));
        returnAlgo = new Button(manager.getPropertyValue(AppPropertyTypes.NO.name()));
        terminateAlgo.setOnMouseClicked(event -> {
            System.exit(0);
        });
        returnAlgo.setOnMouseClicked(event -> {
            dialog.close();
        });
        HBox buttonBox = new HBox(terminateAlgo, returnAlgo);
        Text terminate = new Text(manager.getPropertyValue(AppPropertyTypes.ALGO_TERMINATE.name()));
        messagePane2.getChildren().addAll(terminate, buttonBox);
        messagePane2.setAlignment(Pos.CENTER);
        messagePane2.setPadding(new Insets(10, 20, 20, 20));
        messagePane2.setSpacing(10);
        this.setScene(new Scene(messagePane2));
    }
    @Override
    public void show(String dialogTitle, String message) {
        setTitle(dialogTitle);           // set the title of the dialog
        setAlgoMessage(message); // set the main error message
        showAndWait();              // open the dialog and wait for the user to click the close button
    }
    private void setAlgoMessage(String message) { algoMessage.setText(message); }

}
