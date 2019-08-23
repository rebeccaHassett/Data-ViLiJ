package dataprocessors;

import dataprocessors.TSDProcessor;
import javafx.geometry.Point2D;
import org.junit.Test;
import vilij.templates.ApplicationTemplate;


import static org.junit.Assert.*;

public class TSDProcessorTest {

    /**Parsing Valid String**/
    @Test
    public void testValidStringOne() {
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("label1");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {

        }
        assertEquals("label1", testProcessor.getDataLabels().get("@Instance1"));
        Point2D point = new Point2D(10,20);
        assertEquals(point, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Parsing Two Valid Strings**/
    @Test
    public void testTwoLineStringTwo() {
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("label1");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            builder.append("@Instance2");
            builder.append('\t');
            builder.append("label2");
            builder.append('\t');
            builder.append("30,40");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {

        }
        Point2D point1 = new Point2D(10,20);
        Point2D point2 = new Point2D(30,40);
        assertEquals(point1, testProcessor.getDataPoints().get("@Instance1"));
        assertEquals(point2, testProcessor.getDataPoints().get("@Instance2"));
        assertEquals("label1", testProcessor.getDataLabels().get("@Instance1"));
        assertEquals("label2", testProcessor.getDataLabels().get("@Instance2"));
    }
    /**Parsing Empty String that will be Noticed by AppData: Boundary Value**/
    @Test
    public void testEmptyStringThree() {
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {

        }
        Point2D point1 = new Point2D(10,20);
        Point2D point2 = new Point2D(30,40);
        assertTrue(testProcessor.dataLabels.isEmpty());
        assertTrue(testProcessor.dataPoints.isEmpty());
    }
    /**Boundary Value: Cannot have instance name that does not start with '@'**/
    @Test(expected = Exception.class)
    public void testNoRateSymbolStringFour() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("Instance1");
            builder.append('\t');
            builder.append("label1");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("label1", testProcessor.dataLabels.get("Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("Instance1"));
    }
    /**Boundary Value because Exception Thrown For String That is Not Tab Separated**/
    @Test(expected = Exception.class)
    public void testNoTabsStringFive() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append("label1");
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("label1", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Boundary Value because Exception Thrown for Empty Label**/
    @Test(expected = Exception.class)
    public void testEmptyLabelStringSeven() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Null Label Valid String**/
    @Test
    public void testNullLabelStringEight() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("null");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("null", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Boundary Value: Negative Y Value Valid String**/
    @Test
    public void testNegativeYStringNine() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("label1");
            builder.append('\t');
            builder.append("10,-20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("label1", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,-20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Boundary Value: Negative Y and X Values Valid String**/
    @Test
    public void testNegativePointStringTen() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("label1");
            builder.append('\t');
            builder.append("-10,-20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("label1", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(-10,-20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Boundary Value: Label With Hyphen Valid**/
    @Test
    public void testHyphenStringEleven() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("a-b");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("a-b", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Boundary Value: Label With Numbers Valid**/
    @Test
    public void testNumberLabelStringTwelve() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("a10");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("a10", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Boundary Value: Label With Spaces Valid**/
    @Test
    public void testSpacesLabelStringThirteen() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("rebecca hassett");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("rebecca hassett", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Long Labels**/
    @Test
    public void testLongLabelStringFourteen() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append("rebeccahassettgoestostonybrookuniversitytostudycomputerscience");
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("rebeccahassettgoestostonybrookuniversitytostudycomputerscience", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    /**Boundary Value: Two Tabs in a Row Since Results in Empty Label**/
    @Test(expected = Exception.class)
    public void testTwoTabsConsecutiveStringFifteen() throws Exception{
        TSDProcessor testProcessor = new TSDProcessor();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("@Instance1");
            builder.append('\t');
            builder.append('\t');
            builder.append("10,20");
            builder.append('\n');
            testProcessor.processString(builder.toString());
        }catch (Exception excep) {
            throw new Exception();
        }
        assertEquals("", testProcessor.dataLabels.get("@Instance1"));
        Point2D point3 = new Point2D(10,20);
        assertEquals(point3, testProcessor.getDataPoints().get("@Instance1"));
    }
    @Test
    public void testerParsing() {
        testValidStringOne();
        testTwoLineStringTwo();
        testEmptyStringThree();
        try {
            testNoRateSymbolStringFour();
            testNoTabsStringFive();
            testEmptyLabelStringSeven();
            testNullLabelStringEight();
            testNegativeYStringNine();
            testNegativePointStringTen();
            testHyphenStringEleven();
            testNumberLabelStringTwelve();
            testSpacesLabelStringThirteen();
            testLongLabelStringFourteen();
            testTwoTabsConsecutiveStringFifteen();
        }
        catch (Exception e) {

        }
    }
}