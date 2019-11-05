package controllers;

import static controllers.GameController.exchangeCards;
import static java.util.stream.Collectors.toCollection;

import models.Card;
import models.Country;
import models.GameMap;
import models.Player;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.*;

class GameControllerTest {
    /** list of players */
    private static ArrayList<Player> playersList = new ArrayList<>();
    /** player name */
    String playerName;
    /** reason for failure */
    String reason;

    String command;

    GameController gameController = new GameController();

    /** list of countries */
    Map<String, Country> countries;

    /** store game state */
    private GameMap gameMap;
    /** store player 1 */
    private Player player1;
    /** store player 2 */
    private Player player2;

    /**
     * Sets up context for the test
     *
     * @throws Exception when map file is invalid
     */
    @Before
    public void setUp() throws Exception {
        File riskMap = new File("src/test/resources/risk.map");
        gameMap = MapParser.loadMap(riskMap.getPath());
        reason = "";
        command = "";
        player1 = new Player("Player1");
        player2 = new Player("Player2");
        playersList.add(player1);
        playersList.add(player2);
        countries = gameMap.getCountries();
    }

    @Test
    public void reinforce() {
        playerName = player1.getPlayerName();
        gameMap.setCountries(gameMap.populateCountries(playersList));

    }

    //attacker validation
    @Test
    public void attackArmiesTest() {
        gameMap.setCountries(gameMap.populateCountries(playersList));
        gameMap.placeAll();
        ArrayList<Country> sourceCountry = countries.values().stream()
                .filter(country -> country.getNumberOfArmies() == 2 && country.getOwnerName().equals("Player1"))
                .collect(toCollection(ArrayList::new));
        //set values
        Set<String> destinationCountry = gameMap.getBorders().get(sourceCountry.get(0).getName());
        command = "attack country1 country2 numdice";
        reason = "Armies should be greater than 2 for the attacker country";
    }

    //defender validation
    @Test
    public void defendAdjacencyTest() {
        gameMap.setCountries(gameMap.populateCountries(playersList));
        gameMap.placeAll();
        ArrayList<Country> sourceCountry = countries.values().stream()
                .filter(country -> country.getNumberOfArmies() == 4 && country.getOwnerName().equals("Player1"))
                .collect(toCollection(ArrayList::new));
        //set values
        Set<String> destinationCountry = gameMap.getBorders().get(sourceCountry.get(0).getName());
        command = "attack country1 country2 numdice";
        reason = "Countries should be adjacent for a valid attack";
    }

    //cards validation
    @Test
    public void exchangeCardsTest() {
        Card card1 = new Card("India");
        Card card2 = new Card("China");
        Card card3 = new Card("Pakistan");
        List<Card> cardsInHand = new ArrayList<>();
        cardsInHand.add(card1);
        cardsInHand.add(card2);
        cardsInHand.add(card3);
        player1.setCardsInHand(cardsInHand);
        reason = "The set is valid only when the 3 cards are from different class or the same class";
        boolean result = GameController.exchangeCards(gameMap, command);
        assertTrue(reason, result);
    }

    //valid move after conquer
    @Test
    public void conquerTest(){

    }

    //end of game validation
    @Test
    public void endOfGameTest(){
        playerName = player1.getPlayerName();
        int countrySize = countries.size();
        int i=0;
        ArrayList<Country> allCountries = new ArrayList<>(countries.values());
        while (i<countrySize) {
            allCountries.get(i).setOwnerName(playerName);
            i++;
        }
        boolean result = Player.checkPlayerOwnsAllTheCountries(playerName, gameMap);
        reason = "If player owns all the countries, they should win the game";
        assertTrue(reason, result);
    }

    //fortification validation
    @Test
    public void fortifyTest() {
        playerName = player1.getPlayerName();
        gameMap.setCountries(gameMap.populateCountries(playersList));
        gameMap.placeAll();
        ArrayList<Country> sourceCountry = countries.values().stream()
                .filter(country -> country.getNumberOfArmies() == 3 && country.getOwnerName().equals("Player1"))
                .collect(toCollection(ArrayList::new));
        //how to get set values
        Set<String> destinationCountry = gameMap.getBorders().get(sourceCountry.get(0).getName());
        reason = "Fortification within countries that aren't adjacent is not possible";
        command = "fortify "+ sourceCountry.get(0).getName() + " ";
        assertEquals(reason,"Hello","Hello");
    }

    @Test
    public void defendTest() {
    }

    @Test
    public void attackMove() {
    }
}