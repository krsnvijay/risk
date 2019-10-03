package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import models.GameMap;

public class MapParser {
	public static GameMap loadMap(String fileName) throws IOException, Exception {
		HashMap<String, ArrayList<String>> mapData = new HashMap<String, ArrayList<String>>();
		Scanner scanner = new Scanner(new File(fileName));
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.matches("\\[\\w*\\]")) {
				ArrayList<String> sectionData = new ArrayList<String>();
				while (scanner.hasNext()) {
					String sectionLine = scanner.nextLine();
					if (sectionLine.trim().isEmpty())
						break;
					sectionData.add(sectionLine);
				}
				mapData.put(line, sectionData);
			}
		}
		scanner.close();
		if(!mapData.containsKey("[countries]") || !mapData.containsKey("[continents]") || !mapData.containsKey("[borders]"))
			throw new Exception("Map file is invalid");
		return new GameMap(mapData);
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Invalid argument");
			System.out.println("Usage : MapParser <map_location>");
			System.exit(-1);
		}
		try {
			GameMap testMap = loadMap(args[0]);	
			testMap.showMapByContinents();
			System.out.println();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
