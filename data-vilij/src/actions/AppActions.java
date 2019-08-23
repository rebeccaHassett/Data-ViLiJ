package actions;

import dataprocessors.AppData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Scanner;

import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;
import static vilij.templates.UITemplate.SEPARATOR;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;

    /**
     * Path to the data file currently active.
     */
    private Path dataFilePath;
    private boolean newSave = false;
    private String lastSaved;
    private boolean prompted = false;
    private String priorFile = "";
    private Stage primaryStage;
    private boolean firstInit = false;

    /**
     * The boolean property marking whether or not there are any unsaved changes.
     */
    SimpleBooleanProperty isUnsaved;
    boolean noSaveReq = true;

    public AppActions(ApplicationTemplate applicationTemplate, Stage primaryStage) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = new SimpleBooleanProperty(true);
        this.primaryStage = primaryStage;
    }
    public Path getDataFilePath() {
        return dataFilePath;
    }


    @Override
    public void handleNewRequest() {
        dataFilePath = null;
        try {
            isUnsaved.setValue(true);
            ((AppUI) applicationTemplate.getUIComponent()).getEditButton().setDisable(false);
            ((AppUI) applicationTemplate.getUIComponent()).getDoneButton().setDisable(false);
            if(((AppUI) applicationTemplate.getUIComponent()).getIsRunning()) {
                PropertyManager manager = applicationTemplate.manager;
                ConfirmationDialog dialog = ConfirmationDialog.getDialog();
                dialog.show(manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()),
                        manager.getPropertyValue(AppPropertyTypes.ALGO_COMPLETE.name()));
                if(dialog.getSelectedOption() == null) {
                    return;
                }
                if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL) || dialog.getSelectedOption().equals(ConfirmationDialog.Option.NO)) {
                    return;
                }
                ((AppUI) applicationTemplate.getUIComponent()).getCurrentTask().cancel();
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
                ((AppUI) applicationTemplate.getUIComponent()).setIsRunning(false);
            }
                    ((AppUI) applicationTemplate.getUIComponent()).scrnshotButtonDisabler();
                    ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(false);
                    ((AppUI) applicationTemplate.getUIComponent()).getChartTitle().setVisible(false);
                    ((AppUI) applicationTemplate.getUIComponent()).getChartTitle().toFront();
                    ((AppUI) applicationTemplate.getUIComponent()).getChart().setVisible(true);
                    ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setEditable(true);
                    ((AppUI) applicationTemplate.getUIComponent()).getRun().setVisible(false);
                    ((AppUI) applicationTemplate.getUIComponent()).getRun().setDisable(true);
                    ((AppUI) applicationTemplate.getUIComponent()).getBackButton().setVisible(false);
                    ((AppUI) applicationTemplate.getUIComponent()).getBackButton().setDisable(true);
                    (applicationTemplate.getUIComponent()).clear();
            dataFilePath = null;
                    PropertyManager manager = applicationTemplate.manager;
                    if (!(((AppUI) applicationTemplate.getUIComponent()).getEditButton().isVisible())) {
                        ((AppData) applicationTemplate.getDataComponent()).resetExtraLines();
                        (applicationTemplate.getDataComponent()).clear();
                        (applicationTemplate.getUIComponent()).clear();
                    } else {
                        noSaveReq = false;
                    }
                    ((AppUI) applicationTemplate.getUIComponent()).getLeftPanel().setVisible(true);
                    ((AppUI) applicationTemplate.getUIComponent()).getDoneButton().setVisible(true);
                    ((AppUI) applicationTemplate.getUIComponent()).getDisplayInfoTextArea().setText(manager.getPropertyValue(AppPropertyTypes.NEW_LINE.name()));
                    ((AppUI) applicationTemplate.getUIComponent()).getEditButton().setVisible(true);
                    ((AppUI) applicationTemplate.getUIComponent()).setDisplayInfoText("");
                    ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText("");
                    ((AppUI) applicationTemplate.getUIComponent()).getGroup().selectToggle(((AppUI) applicationTemplate.getUIComponent()).getEditButton());
                    ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setValue(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()));
                    ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(false);
                    if (!((AppUI) applicationTemplate.getUIComponent()).getEditButton().isVisible()) {
                        applicationTemplate.getDataComponent().clear();
                        (applicationTemplate.getUIComponent()).clear();
                    } else if (!noSaveReq && ((AppUI) applicationTemplate.getUIComponent()).getDoneButton().isSelected()) {
                        if (!isUnsaved.get() || promptToSave()) {
                            applicationTemplate.getDataComponent().clear();
                            applicationTemplate.getUIComponent().clear();
                            isUnsaved.set(false);
                            ((AppUI) applicationTemplate.getUIComponent()).scrnshotButtonDisabler();
                            ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(false);
                            dataFilePath = null;
                        }
                    }
                    ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setEditable(true);
                } catch(IOException e){
                    errorHandlingHelper();
                }
    }

    @Override
    public void handleSaveRequest() {
        newSave = false;
        try {
            if(dataFilePath != null) {
                save();
            }
            else {
                promptToSave();
            }
            if(!isUnsaved.get()) {
                ((AppUI) applicationTemplate.getUIComponent()).saveButtonActions();
            }
            lastSaved = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText();
        } catch (IOException e) {
            errorHandlingHelper();
        }
        // TODO: NOT A PART OF HW 1
    }

    public String getLastSaved() {
        return this.lastSaved;
    }

    @Override
    public void handleLoadRequest() {
        PropertyManager manager = applicationTemplate.manager;
        try {
            isUnsaved.setValue(false);
            if(((AppUI) applicationTemplate.getUIComponent()).getIsRunning()) {
                ConfirmationDialog dialog = ConfirmationDialog.getDialog();
                dialog.show(manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()),
                        manager.getPropertyValue(AppPropertyTypes.ALGO_COMPLETE.name()));
                if(dialog.getSelectedOption() == null) {
                    return;
                }
                if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL) || dialog.getSelectedOption().equals(ConfirmationDialog.Option.NO)) {
                    return;
                }
                ((AppUI) applicationTemplate.getUIComponent()).getCurrentTask().cancel();
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
                ((AppUI) applicationTemplate.getUIComponent()).setIsRunning(false);
            }
            ((AppUI) applicationTemplate.getUIComponent()).scrnshotButtonDisabler();
            ((AppUI) applicationTemplate.getUIComponent()).getChart().setVisible(true);
            ((AppUI) applicationTemplate.getUIComponent()).getChartTitle().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getChartTitle().toFront();
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getRun().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getRun().setDisable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getBackButton().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getBackButton().setDisable(true);
             FileChooser fileChooser = new FileChooser();
            String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
            URL dataDirURL = getClass().getResource(dataDirPath);
            if (dataDirURL == null) {
                throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));
            }
            File dataFile = new File(dataDirURL.getFile());
            fileChooser.setInitialDirectory(dataFile);
            String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
            String extension = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
            ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                    String.format("*.%s", extension));
            fileChooser.getExtensionFilters().add(extFilter);
            File selected = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getClassificationList().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getClusteringList().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setDisable(false);
            ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setValue(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPES.name()));
            if(selected != null) {
                Path load = selected.toPath();
                dataFilePath = load;
                Scanner emptyCheck = new Scanner(load.toFile());
                String emptCheck = manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name());
                while (emptyCheck.hasNextLine()) {
                    emptCheck = emptyCheck.nextLine();
                }
                if(emptCheck.equals(manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()))) {
                    ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                    String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                    String          errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
                    String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
                    dialog.show(errTitle, errMsg + errInput + manager.getPropertyValue(AppPropertyTypes.LINE_NUMBER.name()) + 1);
                    if(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().equals(manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()))) {
                        ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(false);
                    }
                    return;
                }
                (applicationTemplate.getDataComponent()).loadData(load);
            }
            else {
                ((AppUI) applicationTemplate.getUIComponent()).getLeftPanel().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setVisible(false);
                ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setVisible(false);
                if(!((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().equals(manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()))) {
                    ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmTypeSelector().setVisible(true);
                }
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setEditable(false);
            }
            ((AppUI) applicationTemplate.getUIComponent()).getEditButton().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getDoneButton().setVisible(false);
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
        }catch (FileNotFoundException fi) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String          errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String          errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput);
        }
        if(dataFilePath != null) {
            priorFile = dataFilePath.toString();
        }
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        try {
            if(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().isEmpty()) {
                System.exit(0);
            }
            if (!isUnsaved.get() || promptToSave()) {
                if(!((AppUI) applicationTemplate.getUIComponent()).getIsRunning()) {
                    System.exit(0);
                }
                else {
                    PropertyManager manager = applicationTemplate.manager;
                    AlgorithmExitDialog algoDialog = AlgorithmExitDialog.getDialog();
                    if(!firstInit) {
                        algoDialog.init(primaryStage);
                        firstInit = true;
                    }
                    algoDialog.show(manager.getPropertyValue(AppPropertyTypes.EXIT.name()), manager.getPropertyValue(AppPropertyTypes.EMPTY_STRING.name()));
                }
            }
        } catch (IOException e) {
            errorHandlingHelper();
        }
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        PropertyManager manager = applicationTemplate.manager;
        try {
            FileChooser fileChooser = new FileChooser();
            String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
            URL dataDirURL = getClass().getResource(dataDirPath);
            if(dataDirURL == null) {
                throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));
            }
            File dataFile = new File(dataDirURL.getFile());
            fileChooser.setInitialDirectory(dataFile);
            fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));
            WritableImage wi = new WritableImage(700,525);
            WritableImage snapshot = ((AppUI) applicationTemplate.getUIComponent()).getChart().snapshot(new SnapshotParameters(), wi);
            String description = manager.getPropertyValue(AppPropertyTypes.IMAGE_FILE_EXT_DESC.name());
            String extension = manager.getPropertyValue(AppPropertyTypes.IMAGE_FILE_EXT.name());
            ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                    String.format("*.%s", extension));
            fileChooser.getExtensionFilters().add(extFilter);
            File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
            if (selected != null) {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), manager.getPropertyValue(AppPropertyTypes.IMAGE_FILE_EXT.name()), selected);
            }
            if(snapshot == null) {
                throw new IOException();
            }
        } catch(IOException io) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            dialog.show(manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ERROR.name()), manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name()) + manager.getPropertyValue(AppPropertyTypes.IMAGE.name()));
            throw new IOException();
        }
        // TODO: NOT A PART OF HW 1
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        prompted = true;
        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog dialog = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) return false; // if user closes dialog using the window's close button
        if(dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL)) {
            ((AppUI) applicationTemplate.getUIComponent()).getListContainer().setVisible(true);
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(false);
        }
        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
            if (dataFilePath == null) {
                FileChooser fileChooser = new FileChooser();
                String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL dataDirURL = getClass().getResource(dataDirPath);
                if(dataDirURL == null) {
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));
                }
                File dataFile = new File(dataDirURL.getFile());
                fileChooser.setInitialDirectory(dataFile);
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
                String extension = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
                ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                        String.format("*.%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (selected != null) {
                    dataFilePath = selected.toPath();
                    save();
                } else return false; // if user presses escape after initially selecting 'yes'
            } else
                save();
        }
        if(dialog.getSelectedOption().equals(ConfirmationDialog.Option.NO)) {
            isUnsaved.set(true);
            return true;
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);

    }

    private void save() throws IOException {
        try {
            applicationTemplate.getDataComponent().saveData(dataFilePath);
            isUnsaved.set(false);
        }
        catch(Exception e) {
            throw new IOException();
        }
    }

    private void errorHandlingHelper() {
        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager = applicationTemplate.manager;
        String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
        String errMsg = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
        String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
        dialog.show(errTitle, errMsg + errInput);
    }
}


