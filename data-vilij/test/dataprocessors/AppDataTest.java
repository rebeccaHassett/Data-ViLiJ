package dataprocessors;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.*;


import static dataprocessors.AppData.saving;


public class AppDataTest {
    /** Test Saving Data With A Valid File Path**/
    @Test
    public void testSaveData() throws IOException{
        String text = "@Instance1  label1  10,20";
        Path currentPath = Paths.get("");
        String pathFileName = currentPath.toAbsolutePath().toString() + "\\file";
        File file = new File(pathFileName);
        String compare = "";
        Path pathFile = file.toPath();
        try {
            saving(pathFile, text);
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    compare = sc.nextLine();
                }
            } catch (IOException e) {

            }
        }catch (IOException excp) {
            throw new IOException();
        }
        assertEquals(text, compare);
    }
    /** Test Saving Two Lines of Data With A Valid File Path**/
    @Test
    public void testSaveDataTwoLines() throws IOException{
        StringBuilder text = new StringBuilder();
        text.append("@Instance1");
        text.append('\t');
        text.append("label1");
        text.append('\t');
        text.append("10,20");
        text.append('\n');
        text.append("@Instance2");
        text.append('\t');
        text.append("label1");
        text.append('\t');
        text.append("10,20");
        text.append('\n');
        Path currentPath = Paths.get("");
        String pathFileName = currentPath.toAbsolutePath().toString() + "\\file";
        File file = new File(pathFileName);
        String compare = "";
        Path pathFile = file.toPath();
        StringBuilder str = new StringBuilder();
        try {
            saving(pathFile, text.toString());
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    str.append(sc.nextLine());
                    str.append('\n');
                }
            } catch (IOException e) {

            }
        }catch (IOException excp) {
            throw new IOException();
        }
        assertEquals(text.toString(), str.toString());
    }
    /** Test Saving Three Lines of Data With A Valid File Path**/
    @Test
    public void testSaveDataThreeLines() throws IOException{
        StringBuilder text = new StringBuilder();
        text.append("@Instance1");
        text.append('\t');
        text.append("label1");
        text.append('\t');
        text.append("10,20");
        text.append('\n');
        text.append("@Instance2");
        text.append('\t');
        text.append("label1");
        text.append('\t');
        text.append("10,20");
        text.append('\n');
        text.append("@Instance3");
        text.append('\t');
        text.append("label1");
        text.append('\t');
        text.append("10,20");
        text.append('\n');
        Path currentPath = Paths.get("");
        String pathFileName = currentPath.toAbsolutePath().toString() + "\\file";
        File file = new File(pathFileName);
        String compare = "";
        Path pathFile = file.toPath();
        StringBuilder str2 = new StringBuilder();
        try {
            saving(pathFile, text.toString());
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    str2.append(sc.nextLine());
                    str2.append('\n');
                }
            } catch (IOException e) {

            }
        }catch (IOException excp) {
            throw new IOException();
        }
        assertEquals(text.toString(), str2.toString());
    }
    /**Test Saving Data with an Invalid File Path**/
    @Test(expected = IOException.class)
    public void testSaveDataInvalidPath() throws IOException {
        String text = "@Instance1   label1  10,20";
        String pathFileName = "C://monkeys";
        File file = new File(pathFileName);
        Path pathFile = file.toPath();
        String compare = "";
        try {
            saving(pathFile, text);
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    compare = sc.nextLine();
                }
            } catch (IOException e) {

            }
        }
        catch (IOException except) {
            throw new IOException();
        }
    }
    @Test
    public void testingSaveMethod() {
        try {
            testSaveData();
            testSaveDataInvalidPath();
        }catch (IOException io) {

        }

    }
}