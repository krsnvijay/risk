package controllers;

import models.GameMap;
import org.junit.Before;
import org.junit.Test;
import utils.EditMap;
import utils.MapParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


class EditorControllerTest {
    private GameMap gameMap;
    private String reason;

    @Before
    public void setUp() throws Exception {
        // Load Risk map from resource folder
        File riskMap = new File("src/test/resources/risk.map");
        gameMap = MapParser.loadMap(riskMap.getPath());
        reason = "";
    }

    @Test
    void editContinent() {
    }

    @Test
    void editCountry() {
    }

    @Test
    void editNeighbor() {
    }

    @Test
    void validateMap() {
        //  Arrange
        File riskMap = new File("src/test/resources/riskinvalid.map");
        //  Act
        try {
            gameMap = MapParser.loadMap(riskMap.getPath());
            EditMap editMap = new EditMap();
            // Assert
            assertFalse(EditMap.validateMap(gameMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveMap() throws IOException {
        Files.write(
                Paths.get("src/test/resources/mapparsersavetest.map"),
                MapParser.serializeMap(gameMap, "mapparsersavetest").getBytes());
        File saveTestFile = new File("src/test/resources/mapparsersavetest.map");
        assertTrue(saveTestFile.exists());
    }

    @Test
    void showMap() {
    }
}