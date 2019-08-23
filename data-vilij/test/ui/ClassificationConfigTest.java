package ui;


import org.junit.Test;

import static org.junit.Assert.*;
import static ui.ClusteringConfig.runConfigValueMax;
import static ui.ClusteringConfig.runConfigValueUpdateAndLabels;

public class ClassificationConfigTest {
    /**Positive Maximum Iterations**/
    @Test
    public void runConfigValueClassifMaxTestAboveZero() {
        assertEquals(10, runConfigValueMax(10));
    }
    /**Zero Maximum Iterations: Boundary Value Because Cannot have less than zero max iterations**/
    @Test
    public void runConfigValueClassifMaxTestZero() {
        assertEquals(0, runConfigValueMax(0));
    }
    /**Negative Maximum Iterations: Boundary Value Because Cannot Iterate a Negative Number of Times**/
    @Test
    public void runConfigValueClassifMaxTestBelowZero() {
        assertEquals(0, runConfigValueMax(-1));
    }
    @Test /**Positive Update Intervals**/
    public void runConfigValueClassifUpdateTestAboveZero() {
        assertEquals(10, runConfigValueUpdateAndLabels(10));
    }
    @Test /**Zero Update Intervals: Boundary Value because cannot update zero times**/
    public void runConfigValueClassifUpdateTestZero() {
        assertEquals(1, runConfigValueUpdateAndLabels(0));
    }
    @Test /**Negative Update Intervals: Boundary value because cannot update negative times**/
    public void runConfigValueUpdateClassifTestBelowZero() {
        assertEquals(1, runConfigValueUpdateAndLabels(-1));
    }

    @Test
    public void configTester() {
        runConfigValueClassifMaxTestAboveZero();
        runConfigValueClassifMaxTestZero();
        runConfigValueClassifMaxTestBelowZero();
        runConfigValueClassifUpdateTestAboveZero();
        runConfigValueClassifUpdateTestZero();
        runConfigValueUpdateClassifTestBelowZero();
    }
}