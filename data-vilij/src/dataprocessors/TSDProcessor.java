package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.propertymanager.PropertyManager;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    public HashMap<String, String> dataLabels;
    public HashMap<String, Point2D> dataPoints;
    private HashMap<String, String> storeDataLabels;
    private HashMap<String, Point2D> storeDataPoints;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();

    }
    public void  setDataLabels(HashMap<String, String> dataLabels) {
        this.dataLabels = dataLabels;
    }
    public void setDataPoints(HashMap<String, Point2D> dataPoints) {
        this.dataPoints = dataPoints;
    }
    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        storeDataLabels = dataLabels;
        storeDataPoints = dataPoints;
        AtomicBoolean hadAnError = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        String name = checkedname(list.get(0));
                        String label = list.get(1);
                        if(label.equals("")) {
                            throw new Exception();
                        }
                        String[] pair = list.get(2).split(",");
                        Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                        dataLabels.put(name, label);
                        dataPoints.put(name, point);
                    } catch (Exception e) {
                        dataLabels = storeDataLabels;
                        dataPoints = storeDataPoints;
                        errorMessage.setLength(0);
                        errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                        hadAnError.set(true);
                    }
                });
       if (errorMessage.length() > 0) {
            throw new Exception(errorMessage.toString());
       }
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        boolean error = false;
        if(labels.size() == 0) {
            error = true;
        }
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                XYChart.Data<Number,Number> d = new XYChart.Data<>(point.getX(), point.getY());
                d.setExtraValue(entry.getKey());
                series.getData().add(d);
            });
            chart.getData().add(series);
            Node line = series.getNode().lookup(".chart-series-line");
            line.setStyle("-fx-stroke: transparent");
        }
        if(error) {
            return;
        }
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }
    public HashMap<String, String> getDataLabels() {
        return this.dataLabels;
    }
    public HashMap<String, Point2D> getDataPoints() {
        return this.dataPoints;
    }
    private static String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }
}
