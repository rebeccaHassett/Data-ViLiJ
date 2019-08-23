package ui;


import org.junit.Test;
import vilij.templates.ApplicationTemplate;

import static org.junit.Assert.*;
import static ui.ClusteringConfig.runConfigValueMax;
import static ui.ClusteringConfig.runConfigValueUpdateAndLabels;

public class ClusteringConfigTest {
    /**Positive Maximum Iterations**/
    @Test
    public void runConfigValueMaxTestAboveZero() {
        assertEquals(10, runConfigValueMax(10));
    }
    /**Zero Maximum Iterations: Boundary Value because cannot have less than zero max iterations**/
    @Test
    public void runConfigValueMaxTestZero() {
        assertEquals(0, runConfigValueMax(0));
    }
    /**Negative Maximum Iterations: Boundary Value because cannot have negative max iterations**/
    @Test
    public void runConfigValueMaxTestBelowZero() {
        assertEquals(0, runConfigValueMax(-1));
    }
    /**Positive Update Intervals**/
    @Test
    public void runConfigValueUpdateAndLabelTestAboveZero() {
        assertEquals(10, runConfigValueUpdateAndLabels(10));
    }
    /**Zero Update Intervals: Boundary Value because cannot update zero times**/
    @Test
    public void runConfigValueUpdateAndLabelTestZero() {
        assertEquals(1, runConfigValueUpdateAndLabels(0));
    }
    /**Negative Update Intervals: Boundary Value because cannot update negative times**/
    @Test
    public void runConfigValueUpdateAndLabelsTestBelowZero() {
        assertEquals(1, runConfigValueUpdateAndLabels(-1));
    }

    @Test
    public void clusterTester() {
        runConfigValueMaxTestAboveZero();
        runConfigValueMaxTestZero();
        runConfigValueMaxTestBelowZero();
        runConfigValueUpdateAndLabelTestAboveZero();
        runConfigValueUpdateAndLabelTestZero();
        runConfigValueUpdateAndLabelsTestBelowZero();
    }
}