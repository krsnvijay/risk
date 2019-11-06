package models;

import java.util.Objects;

/**
 * This class maintains information for a continent.
 *
 * @author Sabari
 * @version 1.0
 */
public class Continent {

  /** The colour of the continent (serialization). */
  private String color;

  /** The name of the continent. */
  private String name;

  /** The control value of the continent. */
  private int value;

  /**
   * The constructor for a Continent object.
   *
   * @param color The colour of the continent.
   * @param name The name of the continent.
   * @param value The control value of the continent.
   */
  public Continent(String color, String name, int value) {
    this.color = color;
    this.name = name;
    this.value = value;
  }

  /**
   * The constructor for the Continent object.
   *
   * @param name The name of the continent.
   * @param value The control value of the continent.
   */
  public Continent(String name, int value) {
    this.color = "BLANK";
    this.name = name;
    this.value = value;
  }

  /**
   * Returns the colour.
   *
   * @return A String with the colour.
   */
  public String getColor() {
    return color;
  }

  /**
   * Sets the colour.
   *
   * @param color A String with the colour.
   */
  public void setColor(String color) {
    this.color = color;
  }

  /**
   * Returns the name.
   *
   * @return A String with the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name A String with the name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the control value.
   *
   * @return An int with the control value.
   */
  public int getValue() {
    return value;
  }

  /**
   * Sets the control value.
   *
   * @param value An int with the control value.
   */
  public void setValue(int value) {
    this.value = value;
  }

  /** Pretty prints the Continent object. */
  @Override
  public String toString() {
    return String.format("%s %d %s", this.name, this.value, this.color);
  }

  /** Checks whether one Continent object is equal to another. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Continent continent = (Continent) o;
    return value == continent.value && color.equals(continent.color) && name.equals(continent.name);
  }

  /** @return int hash code for the Continent object. */
  @Override
  public int hashCode() {
    return Objects.hash(color, name, value);
  }
}
