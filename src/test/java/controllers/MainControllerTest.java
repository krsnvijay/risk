package controllers;

import models.GameMap;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.either;
import static org.junit.Assert.assertThat;



/**
 * test class to check the functionalities of MainController.java
 *
 * @see controllers.MainController
 * @author Siddharth Singh
 */
public class MainControllerTest {

    /**
     * gameMap loads the invalid risk map from the resources folder
     */
    private GameMap gameMap;

    /** reason displays the reason for the failed condition */
    private String reason;

    /**
     * Sets up context for the test
     *
     * @throws Exception when map file is invalid
     */
    @Before
    public void setUp() throws Exception {
        // Load Risk map from resource folder
        File riskMap = new File("src/test/resources/risk.map");
        gameMap = MapParser.loadMap(riskMap.getPath());
        reason = "";
    }


    /** Test to check if edit map commands are valid */
    @Test
    public void editMap() {
    }

    /** Test to check whether the attacking country has more than 2 armies*/
    @Test
    public void loadMap() throws Exception {
        File testMap = new File("src/test/resources/mapparserinvalid.map");
        Scanner scan = new Scanner(testMap);
        StringBuilder sb = new StringBuilder();
        String temp = "", str = "";
        while (scan.hasNext()) {
            sb.append(scan.nextLine());
        }

        assertThat(
                sb.toString(),
                either(containsString("name"))
                        .or(containsString("[file]"))
                        .or(containsString("[countries]"))
                        .or(containsString("[continents]"))
                        .or(containsString("[borders]")));
        scan.close();
    }

    @Test
    public void exitGame() {
    }
}