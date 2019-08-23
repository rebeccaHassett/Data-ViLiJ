package ui;

import algorithmSet.AlgorithmList;
import algorithmSet.ClusteringAlgorithm;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import static vilij.templates.UITemplate.SEPARATOR;

public class ClusteringListCell extends ListCell<String> {
    private HBox hbox = new HBox();
    private Label label = new Label("");
    private String lastItem;


    public ClusteringListCell(ApplicationTemplate applicationTemplate, Stage primaryStage) {
        super();
        Pane pane = new Pane();
        Button button;
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String configIconPath = String.join(SEPARATOR,
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.CONFIG_ICON.name()));
        button = new Button();
        button.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(configIconPath))));
        hbox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        ClusteringConfig dialogCluster = ClusteringConfig.getDialog();
        dialogCluster.init(primaryStage);
        ClusteringConfig dialogCluster2 = ClusteringConfig.getDialog();
        dialogCluster2.init(primaryStage);
        button.setOnAction(event -> {
            AlgorithmList algoCheck = ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmList();
            if (algoCheck.getAlgorithmFromList(lastItem) == null) {
                dialogCluster.setStoreConfig(-20,-20,-20, false);
                dialogCluster.clearFields();
                dialogCluster.show(manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()), manager.getPropertyValue(AppPropertyTypes.CLUSTERING_ALGORITHMS.name()));
                if (!(dialogCluster.getStoreConfig().maxIterations == -20) && !(dialogCluster.getStoreConfig().updateIterations == -20) && !(dialogCluster.getStoreConfig().numLabels == -20)) {
                    ClusteringAlgorithm algo = new ClusteringAlgorithm(manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()), lastItem, dialogCluster.getStoreConfig());
                    ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmList().addAlgorithmToList(lastItem, algo);
                }
            }
            else {
                ClusteringConfig dialogCluster3 = ((ClusteringAlgorithm) algoCheck.getAlgorithmFromList(lastItem)).config;
                int max = dialogCluster3.maxIterations;
                int update = dialogCluster3.updateIterations;
                int label = dialogCluster3.numLabels;
                boolean checked = dialogCluster3.continRun;
                dialogCluster2.setFields(max, update, label, checked);
                dialogCluster2.show(manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()),manager.getPropertyValue(AppPropertyTypes.CLUSTERING_ALGORITHMS.name()));
            }
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
            lastItem = item;
            label.setText(item != null ? item : "");
            setGraphic(hbox);
        }
    }
}
