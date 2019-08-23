package ui;

import actions.AppActions;
import algorithmSet.Algorithm;
import algorithmSet.AlgorithmList;
import algorithmSet.ClassificationAlgorithm;
import algorithmSet.ClusteringAlgorithm;
import algorithms.Classifier;
import algorithms.Clusterer;
import data.DataSet;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import settings.AppPropertyTypes;
import settings.ClassificationProperties;
import settings.ClusteringProperties;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display
    protected String cssVilijPath;
    private Button run;
    private TextArea displayInfo;
    private ChoiceBox<String> algorithmTypeSelector;
    private ListView<String> classificationList;
    private ListView<String> clusteringList;
    private Text leftPanelTitle;
    private VBox leftPanel;
    private ToggleButton editButton;
    private String selectedAlgorithmName;
    private ToggleButton doneButton;
    private ToggleGroup group;
    private StackPane listContainer;
    private AlgorithmList algorithmList;
    private Button backButton;
    private Button displayButton;
    private Text chartTitle;
    private int maxIter;
    private boolean isRunning = false;
    private boolean firstUpdate = true;
    private List<Integer> output;
    private DataSet finalDataSet;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private Task currentTask;
    private Classifier classify;
    private Clusterer clustify;

    public LineChart<Number, Number> getChart() {
        return chart;
    }
    public Task getCurrentTask() {
        return this.currentTask;
    }
    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }
    public Classifier getClassify() {
        return this.classify;
    }
    public Clusterer getClustify() {
        return this.clustify;
    }
    public void setClustify(Clusterer clustify) {
        this.clustify = clustify;
    }
    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    private int getMaxIter() {
        return this.maxIter;
    }

    private void setMaxIter(int maxIterations) {
        this.maxIter = maxIterations;
    }

    private boolean getFirstUpdate() {
        return this.firstUpdate;
    }

    private void setFirstUpdate(boolean firstUpdate) {
        this.firstUpdate = firstUpdate;
    }

    public boolean getIsRunning() {
        return this.isRunning;
    }

    public StackPane getListContainer() {
        return this.listContainer;
    }

    public AlgorithmList getAlgorithmList() {
        return this.algorithmList;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    public Text getLeftPanelTitle() {
        return this.leftPanelTitle;
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath = String.join(SEPARATOR,
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        cssVilijPath = String.join(SEPARATOR,
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.CSS_PATH.name()));
        scrnshotButton = setToolbarButton(scrnshoticonPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),
                true);
        toolBar.getItems().addAll(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate, primaryStage));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
        chart.getData().clear();
    }

    public String getCurrentText() {
        return textArea.getText();
    }

    public ListView<String> getClassificationList() {
        return classificationList;
    }

    public ListView<String> getClusteringList() {
        return clusteringList;
    }

    private void layout() {
        PropertyManager manager = applicationTemplate.manager;
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));
        chart.getStylesheets().add(cssVilijPath);
        chart.setAnimated(false);
        newButton.setDisable(false);
        algorithmList = new AlgorithmList();
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String runiconPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(AppPropertyTypes.RUN_ICON.name()));
        run = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(runiconPath))));
        run.setTooltip(new Tooltip(manager.getPropertyValue(AppPropertyTypes.RUN_TOOLTIP.name())));
        run.setDisable(true);
        run.setVisible(false);
        String backiconPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(AppPropertyTypes.BACK_ICON.name()));
        backButton = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(backiconPath))));
        backButton.setTooltip(new Tooltip(manager.getPropertyValue(AppPropertyTypes.BACK_TOOLTIP.name())));
        backButton.setDisable(true);
        backButton.setVisible(false);
        String displayiconPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(AppPropertyTypes.DISPLAY_ICON.name()));
        displayButton = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(displayiconPath))));
        displayButton.setTooltip(new Tooltip(manager.getPropertyValue(AppPropertyTypes.DISPLAY_TOOLTIP.name())));
        displayButton.setDisable(false);
        displayButton.setVisible(false);
        chart.setVisible(false);
        chartTitle = new Text(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));
        leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight);

        leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();
        textArea.getStylesheets().add(cssVilijPath);
        textArea.applyCss();
        textArea.setPrefRowCount(10);
        displayInfo = new TextArea();
        displayInfo.setEditable(false);
        ObservableList<String> types = FXCollections.observableArrayList(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()), manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()), manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()));
        algorithmTypeSelector = new ChoiceBox<>();
        algorithmTypeSelector.setValue(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()));
        algorithmTypeSelector.setItems(types);
        group = new ToggleGroup();
        editButton = new ToggleButton(manager.getPropertyValue(AppPropertyTypes.EDIT.name()));
        doneButton = new ToggleButton(manager.getPropertyValue(AppPropertyTypes.DONE.name()));
        editButton.setToggleGroup(group);
        doneButton.setToggleGroup(group);
        group.selectToggle(editButton);
        HBox toggleButtons = new HBox(editButton, doneButton);
        String classifList[] = manager.getPropertyValue(ClassificationProperties.CLASSIFICATION_LIST.name()).split(",");
        ObservableList<String> classificationStrings = FXCollections.observableArrayList(classifList);
        String clusterList[] = manager.getPropertyValue(ClusteringProperties.CLUSTERING_LIST.name()).split(",");
        ObservableList<String> clusteringStrings = FXCollections.observableArrayList(clusterList);
        classificationList = new ListView<>(classificationStrings);
        clusteringList = new ListView<>(clusteringStrings);
        classificationList.setCellFactory(param -> new ClassificationListCell(applicationTemplate, primaryStage));
        clusteringList.setCellFactory(param -> new ClusteringListCell(applicationTemplate, primaryStage));
        listContainer = new StackPane();
        listContainer.getChildren().addAll(algorithmTypeSelector, classificationList, clusteringList);
        algorithmTypeSelector.toFront();
        classificationList.setVisible(false);
        classificationList.setDisable(true);
        clusteringList.setVisible(false);
        clusteringList.setDisable(true);
        HBox processButtonBox = new HBox(run, backButton, displayButton);

        leftPanel.getChildren().addAll(leftPanelTitle, textArea, toggleButtons, displayInfo, processButtonBox, listContainer);
        leftPanel.setVisible(false);

        StackPane rightPanel = new StackPane(chart, chartTitle);
        chartTitle.toFront();
        StackPane.setAlignment(chartTitle, Pos.TOP_CENTER);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);
    }

    private List<Integer> getOutput() {
        return this.output;
    }

    private void setWorkspaceActions() {
        setTextAreaActions();
        saveButtonActions();
        scrnshotButtonActions();
        scrnshotButtonDisabler();
        setToggleGroupActions();
        setChoiceBoxActions();
        setClassificationListViewActions();
        setClusteringListViewActions();
        setBackButtonActions();
        setRunButtonActions();
        setDisplayButtonActions();
    }

    public Text getChartTitle() {
        return this.chartTitle;
    }
    private void setClassify(Classifier classify) {
        this.classify = classify;
    }
    private void setRunButtonActions() {
        run.setOnMouseClicked(event -> {
            File file;
            DataSet dataSet = null;
            if (editButton.isVisible()) {
                PropertyManager manager = applicationTemplate.manager;
                file = new File(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()));
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.write(textArea.getText());
                    writer.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                try {
                    Path p = file.toPath();
                    dataSet = DataSet.fromTSDFile(p);
                } catch (IOException excep) {
                    ErrorDialog.getDialog().show();
                }
            } else {
                try {
                    dataSet = DataSet.fromTSDFile(((AppActions) applicationTemplate.getActionComponent()).getDataFilePath());
                } catch (IOException excep) {
                    ErrorDialog.getDialog().show();
                }
            }
            if (classificationList.isVisible()) {
                Algorithm algo = getAlgorithmList().getAlgorithmFromList(classificationList.getSelectionModel().getSelectedItem());
                ClassificationAlgorithm classy = (ClassificationAlgorithm) algo;
                        if (classy.config.continRun2) {
                            setIsRunning(true);
                            displayButton.setDisable(true);
                            scrnshotButton.setDisable(true);
                            backButton.setDisable(true);
                            editButton.setDisable(true);
                            doneButton.setDisable(true);
                            listContainer.setVisible(false);
                            if (getFirstUpdate()) {
                                setMaxIter(classy.config.maxIterations2);
                            }
                            run.setDisable(true);
                            Task task = new Task() {
                                @Override
                                protected List<Integer> call() throws Exception {
                                    try {
                                        File file;
                                        DataSet dataSet = null;
                                        if (editButton.isVisible()) {
                                            PropertyManager manager = applicationTemplate.manager;
                                            file = new File(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()));
                                            try (PrintWriter writer = new PrintWriter(file)) {
                                                writer.write(textArea.getText());
                                                writer.close();
                                                newButton.setVisible(true);
                                            } catch (IOException e) {
                                                System.err.println(e.getMessage());
                                            }
                                        } else {
                                            try {
                                                dataSet = DataSet.fromTSDFile(((AppActions) applicationTemplate.getActionComponent()).getDataFilePath());
                                            } catch (IOException excep) {
                                                ErrorDialog.getDialog().show();
                                            }
                                        }
                                        PropertyManager manager = applicationTemplate.manager;
                                        Class<?> c = Class.forName(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION_RUNNING.name()) + classificationList.getSelectionModel().getSelectedItems().get(0));
                                        Classifier classif = null;
                                        for (Constructor<?> ctor : c.getConstructors()) {
                                            Class<?>[] paramTypes = ctor.getParameterTypes();
                                            if (paramTypes.length == 4) {
                                               Object o = ctor.newInstance(dataSet, classy.config.maxIterations2, classy.config.updateIterations2, classy.config.continRun2);
                                                classif = (Classifier) o;
                                            }
                                        }
                                        setMaxIter(classy.config.maxIterations2);
                                        while (getMaxIter() > 0) {
                                            if(classif != null) {
                                                classif.run();

                                                while (classif.getOutput() == null)
                                                {
                                                    exitButton.setVisible(true);
                                                }
                                                output = classif.getOutput();
                                                output = getOutput();
                                            }
                                            if (output != null) {
                                                xAxis.setAutoRanging(true);
                                                yAxis.setAutoRanging(true);
                                                double upper = xAxis.getUpperBound();
                                                double lower = xAxis.getLowerBound();
                                                xAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumX());
                                                xAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumX());
                                                yAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumY());
                                                yAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumY());
                                                XYChart.Series<Number, Number> seriesLine = new XYChart.Series<>();
                                                if (output.get(1) != 0) {
                                                    double calcYUp = ((output.get(0) * (upper)) + output.get(2)) / (-output.get(1));
                                                    XYChart.Data<Number, Number> dLine3 = new XYChart.Data<>(upper, calcYUp);
                                                    seriesLine.getData().add(dLine3);
                                                    double calcYDown = ((output.get(0) * (lower)) + output.get(2)) / (-output.get(1));
                                                    XYChart.Data<Number, Number> dLine4 = new XYChart.Data<>(lower, calcYDown);
                                                    seriesLine.getData().add(dLine4);
                                                    seriesLine.setName(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()));
                                                }
                                                Platform.runLater(() -> {
                                                    getChart().getData().clear();
                                                    chart.getData().add(seriesLine);
                                                    if(seriesLine.getData().size() == 2) {
                                                        seriesLine.getData().get(0).getNode().setVisible(false);
                                                        seriesLine.getData().get(1).getNode().setVisible(false);
                                                    }
                                                    Node line = seriesLine.getNode().lookup(manager.getPropertyValue(AppPropertyTypes.CHART_LOOKUP.name()));
                                                    line.setStyle(manager.getPropertyValue(AppPropertyTypes.GREEN.name()));
                                                    ((AppData) applicationTemplate.getDataComponent()).displayData();
                                                    setMaxIter(getMaxIter() - classy.config.updateIterations2);
                                                });
                                                try {
                                                    Thread.sleep(1250);
                                                } catch (InterruptedException except) {
                                                    ErrorDialog.getDialog().show();
                                                }
                                                if (getMaxIter() <= 0) {
                                                    run.setDisable(false);
                                                    displayButton.setDisable(false);
                                                    scrnshotButton.setDisable(false);
                                                    backButton.setDisable(false);
                                                    listContainer.setVisible(true);
                                                    editButton.setDisable(false);
                                                    doneButton.setDisable(false);
                                                    setIsRunning(false);
                                                }
                                            }
                                        }
                                    } catch (Throwable et) {
                                        throw new Exception();
                                    }
                                    return output;

                                }
                            };
                            new Thread(task).start();
                            setCurrentTask(task);
                        } else {
                            try {
                                Thread.sleep(500);
                            }
                            catch (InterruptedException ex) {
                                ErrorDialog.getDialog().show();
                            }
                            if (getFirstUpdate()) {
                                setMaxIter(classy.config.maxIterations2);
                            }
                            run.setDisable(true);
                            setFirstUpdate(false);
                            scrnshotButton.setDisable(true);
                            setIsRunning(true);
                            listContainer.setVisible(false);
                            editButton.setDisable(true);
                            doneButton.setDisable(true);
                            displayButton.setDisable(true);
                            backButton.setDisable(true);
                            PropertyManager manager = applicationTemplate.manager;
                            try {
                                Class<?> c = Class.forName(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION_RUNNING.name()) + classificationList.getSelectionModel().getSelectedItems().get(0));
                                Classifier classify;
                                for (Constructor<?> ctor : c.getConstructors()) {
                                    Class<?>[] paramTypes = ctor.getParameterTypes();
                                    if (paramTypes.length == 4) {
                                        try {
                                            Object o = ctor.newInstance(dataSet, classy.config.maxIterations2, classy.config.updateIterations2, true);
                                            classify = (Classifier) o;
                                            setClassify(classify);
                                        }catch (InstantiationException | IllegalAccessException | InvocationTargetException excep_) {
                                            ErrorDialog.getDialog().show();
                                        }
                                    }
                                }
                            }
                            catch (ClassNotFoundException ex) {
                                ErrorDialog.getDialog().show();
                            }
                            Task task = new Task() {

                                @Override
                                protected Integer call() throws Exception {
                                    try {
                                        run.setDisable(true);
                                        getClassify().run();
                                    }catch (Exception e) {
                                        throw new Exception();
                                    }
                                        Platform.runLater(() -> {
                                            run.setDisable(true);
                                            while (getClassify().getOutput() == null) {
                                                exitButton.setVisible(true);
                                            }
                                            List<Integer> myOutput = getClassify().getOutput();
                                            XYChart.Series<Number, Number> seriesLine = new XYChart.Series<>();
                                            xAxis.setAutoRanging(true);
                                            yAxis.setAutoRanging(true);
                                            double upper = xAxis.getUpperBound();
                                            double lower = xAxis.getLowerBound();
                                            xAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumX());
                                            xAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumX());
                                            yAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumY());
                                            yAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumY());
                                            if (myOutput.get(1) != 0) {
                                                double calcYUp = ((myOutput.get(0) * (upper)) + myOutput.get(2)) / (-myOutput.get(1));
                                                XYChart.Data<Number, Number> dLine3 = new XYChart.Data<>(upper, calcYUp);
                                                seriesLine.getData().add(dLine3);
                                                double calcYDown = ((myOutput.get(0) * (lower)) + myOutput.get(2)) / (-myOutput.get(1));
                                                XYChart.Data<Number, Number> dLine4 = new XYChart.Data<>(lower, calcYDown);
                                                seriesLine.getData().add(dLine4);
                                                setIsRunning(true);
                                            }
                                            PropertyManager manager = applicationTemplate.manager;
                                            seriesLine.setName(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()));
                                            run.setDisable(true);
                                            chart.getData().clear();
                                            chart.getData().add(seriesLine);
                                            if(seriesLine.getData().size() == 2) {
                                                seriesLine.getData().get(0).getNode().setVisible(false);
                                                seriesLine.getData().get(1).getNode().setVisible(false);
                                            }
                                            if(classy.config.updateIterations2 > 500) {
                                                try {
                                                    Thread.sleep(3000);
                                                } catch (InterruptedException ex) {
                                                    ErrorDialog.getDialog().show();
                                                }
                                            }
                                            else if(classy.config.updateIterations2 >= 30) {
                                                try {
                                                    Thread.sleep(5 * classy.config.updateIterations2);
                                                } catch (InterruptedException ex) {
                                                    ErrorDialog.getDialog().show();
                                                }
                                            }
                                            else {
                                                try {
                                                    Thread.sleep(100 * classy.config.updateIterations2);
                                                } catch (InterruptedException ex) {
                                                    ErrorDialog.getDialog().show();
                                                }
                                            }
                                            Node line = seriesLine.getNode().lookup(manager.getPropertyValue(AppPropertyTypes.CHART_LOOKUP.name()));
                                            line.setStyle(manager.getPropertyValue(AppPropertyTypes.GREEN.name()));
                                            ((AppData) applicationTemplate.getDataComponent()).displayData();
                                            setMaxIter(getMaxIter() - classy.config.updateIterations2);
                                            if (maxIter <= 0) {
                                                setMaxIter(classy.config.maxIterations2);
                                                setFirstUpdate(true);
                                                setIsRunning(false);
                                                displayButton.setDisable(false);
                                                backButton.setDisable(false);
                                                editButton.setDisable(false);
                                                doneButton.setDisable(false);
                                            }
                                            scrnshotButton.setDisable(false);
                                            listContainer.setVisible(true);
                                            run.setDisable(false);
                                        });
                                    return 0;
                                }
                            };
                           new Thread(task).start();
                           setCurrentTask(task);
                        }
            }
            else if(clusteringList.isVisible()) {
                Algorithm algoClust = getAlgorithmList().getAlgorithmFromList(clusteringList.getSelectionModel().getSelectedItem());
                ClusteringAlgorithm clusty = (ClusteringAlgorithm) algoClust;
                if (clusty.config.continRun) {
                    setIsRunning(true);
                    displayButton.setDisable(true);
                    scrnshotButton.setDisable(true);
                    backButton.setDisable(true);
                    editButton.setDisable(true);
                    doneButton.setDisable(true);
                    listContainer.setVisible(false);
                    if (getFirstUpdate()) {
                        setMaxIter(clusty.config.maxIterations);
                    }
                    run.setDisable(true);
                    Task task = new Task() {
                        @Override
                        protected List<Integer> call() throws Exception {
                            try {
                                File file;
                                DataSet dataSet = null;
                                if (editButton.isVisible()) {
                                    PropertyManager manager = applicationTemplate.manager;
                                    file = new File(manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()));
                                    try (PrintWriter writer = new PrintWriter(file)) {
                                        writer.write(textArea.getText());
                                        writer.close();
                                        newButton.setVisible(true);
                                    } catch (IOException e) {
                                        System.err.println(e.getMessage());
                                    }
                                    try {
                                        Path p = file.toPath();
                                        dataSet = DataSet.fromTSDFile(p);
                                    } catch (IOException excep) {
                                        ErrorDialog.getDialog().show();
                                    }
                                } else {
                                    try {
                                        dataSet = DataSet.fromTSDFile(((AppActions) applicationTemplate.getActionComponent()).getDataFilePath());
                                    } catch (IOException excep) {
                                        ErrorDialog.getDialog().show();
                                    }
                                }
                                PropertyManager manager = applicationTemplate.manager;
                                if(dataSet.getLabels().keySet().size() == 1&& clusteringList.getSelectionModel().getSelectedItems().get(0).equals(manager.getPropertyValue(AppPropertyTypes.K_MEANS_CLUSTERER.name()))) {
                                    Platform.runLater(() -> {
                                        chart.getData().clear();
                                        ((AppData) applicationTemplate.getDataComponent()).displayData();
                                        run.setDisable(false);
                                        displayButton.setDisable(false);
                                        backButton.setDisable(false);
                                        editButton.setDisable(false);
                                        listContainer.setVisible(true);
                                        doneButton.setDisable(false);
                                        clusteringList.setVisible(true);
                                        scrnshotButton.setDisable(false);
                                    });
                                    List<Integer> list2 = new LinkedList<>();
                                    return list2;
                                }
                                Class<?> c = Class.forName(manager.getPropertyValue(AppPropertyTypes.CLUSTERING_RUNNING.name()) + clusteringList.getSelectionModel().getSelectedItems().get(0));
                                Clusterer clustif = null;
                                for (Constructor<?> ctor : c.getConstructors()) {
                                    Class<?>[] paramTypes = ctor.getParameterTypes();
                                    if (paramTypes.length == 4) {
                                        List<String> instanceNames = new ArrayList<>(dataSet.getLabels().keySet());
                                        //Fix KMeans Clusterer Specific Bug
                                        if(clusty.config.numLabels > instanceNames.size() && clusteringList.getSelectionModel().getSelectedItems().get(0).equals(manager.getPropertyValue(AppPropertyTypes.K_MEANS_CLUSTERER.name()))) {
                                            clusty.config.numLabels = instanceNames.size();
                                        }
                                        Object o = ctor.newInstance(dataSet, clusty.config.maxIterations, clusty.config.updateIterations, clusty.config.numLabels);
                                        clustif = (Clusterer) o;
                                    }
                                }
                                xAxis.setAutoRanging(false);
                                yAxis.setAutoRanging(false);
                                xAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumX());
                                xAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumX());
                                yAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumY());
                                yAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumY());
                                setMaxIter(clusty.config.maxIterations);
                                while (getMaxIter() > 0) {
                                    if(clustif != null) {
                                        clustif.run();

                                        while (clustif.getDataSet() == null)
                                        {
                                            exitButton.setVisible(true);
                                        }

                                        finalDataSet = clustif.getDataSet();         //clustif.getDataSet();
                                    }
                                    if (finalDataSet != null) {
                                        Platform.runLater(() -> {
                                            TSDProcessor clusterProcessor = new TSDProcessor();
                                            clusterProcessor.setDataLabels((HashMap<String, String>)finalDataSet.getLabels());
                                            clusterProcessor.setDataPoints((HashMap<String, Point2D>) finalDataSet.getLocations());
                                            getChart().getData().clear();
                                            ((AppData) applicationTemplate.getDataComponent()).clusterDisplay(clusterProcessor);
                                            setMaxIter(getMaxIter() - clusty.config.updateIterations);
                                        });
                                        try {
                                            Thread.sleep(1250);
                                        } catch (InterruptedException except) {
                                            ErrorDialog.getDialog().show();
                                        }
                                        if (getMaxIter() <= 0) {
                                            run.setDisable(false);
                                            displayButton.setDisable(false);
                                            scrnshotButton.setDisable(false);
                                            backButton.setDisable(false);
                                            listContainer.setVisible(true);
                                            editButton.setDisable(false);
                                            doneButton.setDisable(false);
                                            setIsRunning(false);
                                        }
                                    }
                                }
                            } catch (Exception et) {

                            }
                            List<Integer> list = new LinkedList<>();
                            list.add(0);
                            return list;
                        }
                    };
                    new Thread(task).start();
                    setCurrentTask(task);
                } else {
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException ex) {
                        ErrorDialog.getDialog().show();
                    }
                    if (getFirstUpdate()) {
                        setMaxIter(clusty.config.maxIterations);
                    }
                    run.setDisable(true);
                    setFirstUpdate(false);
                    scrnshotButton.setDisable(true);
                    setIsRunning(true);
                    listContainer.setVisible(false);
                    editButton.setDisable(true);
                    doneButton.setDisable(true);
                    displayButton.setDisable(true);
                    backButton.setDisable(true);
                    PropertyManager manager = applicationTemplate.manager;
                    //handles bug specific to KMeansClusterer
                    if(dataSet.getLabels().keySet().size() == 1 && clusteringList.getSelectionModel().getSelectedItems().get(0).equals(manager.getPropertyValue(AppPropertyTypes.K_MEANS_CLUSTERER.name()))) {
                        Platform.runLater(() -> {
                            chart.getData().clear();
                            ((AppData) applicationTemplate.getDataComponent()).displayData();
                            run.setDisable(false);
                            displayButton.setDisable(false);
                            editButton.setDisable(false);
                            doneButton.setDisable(false);
                            listContainer.setVisible(true);
                            setIsRunning(false);
                            backButton.setDisable(false);
                            clusteringList.setVisible(true);
                            scrnshotButton.setDisable(false);
                        });
                    }
                    try {
                        Class<?> c = Class.forName(manager.getPropertyValue(AppPropertyTypes.CLUSTERING_RUNNING.name()) + clusteringList.getSelectionModel().getSelectedItems().get(0));
                        Clusterer clustify;
                        for (Constructor<?> ctor : c.getConstructors()) {
                            Class<?>[] paramTypes = ctor.getParameterTypes();
                            if (paramTypes.length == 4) {
                                try {
                                    List<String> instanceNames = new ArrayList<>(dataSet.getLabels().keySet());
                                    if(clusty.config.numLabels > instanceNames.size() && clusteringList.getSelectionModel().getSelectedItems().get(0).equals(manager.getPropertyValue(AppPropertyTypes.K_MEANS_CLUSTERER.name()))) {
                                        clusty.config.numLabels = instanceNames.size();
                                    }
                                    Object o = ctor.newInstance(dataSet, clusty.config.maxIterations, clusty.config.updateIterations, clusty.config.numLabels);
                                    clustify = (Clusterer) o;
                                    setClustify(clustify);
                                }catch (InstantiationException | IllegalAccessException | InvocationTargetException excep_) {
                                    ErrorDialog.getDialog().show();
                                }
                            }
                        }
                    }
                    catch (ClassNotFoundException ex) {
                        ErrorDialog.getDialog().show();
                    }
                    xAxis.setAutoRanging(false);
                    yAxis.setAutoRanging(false);
                    xAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumX());
                    xAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumX());
                    yAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumY());
                    yAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumY());
                    Task task = new Task() {

                        @Override
                        protected Integer call() throws Exception {
                            run.setDisable(true);
                            try {
                                getClustify().run();
                            }
                            catch (Exception e) {
                                throw new Exception();
                            }
                            while (getClustify().getDataSet() == null)
                            {
                                exitButton.setVisible(true);
                            }

                            finalDataSet = getClustify().getDataSet();
                            if(!(finalDataSet == null)) {
                            Platform.runLater(() -> {
                                run.setDisable(true);
                                while (getClustify().getDataSet() == null) {
                                    exitButton.setVisible(true);
                                }
                                run.setDisable(true);
                                TSDProcessor clusterProcessor = new TSDProcessor();
                                clusterProcessor.setDataLabels((HashMap<String, String>) finalDataSet.getLabels());
                                clusterProcessor.setDataPoints((HashMap<String, Point2D>) finalDataSet.getLocations());
                                getChart().getData().clear();
                                ((AppData) applicationTemplate.getDataComponent()).clusterDisplay(clusterProcessor);
                                if (clusty.config.updateIterations > 500) {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException ex) {
                                        ErrorDialog.getDialog().show();
                                    }
                                } else if (clusty.config.updateIterations >= 30) {
                                    try {
                                        Thread.sleep(5 * clusty.config.updateIterations);
                                    } catch (InterruptedException ex) {
                                        ErrorDialog.getDialog().show();
                                    }
                                } else {
                                    try {
                                        Thread.sleep(100 * clusty.config.updateIterations);
                                    } catch (InterruptedException ex) {
                                        ErrorDialog.getDialog().show();
                                    }
                                }
                                setMaxIter(getMaxIter() - clusty.config.updateIterations);
                                if (maxIter <= 0) {
                                    setMaxIter(clusty.config.maxIterations);
                                    setFirstUpdate(true);
                                    setIsRunning(false);
                                    displayButton.setDisable(false);
                                    backButton.setDisable(false);
                                    editButton.setDisable(false);
                                    doneButton.setDisable(false);
                                }
                                scrnshotButton.setDisable(false);
                                listContainer.setVisible(true);
                                run.setDisable(false);
                            });
                            }
                            return 0;
                        }
                    };
                    new Thread(task).start();
                    setCurrentTask(task);
                }
                }
        });
    }

    private void setDisplayButtonActions() {
        displayButton.setOnAction(event -> {
            if (hasNewText) {
                try {
                    chart.getData().clear();
                    xAxis.setAutoRanging(false);
                    yAxis.setAutoRanging(false);
                    xAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumX());
                    xAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumX());
                    yAxis.setUpperBound(((AppData) applicationTemplate.getDataComponent()).getMaximumY());
                    yAxis.setLowerBound(((AppData) applicationTemplate.getDataComponent()).getMinimumY());
                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                    dataComponent.displayData();
                    scrnshotButton.setDisable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Button getDisplayButton() {
        return this.displayButton;
    }

    private void setBackButtonActions() {
        backButton.setOnMouseClicked(event -> {
            PropertyManager manager = applicationTemplate.manager;
            classificationList.setVisible(false);
            clusteringList.setVisible(false);
            algorithmTypeSelector.setVisible(true);
            algorithmTypeSelector.setDisable(false);
            algorithmTypeSelector.setValue(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()));
            selectedAlgorithmName = manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name());
            backButton.setVisible(false);
            run.setVisible(false);
        });
    }

    private void setClassificationListViewActions() {
        classificationList.setOnMouseClicked(event -> {
            run.setDisable(true);
            selectedAlgorithmName = classificationList.getSelectionModel().getSelectedItem();
            run.setVisible(true);
            if (algorithmList.getAlgorithmFromList(selectedAlgorithmName) != null) {
                run.setDisable(false);
            }
        });
    }

    private void setClusteringListViewActions() {
        clusteringList.setOnMouseClicked(event -> {
            run.setDisable(true);
            selectedAlgorithmName = clusteringList.getSelectionModel().getSelectedItem();
            run.setVisible(true);
            newButton.setVisible(true);
            if (algorithmList.getAlgorithmFromList(selectedAlgorithmName) != null) {
                run.setDisable(false);
            }
        });
    }

    public Button getRun() {
        return this.run;
    }

    public Button getBackButton() {
        return this.backButton;
    }

    public void scrnshotButtonDisabler() {
        scrnshotButton.setDisable(true);
    }

    public void saveButtonActions() {
        saveButton.setDisable(true);
    }

    private void setChoiceBoxActions() {
        PropertyManager manager = applicationTemplate.manager;
        algorithmTypeSelector.setOnAction(event -> {
            if (algorithmTypeSelector.getItems().contains(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()))) {
                if (algorithmTypeSelector.getValue().equals(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()))) {
                    classificationList.toFront();
                    classificationList.setVisible(true);
                    classificationList.setDisable(false);
                    algorithmTypeSelector.setVisible(false);
                    algorithmTypeSelector.setDisable(true);
                    backButton.setVisible(true);
                    backButton.setDisable(false);
                    return;
                }
            }
            if (algorithmTypeSelector.getItems().contains(manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()))) {
                if (algorithmTypeSelector.getValue().equals(manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()))) {
                    clusteringList.toFront();
                    clusteringList.setVisible(true);
                    clusteringList.setDisable(false);
                    algorithmTypeSelector.setVisible(false);
                    algorithmTypeSelector.setDisable(true);
                    backButton.setVisible(true);
                    backButton.setDisable(false);
                }
            }
        });
    }

    private void scrnshotButtonActions() {
        scrnshotButton.setOnAction(event -> {
            try {
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException io) {
                ErrorDialog.getDialog().show();
            }
        });
    }

    public TextArea getTextArea() {
        return this.textArea;
    }

    public void setDisplayInfoText(String text) {
        this.displayInfo.setText(text);
    }

    public VBox getLeftPanel() {
        return this.leftPanel;
    }

    private void setToggleGroupActions() {
        PropertyManager manager = applicationTemplate.manager;
        editButton.setOnMouseClicked(event -> {
            chart.getData().clear();
            scrnshotButtonDisabler();
            textArea.setEditable(true);
            displayInfo.setText(manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()));
            run.setVisible(false);
            listContainer.setVisible(false);
            saveButton.setDisable(true);
            backButton.setVisible(false);
        });
        doneButton.setOnMouseClicked(event -> {
            String textIfError = textArea.getText();
            textArea.setEditable(false);
            (applicationTemplate.getDataComponent()).clear();
            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());

            if (textArea.getText().equals(((AppActions) applicationTemplate.getActionComponent()).getLastSaved()) && (((AppActions) applicationTemplate.getActionComponent()).getDataFilePath()) != null) {
                saveButton.setDisable(true);
            }
            if (textArea.getText().isEmpty()) {
                textArea.setText(textIfError);
                group.selectToggle(editButton);
                algorithmTypeSelector.setVisible(false);
                textArea.setEditable(true);
                saveButton.setDisable(true);
            }
            classificationList.setVisible(false);
            clusteringList.setVisible(false);
            if (!textArea.getText().isEmpty()) {
                algorithmTypeSelector.setVisible(true);
            }
        });
    }


    public ChoiceBox<String> getAlgorithmTypeSelector() {
        return algorithmTypeSelector;
    }

    public ToggleGroup getGroup() {
        return this.group;
    }

    public ToggleButton getEditButton() {
        return this.editButton;
    }

    public ToggleButton getDoneButton() {
        return this.doneButton;
    }

    public Button getSaveButton() {
        return this.saveButton;
    }

    public TextArea getDisplayInfoTextArea() {
        return this.displayInfo;
    }

    private void setTextAreaActions() {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                hasNewText = !(textArea.getText().isEmpty());
                if (!newValue.equals(oldValue)) {
                    if (hasNewText && doneButton.isSelected()) {
                        saveButton.setDisable(false);
                    } else {
                        saveButton.setDisable(true);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println(newValue);
            }
        });
    }
}