package controllers;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Editor {
  public static void main(String[] args) {
    Scanner keyboard = new Scanner(System.in);
    String command = keyboard.nextLine();
    keyboard.close();
    String addPattern = "-add (\\w*) (\\w*)";
    String removePattern = "-remove (\\w*)";
    Pattern r = Pattern.compile(addPattern);
    Matcher m = r.matcher(command);
    while (m.find()) {
      System.out.println("Adding " + m.group(1) + " " + m.group(2));
    }
    r = Pattern.compile(removePattern);
    m = r.matcher(command);
    while (m.find()) {
      System.out.println("Removing" + m.group(1));
    }

    // Use this to check if a country already exists when adding a country
    // this.countries.stream().anyMatch(c->c.name.equals("india"));
  }
}
