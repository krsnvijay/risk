package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import models.Continent;
import models.Country;
import models.GameMap;

public class EditMap extends MapParser {
  public EditMap() {
    super();
  }

  public boolean editContinent(String[] opCmds, GameMap map) {
    Arrays.asList(opCmds).stream().forEach(opcmd -> {
      String[] split_opcmd = opcmd.split(" ");
      String continent = split_opcmd[1];
      Map<String, Continent> copyOfContinents = map.getContinents();
      Map<String, Country> copyOfCountries = map.getCountries();
      Map<String, Set<String>> copyOfBorders = map.getBorders();
      if (split_opcmd[0].equals("add")) {
        String continent_cntrl_val = split_opcmd[2];
        Continent to_insert = new Continent(continent, Integer.parseInt(continent_cntrl_val));
        copyOfContinents.put(continent, to_insert);
        map.setContinents(copyOfContinents);
      } else if (split_opcmd[0].equals("remove")) {
        // perform remove continent
        ArrayList<String> country_black_list = new ArrayList<>();
        map.setContinents(copyOfContinents.entrySet().stream()
            .filter(continent_loc -> !continent_loc.getKey().equals(continent)).collect(Collectors
                .toMap(entry_loc -> entry_loc.getKey(), entry_loc -> entry_loc.getValue())));
        // update countries list
        map.setCountries(copyOfCountries.entrySet().stream().filter(country -> {
          if (country.getValue().getContinent().equals(continent)) {
            country_black_list.add(country.getKey());
            return false;
          }
          return true;
        }).collect(
            Collectors.toMap(entry_loc -> entry_loc.getKey(), entry_loc -> entry_loc.getValue())));
        // update borders list
        map.setBorders(copyOfBorders.entrySet().stream().filter(border -> {
          if (country_black_list.contains(border.getKey())) {
            return false;
          }
          border.setValue(
              border.getValue().stream().filter(neighbor -> !country_black_list.contains(neighbor))
                  .collect(Collectors.toSet()));
          return true;
        }).collect(
            Collectors.toMap(entry_loc -> entry_loc.getKey(), entry_loc -> entry_loc.getValue())));

      }
    });
    return true;
  }

  public boolean editCountry(String[] opCmds, GameMap map) {
    Arrays.asList(opCmds).stream().forEach(opcmd -> {
      Map<String, Country> copyOfCountries = map.getCountries();
      Map<String, Set<String>> copyOfBorders = map.getBorders();
      String[] split_opcmd = opcmd.split(" ");
      String country = split_opcmd[1];
      if (split_opcmd[0].equals("add")) {
        String continent_string = split_opcmd[2];
        Country to_insert = new Country(country, continent_string);
        copyOfCountries.put(country, to_insert);
        map.setCountries(copyOfCountries);
        // perform add country
      } else if (split_opcmd[0].equals("remove")) {
        // perform remove country
        // update country list
        copyOfCountries.remove(country);
        map.setCountries(copyOfCountries);
        // update border list
        map.setBorders(copyOfBorders.entrySet().stream().filter(border_loc -> {
          if (border_loc.getKey().equals(country)) {
            return false;
          }
          border_loc.setValue(border_loc.getValue().stream()
              .filter(neighbor -> !neighbor.equals(country)).collect(Collectors.toSet()));
          return true;
        }).collect(
            Collectors.toMap(entry_loc -> entry_loc.getKey(), entry_loc -> entry_loc.getValue())));
      }
    });
    return true;
  }

  public boolean editNeighbor(String[] opCmds, GameMap map) {
    Arrays.asList(opCmds).stream().forEach(opcmd -> {
      Map<String, Set<String>> copyOfBorders = map.getBorders();
      Map<String, Country> copyOfCountries = map.getCountries();
      String[] split_opcmd = opcmd.split(" ");
      String country = split_opcmd[1];
      String neighbor_country = split_opcmd[2];
      if (split_opcmd[0].equals("add")) {
        // perform add country neighbor_country
        if (country.equals(neighbor_country))
          return;
        if (!copyOfBorders.containsKey(country) && copyOfCountries.containsKey(neighbor_country)) {
          copyOfBorders.put(country, new HashSet<>());
          map.setBorders(copyOfBorders);
        }
        copyOfBorders.get(country).add(neighbor_country);
        map.setBorders(copyOfBorders);
      } else if (split_opcmd[0].equals("remove")) {
        // perform remove country neighbor_country
        if (copyOfBorders.containsKey(country)) {
          copyOfBorders.get(country).remove(neighbor_country);
        }
        map.setBorders(copyOfBorders);
      }
    });
    return true;
  }

}
