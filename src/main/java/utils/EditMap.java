// package utils;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.stream.Collectors;
// import models.Border;
// import models.Continent;
// import models.Country;
// import models.GameMap;

// public class EditMap {

//     public static boolean editContinent(String[] opCmds, GameMap map) {
//         ArrayList<Continent> copyOfContinents = map.getContinents();
//         ArrayList<Country> copyOfCountries = map.getCountries();
//         ArrayList<Border> copyOfBorders = map.getBorders();

//         Arrays.asList(opCmds).stream().forEach(opcmd -> {
//             String[] split_opcmd = opcmd.split(" ");
//             String continent = split_opcmd[1];
//             if (split_opcmd[0].equals("add")) {
//                 String continent_cntrl_val = split_opcmd[2];
//                 Continent toInsert = new Continent(continent, continent_cntrl_val);
//                 copyOfContinents.add(toInsert);
//             } else if (split_opcmd[0].equals("remove")) {
//                 // perform remove continent
//                 ArrayList<Integer> border_black_list = new ArrayList<>();
//                 ArrayList<Continent> filteredIndex =
//                         copyOfContinents.stream().filter(cont -> cont.getName().equals(continent))
//                                 .collect(Collectors.toCollection(ArrayList::new));

//                 int continent_index = filteredIndex.get(0).getId();

//                 // update continent list
//                 map.setContinents(copyOfContinents.stream()
//                         .filter(cont -> !cont.getName().equals(continent)).map((cont) -> {
//                             if (cont.getId() > continent_index) {
//                                 cont.setId(cont.getId()-1);
//                             }
//                             return cont;
//                         }).collect(Collectors.toCollection(ArrayList::new))
//                 );
//                 // update countries list
//                 map.setCountries(copyOfCountries.stream().filter(country -> {
//                     if (country.getContinent() == continent_index) {
//                         border_black_list.add(country.getId());
//                         return false;
//                     }
//                     return true;
//                 }).map((country) -> {
//                     if (country.getContinent() > continent_index) {
//                         country.setContinent(country.getContinent() - 1);
//                     }
//                     return country;
//                 }).collect(Collectors.toCollection(ArrayList::new))
//                 );

//                 // update borders list
//                 map.setBorders(copyOfBorders.stream().filter(border -> {
//                     if (border_black_list.contains(border.getId())) {
//                         return false;
//                     }
//                     border_black_list.forEach(country_id -> {
//                         border.setNeighbors(border.getNeighbors().stream()
//                                 .filter(neighbor -> neighbor != country_id)
//                                 .collect(Collectors.toCollection(ArrayList::new)));
//                     });
//                     return true;
//                 }).collect(Collectors.toCollection(ArrayList::new))
//                 );

//             }
//         });
//         return true;
//     }

//     public static boolean editCountry(String[] opCmds, GameMap map) {
//         Arrays.asList(opCmds).stream().forEach(opcmd -> {
//             String[] split_opcmd = opcmd.split(" ");
//             String country = split_opcmd[1];
//             if (split_opcmd[0].equals("add")) {
//                 String continent_string = split_opcmd[2];
//                 ArrayList<Continent> continent_val_list = map.getContinents().stream().filter(continent->continent_string.equals(continent.getName())).collect(Collectors.toCollection(ArrayList::new));
//                 int continent = continent_val_list.get(0).getId();
//                 ArrayList<Country> copyOfCountries = map.getCountries();
//                 String country_string = (copyOfCountries.size()+1) + " " + country + " " + continent;
//                 copyOfCountries.add(new Country(country_string));
//                 map.setCountries(copyOfCountries);
//                 // perform add country
//             } else if (split_opcmd[0].equals("remove")) {
//                 // perform remove country
//                 // update country list
//                 ArrayList<Country> copyOfCountries = map.getCountries();
//                 ArrayList<Border> copyOfBorders = map.getBorders();
//                 Country reqd_country = copyOfCountries.stream().filter(country_local->country_local.getName().equals(country)).collect(Collectors.toCollection(ArrayList::new)).get(0);
//                 copyOfCountries.remove(reqd_country);
//                 map.setCountries(copyOfCountries);
//                 // update border list
//                 ArrayList<Border> result_borders = copyOfBorders.stream().filter(border->{
//                         if(border.getId() == reqd_country.getId()) {
//                             return false;
//                         }
//                         else if(border.getNeighbors().contains(reqd_country.getId())){
//                             ArrayList<Integer> neighbor_copy = border.getNeighbors();
//                             border.setNeighbors(neighbor_copy.stream().filter(neighbor->{
//                                 if(neighbor == reqd_country.getId()) return false;
//                                 return true;
//                             }).collect(Collectors.toCollection(ArrayList::new)));
//                         }
//                     return true;
//                 }).collect(Collectors.toCollection(ArrayList::new));
//                 map.setBorders(result_borders);
//             }
//         });
//         return true;
//     }

//     public static boolean editNeighbor(String[] opCmds, GameMap map) {
//         Arrays.asList(opCmds).stream().forEach(opcmd -> {
//             String[] split_opcmd = opcmd.split(" ");
//             String country = split_opcmd[1];
//             String neighbor_country = split_opcmd[2];
//             if (split_opcmd[0].equals("add")) {
//                 // perform add country neighbor country
//             } else if (split_opcmd[1].equals("remove")) {
//                 // perform remove country neighbor country
//             }
//         });
//         return true;
//     }

// }
