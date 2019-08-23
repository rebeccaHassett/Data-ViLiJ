package dataprocessors;

import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {
    private TSDProcessor processor;
    private ApplicationTemplate applicationTemplate;
    private int lineNumber = 0;
    private int loadLineNumber = 0;
    private String extraLines = "";
    private int displayNumber;
    private File selected;
    private int minimumX = 1000000000;
    private int maximumX = -1000000000;
    private int minimumY = 1000000000;
    private int maximumY = -1000000000;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }
    public void resetExtraLines() {
        PropertyManager manager = applicationTemplate.manager;
        this.extraLines = manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name());

    }
    public int getMinimumX() {
        return this.minimumX;
    }
    public int getMaximumX() {
        return this.maximumX;
    }
    public void setMinimumX(int minimumX) {
        this.minimumX = minimumX;
    }
    public void setMaximumX(int maximumX) {
        this.maximumX = maximumX;
    }
    public int getMinimumY() {
        return this.minimumY;
    }
    public int getMaximumY() {
        return this.maximumY;
    }
    public void setMinimumY(int minimumY) {
        this.minimumY = minimumY;
    }
    public void setMaximumY(int maximumY) {
        this.maximumY = maximumY;
    }
    private void setExtraLines(String add) {
        this.extraLines = extraLines + add;
    }
    @Override
    public void loadData(Path dataFilePath) {
        setMinimumX(1000000000);
        setMaximumX(-1000000000);
        setMinimumY(1000000000);
        setMaximumY(-1000000000);
        clear();
        (applicationTemplate.getUIComponent()).clear();
        setSelectedFile(dataFilePath.toFile());
        resetExtraLines();
        PropertyManager manager = applicationTemplate.manager;
        StringBuilder loaded = new StringBuilder();
        String loadedCheck;
        String loadFile = dataFilePath.toString();
        File loadingFile = new File(loadFile);
        try {
            int lines = 0;
            Scanner length = new Scanner(loadingFile);
            while (length.hasNextLine()) {
                length.nextLine();
                lines++;
            }
            Scanner names = new Scanner(loadingFile);
            String[] nameCheck = new String[lines];
            boolean noDuplicates = duplicateNameCheck(names, nameCheck);
            names.close();
            if(!noDuplicates) {
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayInfoTextArea().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayInfoTextArea().setText("");
                ((AppUI) applicationTemplate.getUIComponent()).getLeftPanelTitle().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getLeftPanel().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(false);
            }
            if (noDuplicates) {
                Scanner input = new Scanner(loadingFile);
                while (input.hasNextLine() && loadLineNumber < 10) {
                    loadedCheck = input.nextLine();
                    if (loadLineNumber == 9) {
                        loaded.append(loadedCheck);
                    } else {
                        loaded.append(loadedCheck);
                        loaded.append(System.lineSeparator());
                    }
                    loadLineNumber++;
                    processor.processString(loadedCheck);
                }
                loaded.append(System.lineSeparator());
                while (input.hasNextLine()) {
                    loadedCheck = input.nextLine();
                    if (loadLineNumber == 10) {
                        setExtraLines(loadedCheck + System.lineSeparator());
                    } else {
                        setExtraLines(loadedCheck + System.lineSeparator());
                    }
                    loadLineNumber++;
                    processor.processString(loadedCheck);
                }
                processor.getDataPoints().forEach((s, point2D) -> {
                            if (point2D.getX() < getMinimumX()) {
                                setMinimumX((int) point2D.getX() - 5);
                            }
                            if(point2D.getX() > getMaximumX()) {
                                setMaximumX((int) point2D.getX() + 5);
                            }
                            if(point2D.getY() < getMinimumY()) {
                                setMinimumY((int) point2D.getY() - 5);
                            }
                            if(point2D.getY() > getMaximumY()) {
                                setMaximumY((int) point2D.getY() + 5);
                            }
                        }
                );
                input.close();
                String fileName;
                if(getSelectedFile() == null) {
                    fileName = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
                }
                else {
                    fileName = getSelectedFile().toString();
                }
                displayInfo(processor.dataLabels, fileName);
                ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(true);
                if(!noDuplicates) {
                    ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(false);
                }
            }
            } catch(Exception e){
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText("");
            ((AppUI) applicationTemplate.getUIComponent()).getDisplayInfoTextArea().setText("");
            ((AppUI) applicationTemplate.getUIComponent()).setDisplayInfoText("");
            ((AppUI) applicationTemplate.getUIComponent()).getGroup().selectToggle(((AppUI) applicationTemplate.getUIComponent()).getEditButton());
            ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setVisible(false);
           ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(false);
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
                String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
                dialog.show(errTitle, errMsg + errInput + manager.getPropertyValue(AppPropertyTypes.LINE_NUMBER.name()) + loadLineNumber);
                loadLineNumber = 0;
                resetExtraLines();
                return;
            }
            if (loadLineNumber > 10) {
                ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                dialog.show(manager.getPropertyValue(AppPropertyTypes.MESSAGE.name()), manager.getPropertyValue(AppPropertyTypes.LOADING_ONLY_TEN.name()) + loadLineNumber + manager.getPropertyValue(AppPropertyTypes.LINES.name()));
            }
            loadLineNumber = 0;
            AppUI uiComponent = (AppUI) applicationTemplate.getUIComponent();
            uiComponent.getTextArea().setText(loaded.toString());
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setEditable(false);
            ((AppUI) applicationTemplate.getUIComponent()).getEditButton().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getDoneButton().setVisible(false);
            loaded.setLength(0);
        // TODO: NOT A PART OF HW 1
    }

    public void loadData(String dataString) {
        try {
            setMaximumY(-1000000000);
            setMinimumY(1000000000);
            setMaximumX(-1000000000);
            setMinimumX(1000000000);
            PropertyManager manager = applicationTemplate.manager;
            Scanner names = new Scanner(dataString);
            String[] arr = dataString.split(manager.getPropertyValue(AppPropertyTypes.NEW_LINE.name()));
            String[] nameCheck = new String[arr.length];
            boolean noDuplicates = duplicateNameCheck(names, nameCheck);
            names.close();
            if(!noDuplicates) {
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(false);
            }
            if(noDuplicates) {
                Scanner input = new Scanner(dataString);
                String displayCheck;
                displayNumber = 0;
                while (input.hasNextLine()) {
                    displayCheck = input.nextLine();
                    displayNumber++;
                    processor.processString(displayCheck);
                }
                processor.getDataPoints().forEach((s, point2D) -> {
                            if (point2D.getX() < getMinimumX()) {
                                setMinimumX((int) point2D.getX() - 5);
                            }
                            if(point2D.getX() > getMaximumX()) {
                                setMaximumX((int) point2D.getX() + 5);
                            }
                            if(point2D.getY() < getMinimumY()) {
                                setMinimumY((int) point2D.getY() - 5);
                            }
                            if(point2D.getY() > getMaximumY()) {
                                setMaximumY((int) point2D.getY() + 5);
                            }
                        }
                );
                String fileName;
                if(getSelectedFile() == null) {
                    fileName = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
                }
                else {
                    fileName = getSelectedFile().toString();
                }
                displayInfo(processor.dataLabels, fileName);
                ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setVisible(true);
               ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(false);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(false);
                ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setDisable(false);
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(true);
            }
        } catch (Exception e) {
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText("");
            ((AppUI) applicationTemplate.getUIComponent()).getDisplayInfoTextArea().setText("");
            ((AppUI) applicationTemplate.getUIComponent()).setDisplayInfoText("");
            ((AppUI) applicationTemplate.getUIComponent()).getGroup().selectToggle(((AppUI) applicationTemplate.getUIComponent()).getEditButton());
            ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setEditable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getGroup().selectToggle(((AppUI) applicationTemplate.getUIComponent()).getEditButton());
            ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setDisable(true);
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput + manager.getPropertyValue(AppPropertyTypes.LINE_NUMBER.name()) + displayNumber);
            loadLineNumber = 0;
        }
    }
    public static void saving(Path dataFilePath, String textAreaText) throws IOException{
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))) {
            writer.write(textAreaText);
            writer.close();
        } catch (IOException e) {
            throw new IOException();
        }

    }
    @Override
    public void saveData(Path dataFilePath) {
        // NOTE: completing this method was not a part of HW 1. You may have implemented file saving from the
        // confirmation dialog elsewhere in a different way.
        try {
            PropertyManager manager = applicationTemplate.manager;
            AppUI uiComponent = (AppUI) applicationTemplate.getUIComponent();
            Scanner names = new Scanner(uiComponent.getCurrentText());
            String count = uiComponent.getCurrentText();
            String[] arr = count.split(manager.getPropertyValue(AppPropertyTypes.NEW_LINE.name()));
            String[] nameCheck = new String[arr.length];
            boolean noDuplicates = duplicateNameCheck(names, nameCheck);
            names.close();
            if(noDuplicates) {
            Scanner scanTextArea = new Scanner(uiComponent.getCurrentText());
            lineNumber = 0;
            while (scanTextArea.hasNextLine()) {
                String checkText = scanTextArea.nextLine();
                lineNumber++;
                processor.processString(checkText);
            }
                scanTextArea.close();
            }
        } catch (Exception e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
            String errMsg = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
            dialog.show(errTitle, errMsg + errInput + manager.getPropertyValue(AppPropertyTypes.LINE_NUMBER.name()) + lineNumber);
            return;
        }
        String textAr = ((AppUI) applicationTemplate.getUIComponent()).getCurrentText();
        try {
            saving(dataFilePath, textAr);
        }
        catch (IOException io) {
            ErrorDialog.getDialog().show();
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        PropertyManager manager = applicationTemplate.manager;
        String first = manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name());
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        for (XYChart.Series<Number, Number> s : ((AppUI) applicationTemplate.getUIComponent()).getChart().getData()) {
            if (s.getData() == null) {
                return;
            }
            for (XYChart.Data<Number, Number> data : s.getData()) {
                String name;
                if (data.getExtraValue() != null) {
                    name = data.getExtraValue().toString();
                    first = name;
                } else {
                    name = first;
                }
                Tooltip tooltip = new Tooltip(name);
                Tooltip.install(data.getNode(), tooltip);

                tooltip.setOnShown(event -> applicationTemplate.getUIComponent().getPrimaryScene().setCursor(Cursor.CLOSED_HAND));
                tooltip.setOnHidden(event -> applicationTemplate.getUIComponent().getPrimaryScene().setCursor(Cursor.DEFAULT));

                data.getNode().setOnMouseEntered(event -> data.getNode().getStyleClass().add(manager.getPropertyValue(AppPropertyTypes.HOVER.name())));

                data.getNode().setOnMouseExited(event -> data.getNode().getStyleClass().remove(manager.getPropertyValue(AppPropertyTypes.HOVER.name())));
            }
        }

    }
    public void clusterDisplay(TSDProcessor clusterProcessor) {
        PropertyManager manager = applicationTemplate.manager;
        String first = manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name());
        clusterProcessor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        for (XYChart.Series<Number, Number> s : ((AppUI) applicationTemplate.getUIComponent()).getChart().getData()) {
            if (s.getData() == null) {
                return;
            }
            for (XYChart.Data<Number, Number> data : s.getData()) {
                String name;
                if (data.getExtraValue() != null) {
                    name = data.getExtraValue().toString();
                    first = name;
                } else {
                    name = first;
                }
                Tooltip tooltip = new Tooltip(name);
                Tooltip.install(data.getNode(), tooltip);

                tooltip.setOnShown(event -> applicationTemplate.getUIComponent().getPrimaryScene().setCursor(Cursor.CLOSED_HAND));
                tooltip.setOnHidden(event -> applicationTemplate.getUIComponent().getPrimaryScene().setCursor(Cursor.DEFAULT));

                data.getNode().setOnMouseEntered(event -> data.getNode().getStyleClass().add(manager.getPropertyValue(AppPropertyTypes.HOVER.name())));

                data.getNode().setOnMouseExited(event -> data.getNode().getStyleClass().remove(manager.getPropertyValue(AppPropertyTypes.HOVER.name())));
            }
        }
    }
    public File getSelectedFile() {
        return this.selected;
    }

    private void setSelectedFile(File selected) {
        this.selected = selected;
    }
    private boolean duplicateNameCheck(Scanner names, String[] nameCheck) {
        String saveText = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText();
        PropertyManager manager = applicationTemplate.manager;
        int k = 0;
        while (names.hasNext()) {
            String name = names.next();
            names.nextLine();
            nameCheck[k] = name;
            k++;
        }
        for (int i = 0; i < nameCheck.length; i++) {
            for (int j = i + 1; j < nameCheck.length; j++) {
                if (nameCheck[i].equals(nameCheck[j])) {
                    ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                    String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                    String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
                    String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
                    dialog.show(errTitle, errMsg + errInput + manager.getPropertyValue(AppPropertyTypes.DUPLICATE.name()) + (j + 1) + manager.getPropertyValue(AppPropertyTypes.DUPLICATE_NAME.name()) + nameCheck[i]);
                    loadLineNumber = 0;
                    resetExtraLines();
                    clear();
                    ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText(saveText);
                    ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setEditable(true);
                    ((AppUI) applicationTemplate.getUIComponent()).clear();
                    return false;
                }
            }
        }
        return true;
    }
    private void displayInfo(HashMap<String, String> dataLabels, String fileName) {
        int numInstances = dataLabels.size();
        Set<String> labels = new HashSet<>(dataLabels.values());
        int numLabels = labels.size();
        StringBuilder display = new StringBuilder();
        PropertyManager manager = applicationTemplate.manager;
        display.append(numInstances);
        display.append(manager.getPropertyValue(AppPropertyTypes.INSTANCE.name()));
        display.append(numLabels);
        display.append(manager.getPropertyValue(AppPropertyTypes.LABE.name()));
        display.append('\n');
        display.append(manager.getPropertyValue(AppPropertyTypes.LOADED_FROM.name()));
        if(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().equals(manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()))) {
            display.append(fileName);
        }
        else {
            display.append(manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name()));
        }
        display.append('\n');
        display.append(manager.getPropertyValue(AppPropertyTypes.DISPLAY_INFO_LABELS.name()));
        display.append('\n');
        for(String label: labels) {
            display.append(manager.getPropertyValue(AppPropertyTypes.DASH.name()));
            display.append(label);
            display.append('\n');
        }
        ((AppUI) applicationTemplate.getUIComponent()).getLeftPanel().setVisible(true);
        ((AppUI) applicationTemplate.getUIComponent()).setDisplayInfoText(display.toString());
        classificationCheck(dataLabels);
    }
    private void classificationCheck(HashMap<String, String> dataLabels) {
        PropertyManager manager = applicationTemplate.manager;
        Set<String> labels = new HashSet<>(dataLabels.values());
        int numLabels = 0;
        boolean containsNull = false;
        for(String label: labels) {
            if(!(label.equals(manager.getPropertyValue(AppPropertyTypes.NULL.name())))) {
                numLabels++;
            }
            else {
                containsNull = true;
            }
        }
        ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().getItems().removeAll(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()),manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()), manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()));
        if(numLabels == 2 && !(containsNull)) {
            ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().getItems().addAll(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()),manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()), manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()));
        }
        else {
            ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().getItems().addAll(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()), manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()));
        }
        ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setValue(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()));
    }
}
